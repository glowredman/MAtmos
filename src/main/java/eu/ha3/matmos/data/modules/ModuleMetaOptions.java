package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;

/*
 * --filenotes-placeholder
 */

public class ModuleMetaOptions extends ModuleProcessor implements Module {
    private final Matmos mod;

    public ModuleMetaOptions(DataPackage data, Matmos mod) {
        super(data, "meta_option", true);
        this.mod = mod;
    }

    @Override
    protected void doProcess() {
        setValue("altitudes_high", mod.getConfig().getBoolean("useroptions.altitudes.high"));
        setValue("altitudes_low", mod.getConfig().getBoolean("useroptions.altitudes.low"));
        setValue("override_rain", mod.getConfig().getBoolean("rain.suppress"));
    }
}
