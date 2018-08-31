package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/*
 * --filenotes-placeholder
 */

public class ModuleLighting extends ModuleProcessor implements Module {
    public ModuleLighting(DataPackage data) {
        super(data, "cb_light");
    }

    @Override
    protected void doProcess() {
        World w = Minecraft.getMinecraft().world;

        BlockPos playerPos = getPlayer().getPosition();

        setValue("sky", w.getLightFor(EnumSkyBlock.SKY, playerPos));
        setValue("lamp", w.getLightFor(EnumSkyBlock.BLOCK, playerPos));
        setValue("final", w.getLight(playerPos));
        setValue("see_sky", w.canSeeSky(playerPos));
    }
}
