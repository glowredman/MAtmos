package eu.ha3.matmos.core.sound;

import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

/*
 * --filenotes-placeholder
 */

public interface SoundAccessor {
    SoundManager getSoundManager();

    SoundSystem getSoundSystem();
}
