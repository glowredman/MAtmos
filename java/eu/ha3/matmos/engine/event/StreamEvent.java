package eu.ha3.matmos.engine.event;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.game.StreamingSound;
import eu.ha3.matmos.serialize.StreamEventSerialize;
import net.minecraft.util.ResourceLocation;

/**
 * @author dags_ <dags@dags.me>
 */

public class StreamEvent extends EventProcessor
{
    private final int fadeInTicks;
    private final int fadeOutTicks;
    private final boolean repeatable;

    private StreamingSound sound;

    protected StreamEvent(String expansion, StreamEventSerialize e, MAtmos mAtmos)
    {
        super(expansion, e, mAtmos);
        fadeInTicks = e.fadeIn;
        fadeOutTicks = e.fadeOut;
        repeatable = e.repeatable;
    }

    public boolean soundIsPlaying()
    {
        return sound != null && sound.isPlaying();
    }

    @Override
    public void interrupt()
    {
        if (sound != null && sound.isPlaying())
        {
            sound.interrupt();
        }
    }

    @Override
    public void trigger()
    {
        if (sound == null)
        {
            Optional<ResourceLocation> location = getRandomStream();
            if (location.isPresent())
            {
                sound = new StreamingSound(location.get(), volume,fadeInTicks, fadeOutTicks, minVol, maxVol, maxPitch).play();
            }
        }
        else if (!sound.isPlaying() && repeatable)
        {
            Optional<ResourceLocation> location = getRandomStream();
            if (location.isPresent())
            {
                sound = new StreamingSound(location.get(), volume, fadeInTicks, fadeOutTicks, minVol, maxVol, maxPitch).play();
            }
        }
    }
}
