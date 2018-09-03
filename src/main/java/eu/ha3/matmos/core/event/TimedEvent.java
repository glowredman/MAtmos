package eu.ha3.matmos.core.event;

import java.util.Random;

import eu.ha3.matmos.core.Provider;
import eu.ha3.matmos.core.ReferenceTime;
import eu.ha3.matmos.serialisation.expansion.SerialMachineEvent;

public class TimedEvent implements TimedEventInterface {
    private static Random random = new Random();

    private String event;
    private final Provider<Event> provider;
    private final float volMod;
    private final float pitchMod;
    private final float delayMin;
    private final float delayMax;
    private final float delayStart;

    private long nextPlayTime;

    public TimedEvent(Provider<Event> provider, SerialMachineEvent eelt) {
        this(eelt.event, provider, eelt.vol_mod, eelt.pitch_mod, eelt.delay_min, eelt.delay_max, eelt.delay_start);
    }

    public TimedEvent(String event, Provider<Event> provider, float volMod, float pitchMod, float delayMin, float delayMax, float delayStart) {
        this.event = event;
        this.provider = provider;
        this.volMod = volMod;
        this.pitchMod = pitchMod;
        this.delayMin = delayMin;
        this.delayMax = delayMax;
        this.delayStart = delayStart;

        if (delayMax < delayMin) {
            delayMax = delayMin;
        }
    }

    @Override
    public void restart(ReferenceTime time) {
        if (delayStart == 0) {
            nextPlayTime = time.getMilliseconds() + (long)(random.nextFloat() * delayMax * 1000);
        } else {
            nextPlayTime = time.getMilliseconds() + (long)(delayStart * 1000);
        }
    }

    @Override
    public void play(ReferenceTime time, float fadeFactor) {
        if (time.getMilliseconds() < nextPlayTime) {
            return;
        }

        if (provider.exists(event)) {
            provider.get(event).playSound(volMod * fadeFactor, pitchMod);
        }

        if (delayMin == delayMax && delayMin > 0) {
            while (nextPlayTime < time.getMilliseconds()) {
                nextPlayTime = nextPlayTime + (long)(delayMin * 1000);
            }
        } else {
            nextPlayTime = time.getMilliseconds()
                    + (long)((delayMin + random.nextFloat() * (delayMax - delayMin)) * 1000);
        }
    }
}
