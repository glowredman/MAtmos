package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.MODULE_CONSTANTS;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;

public class M__ride_general extends ModuleProcessor implements Module {
    public M__ride_general(Data data) {
        super(data, "ride_general");
    }

    @Override
    protected void doProcess() {
        Entity ride = getPlayer().getRidingEntity();

        setValue("minecart", ride instanceof EntityMinecart);
        setValue("boat", ride instanceof EntityBoat);
        setValue("pig", ride instanceof EntityPig);
        setValue("horse", ride instanceof EntityHorse);
        setValue("player", ride instanceof EntityPlayer);

        setValue("burning", ride != null && ride.isBurning());
        setValue("entity_id", ride == null ? MODULE_CONSTANTS.NO_ENTITY : EntityList.getEntityString(ride));
    }
}
