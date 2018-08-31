package eu.ha3.matmos.game.data.modules.player;

import eu.ha3.matmos.engine.core.interfaces.Data;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ModuleHeldEnchantment extends AbstractEnchantmentModule {
    public ModuleHeldEnchantment(Data data) {
        super(data, "ench_current");
    }

    @Override
    protected ItemStack getItem(EntityPlayer player) {
        return player.getActiveItemStack();
    }
}
