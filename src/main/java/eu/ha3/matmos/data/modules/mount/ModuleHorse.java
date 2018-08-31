package eu.ha3.matmos.data.modules.mount;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;

public class ModuleHorse extends ModuleProcessor implements Module {
    public ModuleHorse(DataPackage data) {
        super(data, "ride_horse");
    }

    @Override
    protected void doProcess() {
        Entity xride = getPlayer().getRidingEntity();

        if (xride == null || !(xride instanceof EntityHorse)) {
            setValue("jumping", false);
            setValue("rearing", false);
            setValue("saddled", false);
            setValue("leashed", false);
            setValue("chested", false);
            setValue("tame", false);
            setValue("type", 0);
            setValue("variant", 0);
            setValue("name_tag", "");
            setValue("health1k", 0);
            setValue("leashed_to_player", false);
            setValue("ridden_by_owner", false);
            setValue("leashed_to_owner", false);
            setValue("leash_distance", 0);
            setValue("temper", 0);
            setValue("owner_name", "");
            setValue("reproduced", false);
            setValue("bred", false);

            return;
        }

        EntityHorse ride = (EntityHorse)xride;

        setValue("jumping", ride.isHorseJumping());
        setValue("rearing", ride.isRearing());
        setValue("saddled", ride.isHorseSaddled());
        setValue("leashed", ride.getLeashed());
        setValue("tame", ride.isTame());
        // TODO: Where'd it go?
        //setValue("chested", ride.isChested());
        setValue("tame", ride.isTame());
        setValue("type", ride.isEntityUndead());
        setValue("variant", ride.getHorseVariant());

        setValue("name_tag", ride.getCustomNameTag());

        setValue("health1k", (int)(ride.getHealth() * 1000));
        setValue("leashed_to_player", ride.getLeashed() && ride.getLeashHolder() instanceof EntityPlayer);
        setValue("ridden_by_owner",
                ride.getControllingPassenger() instanceof EntityPlayer
                        && ride.getOwnerUniqueId() != null
                        && ride.getOwnerUniqueId().equals(((EntityPlayer)ride.getControllingPassenger()).getGameProfile().getId()));
        setValue("leashed_to_owner",
                ride.getLeashHolder() instanceof EntityPlayer
                        && !ride.getOwnerUniqueId().toString().equals("")
                        && ride.getOwnerUniqueId().equals(((EntityPlayer)ride.getLeashHolder()).getGameProfile().getId()));

        if (ride.getLeashed() && ride.getLeashHolder() != null) {
            setValue("leash_distance", (int)(ride.getLeashHolder().getDistance(ride) * 1000));
        } else {
            setValue("leash_distance", 0);
        }

        // Server only?
        setValue("temper", ride.getTemper());
        setValue("owner_uuid", ride.getOwnerUniqueId().toString());
        setValue("reproduced", ride.isBreeding());
        setValue("bred", ride.isEatingHaystack());
    }
}
