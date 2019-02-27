package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Mixin;

import eu.ha3.matmos.core.ducks.ISoundManager;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
abstract class MixinSoundManager implements ISoundManager {

    private SoundSystem __sndSystem;

    @Override
    public SoundSystem getSoundSystem() {
        return __sndSystem;
    }

    @Override
    public void setSoundSystem(SoundSystem sndSystem) {
        __sndSystem = sndSystem;
    }
}
