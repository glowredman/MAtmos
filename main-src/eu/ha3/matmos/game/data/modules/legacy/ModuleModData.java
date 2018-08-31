package eu.ha3.matmos.game.data.modules.legacy;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import eu.ha3.matmos.game.mod.MAtMod;

public class ModuleModData extends ModuleProcessor implements Module {
    private final MAtMod mod;

    public ModuleModData(Data data, MAtMod mod) {
        super(data, "meta_mod");
        this.mod = mod;

        EI("mod_tick", "Current tick internal to the mod.");
    }

    @Override
    protected void doProcess() {
        setValue("mod_tick", this.mod.util().getClientTick());
    }
}
