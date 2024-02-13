package eu.ha3.matmos.core.preinit.forge;

import eu.ha3.matmos.core.preinit.SoundSystemReplacer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/** FML coremod for bootstrapping the SoundSystemReplacer. Used in Forge. */

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class SoundSystemReplacerPlugin implements IFMLLoadingPlugin {

    public SoundSystemReplacerPlugin() {
        SoundSystemReplacer.run();
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
