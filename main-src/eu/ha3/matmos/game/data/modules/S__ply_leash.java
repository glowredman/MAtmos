package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

public class S__ply_leash extends ModuleProcessor implements Module {
    private static final int RADIUS = 20;

    public S__ply_leash(Data data) {
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
