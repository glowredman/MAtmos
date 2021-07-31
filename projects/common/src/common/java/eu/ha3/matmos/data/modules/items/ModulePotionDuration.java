package eu.ha3.matmos.data.modules.items;

import eu.ha3.matmos.core.sheet.DataPackage;
import net.minecraft.potion.PotionEffect;

public class ModulePotionDuration extends AbstractPotionQualityModule {
    public ModulePotionDuration(DataPackage data) {
        super(data, "potion_duration");
    }

    @Override
    protected String getQuality(PotionEffect effect) {
        return Integer.toString(effect.getDuration());
    }
}
