package eu.ha3.matmos.core;

import eu.ha3.matmos.core.logic.Machine;

public class StreamInformation extends MultistateComponent implements Simulated {
    private String path;
    private float volume;
    private float pitch;
    private float delayBeforeFadeIn;
    private float delayBeforeFadeOut;
    private float fadeInTime;
    private float fadeOutTime;
    private boolean isLooping;
    private boolean usesPause;

    private final String machineName;
    private final Provider<Machine> provider;
    private final ReferenceTime time;
    private final SoundRelay relay;

    private boolean initialized;
    private int token;

    private boolean isPlaying;
    private long startTime;
    private long stopTime;

    public StreamInformation(String machineName, Provider<Machine> provider, ReferenceTime time, SoundRelay relay, String path, float volume, float pitch, float delayBeforeFadeIn, float delayBeforeFadeOut, float fadeInTime, float fadeOutTime, boolean isLooping, boolean usesPause) {
        super("_STREAM:" + machineName);

        this.machineName = machineName;
        this.provider = provider;
        this.time = time;
        this.relay = relay;

        this.path = path;
        this.volume = volume;
        this.pitch = pitch;
        this.delayBeforeFadeIn = delayBeforeFadeIn;
        this.delayBeforeFadeOut = delayBeforeFadeOut;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
        this.isLooping = isLooping;
        this.usesPause = usesPause;

        token = -1;
    }

    @Override
    public void evaluate() {
        if (!provider.exists(machineName)) {
            return;
        }

        boolean active = provider.get(machineName).isActive();
        if (active != isActive) {
            isActive = active;
            incrementVersion();
            if (active) {
                signalPlayable();
            } else {
                signalStoppable();
            }
        }
    }

    private void signalPlayable() {
        startTime = time.getMilliseconds() + (long)(delayBeforeFadeIn * 1000);
    }

    private void signalStoppable() {
        stopTime = time.getMilliseconds() + (long)(delayBeforeFadeOut * 1000);
    }

    @Override
    public void simulate() {
        if (!isLooping && usesPause) {
            return; // FIXME: A non-looping sound cannot use the pause scheme.
        }

        if (isActive && !isPlaying) {
            if (time.getMilliseconds() > startTime) {
                isPlaying = true;

                if (!initialized) {
                    token = relay.getNewStreamingToken();

                    if (relay.setupStreamingToken(token, path, volume, pitch, isLooping, usesPause)) {
                        initialized = true;
                        relay.startStreaming(token, fadeInTime);
                    }
                } else {
                    relay.startStreaming(token, fadeInTime);
                }
            }
        } else if (!isActive && isPlaying) {
            if (time.getMilliseconds() > stopTime) {
                isPlaying = false;
                relay.stopStreaming(token, fadeOutTime);
            }
        }
    }
}
