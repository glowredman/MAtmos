package eu.ha3.matmos.game.data.modules.legacy;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;

import net.minecraft.util.math.BlockPos;

public class ModuleLegacyColumn extends ModuleProcessor implements Module {
    public ModuleLegacyColumn(Data data) {
        super(data, "legacy_column");
    }

    @Override
    protected void doProcess() {
        BlockPos pos = getPlayer().getPosition();

        setValue("y-1_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(pos.down())));
        setValue("y-2_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(pos.down(2))));
        setValue("y0_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(pos)));
        setValue("y1_as_number", MAtmosUtility.legacyOf(MAtmosUtility.getBlockAt(pos.up())));
    }
}
