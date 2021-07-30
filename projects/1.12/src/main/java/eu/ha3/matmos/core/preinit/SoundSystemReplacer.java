package eu.ha3.matmos.core.preinit;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import eu.ha3.util.property.simple.ConfigProperty;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/**
 * This class prepares SoundSystemReplacerTransformer.
 */
public class SoundSystemReplacer {

    public static final Logger logger = LogManager.getLogger("matmos-preinit");

    public static ConfigProperty preinitConfig;

    private static List<String> findModsWithConflicts() {
        List<String> conflicts = new ArrayList<>();

        if (Launch.classLoader.getResource("net/minecraftforge/fml") != null) {
            conflicts.add("Forge Modloader");
        }

        return conflicts;
    }

    public static void run() {
        String option = ConfigManager.getConfig().getString("coremod.replacesoundsystem");

        boolean shouldReplace = false;

        switch (option) {
        case "always":
            shouldReplace = true;
            break;
        case "auto":
            List<String> conflicts = findModsWithConflicts();
            if (!conflicts.isEmpty()) {
                logger.info("SoundSystem won't be replaced, because these conflicting mods were found: " + conflicts);
            } else {
                shouldReplace = true;
            }
            break;
        case "never":
            logger.info("SoundSystem won't be replaced, because it was disabled in the config.");
            break;
        }

        if (shouldReplace) {
            logger.info("Initializing SoundSystem replacer transformer");
            URL packageURL = SoundSystemReplacer.class.getClassLoader().getResource("eu/ha3/matmos");
            if (packageURL == null) {
                logger.info("Couldn't find eu.ha3.matmos package in MAtmos jar. SoundSystem will not be overloaded.");
            } else {
                logger.debug("Resolving base jar URL from " + packageURL);
                URL baseJarURL = getBaseJarURL(packageURL);
                if (baseJarURL != null) {
                    logger.debug("Resolved base jar URL to " + baseJarURL);

                    try {
                        LaunchClassLoader lcl = (LaunchClassLoader) Launch.classLoader;

                        Field transformersField = LaunchClassLoader.class.getDeclaredField("transformers");

                        transformersField.setAccessible(true);

                        List<IClassTransformer> transformers = (List<IClassTransformer>) transformersField.get(lcl);

                        // Our transformer has to modify the SoundSystem before any other transformers,
                        // otherwise it would erase the work of previous transformers.
                        transformers.add(0, (IClassTransformer) new SoundSystemReplacerTransformer(baseJarURL));

                        logger.info("Finished initializing SoundSystem replacer transformer");
                    } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
                            | IllegalAccessException e) {
                        logger.warn(
                                "Exception registering SoundSystem replacer transformer. SoundSystem will not be overloaded.");
                        e.printStackTrace();
                    }
                } else {
                    logger.warn("Failed to extract base jar url from " + packageURL
                            + ". SoundSystem will not be overloaded.");
                }
            }
        }
    }

    public static URL getBaseJarURL(URL packageURL) {
        String newURLString = null;

        switch (packageURL.getProtocol()) {
        case "jar":
            String urlString = packageURL.getPath();
            int lastExclamation = urlString.lastIndexOf('!');
            newURLString = urlString.substring(0, lastExclamation);
            break;
        default:
            SoundSystemReplacer.logger.info("The path to " + packageURL
                    + " isn't in a jar; we're probably in a dev environment. "
                    + "You'll have to manually configure your dev environment if you want to override the SoundSystem.");
            break;
        }

        if (newURLString != null) {
            try {
                return new URL(newURLString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
