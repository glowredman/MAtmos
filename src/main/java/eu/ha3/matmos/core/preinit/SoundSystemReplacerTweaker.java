package eu.ha3.matmos.core.preinit;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

/** Tweaker for bootstrapping the SoundSystemReplacer. Used in LiteLoader. */

public class SoundSystemReplacerTweaker implements ITweaker {

    public SoundSystemReplacerTweaker(){
        SoundSystemReplacer.run();
        
    }
    
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        
    }

    @Override
    public String getLaunchTarget() {
        return "";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }

}
