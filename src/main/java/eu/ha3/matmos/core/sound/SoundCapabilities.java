package eu.ha3.matmos.core.sound;

public interface SoundCapabilities {
    /**
     * Plays a mono, localized sound at a certain location.
     */
    void playMono(String event, double xx, double yy, double zz, float volume, float pitch);

    /**
     * Plays a stereo, unlocalized sound.
     */
    void playStereo(String event, float volume, float pitch);

    /**
     * Registers a streaming sound.
     */
    void registerStreaming(String customName, String path, float volume, float pitch, boolean isLooping, boolean usesPause);

    /**
     * Plays a streaming sound, fading in if it's greater than zero. Fading unit is in seconds.
     */
    void playStreaming(String customName, float fadeIn);

    /**
     * Stops a streaming sound, fading out if it's greater than zero. Fading unit is in seconds.
     */
    void stopStreaming(String customName, float fadeOut);

    /**
     * Instantly applies a volume modulation of all currently running stuff and future ones.
     */
    void applyVolume(float volumeMod);

    /**
     * Gracefully stops all activities provided by the implementation. It should stop all sounds from
     * playing.
     */
    void stop();

    /**
     * Clean up all resources that are not freed up. SoundCapabilities should be able to be used again.
     */
    void cleanUp();

    /**
     * Brutally interrupts all activities provided by the implementation. This indicates the sound
     * engine may have been dumped during runtime.
     */
    void interrupt();
}
