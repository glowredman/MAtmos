package eu.ha3.matmos;

import eu.ha3.matmos.engine.SoundSet;
import eu.ha3.matmos.engine.condition.ConditionParser;
import eu.ha3.matmos.engine.event.EventProcessor;
import eu.ha3.matmos.serialize.EventSerialize;
import eu.ha3.matmos.serialize.StreamEventSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class Debug
{
    public static List<EventProcessor> getProcessors(MAtmos mAtmos)
    {
        List<String> e1Rules = new ArrayList<String>();
        e1Rules.add("scan.large.minecraft:water > 500");
        e1Rules.add("player.action.sneaking = false");

        List<String> e2Rules = new ArrayList<String>();
        e2Rules.add("scan.small.minecraft:stone > 50");
        e2Rules.add("weather.rainCanReach = false");

        ConditionParser parser = new ConditionParser(mAtmos.dataManager);
        mAtmos.dataManager.registerConditionSet(parser.parse("e1_rules", e1Rules));
        mAtmos.dataManager.registerConditionSet(parser.parse("e2_rules", e2Rules));

        mAtmos.dataManager.registerSoundSet(new SoundSet("e1_sounds").add("fire.fire"));
        mAtmos.dataManager.registerSoundSet(new SoundSet("e2_sounds").add("matmosphere_stream.stream_out"));

        List<EventProcessor> list = new ArrayList<EventProcessor>();
        EventSerialize e1 = new EventSerialize("e1_debug");
        e1.maxDistance = 15;
        e1.sounds.add("e1_sounds");
        e1.triggers.add("e1_rules");
        e1.delayMin = 0;
        e1.delayMax = 1;
        e1.delayAfter = 80;

        StreamEventSerialize e2 = new StreamEventSerialize("e2_debug");
        e2.triggers.add("e2_rules");
        e2.sounds.add("e2_sounds");
        e2.delayMin = 10;
        e2.delayMax = 40;
        e2.volumeMin = 0.75F;
        e2.volumeMax = 1.5F;
        e2.repeatable = true;
        e2.fadeIn = 100;
        e2.fadeOut = 60;

        list.add(EventProcessor.of(e1, mAtmos).get());
        list.add(EventProcessor.of(e2, mAtmos).get());
        return list;
    }
}
