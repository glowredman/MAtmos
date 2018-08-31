package eu.ha3.matmos.core.expansion;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.MAtLog;
import eu.ha3.matmos.core.Evaluated;
import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.ProviderCollection;
import eu.ha3.matmos.core.ReferenceTime;
import eu.ha3.matmos.core.Simulated;
import eu.ha3.matmos.core.SystemClock;
import eu.ha3.matmos.core.event.EventInterface;
import eu.ha3.matmos.core.expansion.agents.LoadingAgent;
import eu.ha3.matmos.core.expansion.agents.RawJsonLoadingAgent;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sound.SoundAccessor;
import eu.ha3.matmos.core.sound.SoundHelperRelay;
import eu.ha3.matmos.data.Collector;
import eu.ha3.matmos.data.modules.ModuleRegistry;
import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.util.ResourceLocation;

public class Expansion implements VolumeUpdatable, Stable, Simulated, Evaluated {
    private static ReferenceTime TIME = new SystemClock();

    private final ExpansionIdentity identity;
    private final DataPackage data;
    private final Collector collector;
    private final SoundHelperRelay capabilities;
    private final VolumeContainer masterVolume;
    private final ConfigProperty myConfiguration;

    private float volume;
    private boolean isSuccessfullyBuilt;
    private boolean isActive;
    private boolean reliesOnLegacyModules;

    private Knowledge knowledge; // Knowledge is not final

    private LoadingAgent agent;
    private LoadingAgent jasonDebugPush;

    public Expansion(ExpansionIdentity identity, DataPackage data, Collector collector, SoundAccessor accessor, VolumeContainer masterVolume, File configurationSource) {
        this.identity = identity;
        this.masterVolume = masterVolume;
        capabilities = new SoundHelperRelay(accessor);
        this.data = data;
        this.collector = collector;

        newKnowledge();

        myConfiguration = new ConfigProperty();
        myConfiguration.setProperty("volume", 1f);
        myConfiguration.commit();
        try {
            myConfiguration.setSource(configurationSource.getCanonicalPath());
            myConfiguration.load();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        setVolumeAndUpdate(myConfiguration.getFloat("volume"));
    }

    public void setLoadingAgent(LoadingAgent agent) {
        this.agent = agent;
    }

    public void refreshKnowledge() {
        boolean reactivate = isActivated();
        deactivate();

        newKnowledge();
        isSuccessfullyBuilt = false;

        if (reactivate) {
            activate();
        }
    }

    public void pushDebugJasonAndRefreshKnowledge(String jasonString) {
        jasonDebugPush = new RawJsonLoadingAgent(jasonString);
        refreshKnowledge();
    }

    private void newKnowledge() {
        knowledge = new Knowledge(capabilities, TIME);
        knowledge.setData(data);
    }

    private void buildKnowledge() {
        if (agent == null) {
            return;
        }

        newKnowledge();

        if (jasonDebugPush == null) {
            isSuccessfullyBuilt = agent.load(identity, knowledge);
        } else {
            isSuccessfullyBuilt = jasonDebugPush.load(identity, knowledge);
            jasonDebugPush = null;
        }

        if (!isSuccessfullyBuilt) {
            newKnowledge();
        }

        knowledge.cacheSounds(identity);
    }

    public void playSample() {
        if (!isActivated()) {
            return;
        }

        EventInterface event = knowledge.obtainProviders().getEvent().get("__SAMPLE");
        if (event != null) {
            event.playSound(1f, 1f);
        }
    }

    public String getName() {
        return identity.getUniqueName();
    }

    public String getFriendlyName() {
        return identity.getFriendlyName();
    }

    public void saveConfig() {
        myConfiguration.setProperty("volume", volume);
        if (myConfiguration.commit()) {
            myConfiguration.save();
        }
    }

    public boolean reliesOnLegacyModules() {
        return reliesOnLegacyModules;
    }

    @Override
    public void simulate() {
        if (!isActive) {
            return;
        }

        try {
            knowledge.simulate();
        } catch (Exception e) {
            e.printStackTrace();
            deactivate();
        }
    }

    @Override
    public void evaluate() {
        if (!isActive) {
            return;
        }

        try {
            knowledge.evaluate();
        } catch (Exception e) {
            e.printStackTrace();
            deactivate();
        }
    }

    @Override
    public boolean isActivated() {
        return isActive;
    }

    @Override
    public void activate() {
        if (isActive) {
            return;
        }

        if (getVolume() <= 0) {
            return;
        }

        if (!isSuccessfullyBuilt && agent != null) {
            MAtLog.info("Building expansion " + getName() + "...");
            TimeStatistic stat = new TimeStatistic(Locale.ENGLISH);
            buildKnowledge();
            if (isSuccessfullyBuilt) {
                MAtLog.info("Expansion " + getName() + " built (" + stat.getSecondsAsString(3) + "s).");
            } else {
                MAtLog.warning("Expansion " + getName() + " failed to build!!! (" + stat.getSecondsAsString(3) + "s).");
            }
        }

        if (collector != null) {
            Set<String> requiredModules = knowledge.calculateRequiredModules();
            collector.addModuleStack(identity.getUniqueName(), requiredModules);

            MAtLog.info("Expansion " + identity.getUniqueName() + " requires " + requiredModules.size() + " found modules: " + Arrays.toString(requiredModules.toArray()));

            List<String> legacyModules = new ArrayList<>();
            for (String module : requiredModules) {
                if (module.startsWith(ModuleRegistry.LEGACY_PREFIX)) {
                    legacyModules.add(module);
                }
            }
            if (legacyModules.size() > 0) {
                Collections.sort(legacyModules);
                MAtLog.warning("Expansion " + identity.getUniqueName() + " uses LEGACY modules: " + Arrays.toString(legacyModules.toArray()));
                reliesOnLegacyModules = true;
            }
        }

        isActive = true;

    }

    @Override
    public void deactivate() {
        if (!isActive) {
            return;
        }

        if (collector != null) {
            collector.removeModuleStack(identity.getUniqueName());
        }

        isActive = false;
    }

    @Override
    public void dispose() {
        deactivate();
        capabilities.cleanUp();
        newKnowledge();
        setLoadingAgent(null);
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolumeAndUpdate(float volume) {
        this.volume = volume;
        updateVolume();
    }

    @Override
    public void updateVolume() {
        capabilities.applyVolume(masterVolume.getVolume() * getVolume());
    }

    /**
     * Interrupt this expansion brutally, without calling cleanup calls.
     */
    @Override
    public void interrupt() {
        capabilities.interrupt();
    }

    /**
     * Obtain the providers of the knowledge, for debugging purposes.
     */
    public ProviderCollection obtainProvidersForDebugging() {
        return knowledge.obtainProviders();
    }

    public ExpansionDebugUnit obtainDebugUnit() {
        try {
            if (identity.getPack() instanceof FolderResourcePack) {
                FolderResourcePack frp = (FolderResourcePack)identity.getPack();
                String folderName = frp.getPackName();
                // XXX: getPackName might not be specified to return the folder name?

                final File folder = new File(Minecraft.getMinecraft().gameDir, "resourcepacks/" + folderName);

                if (folder.exists() && folder.isDirectory()) {
                    System.out.println(identity.getLocation().getPath());
                    final File file = new File(folder, "assets/matmos/" + identity.getLocation().getPath());

                    return new FolderExpansionDebugUnit() {
                        @Override
                        public Knowledge getKnowledge() {
                            return knowledge;
                        }

                        @Override
                        public DataPackage getData() {
                            return data;
                        }

                        @Override
                        public File getExpansionFile() {
                            return file;
                        }

                        @Override
                        public File getExpansionFolder() {
                            return folder;
                        }
                    };
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new JsonExpansionDebugUnit() {

            @Override
            public Knowledge getKnowledge() {
                return knowledge;
            }

            @Override
            public DataPackage getData() {
                return data;
            }

            @Override
            public String getJsonString() {
                try (Scanner sc = new Scanner(identity.getPack().getInputStream(identity.getLocation()))) {
                    // XXX does not handle XML
                    return sc.useDelimiter("\\Z").next();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.err.println("Jason unavailable.");
                    return "{}";
                }
            }
        };
    }

    public boolean hasMoreInfo() {
        return identity.getPack().resourceExists(new ResourceLocation("matmos", "info.txt"));
    }

    public String getInfo() {
        try (Scanner sc = new Scanner(identity.getPack().getInputStream(new ResourceLocation("matmos", "info.txt")))) {
            return sc.useDelimiter("\\Z").next();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error while fetching info.txt";
        }
    }
}
