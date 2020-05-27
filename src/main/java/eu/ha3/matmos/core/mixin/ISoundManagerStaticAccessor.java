package eu.ha3.matmos.core.mixin;

import java.net.URL;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;

@Mixin(SoundManager.class)
public interface ISoundManagerStaticAccessor {
    @Invoker("getURLForSoundResource")
    static URL invokeGetURLForSoundResource(final ResourceLocation p_148612_0_) {
        throw new UnsupportedOperationException("Static invoker body is running, this shouldn't happen");
    }
}