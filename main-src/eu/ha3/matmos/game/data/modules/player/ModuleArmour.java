package eu.ha3.matmos.game.data.modules.player;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.modules.Module;
import eu.ha3.matmos.game.data.modules.ModuleProcessor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Module for processing a player's armour slot. Only one.
 */
public class ModuleArmour extends ModuleProcessor implements Module {
    public ModuleArmour(Data data) {
        super(data, "ply_armor");
    }

    @Override
    protected void doProcess() {
        EntityPlayer player = getPlayer();

        for (int i = 0; i < 4; i++) {
            ItemProcessorHelper.setValue(this, player.inventory.armorInventory.get(i), Integer.toString(i));
        }
    }
}
