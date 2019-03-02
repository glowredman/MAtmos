package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import eu.ha3.matmos.core.ducks.ISoundManager;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Mixin(targets = "net/minecraft/client/audio/SoundManager$1")
abstract class MixinSoundManager$1 implements Runnable {

    @Shadow(aliases = {"this$0", "field_177224_a", "a"})
    @Final
    private SoundManager outerThis;

    @Redirect(method = "run()V",
            at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;setMasterVolume(F)V"))
    private void redirectSetMasterVolume(SoundSystem sndSystem, float value) {
        ((ISoundManager)outerThis).setSoundSystem(sndSystem);
        sndSystem.setMasterVolume(value);
    }
}
