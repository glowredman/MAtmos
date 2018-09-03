package eu.ha3.matmos.data.modules.items;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.data.modules.RegistryBasedModule;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * An abstract module that extracts a certain quality of all potion effects (such as time,
 * strength...) that is currently affecting the player. The quality is defined by the implementing
 * class.
 *
 * @author Hurry
 */
abstract class AbstractPotionQualityModule extends ModuleProcessor implements RegistryBasedModule {
    private Set<String> oldThings = new LinkedHashSet<>();

    public AbstractPotionQualityModule(DataPackage data, String name) {
        super(data, name);

        data.getSheet(name).setDefaultValue("0");
        data.getSheet(name + ModuleProcessor.DELTA_SUFFIX).setDefaultValue("0");
    }

    @Override
    public String getRegistryName() {
        return "potion";
    }

    @Override
    protected void doProcess() {
        for (String i : oldThings) {
            setValue(i, 0);
        }

        oldThings.clear();

        for (PotionEffect effect : getPlayer().getActivePotionEffects()) {
            int id = Potion.getIdFromPotion(effect.getPotion());

            setValue(Integer.toString(id), getQuality(effect));

            oldThings.add(Integer.toString(id));
        }
    }

    abstract protected String getQuality(PotionEffect effect);
}
