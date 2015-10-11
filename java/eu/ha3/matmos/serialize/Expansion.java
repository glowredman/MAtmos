package eu.ha3.matmos.serialize;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.SoundSet;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.engine.event.EventProcessor;
import eu.ha3.matmos.serialize.jsonadapters.ConditionAdapter;
import eu.ha3.matmos.serialize.jsonadapters.EventAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class Expansion
{
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
            .registerTypeAdapter(EventSerialize.class, new EventAdapter())
            .registerTypeAdapter(ConditionSet.class, new ConditionAdapter()).create();

    private String name;
    private List<ConditionSet> conditions = new ArrayList<ConditionSet>();
    private List<SoundSet> sounds = new ArrayList<SoundSet>();
    private List<EventSerialize> processors = new ArrayList<EventSerialize>();

    public Expansion(){}

    public Expansion(String expansionName)
    {
        name = expansionName;
    }

    public Expansion addConditions(ConditionSet... conditionSets)
    {
        Collections.addAll(conditions, conditionSets);
        return this;
    }

    public Expansion addSounds(SoundSet... soundSets)
    {
        Collections.addAll(sounds, soundSets);
        return this;
    }

    public Expansion addProcessors(EventSerialize... eventSerializes)
    {
        Collections.addAll(processors, eventSerializes);
        return this;
    }

    public void register(MAtmos mAtmos)
    {
        MAtmos.log("Registering expansion: " + name + ". Conditions: " + conditions.size() + ", SoundSets: " + sounds.size() + ", Processors: " + processors.size());
        for (ConditionSet conditionSet : conditions)
        {
            mAtmos.dataManager.registerConditionSet(conditionSet);
        }
        for (SoundSet soundSet : sounds)
        {
            mAtmos.dataManager.registerSoundSet(soundSet);
        }
        for (EventSerialize eventSerialize : processors)
        {
            Optional<EventProcessor> optional = EventProcessor.of(name, eventSerialize, mAtmos);
            if (optional.isPresent())
            {
                mAtmos.expansionManager.addEventProcessor(optional.get());
            }
        }
    }

    public static void toJson(File parentDir, Expansion expansion)
    {
        if (!parentDir.exists() && parentDir.mkdirs())
        {
            MAtmos.log("Creating new directory: " + parentDir.getPath());
        }
        File outFile = new File(parentDir, expansion.name + ".json");
        try
        {
            if (!outFile.exists() && outFile.createNewFile())
            {
                MAtmos.log("Creating new file: " + outFile.getPath());
            }
            MAtmos.log("Writing " + expansion.name + " to file " + outFile.getName());
            FileWriter writer = new FileWriter(outFile);
            writer.write(gson.toJson(expansion));
            writer.flush();
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Optional<Expansion> fromJson(Reader reader)
    {
        try
        {
            Expansion expansion = gson.fromJson(reader, Expansion.class);
            reader.close();
            return Optional.of(expansion);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Optional.absent();
    }
}
