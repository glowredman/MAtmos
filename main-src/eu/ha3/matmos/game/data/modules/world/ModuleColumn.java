package eu.ha3.matmos.game.data.modules.world;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import eu.ha3.matmos.game.system.MAtmosUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModuleColumn extends ModuleProcessor implements Module {
    public ModuleColumn(Data data) {
        super(data, "cb_column");

        EI("y-2", "Block under the feet");
        EI("y-1", "Block at the legs");
        EI("y0", "Block at the body (y)");
        EI("y1", "Block over the head");
        EI("topmost_block", "y coordinate of the top most solid block");
        EI("thickness_overhead", "Number of blocks over the player until topmost_block");
        EI("can_rain_reach", "Can rain reach y?");
    }

    @Override
    protected void doProcess() {
        World w = Minecraft.getMinecraft().world;

        BlockPos pos = getPlayer().getPosition();
        BlockPos topMostBlock = w.getTopSolidOrLiquidBlock(pos);

        setValue("y-1", MAtmosUtility.getNameAt(pos.down(), MODULE_CONSTANTS.NO_BLOCK_OUT_OF_BOUNDS));
        setValue("y-2", MAtmosUtility.getNameAt(pos.down(2), MODULE_CONSTANTS.NO_BLOCK_OUT_OF_BOUNDS));
        setValue("y0", MAtmosUtility.getNameAt(pos, MODULE_CONSTANTS.NO_BLOCK_OUT_OF_BOUNDS));
        setValue("y1", MAtmosUtility.getNameAt(pos.up(), MODULE_CONSTANTS.NO_BLOCK_OUT_OF_BOUNDS));

        setValue("topmost_block", topMostBlock.getY());
        setValue("thickness_overhead", topMostBlock.getY() - pos.getY());
        setValue("can_rain_reach", w.canSeeSky(pos) && !(topMostBlock.getY() > pos.getY()));
    }
}
