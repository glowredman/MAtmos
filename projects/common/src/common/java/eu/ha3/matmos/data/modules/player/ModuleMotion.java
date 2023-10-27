package eu.ha3.matmos.data.modules.player;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Processing module for player motion.
 */
public class ModuleMotion extends ModuleProcessor implements Module {
    public ModuleMotion(DataPackage data) {
        super(data, "ply_motion");
    }

    @Override
    protected void doProcess() {
        EntityPlayer player = getPlayer();

        int mxx = (int)Math.round(player.motionX * 1000);
        int myy = (int)Math.round(player.motionY * 1000);
        int mzz = (int)Math.round(player.motionZ * 1000);
        
        setValue("x_1k", mxx);
        setValue("y_1k", myy);
        setValue("z_1k", mzz);
        setValue("sqrt_xx_zz", (int) Math.floor(Math.sqrt(mxx * mxx + mzz * mzz)));
        setValue("sqrt_xx_yy_zz", (int) Math.floor(Math.sqrt(mxx * mxx + myy * myy + mzz * mzz)));
    }
}
