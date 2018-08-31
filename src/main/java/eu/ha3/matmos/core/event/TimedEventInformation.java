package eu.ha3.matmos.core.event;

import java.util.List;

import eu.ha3.matmos.core.MultistateComponent;
import eu.ha3.matmos.core.Provider;
import eu.ha3.matmos.core.ReferenceTime;
import eu.ha3.matmos.core.Simulated;
import eu.ha3.matmos.core.logic.Machine;
import eu.ha3.matmos.util.math.HelperFadeCalculator;

/*
 * --filenotes-placeholder
 */

public class TimedEventInformation extends MultistateComponent implements Simulated {
    private float delayBeforeFadeIn = 0f;
    private float delayBeforeFadeOut = 0f;
    private float fadeInTime = 0f;
    private float fadeOutTime = 0f;

    //

    private final String machineName;
    private final Provider<Machine> provider;
    private final ReferenceTime time;
    private List<TimedEvent> events;

    private HelperFadeCalculator calc;

    private boolean isPlaying;
    private long startTime;
    private long stopTime;

    public TimedEventInformation(String machineName, Provider<Machine> provider, ReferenceTime time, List<TimedEvent> events, float delayBeforeFadeIn, float delayBeforeFadeOut, float fadeInTime, float fadeOutTime) {
        super("_TIMED:" + machineName);

        this.machineName = machineName;
        this.provider = provider;
        this.time = time;

        calc = new HelperFadeCalculator(time);

        this.events = events;
        this.delayBeforeFadeIn = delayBeforeFadeIn;
        this.delayBeforeFadeOut = delayBeforeFadeOut;
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
    }

    private void signalPlayable() {
        startTime = time.getMilliseconds() + (long)(delayBeforeFadeIn * 1000);
    }

    private void signalStoppable() {
        stopTime = time.getMilliseconds() + (long)(delayBeforeFadeOut * 1000);
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

    @Override
    public void simulate() {
        if (isActive && !isPlaying) {
            if (time.getMilliseconds() > startTime) {
                isPlaying = true;
                calc.fadeIn((long)(fadeInTime * 1000));
                for (TimedEventInterface t : events) {
                    t.restart(time);
                }
            }
        } else if (!isActive && isPlaying) {
            if (time.getMilliseconds() > stopTime) {
                isPlaying = false;
                calc.fadeOut((long)(fadeOutTime * 1000));
            }
        }

        if (isPlaying || calc.calculateFadeFactor() > 0f) {
            play();
        }
    }

    private void play() {
        float fadeFactor = calc.calculateFadeFactor();
        for (TimedEventInterface t : events) {
            t.play(time, fadeFactor);
        }
    }
}
