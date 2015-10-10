package eu.ha3.matmos.engine.event;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.StreamingSound;
import eu.ha3.matmos.serialize.StreamEventSerialize;

/**
 * @author dags_ <dags@dags.me>
 */

public class StreamEvent extends EventProcessor
{
    private final int fadeInTicks;
    private final int fadeOutTicks;
    private final boolean repeatable;

    private StreamingSound sound;
    private boolean firstPlay = true;

    protected StreamEvent(StreamEventSerialize e, MAtmos mAtmos)
    {
        super(e, mAtmos);
        fadeInTicks = e.fadeIn;
        fadeOutTicks = e.fadeOut;
        repeatable = e.repeatable;
        sound = new StreamingSound(getRandomSound(), fadeInTicks, fadeOutTicks, minVol, maxVol, maxPitch);
    }

    @Override
    public void interrupt()
    {
        if (sound.isPlaying())
        {
            sound.interrupt();
        }
        firstPlay = true;
    }

    @Override
    public void trigger()
    {
        if (!sound.isPlaying() && (firstPlay || repeatable))
        {
            sound = new StreamingSound(getRandomSound(), fadeInTicks, fadeOutTicks, minVol, maxVol, maxPitch).play();
            firstPlay = false;
        }
    }
}
