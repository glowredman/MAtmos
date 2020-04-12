package eu.ha3.matmos.core.preinit.forge;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import eu.ha3.matmos.core.preinit.ClassLoaderPrepender;

import java.util.Map;

/** FML coremod for bootstrapping the ClassLoaderPrepender. Used in Forge. */

public class ClassLoaderPrependerPlugin implements IFMLLoadingPlugin {
    
    public ClassLoaderPrependerPlugin(){
        ClassLoaderPrepender.run();
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
