package eu.ha3.matmos.core.preinit;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.launchwrapper.LaunchClassLoader;

/* This is a broken mess */
public class ProxyPrependedClassLoader extends LaunchClassLoader {

    LaunchClassLoader original;
    
    Set<String> packagesToPassToOriginal = new HashSet<String>();
    
    public ProxyPrependedClassLoader(URL[] sources, LaunchClassLoader original) {
        super(sources);
        this.original = original;
    }
    
    @Override
    public void addURL(URL url) {
        super.addURL(url);
        System.out.println("ADDURL!!!");
    }
    
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        System.out.println("lol @ findClass(" + name + ")");
        
        Map<String, Class<?>> cached = getCachedClasses();
        Set<String> cachedBefore = new HashSet<>(cached.keySet());
        
        Class<?> result = shouldPass(name) ? original.findClass(name) : super.findClass(name);
        System.out.println("lol @ findClass result: " + result.getClassLoader());
        
        Set<String> cachedAfter = new HashSet<>(cached.keySet());
        cachedAfter.removeAll(cachedBefore);
        cachedAfter.forEach(x -> System.out.println("new cached: " + x));
        
        return result;
    }
    
    private Map<String, Class<?>> getCachedClasses(){
        Field cc;
        try {
            cc = LaunchClassLoader.class.getDeclaredField("cachedClasses");
            cc.setAccessible(true);
            Map<String, Class<?>> cachedClasses = (Map<String, Class<?>>)cc.get(this);
            return cachedClasses;
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        System.out.println("lol @ loadClass(" + name + ")");
        
        Map<String, Class<?>> cached = getCachedClasses();
        Set<String> cachedBefore = new HashSet<>(cached.keySet());
        
        Class<?> result = shouldPass(name) ? original.loadClass(name) : super.loadClass(name);
        System.out.println("lol @ loadClass result: " + result.getClassLoader());
        
        Set<String> cachedAfter = new HashSet<>(cached.keySet());
        cachedAfter.removeAll(cachedBefore);
        cachedAfter.forEach(x -> System.out.println("new cached: " + x));
        
        return result;
    }
    
    @Override
    public byte[] getClassBytes(String name) throws IOException {
        System.out.println("lol @ getClassBytes(" + name + ")");
        byte[] result = shouldPass(name) ? original.getClassBytes(name) : super.getClassBytes(name);
        System.out.println("lol @ getClassBytes result");
        return result;
    }
    
    private boolean shouldPass(String name) {
        /*boolean yeah = new HashSet(Arrays.asList(
                //"cpw.mods.fml.common.asm.transformers.EventSubscriptionTransformer",
                "blah"
                )).contains(name);
        if(yeah) {
            return true;
        } else {
            return false;
        }*/
        for(String pkg : packagesToPassToOriginal) {
            if(name.startsWith(pkg)) {
                System.out.println("passing " + name + " to original");
                return true;
            }
        }
        System.out.println("not passing " + name + " to original");
        return false;
    }
    
    public void addAlreadyLoadedPackageExclusion(String name) {
        //System.out.println("gonna pass " + name + " to original");
        packagesToPassToOriginal.add(name);
    }
    
    public void printAlreadyLoadedPackageExclusion() {
        packagesToPassToOriginal.forEach(x -> System.out.println("gonna pass " + x));
    }
}

// -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=8000

// TODO override findResouce?