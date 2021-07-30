package eu.ha3.matmos.core;

import java.util.Optional;

import eu.ha3.matmos.core.sound.StreamingSound;

public class StreamHandle<T extends StreamingSound> {

    private String path;

    private int token;

    private float largestVolumeCommandThisTick = -1f;
    private float fadeOfLargestVolumeCommandThisTick = -1f;

    /**
     * If this is false, the stream is not playing. If it's true, the stream is
     * supposed to be playing, but maybe it's not. (For example, interdimensional
     * travel can result in the stream stopping after this has been set to true.)
     */
    private boolean commandedToBePlaying;

    private T sound;

    private SoundRelay relay;

    public StreamHandle(T sound, SoundRelay relay) {
        this.sound = sound;
        this.relay = relay;
    }

    public static <S extends StreamingSound> StreamHandle<S> of(S sound, SoundRelay relay) {
        return new StreamHandle<S>(sound, relay);
    }

    public T getSound() {
        return sound;
    }

    public void setSound(T sound) {
        this.sound = sound;
    }

    public boolean isCommandedToBePlaying() {
        return commandedToBePlaying;
    }

    /**
     * This doesn't set the stream's volume immediately. Instead, the volume gets
     * applied in SoundHelper.routine(), after all machines have been ticked. If
     * multiple volume values are set during the same tick, the largest one wins.
     */
    public void setVolume(float volume, float fade) {
        if (volume > largestVolumeCommandThisTick) {
            largestVolumeCommandThisTick = volume;
            fadeOfLargestVolumeCommandThisTick = fade;
        }
    }

    public float getLargestVolumeCommandThisTick() {
        return largestVolumeCommandThisTick;
    }

    public float getFadeOfLargestVolumeCommandThisTick() {
        return fadeOfLargestVolumeCommandThisTick;
    }

    public void resetLargestVolumeCommandThisTick() {
        largestVolumeCommandThisTick = -1;
    }

    public void setCommandedToBePlaying(boolean commandedToBePlaying) {
        this.commandedToBePlaying = commandedToBePlaying;
    }

}
