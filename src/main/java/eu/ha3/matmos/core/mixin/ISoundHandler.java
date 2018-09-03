package eu.ha3.matmos.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Mixin(SoundHandler.class)
public interface ISoundHandler {

    @Accessor("sngManager")
    SoundManager getSoundManager();

    default SoundSystem getSoundSystem() {
        return ((ISoundManager)getSoundManager()).getSoundSystem();
    }
}
