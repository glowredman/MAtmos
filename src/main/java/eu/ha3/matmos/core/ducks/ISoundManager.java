package eu.ha3.matmos.core.ducks;

import paulscode.sound.SoundSystem;

public interface ISoundManager {
    SoundSystem getSoundSystem();

    void setSoundSystem(SoundSystem sndSystem);
}
