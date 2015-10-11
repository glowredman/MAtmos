package eu.ha3.matmos.engine;

import eu.ha3.matmos.engine.event.EventProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public class ExpansionManager
{
    private final Map<String, VolumeModifier> volumes = new HashMap<String, VolumeModifier>();
    private final List<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();
    public final Map<String, Boolean> active = new HashMap<String, Boolean>();

    public void wipe()
    {
        eventProcessors.clear();
        active.clear();
    }

    public void process()
    {
        for (EventProcessor eventProcessor : eventProcessors)
        {
            eventProcessor.process();
            // TODO only collect 'active' data if GUIData needs it
            active.putAll(eventProcessor.getActive());
        }
    }

    public void addEventProcessor(EventProcessor eventProcessor)
    {
        eventProcessors.add(eventProcessor);
    }

    public VolumeModifier getVolume(String name)
    {
        if (!volumes.containsKey(name))
        {
            volumes.put(name, new VolumeModifier());
        }
        return volumes.get(name);
    }
}
