package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import net.minecraft.util.math.BlockPos;

public class ModulePosition extends ModuleProcessor implements Module {
    public ModulePosition(DataPackage data) {
        super(data, "cb_pos");
    }

    @Override
    protected void doProcess() {
        BlockPos pos = getPlayer().getPosition();

        setValue("x", pos.getX());
        setValue("y", pos.getY());
        setValue("z", pos.getZ());
    }
}
