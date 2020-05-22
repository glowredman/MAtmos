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

/**
 * <p>
 * This class transformer loads SoundSystem classes embedded in the mod's jar,
 * and replaces SoundSystem classes with them, discarding any previous
 * transformations.
 * </p>
 * 
 * <p>
 * The purpose of this is to make overriding the SoundSystem possible, in order
 * to port Forge's fixes to platforms which don't have it (i.e. older Forge
 * versions and LiteLoader).
 * </p>
 */

public class SoundSystemReplacerTransformer implements IClassTransformer {

    private static final Logger logger = LogManager.getLogger("matmos-preinit");

    Map<String, byte[]> classData;

    private static boolean hasMadeChanges;

    public SoundSystemReplacerTransformer(URL baseJarURL) {
        classData = readClassesFrom(baseJarURL);
    }

    private Map<String, byte[]> readClassesFrom(URL baseJarURL) {
        HashMap<String, byte[]> data = new HashMap<>();

        try (ZipFile zipFile = new ZipFile(new File(baseJarURL.toURI()))) {

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                if (entry.getName().startsWith("paulscode/") && entry.getName().endsWith(".class")) {
                    InputStream is = zipFile.getInputStream(entry);
                    byte[] buf = IOUtils.toByteArray(is);

                    IOUtils.closeQuietly(is);

                    if (entry.getSize() != buf.length) { // sanity check
                        logger.warn("Class " + entry.getName() + " has a wrong size: " + buf.length + " (expected "
                                + entry.getSize() + ")");
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
        if (name.startsWith("paulscode.")) {
            Optional<byte[]> newData = loadOverridenClass(name, transformedName);

            if (newData.isPresent())
                hasMadeChanges = true;

            return newData.orElse(basicClass);
        }
        return basicClass;
    }

    private Optional<byte[]> loadOverridenClass(String name, String transformedName) {
        return Optional.ofNullable(classData.get(name));
    }

    public static boolean hasMadeChanges() {
        return hasMadeChanges;
    }

}
