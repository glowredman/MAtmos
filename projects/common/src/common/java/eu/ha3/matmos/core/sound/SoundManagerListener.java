package eu.ha3.matmos.core.sound;

public interface SoundManagerListener {

    public default void onStopAllSounds() {};

    public default void onPauseAllSounds(boolean pause) {};
    
    public default void onLoadSoundSystem(boolean load) {};

}