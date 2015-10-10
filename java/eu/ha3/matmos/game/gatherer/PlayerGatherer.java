package eu.ha3.matmos.game.gatherer;

import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

/**
 * @author dags_ <dags@dags.me>
 */

public class PlayerGatherer implements DataGatherer
{
    private Data<Boolean> jumping = new Data<Boolean>();
    private Data<Boolean> sprinting = new Data<Boolean>();
    private Data<Boolean> sneaking = new Data<Boolean>();
    private Data<Boolean> blocking = new Data<Boolean>();
    private Data<Boolean> usingItem = new Data<Boolean>();
    private Data<Number> itemUseDuration = new Data<Number>();
    private Data<Boolean> swinging = new Data<Boolean>();
    private Data<Number> swingProgress = new Data<Number>();
    private Data<Boolean> riding = new Data<Boolean>();

    private Data<Number> health = new Data<Number>();
    private Data<Number> maxHealth = new Data<Number>();
    private Data<Number> hunger = new Data<Number>();
    private Data<Number> foodSaturation = new Data<Number>();
    private Data<Number> armour = new Data<Number>();
    private Data<Number> breath = new Data<Number>();

    private Data<String> vehicle = new Data<String>();

    private Data<Number> velocityHorizontal = new Data<Number>();
    private Data<Number> velocityVertical = new Data<Number>();
    private Data<Number> fallDistance = new Data<Number>();

    @Override
    public PlayerGatherer register(DataManager manager)
    {
        manager.registerBool("player.action.jumping", jumping);
        manager.registerBool("player.action.sprinting", sprinting);
        manager.registerBool("player.action.sneaking", sneaking);
        manager.registerBool("player.action.blocking", blocking);
        manager.registerBool("player.action.usingItem", usingItem);
        manager.registerNum("player.action.itemUseDuration", itemUseDuration);
        manager.registerBool("player.action.swinging", swinging);
        manager.registerNum("player.action.swingProgress", swingProgress);
        manager.registerBool("player.action.riding", riding);

        manager.registerNum("player.stat.health", health);
        manager.registerNum("player.stat.maxHealth", maxHealth);
        manager.registerNum("player.stat.hunger", hunger);
        manager.registerNum("player.stat.foodSaturation", foodSaturation);
        manager.registerNum("player.stat.armour", armour);
        manager.registerNum("player.stat.breath", breath);

        manager.registerString("player.vehicle", vehicle);

        manager.registerNum("player.velocityHorizontal", velocityHorizontal);
        manager.registerNum("player.velocityVertical", velocityVertical);
        manager.registerNum("player.fallDistance", fallDistance);
        return this;
    }

    @Override
    public void update()
    {
        jumping.value = Minecraft.getMinecraft().thePlayer.movementInput.jump;
        sprinting.value = MCGame.player.isSprinting();
        sneaking.value = MCGame.player.isSneaking();
        blocking.value = MCGame.player.isBlocking();
        usingItem.value = MCGame.player.isUsingItem();
        itemUseDuration.value = MCGame.player.getItemInUseDuration();
        swinging.value = MCGame.player.isSwingInProgress;
        swingProgress.value = MCGame.player.swingProgressInt;
        riding.value = MCGame.player.isRiding();

        health.value = MCGame.player.getHealth();
        maxHealth.value = MCGame.player.getMaxHealth();
        hunger.value = MCGame.player.getFoodStats().getFoodLevel();
        foodSaturation.value = MCGame.player.getFoodStats().getSaturationLevel();
        armour.value = MCGame.player.getTotalArmorValue();
        breath.value = MCGame.player.getAir();

        Entity ridden = MCGame.player.ridingEntity;
        vehicle.value = ridden != null ? ridden.getClass().getSimpleName() : "none";

        Entity e = ridden == null ? MCGame.player : ridden;
        velocityHorizontal.value = NumberUtil.round1dp((e.motionX * e.motionX + e.motionZ * e.motionZ) * 100D);
        velocityVertical.value = NumberUtil.round1dp(e.motionY + (MCGame.player.capabilities.isFlying ? 0 : 0.08));
        fallDistance.value = NumberUtil.round1dp(MCGame.noFall() ? 0F : e.fallDistance);
    }
}
