package eu.ha3.matmos.game.data.modules.player;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Processing module for player motion.
 */
public class ModuleMotion extends ModuleProcessor implements Module {
    public ModuleMotion(Data data) {
        super(data, "ply_motion");
    }

    @Override
    protected void doProcess() {
        EntityPlayer player = getPlayer();

        int mxx = (int)Math.round(player.motionX * 1000);
        int mzz = (int)Math.round(player.motionZ * 1000);

        setValue("x_1k", mxx);
        setValue("y_1k", (int)Math.round(player.motionY * 1000));
        setValue("z_1k", mzz);
        setValue("sqrt_xx_zz", (int)Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
    }
}
