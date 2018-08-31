package eu.ha3.matmos.game.data.modules.world;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import net.minecraft.util.math.BlockPos;

/*
 * --filenotes-placeholder
 */

public class ModulePosition extends ModuleProcessor implements Module {
    public ModulePosition(Data data) {
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
