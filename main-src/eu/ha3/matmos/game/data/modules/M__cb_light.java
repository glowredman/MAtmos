package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

/*
 * --filenotes-placeholder
 */

public class M__cb_light extends ModuleProcessor implements Module {
    public M__cb_light(Data data) {
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
