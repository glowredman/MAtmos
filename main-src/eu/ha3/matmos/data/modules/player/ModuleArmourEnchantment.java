package eu.ha3.matmos.data.modules.player;

import eu.ha3.matmos.core.sheet.DataPackage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Module for discovering enchantments on a player's armour.
 */
public class ModuleArmourEnchantment extends AbstractEnchantmentModule {
    private final int slot;

    public ModuleArmourEnchantment(DataPackage data, EntityEquipmentSlot slot) {
        super(data, "ench_armor" + slot.getSlotIndex());
        this.slot = slot.getSlotIndex();
    }

    @Override
    protected ItemStack getItem(EntityPlayer player) {
        return player.inventory.armorInventory.get(slot);
    }
}
