package eu.ha3.matmos.core.preinit;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.preinit.forge.ClassLoaderPrependerPlugin;
import net.minecraft.launchwrapper.Launch;
import paulscode.sound.SoundSystemException;

/**
 * <p>This class hacks LaunchClassLoader to make it check this mod's jar first when loading
 *    the classes of the paulscode SoundSystem.</p>
 * 
 * <p>The purpose of this is to make overriding the SoundSystem possible, in order to port
 *    Forge's fixes to platforms which don't have it (i.e. older Forge versions and LiteLoader).</p>
 *
 */ 
public class ClassLoaderPrepender {
    
    public static final Logger logger = LogManager.getLogger("matmos-preinit");
    
    public static void run() {
        boolean shouldOverloadSoundSystem = true; // TODO make configurable
        
        if(!shouldOverloadSoundSystem) return;
        
        logger.info("Attempting to hack LaunchClassLoader to load my implementation of the SoundSystem");
        
        if(true) {
            URL packageURL = ClassLoaderPrepender.class.getClassLoader().getResource("eu/ha3/matmos");
            if(packageURL == null) {
                logger.info("Couldn't find eu.ha3.matmos package in MAtmos jar. SoundSystem will not be overloaded.");
            } else {
                logger.info("Resolving base jar URL from " + packageURL);
                URL baseJarURL = PreinitHelper.getBaseJarURL(packageURL);
                if(baseJarURL != null) {
                    logger.debug("Resolved base jar URL to " + baseJarURL);
                    
                    prependClassLoaderSources(Arrays.asList(baseJarURL));
                } else {
                    logger.info("Failed to extract base jar url from " + packageURL + ". SoundSystem will not be overloaded.");
                }
            }
        }
        
        logger.info("Finished hacking LaunchClassLoader");
    }
    
    private static void prependClassLoaderSources(List<URL> sources) {
        ParentPrependedClassLoader.of(Launch.classLoader, sources, Arrays.asList("paulscode"));
    }
    
    public static void printDebugInfo() {
        logger.debug("Printing debug info for ClassLoaderReorderPlugin");
        
        logger.debug("Resource paths:");
        List<String> tryMe = Arrays.asList(".", "", "/", "eu", "eu.ha3.matmos", "eu/ha3/matmos", "paulscode", "paulscode/sound", "paulscode/sound/SoundSystem.class");
        for(String toTry : tryMe) {
            URL result1 = null;
            URL result2 = null;
            try {
                result1 = ClassLoaderPrependerPlugin.class.getClassLoader().getResource(toTry);
                result2 = Launch.classLoader.findResource(toTry);
            } catch(Exception e) {
                
            }
            System.out.println(toTry + ": " + result1 + " / " + result2);
        }
        
        logger.debug("I'm gonna dump a bunch of debug stuff now. first, the class path:");
        logger.debug(System.getProperty("java.class.path").replace(';', '\n'));
        
        logger.debug("classloader of Matmos is " + Matmos.class.getClassLoader());
        logger.debug("classloader of SoundSystemException is " + SoundSystemException.class.getClassLoader());
        logger.debug("urls:" + String.join("\n", 
                Arrays.stream(((URLClassLoader)SoundSystemException.class.getClassLoader()).getURLs())
                .map(x -> String.valueOf(x)).collect(Collectors.toList())));
    }
    
}
