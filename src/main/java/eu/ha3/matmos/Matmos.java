package eu.ha3.matmos;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import eu.ha3.easy.StopWatchStatistic;
import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.core.ducks.ISoundHandler;
import eu.ha3.matmos.core.expansion.Expansion;
import eu.ha3.matmos.core.expansion.Stable;
import eu.ha3.matmos.core.expansion.VolumeUpdatable;
import eu.ha3.matmos.core.preinit.ClassLoaderPrepender;
import eu.ha3.matmos.core.preinit.PreinitHelper;
import eu.ha3.matmos.core.sound.Simulacrum;
import eu.ha3.matmos.debug.Pluggable;
import eu.ha3.matmos.game.user.UserControl;
import eu.ha3.matmos.game.user.VisualDebugger;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.UpdatableIdentity;
import eu.ha3.mc.haddon.implem.HaddonIdentity;
import eu.ha3.mc.haddon.implem.HaddonImpl;
import eu.ha3.mc.haddon.implem.HaddonVersion;
import eu.ha3.mc.haddon.implem.UpdatableHaddonIdentity;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsInGameChangeEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.mc.quick.update.NotifiableHaddon;
import eu.ha3.mc.quick.update.UpdateNotifier;
import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import paulscode.sound.SoundSystemConfig;

public class Matmos extends HaddonImpl implements SupportsFrameEvents, SupportsTickEvents, SupportsInGameChangeEvents, NotifiableHaddon, IResourceManagerReloadListener, Stable {
    private static final boolean _COMPILE_IS_UNSTABLE = false;

    public static final Logger LOGGER = LogManager.getLogger("matmos");

    // Identity
    protected static final String NAME = "MAtmos";
    protected static final String VERSION = "34";
    protected static final String FOR = "1.12.2";
    protected static final String ADDRESS = "https://github.com/makamys/MAtmos";
    protected static final String UPDATE_JSON = "https://raw.githubusercontent.com/makamys/MAtmos/master/updatejson/update-matmos.json";
    public static final Identity identity = new HaddonIdentity(NAME, VERSION, FOR, ADDRESS);

    // NotifiableHaddon and UpdateNotifier
    private final ConfigProperty config = new ConfigProperty();
    private final Chatter chatter = new Chatter(this, "<MAtmos> ");
    private final UpdateNotifier updateNotifier = new UpdateNotifier(this, new HaddonVersion(FOR + "-" + VERSION), UPDATE_JSON);

    // State
    private boolean isListenerInstalled;
    private Optional<Simulacrum> simulacrum = Optional.empty();
    private boolean isUnderwaterMode;
    private boolean isDebugMode;

    // Components
    private UserControl userControl;

    // Use once
    private boolean hasFirstTickPassed;

    // Debug
    private StopWatchStatistic timeStat = new StopWatchStatistic();

    // Debug queue
    private Object queueLock = new Object();
    private List<Runnable> queue = new ArrayList<>();
    private boolean hasResourcePacks_FixMe;

    @Override
    public void onLoad() {
        String soundSystemTitle = PreinitHelper.getManifestAttributesOfClass("paulscode.sound.SoundSystem")
                .getValue("Implementation-Title");
        LOGGER.debug("SoundSystem implementation title: " + soundSystemTitle);
        if("MAtmos".equals(soundSystemTitle)) {
            LOGGER.info("Overriding SoundSystem was successful! (SoundSystem implementation title matches mod title.)");
        } else {
            LOGGER.info("Overriding SoundSystem probably failed! (SoundSystem implementation title doesn't match mod title!)");
        }
        
        this.<OperatorCaster>op().setTickEnabled(true);
        this.<OperatorCaster>op().setFrameEnabled(true);

        TimeStatistic timeMeasure = new TimeStatistic(Locale.ENGLISH);
        userControl = new UserControl(this);

        // Create default configuration
        updateNotifier.fillDefaults(config);
        config.setProperty("world.height", 256);
        config.setProperty("dump.sheets.enabled", false);
        config.setProperty("start.enabled", true);
        config.setProperty("reversed.controls", false);
        config.setProperty("sound.autopreview", true);
        config.setProperty("globalvolume.scale", 1f);
        config.setProperty("key.code", 65);
        config.setProperty("useroptions.altitudes.high", true);
        config.setProperty("useroptions.altitudes.low", true);
        config.setProperty("useroptions.biome.override", -1);
        config.setProperty("debug.mode", 0);
        config.setProperty("minecraftsound.ambient.volume", 1f);
        config.setProperty("version.last", VERSION);
        config.setProperty("version.warnunstable", 3);
        config.commit();

        // Load configuration from source
        try {
            config.setSource(new File(util().getMcFolder(), "matmos/userconfig.cfg").getCanonicalPath());
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error caused config not to work: " + e.getMessage());
        }

        resetAmbientVolume();

        updateNotifier.loadConfig(config);

        // This registers stuff to Minecraft (key bindings...)
        userControl.load();

        LOGGER.info("Took " + timeMeasure.getSecondsAsString(3) + " seconds to setup MAtmos base.");
    }

    private void resetAmbientVolume() {
        setSoundLevelAmbient(config.getFloat("minecraftsound.ambient.volume"));
    }

    private void overrideAmbientVolume() {
        if (config.getFloat("minecraftsound.ambient.volume") <= 0) {
            return;
        }
        setSoundLevelAmbient(0.01f);
    }

    private void setSoundLevelAmbient(float level) {
        GameSettings settings = Minecraft.getMinecraft().gameSettings;
        // For some reason it has to be set twice to validate it (???!)
        settings.setSoundLevel(SoundCategory.AMBIENT, level);
        settings.setSoundLevel(SoundCategory.AMBIENT, level);
    }

    public void start() {
        if (!isListenerInstalled) {
            isListenerInstalled = true;
            util().getClient().registerReloadListener(this);
        }
        refresh();
    }

    public void refresh() {
        deactivate();
        activate();
    }

    @Override
    public boolean isActivated() {
        return simulacrum.isPresent();
    }

    @Override
    public void activate() {
        if (isActivated()) {
            return;
        }
        LOGGER.info("Loading...");
        simulacrum = Optional.of(new Simulacrum(this));
        LOGGER.info("Loaded.");
    }

    @Override
    public void deactivate() {
        if (!isActivated()) {
            return;
        }
        LOGGER.info("Stopping...");
        simulacrum.get().dispose();
        simulacrum = Optional.empty();
        LOGGER.info("Stopped.");
    }

    // Events

    @Override
    public void onFrame(float semi) {
        Minecraft.getMinecraft().profiler.startSection("onframe");
        if (!isActivated() || util().isGamePaused()) {
            return;
        }
        simulacrum.get().onFrame(semi);
        userControl.onFrame(semi);
        Minecraft.getMinecraft().profiler.endSection();
    }

    @Override
    public void onTick() {
        Minecraft.getMinecraft().profiler.startSection("ontick");
        
        userControl.onTick();
        if (isActivated()) {
            if (!queue.isEmpty()) {
                synchronized (queueLock) {
                    while (!queue.isEmpty()) {
                        queue.remove(0).run();
                    }
                }
            }

            timeStat.reset();
            simulacrum.get().onTick();
            timeStat.stop();

            if (MAtUtil.isUnderwaterAnyGamemode()) {
                if (!isUnderwaterMode) {
                    isUnderwaterMode = true;
                    overrideAmbientVolume();
                }
            } else if (isUnderwaterMode) {
                isUnderwaterMode = false;
                resetAmbientVolume();
            }
        } else if (isUnderwaterMode) {
            isUnderwaterMode = false;
            resetAmbientVolume();
        }

        if (!hasFirstTickPassed) {
            hasFirstTickPassed = true;
            updateNotifier.attempt();
            if (_COMPILE_IS_UNSTABLE) {
                String lastVersion = config.getString("version.last");
                int warns = config.getInteger("version.warnunstable");
                if (!lastVersion.equals(VERSION)) {
                    warns = 3;
                    config.setProperty("version.last", VERSION);
                }
                if (warns > 0) {
                    warns--;
                    config.setProperty("version.warnunstable", warns);
                    getChatter().printChat(
                            TextFormatting.RED, "You are using an ", TextFormatting.YELLOW, "Unofficial Beta", TextFormatting.RED, " version of MAtmos.");
                    getChatter().printChatShort("By using this version, you understand that this mod isn't intended for " +
                            "actual game sessions, MAtmos may not work, might crash, the sound ambience is incomplete, etc. Use at your own risk. ");
                    getChatter().printChatShort("Please check regularly for updates and resource pack updates.");
                    if (warns > 0) {
                        getChatter().printChatShort("This message will appear ", TextFormatting.YELLOW, warns, " more times.");
                    }
                }
                if (config.commit()) {
                    config.save();
                }
            }

            if (isDebugMode()) {
                getChatter().printChat(TextFormatting.GOLD, "Developer mode is enabled in the Advanced options.");
                getChatter().printChatShort("This affects performance. Your game may run slower.");
            }

            if (!simulacrum.get().hasResourcePacks()) {
                hasResourcePacks_FixMe = true;
                if (simulacrum.get().hasDisabledResourcePacks()) {
                    chatter.printChat(TextFormatting.RED, "Resource Pack not enabled yet!");
                    chatter.printChatShort(TextFormatting.WHITE, "You need to activate \"MAtmos Resource Pack\" in the Minecraft Options menu for it to run.");
                } else {
                    chatter.printChat(TextFormatting.RED, "Resource Pack missing from resourcepacks/!");
                    chatter.printChatShort(TextFormatting.WHITE, "You may have forgotten to put the Resource Pack file into your resourcepacks/ folder.");
                }
            }
        }

        if (isActivated() && hasResourcePacks_FixMe && simulacrum.get().hasResourcePacks()) {
            hasResourcePacks_FixMe = false;
            chatter.printChat(TextFormatting.GREEN, "It should work now!");
        }
        Minecraft.getMinecraft().profiler.endSection();
    }

    @Override
    public void dispose() {
        if (isActivated()) {
            simulacrum.get().dispose();
        }
    }

    @Override
    public void interrupt() {
        if (isActivated()) {
            simulacrum.get().interruptBrutally();
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        LOGGER.warn("ResourceManager has changed. Unintended side-effects may happen.");
        interrupt();
        // Initiate hot reload
        if (isActivated()) {
            simulacrum.get().interruptBrutally();
            deactivate();
            activate();
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Expansion> getExpansionList() {
        if (isActivated()) {
            return simulacrum.get().getExpansions();
        }
        return Collections.EMPTY_MAP;
    }

    public boolean isInitialized() {
        return isListenerInstalled;
    }

    @Override
    public Identity getIdentity() {
        return identity;
    }

    @Override
    public Chatter getChatter() {
        return chatter;
    }

    @Override
    public ConfigProperty getConfig() {
        return config;
    }

    @Override
    public void saveConfig() {
        // If there were changes...
        if (config.commit()) {
            LOGGER.info("Saving configuration...");
            // Write changes on disk.
            config.save();
        }
    }

    public ISoundHandler getSoundHandler() {
        return ((ISoundHandler)Minecraft.getMinecraft().getSoundHandler());
    }

    public VolumeUpdatable getGlobalVolumeControl() {
        return simulacrum.get().getGlobalVolumeControl();
    }

    public boolean hasResourcePacksLoaded() {
        return isActivated() && simulacrum.get().hasResourcePacks();
    }

    public boolean hasNonethelessResourcePacksInstalled() {
        return isActivated() && simulacrum.get().hasDisabledResourcePacks();
    }

    public void synchronize() {
        if (isActivated()) {
            simulacrum.get().synchronize();
        }
    }

    public void saveExpansions() {
        if (isActivated()) {
            simulacrum.get().saveConfig();
        }
    }
    
    public void addUpdateNotifierJob(UpdatableIdentity id) {
        updateNotifier.addJob(id);
    }
    
    public void attemptUpdateNotifier() {
        updateNotifier.attempt();
    }

    public VisualDebugger getVisualDebugger() {
        // UNCHECKED!
        return simulacrum.get().getVisualDebugger();
    }

    public StopWatchStatistic getLag() {
        return timeStat;
    }

    public void queueForNextTick(Runnable runnable) {
        synchronized (queueLock) {
            queue.add(runnable);
        }
    }

    public boolean isDebugMode() {
        return isDebugMode;
    }

    public void changedDebugMode() {
        isDebugMode = config.getInteger("debug.mode") > 0;
        
        if (isDebugMode()) {
            getChatter().printChat(TextFormatting.GOLD, "Dev/Editor mode enabled.");
            getChatter().printChatShort("Enabling this mode may cause Minecraft to run slower.");
        } else {
            getChatter().printChat(TextFormatting.GOLD, "Dev/Editor mode disabled.");
        }
        refresh();
    }

    public boolean isEditorAvailable() {
        return util().isPresent("eu.ha3.matmos.editor.EditorMaster");
    }

    public Runnable instantiateRunnableEditor(Pluggable pluggable) {
        return util().<Runnable>getInstantiator("eu.ha3.matmos.editor.EditorMaster", Pluggable.class).instantiate(pluggable);
    }

    public Optional<Expansion> getExpansionEffort(String expansionName) {
        if (!isActivated() || !simulacrum.get().getExpansions().containsKey(expansionName)) {
            return Optional.empty();
        }
        return Optional.of(simulacrum.get().getExpansions().get(expansionName));
    }

    @Override
    public void onInGameChange(boolean inGame) {
        if(inGame) {
            if (config.getBoolean("start.enabled")) {
                start();
            }
        } else {
            deactivate();
        }
    }
}
