package eu.ha3.matmos.core.preinit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.logging.log4j.Logger;

import net.minecraft.launchwrapper.Launch;

public class PreinitHelper {
    
    public static void copyObjectFieldsExcept(Object a, Object b, List<String> exceptions, boolean recurse) {
        Logger logger = ClassLoaderPrepender.logger;
        
        Class clazz = a.getClass();
        Class clazzB = b.getClass();
        
        if(!clazzB.isAssignableFrom(clazzB)) {
            logger.error("Tried to copy object of class " + clazz + " to object of class " + b);
            return;
        }
        
        while(clazz != null) {
            logger.debug("Copying fields in class " + clazz);
            for(Field field: clazz.getDeclaredFields()) {
                try {
                    if(Modifier.isFinal(field.getModifiers())) {
                        continue;
                    }
                    
                    if(exceptions.contains(field.getName())) {
                        logger.debug("  Skipping field " + field);
                        continue;
                    }
                    
                    field.setAccessible(true);
                    Object value = field.get(a);
                    logger.debug("  Copying field " + field);
                    field.set(b, value);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.debug("  Got error copying field: " + e.getMessage());
                }
            }
            
            clazz = recurse ? clazz.getSuperclass() : null;
        }
    }
    
    /** Returns the implementation version of the package of 'clazz'.
     * More formally: returns the value of the 'Implementation-Version' entry in the
     * manifest of the jar which holds 'clazz', or null if the value doesn't exist or cannot
     * be retrieved for some other reason.
     * @param clazz
     * @return
     */
    public static Attributes getManifestAttributesOfClass(String clazz) {
        Logger logger = ClassLoaderPrepender.logger;
        
        URLClassLoader classLoader = null;
        try {
            classLoader = (URLClassLoader)Class.forName(clazz, false, Launch.classLoader).getClassLoader();
        } catch (ClassNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return null;
        }
        
        logger.debug("Attempting to get manifest attributes of: " + clazz + " using class loader: " + classLoader);
        
        URL packageURL = /*Launch.classLoader*/classLoader.findResource(clazz.replace('.', '/') + ".class");
        
        logger.debug("package url: " + packageURL);
        
        if(packageURL != null) {
            URL baseJarURL = getBaseJarURL(packageURL);
            
            logger.debug("base jar url: " + baseJarURL);
            
            JarFile jar = null;
            
            try {
                File jarFile = new File(baseJarURL.toURI());
                
                jar = new JarFile(jarFile);
                
                Manifest mf = jar.getManifest();
                
                Attributes atts = mf.getMainAttributes();
                
                logger.debug("Attributes:");
                
                atts.entrySet().forEach(x -> logger.debug(x.getKey() + ": " + x.getValue()));
                
                return atts;
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    jar.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    
    
    public static URL getBaseJarURL(URL packageURL) {
        String newURLString = null;
        
        switch(packageURL.getProtocol()) {
        case "jar":
            String urlString = packageURL.getPath();
            int lastExclamation = urlString.lastIndexOf('!');
            newURLString = urlString.substring(0, lastExclamation);
            break;
        default:
            ClassLoaderPrepender.logger.info("The path to " + packageURL + " isn't in a jar; we're probably in a dev environment. " + 
                                "You'll have to manually configure your dev environment if you want to override the SoundSystem.");
            break;
        }
        
        if(newURLString != null) {
            try {
                return new URL(newURLString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
