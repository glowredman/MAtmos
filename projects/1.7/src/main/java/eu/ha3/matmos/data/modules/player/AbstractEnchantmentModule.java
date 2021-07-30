package eu.ha3.matmos.data.modules.player;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.data.modules.RegistryBasedModule;
import eu.ha3.matmos.util.Tags;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * An abstract module that extracts all enchantments associated to an item
 * defined by the implementing class.
 *
 * @author Hurry
 */
public abstract class AbstractEnchantmentModule extends ModuleProcessor implements RegistryBasedModule {
    private Set<String> oldThings = new LinkedHashSet<>();

    public AbstractEnchantmentModule(DataPackage dataIn, String name) {
        super(dataIn, name);
        dataIn.getSheet(name).setDefaultValue("0");
        dataIn.getSheet(name + ModuleProcessor.DELTA_SUFFIX).setDefaultValue("0");
    }

    @Override
    public String getRegistryName() {
        return "enchantment";
    }

    @Override
    protected void doProcess() {
        ItemStack item = getItem(getPlayer());

        for (String i : oldThings) {
            setValue(i, 0);
        }
        oldThings.clear();

        if (item != null) {
            for (NBTTagCompound tag : Tags.of(item.getEnchantmentTagList())) {
                int id = tag.getShort("id");
                short lvl = tag.getShort("lvl");

                setValue(Integer.toString(id), Short.toString(lvl));
                oldThings.add(Integer.toString(id));
            }
        }
    }

    protected abstract ItemStack getItem(EntityPlayer player);

}
