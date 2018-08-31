package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.AbstractEnchantmentModule;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class S__ench_armor extends AbstractEnchantmentModule {
    private final int slot;

    public S__ench_armor(Data data, EntityEquipmentSlot slot) {
        super(data, "ench_armor" + slot.getSlotIndex());
        this.slot = slot.getSlotIndex();
    }

    @Override
    protected ItemStack getItem(EntityPlayer player) {
        return player.inventory.armorInventory.get(slot);
    }
}
