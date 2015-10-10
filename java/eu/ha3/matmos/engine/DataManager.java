package eu.ha3.matmos.engine;

import com.google.common.base.Optional;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.engine.event.EventProcessor;
import eu.ha3.matmos.game.gatherer.DataGatherer;
import eu.ha3.matmos.game.scanner.Scanner;

import java.util.*;

/**
 * @author dags_ <dags@dags.me>
 */

public class DataManager
{
    public static long processTime = 0L;

    public final Map<String, Scanner> scanners = new HashMap<String, Scanner>();
    public final Map<String, Data<Number>> numData = new HashMap<String, Data<Number>>();
    public final Map<String, Data<String>> stringData = new HashMap<String, Data<String>>();
    public final Map<String, Data<Boolean>> boolData = new HashMap<String, Data<Boolean>>();

    public final Map<String, ConditionSet> conditions = new HashMap<String, ConditionSet>();
    private final Map<String, SoundSet> soundSets = new HashMap<String, SoundSet>();

    private final List<DataGatherer> dataGatherers = new ArrayList<DataGatherer>();
    private final List<EventProcessor> eventProcessors = new ArrayList<EventProcessor>();

    public final Map<String, Boolean> active = new HashMap<String, Boolean>();

    private boolean flipFlop = false;

    public void process()
    {
        long start = System.currentTimeMillis();
        flipFlop = !flipFlop;
        for (DataGatherer gatherer : dataGatherers)
        {
            gatherer.update();
        }
        scanners.get("scan.entity").scan();
        scanners.get(flipFlop ? "scan.small" : "scan.large").scan();
        for (EventProcessor eventProcessor : eventProcessors)
        {
            eventProcessor.process();
            active.putAll(eventProcessor.getActive());
        }
        if (!flipFlop)
            processTime = System.currentTimeMillis() - start;
    }

    private <T> Optional<T> get(String name, Map<String, T> map)
    {
        name = name.toLowerCase();
        if (map.containsKey(name))
        {
            return Optional.of(map.get(name));
        }
        return Optional.absent();
    }

    private <T> void register(String key, T value, Map<String, T> map)
    {
        map.put(key.toLowerCase(), value);
    }

    public void addDataGatherer(DataGatherer gatherer)
    {
        dataGatherers.add(gatherer);
    }

    public void addEventProcessor(EventProcessor eventProcessor)
    {
        eventProcessors.add(eventProcessor);
    }

    public void registerScanner(Scanner scanner)
    {
        register(scanner.displayId(), scanner, scanners);
    }

    public void registerNum(String key, Data<Number> data)
    {
        register(key, data, numData);
    }

    public void registerString(String key, Data<String> data)
    {
        register(key, data, stringData);
    }

    public void registerBool(String key, Data<Boolean> data)
    {
        register(key, data, boolData);
    }

    public void registerConditionSet(ConditionSet conditionSet)
    {
        register(conditionSet.getName(), conditionSet, conditions);
    }

    public void registerSoundSet(SoundSet soundSet)
    {
        register(soundSet.getName(), soundSet, soundSets);
    }

    public Optional<Scanner> getScanner(String name)
    {
        return get(name, scanners);
    }

    public Optional<SoundSet> getSoundSet(String name)
    {
        return get(name, soundSets);
    }

    public Optional<ConditionSet> getConditionSet(String name)
    {
        return get(name, conditions);
    }

    public Optional<Data<Number>> getNumData(String key)
    {
        return get(key, numData);
    }

    public Optional<Data<String>> getStringData(String key)
    {
        return get(key, stringData);
    }

    public Optional<Data<Boolean>> getBoolData(String key)
    {
        return get(key, boolData);
    }

    public List<String> findDataKey(String match)
    {
        match = match.toLowerCase().trim();
        Set<String> matchSet = new HashSet<String>();
        addMatches(match, scanners.keySet(), matchSet);
        addMatches(match, numData.keySet(), matchSet);
        addMatches(match, stringData.keySet(), matchSet);
        addMatches(match, boolData.keySet(), matchSet);
        List<String> results = new ArrayList<String>(matchSet);
        Collections.sort(results);
        return results;
    }

    private void addMatches(String find, Collection<String> lookIn, Collection<String> matches)
    {
        int fromIndex = find.endsWith(".") ? find.length() : find.length() - 1;
        for (String s : lookIn)
        {
            if (s.startsWith(find))
            {
                int index = s.indexOf('.', fromIndex) + 1;
                if (index > 0)
                    matches.add(s.substring(0, index));
                else
                    matches.add(s);
            }
        }
    }

    public String dataKeyType(String key)
    {
        key = key.toLowerCase().trim();
        if (numData.containsKey(key))
            return "number";
        if (stringData.containsKey(key))
            return "string";
        if (boolData.containsKey(key))
            return "boolean";
        if (key.startsWith("scan.small") || key.startsWith("scan.large"))
            return "volume_scan";
        if (key.startsWith("scan.entity"))
            return "entity_scan";
        return "invalid";
    }

    private String findShortestOccurance(String match, String currentShortest, Collection<String> collection)
    {
        for (String s : collection)
        {
            if (s.startsWith(match) && (currentShortest.equals("") || s.length() > currentShortest.length()))
            {
                currentShortest = s;
            }
        }
        return currentShortest;
    }
}
