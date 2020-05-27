package eu.ha3.matmos.core.ducks;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.SoundCategory;
import paulscode.sound.SoundSystem;

@Mixin(SoundManager.class)
public interface ISoundManager {
    boolean isLoaded();
    
    SoundSystem getSoundSystem();
    
    void setSoundSystemAccessor(SoundSystem sndSystem);
    
    public float invokeGetVolume(SoundCategory category);
}