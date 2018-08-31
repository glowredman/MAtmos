package eu.ha3.matmos.game.data.modules.player;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Inventory processing to discover items the player is holding or has as a helmet.
 */
public class ModuleInventory extends ModuleProcessor implements Module {
    public ModuleInventory(Data data) {
        super(data, "ply_inventory");
    }

    @Override
    protected void doProcess() {
        EntityPlayer player = getPlayer();

        setValue("held_slot", player.inventory.currentItem);
        ItemProcessorHelper.setValue(this, player.inventory.getCurrentItem(), "current");
        ItemProcessorHelper.setValue(this, player.inventory.getItemStack(), "item_in_cursor");
    }
}
