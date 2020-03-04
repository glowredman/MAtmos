package eu.ha3.matmos.core;

public interface SoundRelay {
    void routine();

    void cacheSound(String path);

    void playSound(String path, float volume, float pitch, int meta);

    int getNewStreamingToken();

    boolean setupStreamingToken(int token, String path, float volume, float pitch, boolean isLooping, boolean usesPause, boolean underwater);

    void startStreaming(int token, float fadeDuration);

    void stopStreaming(int token, float fadeDuration);

    void eraseStreamingToken(int token);
    
    boolean isPlaying(int token);

}
