package eu.ha3.matmos.data.modules.legacy;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;

public class ModuleModData extends ModuleProcessor implements Module {
    private final MAtMod mod;

    public ModuleModData(DataPackage data, MAtMod mod) {
        super(data, "meta_mod");
        this.mod = mod;

        EI("mod_tick", "Current tick internal to the mod.");
    }

    @Override
    protected void doProcess() {
        setValue("mod_tick", mod.util().getClientTick());
    }
}
