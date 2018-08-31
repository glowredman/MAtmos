package eu.ha3.matmos.core.expansion;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.matmos.MAtLog;
import eu.ha3.matmos.MAtResourcePackDealer;
import eu.ha3.matmos.core.expansion.agents.JasonLoadingAgent;
import eu.ha3.matmos.core.expansion.agents.LegacyXMLLoadingAgent;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sound.SoundAccessor;
import eu.ha3.matmos.data.Collector;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.ResourceLocation;

/* x-placeholder */

public class ExpansionManager implements VolumeUpdatable, SupportsTickEvents, SupportsFrameEvents {
    private final SoundAccessor accessor;
    private final File userconfigFolder;

    private final MAtResourcePackDealer dealer = new MAtResourcePackDealer();
    private final Map<String, Expansion> expansions = new HashMap<>();
    //private final Map<String, IResourcePack> confirmedResourcePacks = new LinkedHashMap<String, IResourcePack>();

    private DataPackage data;

    private float volume = 1f;
    private Collector collector;

    public ExpansionManager(File userconfigFolder, SoundAccessor accessor) {
        this.userconfigFolder = userconfigFolder;
        this.accessor = accessor;

        if (!this.userconfigFolder.exists()) {
            this.userconfigFolder.mkdirs();
        }
    }

    public void loadExpansions() {
        dispose();

        List<ExpansionIdentity> identities = new ArrayList<>();
        findExpansions(identities);
        for (ExpansionIdentity identity : identities) {
            addExpansion(identity);
        }
    }

    private void findExpansions(List<ExpansionIdentity> identities) {
        List<ResourcePackRepository.Entry> resourcePacks = dealer.findResourcePacks();
        for (ResourcePackRepository.Entry pack : resourcePacks) {
            try {
                InputStream is = dealer.openExpansionsPointerFile(pack.getResourcePack());
                String jasonString = IOUtils.toString(is, "UTF-8");

                JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
                JsonArray expansions = jason.get("expansions").getAsJsonArray();
                for (JsonElement element : expansions) {
                    JsonObject o = element.getAsJsonObject();
                    String uniqueName = MAtUtil.sanitizeUniqueName(o.get("uniquename").getAsString());
                    String friendlyName = o.get("friendlyname").getAsString();
                    String pointer = o.get("pointer").getAsString();
                    ResourceLocation location = new ResourceLocation("matmos", pointer);
                    if (pack.getResourcePack().resourceExists(location)) {
                        ExpansionIdentity identity = new ExpansionIdentity(uniqueName, friendlyName, pack.getResourcePack(), location);
                        identities.add(identity);
                    } else {
                        MAtLog.warning("An expansion pointer doesn't exist: " + pointer);
                    }
                }
            } catch (Exception e) {
                MAtLog.warning(pack.getResourcePackName() + " " + "has failed with an error: " + e.getMessage());
            }
        }
    }

    private void addExpansion(ExpansionIdentity identity) {
        Expansion expansion = new Expansion(identity, data, collector, accessor, this, new File(
                userconfigFolder, identity.getUniqueName() + ".cfg"));
        expansions.put(identity.getUniqueName(), expansion);

        if (identity.getLocation().getPath().endsWith(".xml")) {
            File folder = new File(userconfigFolder, "DO NOT EDIT UNLESS COPIED/");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filename = identity.getUniqueName() + ".json";
            if (filename.startsWith("legacy__")) {
                filename = filename.substring("legacy__".length());
            }
            expansion.setLoadingAgent(new LegacyXMLLoadingAgent(new File(folder, filename)));
        } else {
            expansion.setLoadingAgent(new JasonLoadingAgent());
        }

        expansion.updateVolume();
    }

    public Map<String, Expansion> getExpansions() {
        return expansions;
    }

    private void synchronizeStable(Expansion expansion) {
        if (expansion == null) {
            return;
        }

        if (expansion.isActivated()) {
            if (expansion.getVolume() <= 0f) {
                expansion.deactivate();
            }
        } else {
            if (expansion.getVolume() > 0f) {
                expansion.activate();
            }
        }
    }

    public void synchronize() {
        for (Expansion expansion : expansions.values()) {
            synchronizeStable(expansion);
        }
    }

    @Override
    public void onFrame(float f) {
        for (Expansion expansion : expansions.values()) {
            expansion.simulate();
        }
    }

    @Override
    public void onTick() {
        for (Expansion expansion : expansions.values()) {
            expansion.evaluate();
        }
    }

    public void setData(DataPackage data) {
        this.data = data;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
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
        for (Expansion e : expansions.values()) {
            e.updateVolume();
        }
    }

    public void interrupt() {
        for (Expansion exp : expansions.values()) {
            exp.interrupt();
        }
    }

    public void dispose() {
        for (Expansion expansion : expansions.values()) {
            expansion.dispose();
        }
    }

    public void saveConfig() {
        for (Expansion expansion : expansions.values()) {
            expansion.saveConfig();
        }
    }
}
