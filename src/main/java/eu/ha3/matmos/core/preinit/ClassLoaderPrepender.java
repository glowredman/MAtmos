package eu.ha3.matmos.core.preinit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.preinit.forge.ClassLoaderPrependerPlugin;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import paulscode.sound.SoundSystemException;
import sun.misc.URLClassPath;

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
    
    enum ReorderMethod {
        PARENT, // Change the LCL's parent to an own LCL subclass that 'paulscode' load requests get delegated to.
        PROXY,  // Change the LCL to an own LCL subclass that delegates non-paulscode requests to the original
                // Works with vanilla, but not with coremods such as CodeChickenCore.
        JVMHACK // Apply JVM-specific hacks to change the search path of the LCL.
                // Works in the Oracle JVM, but it's a very ugly solution.
    };
    
    private static void prependClassLoaderSources(List<URL> sources) {
        ReorderMethod method = ReorderMethod.PARENT;
        
        switch(method) {
        case PARENT:
        case PROXY:
            boolean parenty = method == ReorderMethod.PARENT;
            
            LaunchClassLoader newLCL;
            
            if(parenty) {
                newLCL = ParentPrependedClassLoader.of(Launch.classLoader, sources, Arrays.asList("paulscode"));
            } else {
                newLCL = new ProxyPrependedClassLoader(Launch.classLoader.getSources().toArray(new URL[0]), Launch.classLoader);
            }
            
            List<String> exceptions = Arrays.asList();
            
            if(!parenty) {
                // this makes the fields of the original and the new LCL interlinked
                PreinitHelper.copyObjectFieldsExcept(Launch.classLoader, newLCL, exceptions, false);
            }
            
            try {
                if(parenty) {
                    
                } else {
                    Field cachedField = LaunchClassLoader.class.getDeclaredField("cachedClasses");
                    cachedField.setAccessible(true);
                    Map<String, Class<?>> cachedClasses = (Map<String, Class<?>>)cachedField.get(Launch.classLoader);
                    for(String className : cachedClasses.keySet()) {
                        String packageName = className.substring(0, className.lastIndexOf('.') + 1);
                        
                        ((ProxyPrependedClassLoader)newLCL).addAlreadyLoadedPackageExclusion(packageName);
                    }
                    ((ProxyPrependedClassLoader)newLCL).printAlreadyLoadedPackageExclusion();
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            
            logger.debug("Current classLoader: " + Launch.classLoader);
            logger.debug("Current context class loader: " + Thread.currentThread().getContextClassLoader());
            
            if(!parenty) {
                Launch.classLoader = newLCL;
                Thread.currentThread().setContextClassLoader(newLCL);
            }
            
            break;
        case JVMHACK:
            Field ucpField;
            try {
                ucpField = URLClassLoader.class.getDeclaredField("ucp");
                ucpField.setAccessible(true);
                
                URLClassPath ucp = (URLClassPath)ucpField.get(Launch.classLoader);
                
                Field pathField = URLClassPath.class.getDeclaredField("path");
                pathField.setAccessible(true);
                Field urlsField = URLClassPath.class.getDeclaredField("urls");
                urlsField.setAccessible(true);
                Field loadersField = URLClassPath.class.getDeclaredField("loaders");
                loadersField.setAccessible(true);
                
                Class loaderClass = Class.forName("sun.misc.URLClassPath$Loader");
                
                List<URL> path = (List<URL>)pathField.get(ucp);
                Stack<URL> urls = (Stack<URL>)urlsField.get(ucp);
                List loaders = (List)loadersField.get(ucp);
                
                path.removeAll(sources);
                path.addAll(0, sources);
                urls.removeAll(sources);
                urls.addAll(sources);
                
                Method getLoaderMethod = URLClassPath.class.getDeclaredMethod("getLoader", URL.class);
                getLoaderMethod.setAccessible(true);
                Object myLoader = getLoaderMethod.invoke(ucp, sources.get(0));
                
                System.out.println("loader already in? " + loaders.contains(myLoader)); // actually it is
                
                loaders.add(0, myLoader);
                
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            break;
        }
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
