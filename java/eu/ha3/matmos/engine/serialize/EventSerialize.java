package eu.ha3.matmos.engine.serialize;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.util.NumberUtil;

import java.util.ArrayList;

/**
 * @author dags_ <dags@dags.me>
 */

public class EventSerialize
{
    public final String name;
    public float volumeMax = 1F;
    public float volumeMin = 1F;
    public float pitchMax = 1F;
    public float pitchMin = 1F;
    public int maxDistance = 0;
    public int delayMax = 0;
    public int delayMin = 0;
    public int delayAfter = 0;
    public ArrayList<String> triggers = new ArrayList<String>();
    public ArrayList<String> blockers = new ArrayList<String>();
    public ArrayList<String> sounds = new ArrayList<String>();

    public EventSerialize(String eventName)
    {
        name = eventName;
    }

    public boolean valid(MAtmos m)
    {
        MAtmos.log("Validating " + name);
        if (maxDistance < 0)
        {
            MAtmos.log("MaxDistance must be greater than or equal to 0!");
            return false;
        }
        if (delayAfter < 0)
        {
            MAtmos.log("DelayAfter must be greater than or equal to 0!");
            return false;
        }
        return volumes(m) && pitches(m) && delays(m) && triggers(m) && sounds(m);
    }

    private boolean volumes(MAtmos mAtmos)
    {
        return NumberUtil.validMinMax(mAtmos, "Volume", volumeMin, volumeMax);
    }

    private boolean pitches(MAtmos mAtmos)
    {
        return NumberUtil.validMinMax(mAtmos, "Pitch", pitchMin, pitchMax);
    }

    private boolean delays(MAtmos mAtmos)
    {
        return NumberUtil.validMinMax(mAtmos, "Delay", delayMin, delayMax);
    }

    private boolean triggers(MAtmos mAtmos)
    {
        for (String s : triggers)
        {
            if (mAtmos.dataManager.getConditionSet(s).isPresent())
            {
                return true;
            }
        }
        MAtmos.log("No valid Triggers found for event: " + name);
        return false;
    }

    private boolean sounds(MAtmos mAtmos)
    {
        for (String s : sounds)
        {
            if (mAtmos.dataManager.getSoundSet(s).isPresent())
            {
                return true;
            }
        }
        MAtmos.log("No valid Sounds found for event: " + name);
        return false;
    }
}
