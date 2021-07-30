package eu.ha3.matmos.data.modules.player;

import eu.ha3.matmos.core.expansion.ExpansionManager;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Inventory processing to discover items the player is holding or has as a
 * helmet.
 */
public class ModuleInventory extends ModuleProcessor implements Module {
    private DataPackage data;
    public ModuleInventory(DataPackage data) {
        super(data, "ply_inventory");
        this.data = data;
    }

    @Override
    protected void doProcess() {
        EntityPlayer player = getPlayer();

        setValue("held_slot", player.inventory.currentItem);
        
        ItemProcessorHelper.setValue(this, ExpansionManager.dealias(player.inventory.getCurrentItem(), data), "current");
        ItemProcessorHelper.setValue(this, ExpansionManager.dealias(player.inventory.getCurrentItem(), data), "item_in_cursor");
    }
}
