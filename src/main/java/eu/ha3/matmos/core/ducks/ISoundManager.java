package eu.ha3.matmos.core.ducks;

import net.minecraft.util.SoundCategory;
import paulscode.sound.SoundSystem;

public interface ISoundManager {
    boolean isLoaded();

    SoundSystem getSoundSystem();

    void setSoundSystemAccessor(SoundSystem sndSystem);

    public float invokeGetVolume(SoundCategory category);
}