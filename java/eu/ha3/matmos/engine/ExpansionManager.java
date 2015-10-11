package eu.ha3.matmos.engine;

import eu.ha3.matmos.engine.processor.AbstractProcessor;
import eu.ha3.matmos.engine.processor.VolumeModifier;

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
    private final List<AbstractProcessor> abstractProcessors = new ArrayList<AbstractProcessor>();
    public final Map<String, Boolean> active = new HashMap<String, Boolean>();

    public void wipe()
    {
        abstractProcessors.clear();
        active.clear();
    }

    public void process()
    {
        for (AbstractProcessor abstractProcessor : abstractProcessors)
        {
            abstractProcessor.process();
            // TODO only collect 'active' data if GUIData needs it
            active.putAll(abstractProcessor.getActive());
        }
    }

    public void addEventProcessor(AbstractProcessor abstractProcessor)
    {
        abstractProcessors.add(abstractProcessor);
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
