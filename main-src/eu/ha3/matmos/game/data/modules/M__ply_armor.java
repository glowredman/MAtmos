package eu.ha3.matmos.game.data.modules;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.data.abstractions.module.Module;
import eu.ha3.matmos.game.data.abstractions.module.ModuleProcessor;

import net.minecraft.entity.player.EntityPlayer;

public class M__ply_armor extends ModuleProcessor implements Module {
    public M__ply_armor(Data data) {
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
