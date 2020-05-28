package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Redirect;

import eu.ha3.matmos.core.ducks.ISoundManager;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Mixin(targets = "net/minecraft/client/audio/SoundManager$1")
abstract class MixinSoundManager$1 implements Runnable {

    @Shadow(aliases = { "this$0", "field_177224_a", "a" })
    @Final
    private SoundManager outerThis;

    // Access redirectors don't work because the field is accessed via a synthetic
    // method.
    @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/audio/SoundManager$SoundSystemStarterThread;setMasterVolume(F)V"))
    private void redirectSetMasterVolume(@Coerce SoundSystem sndSystem, float value) {
        ((ISoundManager) outerThis).setSoundSystemAccessor(sndSystem);
        sndSystem.setMasterVolume(value);
    }
}
