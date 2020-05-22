package eu.ha3.matmos.core.ducks;

import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

public interface ISoundHandler {

    SoundManager getSoundManager();

    default SoundSystem getSoundSystem() {
        return ((ISoundManager) getSoundManager()).getSoundSystem();
    }
}
