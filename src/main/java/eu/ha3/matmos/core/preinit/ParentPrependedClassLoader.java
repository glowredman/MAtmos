package eu.ha3.matmos.core.preinit;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/** <p>A class loader meant to be set as the parent of a LaunchClassLoader in order to get
 *     the loading of certain packages (referred to as the captured packages) delegated to it.
 *     This is achieved by having the captured packages be in the exceptions list of the original
 *     LaunchClassLoader.
 */

class ParentPrependedClassLoader extends LaunchClassLoader implements ListAddListener<URL> {

    private LaunchClassLoader original;
    
    private ParentPrependedClassLoader(URL[] sources, LaunchClassLoader original) {
        super(sources);
        this.original = original;
        
        for(URL source : original.getSources()) {
            addURL(source);
        }
    }
    
    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    /** 
     * <p>Hijacks a LaunchClassLoader. The loading of the packages listed in <tt>capturedPackages</tt>
     *    will be delegated to a newly created <tt>ParentPrependedClassLoader</tt>, the URL search path
     *    of which is the search path of the <tt>original</tt> prepended with <tt>prependedURLs</tt>.</p>
     * <p>What this practically means is that when <tt>original</tt> loads one of the packages listed in
     *    <tt>capturedPackages</tt>, the URLs in <tt>prependedURLs</tt> will be checked first to find it.</p>
     * <p>This allows overriding packages with our own implementations.</p>
     * 
     * <p>The process makes the following modifications are made to the <tt>original</tt> class loader:
     *     <li> The <tt>classLoaderExceptions</tt> list of the original class loader is changed to a <tt>ExtraWrappedSet</tt>
     *          wrapped around it.
     *     <li> The parent of the original class loader gets changed to the newly created class loader.
     *     <li> The <tt>sources</tt> list of the original class loader is changed to a <tt>AddListenableWrappedList</tt>
     *          wrapped around it.
     * @param original
     */
    public static ParentPrependedClassLoader of(LaunchClassLoader original, List<URL> prependedURLs, List<String> capturedPackages) {
        // XXX the sources field of the new class loader does not include the prepended urls. isn't that weird? but it works
        ArrayList<URL> newSources = new ArrayList<URL>(prependedURLs);
        newSources.addAll(original.getSources());
        ParentPrependedClassLoader newLCL = new ParentPrependedClassLoader(newSources.toArray(new URL[0]), original);
        
        // newLCL.everything <- original.everything
        // (this makes the fields of the new LCL mirror the ones in the original)
        PreinitHelper.copyObjectFieldsExcept(original, newLCL, Arrays.asList(), false);
        
        try {
            Field parentField = LaunchClassLoader.class.getDeclaredField("parent");
            parentField.setAccessible(true);
            
            Field exceptionsField = LaunchClassLoader.class.getDeclaredField("classLoaderExceptions");
            exceptionsField.setAccessible(true);
            
            Field sourcesField = LaunchClassLoader.class.getDeclaredField("sources");
            sourcesField.setAccessible(true);
            
            
            // original.parent <- newLCL
            parentField.set(original, newLCL);
            
            
            // original.exceptions <- newLCL.exceptions + capturedPackages 
            Set<String> originalExceptions = (Set<String>)exceptionsField.get(original);
            ExtraWrappedSet<String> newExceptions = new ExtraWrappedSet<String>(originalExceptions);
            
            newExceptions.getExtraSet().addAll(capturedPackages.stream().map(x -> x + ".").collect(Collectors.toList()));
            
            exceptionsField.set(original, newExceptions);
            
            
            // original.sources <- listenable version of original.sources
            // (might be unnecessary)
            List<URL> originalSources = (List<URL>)sourcesField.get(original);
            AddListenableWrappedList<URL> listenableList = new AddListenableWrappedList<>(originalSources);
            listenableList.addListener((ParentPrependedClassLoader)newLCL);
            sourcesField.set(original, listenableList);
            
            
            // Preload SoundSystem classes to make sure our LCL loads them.
            // (This is required because they aren't delegated to us when they are loaded
            //  in the original LCL as a byproduct of loading a class that depends on them!) 
            ClassPath cp = ClassPath.from(newLCL);
            for(ClassInfo info : cp.getTopLevelClassesRecursive("paulscode")) {
                newLCL.loadClass(info.getName());
            }
        } catch(Exception e) {
            return null;
        }
        
        return newLCL;
    }

    @Override
    // XXX wrong things could happen if someone added to the middle of the list
    public void onElementAdded(int index, URL addedElement) {
        addURL(addedElement);
    }
    
}