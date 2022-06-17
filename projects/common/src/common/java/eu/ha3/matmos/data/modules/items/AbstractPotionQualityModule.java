package eu.ha3.matmos.data.modules.items;

import java.util.LinkedHashSet;
import java.util.Set;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.data.modules.RegistryBasedModule;
import eu.ha3.matmos.util.IDontKnowHowToCode;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.potion.PotionEffect;

/**
 * An abstract module that extracts a certain quality of all potion effects
 * (such as time, strength...) that is currently affecting the player. The
 * quality is defined by the implementing class.
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

        for (Object effectObj : getPlayer().getActivePotionEffects()) {
            if (effectObj instanceof PotionEffect) {
                PotionEffect effect = (PotionEffect) effectObj;
                int id = MAtUtil.getPotionEffectID(effect);

                setValue(Integer.toString(id), getQuality(effect));

                oldThings.add(Integer.toString(id));
            } else {
                IDontKnowHowToCode.warnOnce("getActivePotionEffects() contained a " + effectObj.getClass() + ", ("
                        + effectObj + "), expected PotionEffect");
            }

        }
    }

    abstract protected String getQuality(PotionEffect effect);
}
