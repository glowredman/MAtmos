package eu.ha3.matmos.core.preinit;

import java.io.File;
import java.util.List;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

/** Tweaker for bootstrapping the ClassLoaderPrepender. */

public class ClassLoaderPrependerTweaker implements ITweaker {

    public ClassLoaderPrependerTweaker(){
        ClassLoaderPrepender.run();
        
    }
    
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        ((List<String>)Launch.blackboard.get("TweakClasses")).add("org.spongepowered.asm.launch.MixinTweaker");
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
