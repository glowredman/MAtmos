package eu.ha3.matmos.core.sound;

import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

@Deprecated
public interface SoundAccessor {
    SoundManager getSoundManager();

    SoundSystem getSoundSystem();
}
