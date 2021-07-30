package eu.ha3.matmos;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.launchwrapper.Launch;

import static eu.ha3.matmos.util.MAtUtil.getParentSafe;

/**
 * Class for statically initializing and accessing the config. Required to make
 * it possible to access the config from the coremod/tweaker.
 */

public class ConfigManager {

    private static final Logger LOGGER = LogManager.getLogger("matmos");

    private static final ConfigProperty config = new ConfigProperty();
    private static boolean hasInitialized = false;

    private static File configFolder = null;

    private static void initConfig() {
        // Create default configuration

        config.setProperty("world.height", 256);
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
        config.setProperty("coremod.replacesoundsystem", "auto",
                "There's a bug in Minecraft's sound system that causes it to crash after some time if looping streams are played.\n"
                        + "Forge provides a fix for this in 1.12.2, but MAtmos has to provide its own fix on 1.7.10, and on LiteLoader versions.\n"
                        + "Use this option to control when the SoundSystem should be overridden.\n\n"
                        + "Allowed values are: always, never, auto (which only overrides if no other mod is present which also overrides it (like DynamicSurroundings on 1.7.10, or Forge itself on 1.12.2))");
        config.setProperty("dimensions.list", "",
                "Comma-separated list of dimensions. If dimensions.listtype is black, then ambience will NOT be played in these dimensions.\n"
                        + "If it's white, then ambience will ONLY play in these dimensions.\n");
        config.setProperty("dimensions.listtype", "black", "BLACK or WHITE?\n");
        config.setProperty("rain.suppress", "auto",
                "Use this option to control how the conflict should be resolved between MAtmos rain sounds\n" +
                "and rain sounds from vanilla or other mods.\n" +
                "True: rain from other sources is muted\n" +
                "False: rain is muted from MAtmos soundpacks which support this option\n" +
                "Auto: true if there's at least one soundpack which supports this option present, false otherwise\n");
        config.setProperty("rain.soundlist", "ambient.weather.rain,ambient.weather.rain_calm,rain",
                "Comma-separated list of rain sounds to suppress if rain.suppress is true");
        config.setProperty("rain.strengththreshold", "-1",
                "Rain strength threshold above which it's considered to be raining by soundpacks\n" +
                "Range: 0~1, or -1 to use the default setting, which is 0.2 in vanilla\n" +
                "Set this to something low like 0 for better compatibility with Weather2\n"
                );
        config.setProperty("dealias.oredict", "true",
                "Dealias oredicted blocks to the lowest id block of the oredict group."
                );
        config.setProperty("dealias.guessfromclass", "true",
                "Guess the aliases of items from their class and name"
                );
        config.setProperty("debug.verbosealiasparsing", "false",
                "Show alias map warnings unconditionally"
                );
        config.setProperty("log.printcrashestochat", "true",
                "Display message in chat when MAtmos crashes"
                );
        config.commit();

        config.setGlobalDescription("Tip: restart MAtmos to reload the configs without restarting Minecraft");

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
        if (!hasInitialized) {
            initConfig();
        }
        return config;
    }

    public static File getConfigFolder() {
        if (configFolder == null) {
            configFolder = new File(Launch.minecraftHome, "config/matmos");
        }
        return configFolder;
    }
    
    public static Path getDefaultConfigFilePath(Path relPath) throws IOException {
        String resourceRelPath = Paths.get("assets/matmos/default_config/").resolve(relPath).toString().replace('\\', '/');
        URL resourceURL = ConfigManager.class.getClassLoader().getResource(resourceRelPath);
        
        switch(resourceURL.getProtocol()) {
        case "jar":
            String urlString = resourceURL.getPath();
            int lastExclamation = urlString.lastIndexOf('!');
            String newURLString = urlString.substring(0, lastExclamation);
            return FileSystems.newFileSystem(new File(URI.create(newURLString)).toPath(), null).getPath(resourceRelPath);
        case "file":
            return new File(URI.create(resourceURL.toString())).toPath();
        default:
            return null;
        }
    }
    
    private static void copyDefaultConfigFile(Path src, Path dest) throws IOException {
        Files.createDirectories(getParentSafe(src));
        LOGGER.debug("Copying " + src + " -> " + dest);
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
    }

    public static boolean createDefaultConfigFileIfMissing(File configFile, boolean overwrite) {
        return createDefaultConfigFileIfMissing(configFile, s -> overwrite);
    }
    
    public static boolean createDefaultConfigFileIfMissing(File configFile, Predicate<? super byte[]> overwrite) {
        Path configFolderPath = Paths.get(getConfigFolder().getPath());
        Path configFilePath = Paths.get(configFile.getPath());

        Path relPath = configFolderPath.relativize(configFilePath);
        
        if (configFilePath.startsWith(configFolderPath)) {
            try {
                Path defaultConfigPath = getDefaultConfigFilePath(relPath);
                if(Files.isRegularFile(defaultConfigPath)) {
                    byte[] data = null;
                    if(configFile.exists()){
                        try(InputStream is = Files.newInputStream(configFile.toPath())){
                            data = IOUtils.toByteArray(is);
                        }
                    }
                    if(!configFile.exists() || overwrite.test(data)) {
                        copyDefaultConfigFile(defaultConfigPath, configFile.toPath());
                    }
                } else if(Files.isDirectory(defaultConfigPath)) {
                    Files.createDirectories(Paths.get(configFile.getPath()));
                    // create contents of directory as well
                    for(Object po : Files.walk(defaultConfigPath).toArray()) {
                        if(Files.isRegularFile((Path)po)) {
                            copyDefaultConfigFile((Path)po, configFile.toPath().resolve(
                                    defaultConfigPath.toAbsolutePath().relativize(((Path)po).toAbsolutePath()).toString()));
                        }
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to create default config file for " + relPath.toString() + ": " + e.getMessage());
                return false;
            }
        } else {
            LOGGER.debug("Invalid argument for creating default config file: " + relPath.toString()
                    + " (file is not in the config directory)");
            return false;
        }
        return true;
    }
}
