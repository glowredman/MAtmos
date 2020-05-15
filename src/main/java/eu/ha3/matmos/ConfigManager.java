package eu.ha3.matmos;

import java.io.File;
import java.io.IOException;

import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.launchwrapper.Launch;

/** Class for statically initializing and accessing the config.
 * Required to make it possible to access the config from the coremod/tweaker.
 */

public class ConfigManager {
    private static final ConfigProperty config = new ConfigProperty();
    private static boolean hasInitialized = false;
    
    private static File configFolder = null;
    
    private static void initConfig() {
        // Create default configuration
        
        config.setProperty("world.height", 256);
        config.setProperty("world.maxblockid", 4096);
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
        config.setProperty("coremod.replacesoundsystem", "auto");
        config.commit();

        // Load configuration from source
        try {
            config.setSource(new File(getConfigFolder(), "userconfig.cfg").getCanonicalPath());
            config.load();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error caused config not to work: " + e.getMessage());
        }
        
        hasInitialized = true;
    }
    
    public static ConfigProperty getConfig() {
        if(!hasInitialized) {
            initConfig();
        }
        return config;
    }
    
    public static File getConfigFolder() {
        if(configFolder == null) {
            configFolder = new File(Launch.minecraftHome, "config/matmos");
        }
        return configFolder;
    }
}
