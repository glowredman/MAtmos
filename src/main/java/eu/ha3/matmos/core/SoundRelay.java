package eu.ha3.matmos.core;

/* x-placeholder */

public interface SoundRelay {
    void routine();

    void cacheSound(String path);

    void playSound(String path, float volume, float pitch, int meta);

    int getNewStreamingToken();

    boolean setupStreamingToken(int token, String path, float volume, float pitch, boolean isLooping, boolean usesPause);

    void startStreaming(int token, float fadeDuration);

    void stopStreaming(int token, float fadeDuration);

    //public void pauseStreaming(int token, float fadeDuration);

    void eraseStreamingToken(int token);

}
