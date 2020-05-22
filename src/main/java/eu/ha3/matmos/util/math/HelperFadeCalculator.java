package eu.ha3.matmos.util.math;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.ReferenceTime;

public class HelperFadeCalculator {
    private final ReferenceTime time;

    private double slope = 0;
    private double intercept;

    private float targetFade;
    private boolean doneFading;

    public HelperFadeCalculator(ReferenceTime time) {
        this.time = time;
    }

    public void fadeIn(long durationMs) {
        fadeTo(1f, durationMs);
    }

    public void fadeOut(long durationMs) {
        fadeTo(0f, durationMs);
    }

    public void fadeTo(float newVolume, long durationMs) {
        float duration = durationMs / 1000f;
        float currentFade = calculateFadeFactor();

        targetFade = newVolume;
        doneFading = false;

        if (currentFade != newVolume) {
            slope = (newVolume - currentFade) / duration;

            double now = time.getMilliseconds() / 1000.0;

            // slope * now + intercept = currentFade
            intercept = currentFade - slope * now;
        }
    }

    public float calculateFadeFactor() {
        if (doneFading) {
            return targetFade;
        }

        if (Double.isInfinite(slope)) {
            doneFading = true;
            return targetFade;
        } else {
            double now = time.getMilliseconds() / 1000.0;

            float fadeNow = (float) (slope * now + intercept);

            if ((slope > 0 && fadeNow > targetFade) || (slope < 0 && fadeNow < targetFade)) {
                fadeNow = targetFade;
                doneFading = true;
            }

            return fadeNow;
        }
    }

    public boolean isDoneFadingOut() {
        return doneFading && targetFade == 0;
    }

    public float getTargetFade() {
        return targetFade;
    }
}
