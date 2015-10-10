package eu.ha3.matmos.engine.event;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.SoundSet;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.serialize.EventSerialize;
import eu.ha3.matmos.serialize.StreamEventSerialize;
import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public abstract class EventProcessor
{
    private static final Map<String, ResourceLocation> soundCache = new HashMap<String, ResourceLocation>();

    private final List<ConditionSet> triggers = new ArrayList<ConditionSet>();
    private final List<ConditionSet> blockers = new ArrayList<ConditionSet>();
    private final List<String> sounds = new ArrayList<String>();
    private final DelayTimer delay;
    protected final float minVol;
    protected final float maxVol;
    protected final float minPitch;
    protected final float maxPitch;
    protected final int distance;

    private boolean active = false;

    protected EventProcessor(EventSerialize e, MAtmos mAtmos)
    {
        for (String s : e.triggers)
        {
            Optional<ConditionSet> optional = mAtmos.dataManager.getConditionSet(s);
            if (optional.isPresent())
                triggers.add(optional.get());
        }
        for (String s : e.blockers)
        {
            Optional<ConditionSet> optional = mAtmos.dataManager.getConditionSet(s);
            if (optional.isPresent())
                blockers.add(optional.get());
        }
        for (String s : e.sounds)
        {
            Optional<SoundSet> optional = mAtmos.dataManager.getSoundSet(s);
            if (optional.isPresent())
                sounds.addAll(optional.get().getSounds());
        }
        delay = new DelayTimer(e.delayMin, e.delayMax, e.delayAfter);
        minVol = e.volumeMin;
        maxVol = e.volumeMax;
        minPitch = e.pitchMin;
        maxPitch = e.pitchMax;
        distance = e.maxDistance;
    }

    public final void process()
    {
        for (ConditionSet s : blockers)
        {
            if (s.active())
            {
                interrupt();
                return;
            }
        }
        boolean delayComplete = delay.complete();
        for (ConditionSet s : triggers)
        {
            if (s.active())
            {
                active = true;
                if (delayComplete)
                    trigger();
                return;
            }
        }
        if (active)
        {
            active = false;
            interrupt();
        }
    }

    public final Map<String, Boolean> getActive()
    {
        Map<String, Boolean> active = new HashMap<String, Boolean>();
        for (ConditionSet cs : triggers)
        {
            active.put(cs.getName(), cs.active());
        }
        return active;
    }

    public final ResourceLocation getRandomSound()
    {
        String sound = sounds.get(NumberUtil.nextInt(0, sounds.size()));
        if (!soundCache.containsKey(sound))
        {
            soundCache.put(sound, new ResourceLocation(sound));
        }
        return soundCache.get(sound);
    }

    public abstract void interrupt();

    public abstract void trigger();

    public static Optional<EventProcessor> of(EventSerialize e, MAtmos mAtmos)
    {
        if (e.valid(mAtmos))
        {
            EventProcessor ep;
            if (e instanceof StreamEventSerialize)
                ep = new StreamEvent((StreamEventSerialize) e, mAtmos);
            else
                ep = new SoundEvent(e, mAtmos);
            return Optional.of(ep);
        }
        return Optional.absent();
    }
}
