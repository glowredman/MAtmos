package eu.ha3.matmos.game.data.modules.items;

import eu.ha3.matmos.engine.core.interfaces.Data;
import net.minecraft.potion.PotionEffect;

public class ModulePotionStrength extends AbstractPotionQualityModule {
    public ModulePotionStrength(Data data) {
        super(data, "potion_power");
    }

    @Override
    protected String getQuality(PotionEffect effect) {
        return Integer.toString(effect.getAmplifier() + 1);
    }
}
