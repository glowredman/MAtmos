package eu.ha3.matmos.data.modules.items;

import eu.ha3.matmos.core.sheet.DataPackage;
import net.minecraft.potion.PotionEffect;

public class ModulePotionStrength extends AbstractPotionQualityModule {
    public ModulePotionStrength(DataPackage data) {
        super(data, "potion_power");
    }

    @Override
    protected String getQuality(PotionEffect effect) {
        return Integer.toString(effect.getAmplifier() + 1);
    }
}
