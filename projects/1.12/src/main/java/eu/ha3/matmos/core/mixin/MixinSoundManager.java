package eu.ha3.matmos.core.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.ha3.matmos.Matmos;
import net.minecraft.client.audio.SoundManager;

@Mixin(SoundManager.class)
abstract class MixinSoundManager {
    @Shadow
    private List<String> pausedChannels;

    @Shadow
    private boolean loaded;

    @Inject(method = "stopAllSounds", at = @At("RETURN"))
    public void stopAllSounds(CallbackInfo ci) {
        if (this.loaded) {
            // Apply Forge's fix for MC-35856. Required for LiteLoader. Redundant if we're
            // using Forge, but should be harmless.
            this.pausedChannels.clear(); // Forge: MC-35856 Fixed paused sounds repeating when switching worlds
        }

        Matmos.getSoundManagerListeners().forEach(l -> l.onStopAllSounds());
    }

    @Inject(method = "pauseAllSounds", at = @At("RETURN"))
    public void pauseAllSounds(CallbackInfo ci) {
        Matmos.getSoundManagerListeners().forEach(l -> l.onPauseAllSounds(true));
    }

    @Inject(method = "resumeAllSounds", at = @At("RETURN"))
    public void resumeAllSounds(CallbackInfo ci) {
        Matmos.getSoundManagerListeners().forEach(l -> l.onPauseAllSounds(false));
    }
    
    @Inject(method = "unloadSoundSystem", at = @At("RETURN"))
    public void unloadSoundSystem(CallbackInfo ci) {
        Matmos.getSoundManagerListeners().forEach(l -> l.onLoadSoundSystem(false));
    }
    
    @Inject(method = "loadSoundSystem", at = @At("RETURN"))
    public void loadSoundSystem(CallbackInfo ci) {
        Matmos.getSoundManagerListeners().forEach(l -> l.onLoadSoundSystem(true));
    }
}
