package eu.ha3.matmos.core.preinit;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.launchwrapper.IClassTransformer;

public class SoundSystemReplacer implements IClassTransformer{
    
    private static final Logger logger = LogManager.getLogger("matmos-preinit");
    
    Map<String, byte[]> classData;
    
    public SoundSystemReplacer(URL baseJarURL) {
        classData = readClassesFrom(baseJarURL);
    }
    
    private Map<String, byte[]> readClassesFrom(URL baseJarURL) {
        HashMap<String, byte[]> data = new HashMap<>();
        
        try (ZipFile zipFile = new ZipFile(new File(baseJarURL.toURI()))){
            
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while(entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                
                if(entry.getName().startsWith("paulscode/") && entry.getName().endsWith(".class")) {
                    InputStream is = zipFile.getInputStream(entry);
                    byte[] buf = IOUtils.toByteArray(is);
                    
                    IOUtils.closeQuietly(is);
                    
                    if(entry.getSize() != buf.length) { // sanity check
                        logger.warn("Class " + entry.getName() + " has a wrong size: " + buf.length + " (expected " + entry.getSize() + ")");
                    }
                    
                    String className = entry.getName().substring(0, entry.getName().length() - 6).replaceAll("/", ".");
                    
                    data.put(className, buf);
                }
            }
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(name.startsWith("paulscode.")) {
            return loadOverridenClass(name, transformedName).orElse(basicClass);
        }
        return basicClass;
    }
    
    private Optional<byte[]> loadOverridenClass(String name, String transformedName) {
        return Optional.ofNullable(classData.get(name));
    }

}
