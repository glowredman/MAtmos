package eu.ha3.matmos.game.data.modules.items;

import eu.ha3.matmos.engine.core.interfaces.Data;
import net.minecraft.potion.PotionEffect;

/*
 * --filenotes-placeholder
 */

public class ModulePotionDuration extends AbstractPotionQualityModule {
    public ModulePotionDuration(Data data) {
        super(data, "potion_duration");
    }

    @Override
    protected String getQuality(PotionEffect effect) {
        return Integer.toString(effect.getDuration());
    }
}
