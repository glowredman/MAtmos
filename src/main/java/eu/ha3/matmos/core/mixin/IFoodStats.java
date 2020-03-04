package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.FoodStats;

@Mixin(FoodStats.class)
public interface IFoodStats {
    @Accessor("foodExhaustionLevel")
    float getFoodExhaustionLevel();
}
