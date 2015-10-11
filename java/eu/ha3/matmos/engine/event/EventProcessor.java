package eu.ha3.matmos.engine.event;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.PackManager;
import eu.ha3.matmos.engine.SoundSet;
import eu.ha3.matmos.engine.VolumeModifier;
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
    private final List<ConditionSet> triggers = new ArrayList<ConditionSet>();
    private final List<ConditionSet> blockers = new ArrayList<ConditionSet>();
    private final List<String> sounds = new ArrayList<String>();
    private final DelayTimer delay;
    protected final VolumeModifier volume;
    protected final float minVol;
    protected final float maxVol;
    protected final float minPitch;
    protected final float maxPitch;
    protected final int distance;

    protected EventProcessor(String expansionName, EventSerialize e, MAtmos mAtmos)
    {
        volume = mAtmos.expansionManager.getVolume(expansionName);
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
            {
                sounds.addAll(optional.get().getSounds());
            }
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
                if (delayComplete && !soundIsPlaying())
                {
                    trigger();
                }
                return;
            }
        }
        if (soundIsPlaying())
        {
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

    private Optional<ResourceLocation> checkAndReturn(Optional<ResourceLocation> optional, int index, String name)
    {
        if (!optional.isPresent())
        {
            sounds.remove(index);
            MAtmos.log("Sound: " + name + " could not be found, removing from processor!");
        }
        return optional;
    }

    public final Optional<ResourceLocation> getRandomSound()
    {
        if (sounds.size() > 0)
        {
            int index = NumberUtil.nextInt(0, sounds.size());
            String sound = sounds.get(index);
            return checkAndReturn(PackManager.getSound(sound), index, sound);
        }
        return Optional.absent();
    }

    public final Optional<ResourceLocation> getRandomStream()
    {
        if (sounds.size() > 0)
        {
            int index = NumberUtil.nextInt(0, sounds.size());
            String sound = sounds.get(index);
            return checkAndReturn(PackManager.getStream(sound), index, sound);
        }
        return Optional.absent();
    }

    public abstract boolean soundIsPlaying();

    public abstract void interrupt();

    public abstract void trigger();

    public static Optional<EventProcessor> of(String expansion, EventSerialize e, MAtmos mAtmos)
    {
        if (e.valid(mAtmos))
        {
            EventProcessor ep;
            if (e instanceof StreamEventSerialize)
                ep = new StreamEvent(expansion, (StreamEventSerialize) e, mAtmos);
            else
                ep = new SoundEvent(expansion, e, mAtmos);
            return Optional.of(ep);
        }
        return Optional.absent();
    }
}
