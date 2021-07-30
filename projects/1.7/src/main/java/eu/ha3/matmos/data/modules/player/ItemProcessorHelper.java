package eu.ha3.matmos.data.modules.player;

import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ProcessorModel;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.item.ItemStack;

class ItemProcessorHelper {
    static void setValue(ProcessorModel model, ItemStack item, String prefix) {
        if (item == null) {
            model.setValue(prefix + "_item", Module.NO_ITEM);
            model.setValue(prefix + "_damage", Module.NO_META);
            model.setValue(prefix + "_name_display", Module.NO_NAME);
            model.setValue(prefix + "_powermeta", Module.NO_POWERMETA);
        } else {
            model.setValue(prefix + "_item", MAtUtil.nameOf(item));
            model.setValue(prefix + "_damage", item.getMetadata());
            model.setValue(prefix + "_name_display", item.getDisplayName());
            model.setValue(prefix + "_powermeta", MAtUtil.asPowerMeta(item));
        }
    }
}
