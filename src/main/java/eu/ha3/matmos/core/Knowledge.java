package eu.ha3.matmos.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.event.Event;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;
import eu.ha3.matmos.core.expansion.ExpansionManager;
import eu.ha3.matmos.core.logic.Condition;
import eu.ha3.matmos.core.logic.Junction;
import eu.ha3.matmos.core.logic.Machine;
import eu.ha3.matmos.core.sfx.BlockChangeSound;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.Sheet;
import eu.ha3.matmos.core.sheet.SheetCommander;
import eu.ha3.matmos.core.sheet.SheetDataPackage;
import eu.ha3.matmos.core.sheet.SheetEntry;
import eu.ha3.matmos.core.sheet.SheetIndex;
import eu.ha3.matmos.util.BetterStreams;
import eu.ha3.mc.haddon.supporting.SupportsBlockChangeEvents;
import eu.ha3.mc.haddon.supporting.event.BlockChangeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;

/**
 * Stores a Knowledge.
 */
public class Knowledge implements Evaluated, Simulated {
    private DataPackage data;

    private final Map<String, Dynamic> dynamicMapped = new TreeMap<>();
    private final Map<String, PossibilityList> possibilityMapped = new TreeMap<>();
    private final Map<String, Condition> conditionMapped = new TreeMap<>();
    private final Map<String, Junction> junctionMapped = new TreeMap<>();
    private final Map<String, Machine> machineMapped = new TreeMap<>();
    private final Map<String, Event> eventMapped = new TreeMap<>();

    private final Map<String, BlockChangeSound> blockChangeMapped = new TreeMap<>();

    private final Map<String, String> conditionValueOverrides = new HashMap<>();
    
    private final SheetCommander<String> sheetCommander = new SheetCommander<String>() {
        @Override
        public int version(SheetIndex sheetIndex) {
            return data.getSheet(sheetIndex.getSheet()).version(sheetIndex.getIndex());
        }

        @Override
        public boolean listHas(String constantX, String value) {
            return possibilityMapped.containsKey(constantX) ? possibilityMapped.get(constantX).listHas(value) : false;
        }

        @Override
        public String get(SheetIndex sheetIndex) {
            return data.getSheet(sheetIndex.getSheet()).get(sheetIndex.getIndex());
        }

        @Override
        public boolean exists(SheetIndex sheetIndex) {
            return data.getSheet(sheetIndex.getSheet()).exists(sheetIndex.getIndex());
        }
    };

    private final Provider<Dynamic> dynamicProvider = new MappedProvider<>(dynamicMapped);
    private final Provider<Condition> conditionProvider = new MappedProvider<>(conditionMapped);
    private final Provider<Junction> junctionProvider = new MappedProvider<>(junctionMapped);
    private final Provider<Machine> machineProvider = new MappedProvider<>(machineMapped);
    private final Provider<Event> eventProvider = new MappedProvider<>(eventMapped);

    private final ProviderCollection providerCollection;

    //

    private final SoundRelay relay;

    public Knowledge(SoundRelay relay, ReferenceTime time) {
        this.relay = relay;

        providerCollection = new Providers(time, relay, sheetCommander, conditionProvider, junctionProvider,
                machineProvider, eventProvider, dynamicProvider);
    }

    public void setData(DataPackage data) {
        this.data = data;
    }

    public ProviderCollection obtainProviders() {
        return providerCollection;
    }

    public SheetCommander<String> obtainSheetCommander() {
        return sheetCommander;
    }

    public void addKnowledge(List<Named> namedThings) {
        for (Named n : namedThings) {
            if (n instanceof Condition) {
                conditionMapped.put(n.getName(), (Condition) n);
            } else if (n instanceof Junction) {
                junctionMapped.put(n.getName(), (Junction) n);
            } else if (n instanceof Machine) {
                machineMapped.put(n.getName(), (Machine) n);
            } else if (n instanceof Event) {
                eventMapped.put(n.getName(), (Event) n);
            } else if (n instanceof PossibilityList) {
                possibilityMapped.put(n.getName(), (PossibilityList) n);
            } else if (n instanceof Dynamic) {
                dynamicMapped.put(n.getName(), (Dynamic) n);
            } else if (n instanceof BlockChangeSound) {
                blockChangeMapped.put(n.getName(), (BlockChangeSound) n);
            } else {
                System.err.println("Cannot handle named element: " + n.getName() + " " + n.getClass());
            }
        }
    }

    public void addKnowledge(Knowledge other) {
        LinkedList<Named> things = new LinkedList<>();
        things.addAll(other.conditionMapped.values());
        things.addAll(other.junctionMapped.values());
        things.addAll(other.machineMapped.values());
        things.addAll(other.eventMapped.values());
        for (PossibilityList p : other.possibilityMapped.values()) {
            things.add((Named) p);
        }
        things.addAll(other.dynamicMapped.values());

        addKnowledge(things);
    }

    private void createInvertedJunctions() {
        Set<String> junctionsToInvert = new TreeSet<>();
        LinkedList<Named> newStuff = new LinkedList<>();

        for (String machineName : machineMapped.keySet()) {
            Machine m = machineMapped.get(machineName);
            for (String dep : m.getDependencies()) {
                if (dep.startsWith("!")) {
                    junctionsToInvert.add(dep.substring(1));
                }
            }
        }
        for (String junctionName : junctionsToInvert) {
            Junction junction = junctionMapped.get(junctionName);
            if (junction != null) {
                newStuff.add(junction.getInverted());
            } else {
                Matmos.LOGGER.warn("Missing junction: " + junctionName);
            }

        }
        addKnowledge(newStuff);
    }

    private void buildIDList() {
        Set<String> sheetIndexes = new HashSet<String>();
        Set<String> nameSet = new HashSet<String>();

        for (Condition condition : conditionMapped.values()) {
            sheetIndexes.add(condition.getIndex().getIndex());
        }
        for (Dynamic dynamic : dynamicMapped.values()) {
            for (SheetIndex si : dynamic.getIndexes()) {
                sheetIndexes.add(si.getIndex());
            }
        }
        for (PossibilityList possibility : possibilityMapped.values()) {
            for (String entry : possibility.getList()) {
                sheetIndexes.add(entry);
            }
        }

        for (String index : sheetIndexes) {
            if (index.contains("^")) {
                index = index.substring(0, index.indexOf('^'));
            }
            nameSet.add(index);
        }
        if(data instanceof SheetDataPackage) {
            ((SheetDataPackage)data).addReferencedIDs(nameSet.stream().map(s -> {
                int id = Block.getIdFromBlock(Block.getBlockFromName(s));
                if(id == -1) {
                    id = Item.getIdFromItem((Item)Item.itemRegistry.getObject(s));
                }
                return id;   
            }).collect(Collectors.toList()));
        }
    }

    public void compile() {
        buildIDList();

        createInvertedJunctions();

        purge(machineMapped, junctionMapped, "junctions");
        purge(junctionMapped, conditionMapped, "conditions");
    }

    /**
     * This method must return an object that can be modified afterwards by
     * something else.
     */
    public Set<String> calculateRequiredModules() {
        return BetterStreams.<Dependable>of(conditionMapped, dynamicMapped).flatten(a -> a.getDependencies()).asSet();
    }

    private void purge(Map<String, ? extends Dependable> superior, Map<String, ? extends Dependable> inferior,
            String inferiorName) {
        Set<String> requirements = new TreeSet<>();
        Set<String> unused = new TreeSet<>();
        Set<String> missing = new TreeSet<>();

        for (Dependable m : superior.values()) {
            requirements.addAll(m.getDependencies());
        }

        unused.addAll(inferior.keySet());
        unused.removeAll(requirements);
        unused.removeIf(s -> s.startsWith("_"));

        missing.addAll(requirements);
        missing.removeAll(inferior.keySet());

        if (missing.size() > 0) {
            Matmos.LOGGER.warn("Missing " + inferiorName + ": " + Arrays.toString(missing.toArray()));
        }

        if (unused.size() > 0) {
            Matmos.LOGGER.warn("Unused " + inferiorName + ": " + Arrays.toString(unused.toArray()));

            unused.forEach(inferior::remove);
        }
    }

    public void cacheSounds(ExpansionIdentity identity) {
        IResourcePack resourcePack = identity.getPack();

        eventMapped.values().forEach(event -> event.cacheSounds(resourcePack));
    }

    @Override
    public void simulate() {
        relay.routine();
        machineMapped.values().forEach(Machine::simulate);
    }

    @Override
    public void evaluate() {
        if (dynamicMapped.size() > 0) {
            Sheet dynamic = data.getSheet(Dynamic.DEDICATED_SHEET);
            for (Dynamic o : dynamicMapped.values()) {
                o.evaluate();
                dynamic.set(o.getName(), Long.toString(o.getInformation()));
            }
        }

        BetterStreams.<Evaluated>of(conditionMapped, junctionMapped, machineMapped).forEach(Evaluated::evaluate);
    }

    public void onBlockChanged(BlockChangeEvent event) {
        event.oldBlock = ExpansionManager.dealias(event.oldBlock, data);
        event.newBlock = ExpansionManager.dealias(event.newBlock, data);
        blockChangeMapped.forEach((k, v) -> v.onBlockChange(event));
    }

    public void setOverrideOff(boolean overrideOff) {
        machineMapped.forEach((s, m) -> {
            if (overrideOff) {
                m.overrideForceOff();
            } else {
                m.overrideFinish();
            }
        });
    }
    
    public boolean hasOverrideRainCondition() {
        return conditionMapped.values().stream().anyMatch(c -> 
            c.getIndex().getSheet().equals("meta_option") && 
            c.getIndex().getIndex().equals("override_rain"));
    }
    
    public void setConditionValueOverrides(Map<String, String> overrides) {
        conditionValueOverrides.clear();
        conditionValueOverrides.putAll(overrides);
    }
    
    public Map<String, String> getConditionValueOverrides() {
        return conditionValueOverrides;
    }

    // might be nicer to have this read from a json file
    public static List<Named> getBuiltins(ProviderCollection providers) {
        return Arrays.asList(
                new Condition("_RAYCAST_SCAN_OUTDOORS", providers.getSheetCommander(),
                        new SheetEntry("scan_raycast", ".is_outdoors"), Operator.EQUAL, "1"),
                new Condition("_FLOOD_SCAN_DEEP_INDOORS", providers.getSheetCommander(),
                        new SheetEntry("scan_air", ".is_near_surface"), Operator.EQUAL, "0"),
                new Junction("_DEEP_INDOORS", providers.getCondition(), Arrays.asList("_FLOOD_SCAN_DEEP_INDOORS"),
                        Arrays.asList("_RAYCAST_SCAN_OUTDOORS")),
                new Junction("_INDOORS", providers.getCondition(), Arrays.asList(),
                        Arrays.asList("_RAYCAST_SCAN_OUTDOORS", "_FLOOD_SCAN_DEEP_INDOORS")),
                new Junction("_OUTDOORS", providers.getCondition(), Arrays.asList("_RAYCAST_SCAN_OUTDOORS"),
                        Arrays.asList()));
    }
}
