package eu.ha3.matmos.util.math;

import eu.ha3.matmos.core.ReferenceTime;

public class HelperFadeCalculator {
    private final ReferenceTime time;

    private long fadeInTime = 0;
    private long fadeInDuration = 0;

    private long fadeOutTime = 0;
    private long fadeOutDuration = 0;

    private boolean foLast = true;
    private boolean complete = true;
    private boolean doneFadingOut = false;
    private float fade = 0;

    public HelperFadeCalculator(ReferenceTime time) {
        this.time = time;
    }

    public void fadeIn(long durationMs) {
        float currentFade = calculateFadeFactor();

        long minTime = (long)(currentFade * durationMs);

        fadeInTime = time.getMilliseconds() - minTime;
        fadeInDuration = durationMs;

        foLast = false;
        complete = false;
        doneFadingOut = false;
    }

    public void fadeOut(long durationMs) {
        float currentFade = calculateFadeFactor();

        long minTime = (long)(durationMs - durationMs * currentFade);

        fadeOutTime = time.getMilliseconds() - minTime;
        fadeOutDuration = durationMs;

        foLast = true;
        complete = false;
        doneFadingOut = false;
    }

    public float calculateFadeFactor() {
        if (complete) {
            return fade;
        }

        long curTime = time.getMilliseconds();
        if (foLast) {
            if (fadeOutDuration <= 0f) {
                fade = 0f;
                complete = true;
                doneFadingOut = true;
            } else {
                fade = 1 - (curTime - fadeOutTime) / (float)fadeOutDuration;
                if (fade < 0f) {
                    fade = 0f;
                    complete = true;
                    doneFadingOut = true;
                }
            }
        } else {
            if (fadeInDuration <= 0f) {
                fade = 1f;
                complete = true;
            } else {
                fade = (curTime - fadeInTime) / (float)fadeInDuration;
                if (fade > 1f) {
                    fade = 1f;
                    complete = true;
                }
            }
        }

        return fade;
    }

    public boolean isDoneFadingOut() {
        return doneFadingOut;
    }
}
