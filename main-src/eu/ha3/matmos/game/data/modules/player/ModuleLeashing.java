package eu.ha3.matmos.game.data.modules.player;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * Processing module for the total entities a player has leashed and withing range. (default 20 blocks)
 */
public class ModuleLeashing extends ModuleProcessor implements Module {
    private static final int RADIUS = 20;

    public ModuleLeashing(Data data) {
        super(data, "ply_leash");
    }

    @Override
    protected void doProcess() {
        EntityPlayer player = getPlayer();

        List<EntityLiving> entities = player.getEntityWorld().getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(
                player.posX - RADIUS, player.posY - RADIUS,
                player.posZ - RADIUS, player.posX + RADIUS,
                player.posY + RADIUS, player.posZ + RADIUS));

        setValue("total", entities.stream().filter(entity -> {
            return entity.getLeashed() && entity.getLeashHolder() == player;
        }).count());
    }
}
