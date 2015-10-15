package eu.ha3.matmos.util;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.SoundSet;
import eu.ha3.matmos.engine.condition.ConditionParser;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.serialize.Expansion;
import eu.ha3.matmos.serialize.StreamEventSerialize;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class Debug
{
    public static void dummyExpansion(MAtmos mAtmos)
    {
        List<String> rules = new ArrayList<String>();
        rules.add("scan.block.small.minecraft:stone >= 50");
        rules.add("player.gamemode = creative | survival");
        rules.add("player.action.sneaking = true");

        ConditionSet conditions = ConditionParser.parse("debug_rules", rules);
        SoundSet sounds = new SoundSet("debug_sounds").add("matmosphere_stream.stream_out");
        StreamEventSerialize e = new StreamEventSerialize("debug_processor");
        e.triggers.add("debug_rules");
        e.sounds.add("debug_sounds");
        e.delayMin = 10;
        e.delayMax = 40;
        e.volumeMin = 0.75F;
        e.volumeMax = 1.5F;
        e.repeatable = true;
        e.fadeIn = 100;
        e.fadeOut = 60;

        Expansion expansion = new Expansion("debug_expansion").addConditions(conditions).addSounds(sounds).addProcessors(e);
        Expansion.toJson(mAtmos.configFolder, expansion);

        expansion.register(mAtmos);
    }
}
