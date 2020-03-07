package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.biome.BiomeGenBase;

@Mixin(BiomeGenBase.class)
public interface IBiomeGenBase {
    @Accessor("enableRain")
    boolean enableRain();
}