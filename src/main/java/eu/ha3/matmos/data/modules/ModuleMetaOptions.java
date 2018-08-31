package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.sheet.DataPackage;

/*
 * --filenotes-placeholder
 */

public class ModuleMetaOptions extends ModuleProcessor implements Module {
    private final MAtMod mod;

    public ModuleMetaOptions(DataPackage data, MAtMod mod) {
        super(data, "meta_option", true);
        this.mod = mod;
    }

    @Override
    protected void doProcess() {
        setValue("altitudes_high", this.mod.getConfig().getBoolean("useroptions.altitudes.high"));
        setValue("altitudes_low", this.mod.getConfig().getBoolean("useroptions.altitudes.low"));
    }
}
