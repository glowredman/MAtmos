package eu.ha3.matmos.engine;

import com.google.common.base.Optional;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.game.gatherer.DataGatherer;
import eu.ha3.matmos.game.scanner.Scanner;

import java.util.*;

/**
 * @author dags_ <dags@dags.me>
 */

public class DataManager
{
    private boolean flipFlop = false;

    public final Map<String, Scanner> scanners = new HashMap<String, Scanner>();
    public final Map<String, Data<Number>> numData = new HashMap<String, Data<Number>>();
    public final Map<String, Data<String>> stringData = new HashMap<String, Data<String>>();
    public final Map<String, Data<Boolean>> boolData = new HashMap<String, Data<Boolean>>();

    public final Map<String, ConditionSet> conditions = new HashMap<String, ConditionSet>();
    public final Map<String, SoundSet> soundSets = new HashMap<String, SoundSet>();

    private final List<DataGatherer> dataGatherers = new ArrayList<DataGatherer>();

    public void process()
    {
        long start = System.currentTimeMillis();
        flipFlop = !flipFlop;
        for (DataGatherer gatherer : dataGatherers)
        {
            gatherer.update();
        }
        scanners.get("scan.entity").scan();
        scanners.get(flipFlop ? "scan.block.small" : "scan.block.large").scan();
    }

    public void wipe()
    {
        flipFlop = false;
        scanners.clear();
        numData.clear();
        stringData.clear();
        boolData.clear();
        conditions.clear();
        soundSets.clear();
        dataGatherers.clear();
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

    public List<String> findMatches(String in)
    {
        Set<String> results = new HashSet<String>();
        addMatches(in, results, scanners.keySet());
        addMatches(in, results, numData.keySet());
        addMatches(in, results, stringData.keySet());
        addMatches(in, results, boolData.keySet());
        List<String> ordered = new ArrayList<String>(results);
        Collections.sort(ordered);
        return ordered;
    }

    private void addMatches(String input, Collection<String> results, Collection<String> keys)
    {
        for (String key : keys)
        {
            if (key.length() < input.length())
            {
                continue;
            }
            boolean match = true;
            for (int i = 0; i < input.length(); i++)
            {
                if (input.charAt(i) !=key.charAt(i))
                {
                    match = false;
                    break;
                }
            }
            if (match)
            {
                int next = key.indexOf(".", input.length());
                next = next > 0 ? next : key.length();
                results.add(key.substring(input.length(), next));
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
}
