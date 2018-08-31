package eu.ha3.matmos.data.modules.player;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.PrivateAccessException;
import eu.ha3.mc.haddon.Utility;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;

/**
 * Module for processing the base player statistics.
 *
 */
public class ModulePlayerStats extends ModuleProcessor implements Module {
    private final Utility util;

    public ModulePlayerStats(DataPackage data, Utility util) {
        super(data, "ply_general");
        this.util = util;
    }

    @Override
    protected void doProcess() {
        EntityPlayerSP player = (EntityPlayerSP)getPlayer();

        setValue("in_water", player.isInWater());
        setValue("wet", player.isWet());
        setValue("on_ground", player.onGround);
        setValue("burning", player.isBurning());
        setValue("jumping", player.movementInput.jump);
        
        try {
            setValue("in_web", (Boolean)this.util.getPrivate(player, "isInWeb"));
        } catch (PrivateAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        setValue("on_ladder", player.isOnLadder());

        ItemStack held = player.getHeldItemMainhand();
        if (held == null) held = player.getHeldItemOffhand();

        setValue("blocking", player.isHandActive() && held != null && held.getItem() instanceof ItemShield);
        setValue("sprinting", player.isSprinting());
        setValue("sneaking", player.isSneaking());
        setValue("airborne", player.isAirBorne);
        setValue("using_item", player.isHandActive());
        setValue("riding", player.isRiding());
        setValue("creative", player.capabilities.isCreativeMode);
        setValue("flying", player.capabilities.isFlying);

        setValue("under_water", MAtUtil.isUnderwaterAnyGamemode());
    }
}
