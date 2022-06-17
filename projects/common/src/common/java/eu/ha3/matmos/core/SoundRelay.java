package eu.ha3.matmos.core;

public interface SoundRelay {
    void routine();

    void cacheSound(String path);

    void playSound(String path, float volume, float pitch, int meta);
    
    void playSoundEvent(String name, float x, float y, float z, float volume, float pitch);

    int getNewStreamingToken();

    int getStreamingTokenFor(String path, float volume, float pitch, boolean isLooping, boolean usesPause,
            boolean underwater);

    boolean setupStreamingToken(int token, String path, float pitch, boolean isLooping, boolean usesPause,
            boolean underwater);

    void setVolume(int token, float volume, float fadeDuration);

    void eraseStreamingToken(int token);
}
