package eu.ha3.matmos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.launchwrapper.Launch;

/** Class for statically initializing and accessing the config.
 * Required to make it possible to access the config from the coremod/tweaker.
 */

public class ConfigManager {
    
    private static final Logger LOGGER = LogManager.getLogger("matmos");
    
    private static final ConfigProperty config = new ConfigProperty();
    private static boolean hasInitialized = false;
    
    private static File configFolder = null;
    
    private static void initConfig() {
        // Create default configuration
        
        config.setProperty("world.height", 256);
        config.setProperty("world.maxblockid", 4096, "The max block ID. This is 4096 normally, but there are mods that raise it. Getting an ArrayIndexOutOfBoundsException is indication that it needs to be raised.");
        config.setProperty("dump.sheets.enabled", false);
        config.setProperty("start.enabled", true, "If false, MAtmos won't start until the MAtmos key is pressed.");
        config.setProperty("reversed.controls", false);
        config.setProperty("sound.autopreview", true);
        config.setProperty("globalvolume.scale", 1f);
        config.setProperty("key.code", 65);
        config.setProperty("useroptions.altitudes.high", true);
        config.setProperty("useroptions.altitudes.low", true);
        config.setProperty("useroptions.biome.override", -1);
        config.setProperty("debug.mode", 0);
        config.setProperty("minecraftsound.ambient.volume", 1f);
        config.setProperty("coremod.replacesoundsystem", "auto", "There's a bug in Minecraft's sound system that causes it to crash after some time if looping streams are played.\n" +
                                                                 "Forge provides a fix for this in 1.12.2, but MAtmos has to provide its own fix on 1.7.10, and on LiteLoader versions.\n" +
                                                                 "Use this option to control when the SoundSystem should be overridden.\n\n" +
                                                                 "Allowed values are: always, never, auto (which only overrides if no other mod is present which also overrides it (like DynamicSurroundings on 1.7.10, or Forge itself on 1.12.2))");
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
    
    public static void createDefaultConfigFileIfMissing(File configFile) {
        Path configFolderPath = Paths.get(getConfigFolder().getPath());
        Path configFilePath = Paths.get(configFile.getPath());
        
        Path relPath = configFolderPath.relativize(configFilePath);
        
        if(configFilePath.startsWith(configFolderPath)) {
            if(!configFile.exists()) {
                try {
                    InputStream defaultFileStream = ConfigManager.class.getClassLoader()
                    .getResourceAsStream(Paths.get("assets/matmos/default_config/").resolve(relPath)
                            .toString().replace('\\', '/'));
                    
                    if(defaultFileStream != null) {
                        String contents = IOUtils.toString(defaultFileStream);
                        
                        try(FileWriter out = new FileWriter(configFile)){
                            IOUtils.write(contents, out);
                        }
                    }
                } catch (IOException e) {
                    LOGGER.error("Failed to create default config file for " + relPath.toString());
                }
            }
        } else {
            LOGGER.debug("Invalid argument for creating default config file: " + relPath.toString() + " (file is not in the config directory)");
        }
    }
}
