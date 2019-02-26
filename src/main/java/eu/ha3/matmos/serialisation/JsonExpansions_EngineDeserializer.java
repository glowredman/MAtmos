package eu.ha3.matmos.serialisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import eu.ha3.matmos.core.Dynamic;
import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.Named;
import eu.ha3.matmos.core.Operator;
import eu.ha3.matmos.core.Possibilities;
import eu.ha3.matmos.core.ProviderCollection;
import eu.ha3.matmos.core.StreamInformation;
import eu.ha3.matmos.core.event.Event;
import eu.ha3.matmos.core.event.TimedEvent;
import eu.ha3.matmos.core.event.TimedEventInformation;
import eu.ha3.matmos.core.expansion.ExpansionIdentity;
import eu.ha3.matmos.core.logic.Condition;
import eu.ha3.matmos.core.logic.Junction;
import eu.ha3.matmos.core.logic.Machine;
import eu.ha3.matmos.core.sheet.SheetEntry;
import eu.ha3.matmos.core.sheet.SheetIndex;
import eu.ha3.matmos.serialisation.expansion.SerialCondition;
import eu.ha3.matmos.serialisation.expansion.SerialDynamic;
import eu.ha3.matmos.serialisation.expansion.SerialDynamicSheetIndex;
import eu.ha3.matmos.serialisation.expansion.SerialEvent;
import eu.ha3.matmos.serialisation.expansion.SerialList;
import eu.ha3.matmos.serialisation.expansion.SerialMachine;
import eu.ha3.matmos.serialisation.expansion.SerialMachineEvent;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;
import eu.ha3.matmos.serialisation.expansion.SerialSet;

public class JsonExpansions_EngineDeserializer {
    private static final Gson gson = new Gson();

    private List<Named> elements;
    private Knowledge knowledgeWorkstation;
    private ProviderCollection providers;

    private String UID;

    public boolean loadJson(String jasonString, ExpansionIdentity identity, Knowledge knowledge) {
        try {
            parseJsonUnsafe(jasonString, identity, knowledge);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadSerial(SerialRoot root, ExpansionIdentity identity, Knowledge knowledge) {
        prepare(identity, knowledge);
        continueFromSerial(root, identity, knowledge);
    }

    public SerialRoot jsonToSerial(String jasonString) {
        return gson.fromJson(new JsonParser().parse(jasonString).getAsJsonObject(), SerialRoot.class);
    }

    private void prepare(ExpansionIdentity identity, Knowledge knowledge) {
        UID = identity.getUniqueName();
        knowledgeWorkstation = knowledge;
        elements = new ArrayList<>();
        providers = knowledgeWorkstation.obtainProviders();
    }

    private void parseJsonUnsafe(String jasonString, ExpansionIdentity identity, Knowledge knowledge) {
        SerialRoot root = jsonToSerial(jasonString);
        loadSerial(root, identity, knowledge);
    }

    private void continueFromSerial(SerialRoot root, ExpansionIdentity identity, Knowledge knowledge) {
        if (root.dynamic != null) {
            for (Entry<String, SerialDynamic> entry : root.dynamic.entrySet()) {
                List<SheetIndex> sheetIndexes = new ArrayList<>();
                for (SerialDynamicSheetIndex eelt : entry.getValue().entries) {
                    sheetIndexes.add(new SheetEntry(eelt.sheet, eelt.index));
                }
                elements.add(new Dynamic(dynamicSheetHash(entry.getKey()), providers.getSheetCommander(), sheetIndexes));
            }
        }
        if (root.list != null) {
            for (Entry<String, SerialList> entry : root.list.entrySet()) {
                elements.add(new Possibilities(entry.getKey(), asList(entry.getValue().entries)));
            }
        }
        if (root.condition != null) {
            for (Entry<String, SerialCondition> entry : root.condition.entrySet()) {
                String indexNotComputed = entry.getValue().index;
                if (entry.getValue().sheet.equals(Dynamic.DEDICATED_SHEET)) {
                    indexNotComputed = dynamicSheetHash(indexNotComputed);
                }

                elements.add(new Condition(
                        entry.getKey(), providers.getSheetCommander(), new SheetEntry(entry.getValue().sheet, indexNotComputed),
                        Operator.fromSerializedForm(entry.getValue().symbol), entry.getValue().value));
            }
        }
        if (root.set != null) {
            for (Entry<String, SerialSet> entry : root.set.entrySet()) {
                elements.add(new Junction(entry.getKey(), providers.getCondition(), asList(entry.getValue().yes), asList(entry.getValue().no)));
            }
        }
        if (root.event != null) {
            for (Entry<String, SerialEvent> entry : root.event.entrySet()) {
                elements.add(new Event(
                        entry.getKey(), providers.getSoundRelay(), asList(entry.getValue().path),
                        entry.getValue().vol_min, entry.getValue().vol_max, entry.getValue().pitch_min,
                        entry.getValue().pitch_max, entry.getValue().distance));
            }
        }
        if (root.machine != null) {
            for (Entry<String, SerialMachine> entry : root.machine.entrySet()) {
                SerialMachine serial = entry.getValue();

                List<TimedEvent> events = new ArrayList<>();

                if (serial.event != null) {
                    for (SerialMachineEvent eelt : serial.event) {
                        events.add(new TimedEvent(providers.getEvent(), eelt));
                    }
                }

                StreamInformation stream = null;
                if (serial.stream != null) {
                    stream = new StreamInformation(entry.getKey(), providers.getMachine(), providers.getReferenceTime(), providers.getSoundRelay(), serial.stream.path, serial.stream.vol, serial.stream.pitch, serial.delay_fadein, serial.delay_fadeout, serial.fadein, serial.fadeout, serial.stream.looping, serial.stream.pause);
                }

                TimedEventInformation tie = null;
                if (serial.event.size() > 0) {
                    tie = new TimedEventInformation(entry.getKey(), providers.getMachine(), providers.getReferenceTime(), events, serial.delay_fadein, serial.delay_fadeout, serial.fadein, serial.fadeout);
                }

                if (tie != null || stream != null) {
                    Named element = new Machine(entry.getKey(), providers.getJunction(), asList(serial.allow), asList(serial.restrict), tie, stream);
                    elements.add(element);
                }
            }
        }

        if (elements.size() > 0) {
            knowledgeWorkstation.addKnowledge(elements);
        }
    }

    private String dynamicSheetHash(String name) {
        return UID.hashCode() % 1000 + "_" + name;
    }

    private <T> List<T> asList(Collection<T> thing) {
        return new ArrayList<>(thing);
    }
}
