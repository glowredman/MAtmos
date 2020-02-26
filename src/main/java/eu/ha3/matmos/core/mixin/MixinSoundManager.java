package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.ha3.matmos.core.ducks.ISoundManager;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;

@Mixin(SoundManager.class)
abstract class MixinSoundManager implements ISoundManager {

    private SoundSystem __sndSystem;
    
    @Inject(method = "<init>*", at = @At("RETURN"))
    private void onConstructed(CallbackInfo ci) {
        System.out.println("SoundManger mixin onConstructed!");
        SoundSystemConfig.setNumberStreamingChannels(11);
        SoundSystemConfig.setNumberNormalChannels(32-11);
        SoundSystemConfig.setStreamQueueFormatsMatch(true);
    }

    @Override
    public SoundSystem getSoundSystem() {
        return __sndSystem;
    }

    @Override
    public void setSoundSystem(SoundSystem sndSystem) {
        __sndSystem = sndSystem;
    }
}
