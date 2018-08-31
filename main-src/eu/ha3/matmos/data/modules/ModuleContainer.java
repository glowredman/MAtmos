package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.core.sheet.DataPackage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;

public class ModuleContainer extends ModuleProcessor implements Module {
    public ModuleContainer(DataPackage data) {
        super(data, "gui_general");
    }

    @Override
    protected void doProcess() {
        GuiScreen gui = Minecraft.getMinecraft().currentScreen;

        setValue("open", gui != null);

        setValue("is_beacon", gui instanceof GuiBeacon);
        setValue("is_brewing", gui instanceof GuiBrewingStand);
        setValue("is_chest", gui instanceof GuiChest);
        setValue("is_crafting", gui instanceof GuiCrafting);
        setValue("is_dispenser", gui instanceof GuiDispenser);
        setValue("is_enchantment", gui instanceof GuiEnchantment);
        setValue("is_furnace", gui instanceof GuiFurnace);
        setValue("is_hopper", gui instanceof GuiHopper);
        setValue("is_npc_trade", gui instanceof GuiMerchant);
        setValue("is_anvil", gui instanceof GuiRepair);
        setValue("is_horse", gui instanceof GuiScreenHorseInventory);
        setValue("is_shulker", gui instanceof GuiShulkerBox);
        
        setValue("is_commandblock", gui instanceof GuiCommandBlock);
        setValue("is_container", gui instanceof GuiContainer);
        setValue("is_inventory", gui instanceof GuiInventory);
        setValue("is_creative", gui instanceof GuiContainerCreative);
    }
}
