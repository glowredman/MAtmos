package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import eu.ha3.matmos.core.ducks.ISoundHandler;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;

@Mixin(SoundHandler.class)
abstract class MixinSoundHandler implements ISoundHandler {
    @Override
    @Accessor("sndManager")
    public abstract SoundManager getSoundManager();
}
