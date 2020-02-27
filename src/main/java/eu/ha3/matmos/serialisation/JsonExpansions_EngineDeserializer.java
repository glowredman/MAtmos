package eu.ha3.matmos.serialisation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import eu.ha3.matmos.Matmos;
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
import eu.ha3.matmos.serialisation.expansion.SerialMachineStream;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;
import eu.ha3.matmos.serialisation.expansion.SerialSet;

public class JsonExpansions_EngineDeserializer {
    private static final Gson gson = new Gson();

    private List<Named> elements;
    private Knowledge knowledgeWorkstation;
    private ProviderCollection providers;

    private String UID;

    public Exception loadJson(String jasonString, ExpansionIdentity identity, Knowledge knowledge) {
        try {
            parseJsonUnsafe(jasonString, identity, knowledge);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return e;
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
                deserializeMachine(entry);
            }
        }

        if (elements.size() > 0) {
            knowledgeWorkstation.addKnowledge(elements);
        }
    }
    
    private void deserializeMachine(Entry<String, SerialMachine> entry) {
        SerialMachine serial = entry.getValue();
        
        List<SerialMachineEvent>[] serialEventLists = getSerialMachineEventList(entry, serial);
        List<SerialMachineStream>[] serialStreamLists = getSerialMachineStreamLists(entry, serial);
        
        boolean doubleMachine = serialEventLists[1] != null || serialStreamLists[1] != null;//
        if(doubleMachine) {
            // If a machine doesn't have a list defined, copy the list from the other machine
            if(serialEventLists[0] == null) {
                serialEventLists[0] = serialEventLists[1];
            } else if(serialEventLists[1] == null) {
                serialEventLists[1] = serialEventLists[0];
            }
            
            if(serialStreamLists[0] == null) {
                serialStreamLists[0] = serialStreamLists[1];
            } else if(serialStreamLists[1] == null) {
                serialStreamLists[1] = serialStreamLists[0];
            }
        }
        
        for(int m = 0; m < (doubleMachine ? 2 : 1); m++) {
            MachineType type = !doubleMachine ? MachineType.NORMAL : (m == 0 ? MachineType.INDOOR : MachineType.OUTDOOR);
            deserializeMachineFromLists(entry, serial, serialEventLists[m], serialStreamLists[m], type);
        }
    }
    
    enum MachineType {NORMAL, INDOOR, OUTDOOR};
    
    private void deserializeMachineFromLists(Entry<String, SerialMachine> entry, SerialMachine serial,
            List<SerialMachineEvent> serialEventList, List<SerialMachineStream> serialStreamList, MachineType type) {
        ArrayList<TimedEvent> events = new ArrayList<>();
        if(serialEventList != null) {
            serialEventList.forEach(x -> {events.add(new TimedEvent(providers.getEvent(), x));});
        }
        
        ArrayList<StreamInformation> streamList = new ArrayList<StreamInformation>();
        
        boolean normal = type == MachineType.NORMAL;
        boolean indoor = type == MachineType.INDOOR;
        String machineName = entry.getKey();
        if(!normal) {
            machineName += (indoor ? " (Indoor)" : " (Outdoor)");
        }
        
        if(serialStreamList != null) {
            for(SerialMachineStream sms : serialStreamList) {
                streamList.add(new StreamInformation(machineName,
                        providers.getMachine(), providers.getReferenceTime(), providers.getSoundRelay(),
                        sms.path, sms.vol, sms.pitch,
                        serial.delay_fadein, serial.delay_fadeout, serial.fadein, serial.fadeout,
                        sms.looping, sms.pause, sms.underwater));
                
            }
        }
        
        TimedEventInformation tie = null;
        if (!events.isEmpty()) {
            tie = new TimedEventInformation(machineName, providers.getMachine(), providers.getReferenceTime(), events, serial.delay_fadein, serial.delay_fadeout, serial.fadein, serial.fadeout);
        }

        if (tie != null || streamList != null) {
            List<String> allowList = asList(serial.allow);
            
            List<String> restrictList = asList(serial.restrict);
            if(!normal) {
                if(!serial.play_deep_indoors) {
                    restrictList.add("_DEEP_INDOORS");
                    if(indoor) {
                        restrictList.add("_OUTDOORS");
                        restrictList.add("!_INDOORS");
                    } else {
                        restrictList.add("!_OUTDOORS");
                        restrictList.add("_INDOORS");
                    }
                    
                } else {
                    if(indoor) {
                        restrictList.add("_OUTDOORS");
                    } else {
                        restrictList.add("_INDOORS");
                        restrictList.add("_DEEP_INDOORS");
                    }
                }
            }
            
            Named element = new Machine(machineName, providers.getJunction(), allowList, restrictList, tie, streamList);
            elements.add(element);
        }
    }
    
    private List<SerialMachineEvent>[] getSerialMachineEventList(Entry<String, SerialMachine> entry, SerialMachine serial) {
        List<SerialMachineEvent>[] eventList = new ArrayList[2];
        
        if(serial.event_pair != null) {
            if (serial.event != null) {
                Matmos.LOGGER.warn("Machine " + entry.getKey() + " has both 'event' and 'event_pair' defined. Ignoring 'event'.");
            }
            
            eventList[0] = serial.event_pair.indoor;
            eventList[1] = serial.event_pair.outdoor;
        } else if (serial.event != null) {
            eventList[0] = serial.event;
        }
        
        return eventList;
    }
    
    private List<SerialMachineStream>[] getSerialMachineStreamLists(Entry<String, SerialMachine> entry, SerialMachine serial) {
        
        List<SerialMachineStream>[] smsLists = new ArrayList[2];
        
        if(serial.stream_pair!= null) {
            if (serial.stream != null) {
                Matmos.LOGGER.warn("Machine " + entry.getKey() + " has both 'stream' and 'stream_pair' defined. Ignoring 'stream'.");
            }
            
            smsLists[0] = serial.stream_pair.indoor;
            smsLists[1] = serial.stream_pair.outdoor;
        }
        
        if (serial.stream != null) {
            smsLists[0] = new ArrayList<>();
            
            List<Object> smsObjectList = new ArrayList<>();
            
            if(serial.stream instanceof Map) { // stream is an object
                smsObjectList.add(serial.stream);
            } else if(serial.stream instanceof List<?>) { // stream is a list of objects
                //smsObjectList = (List<Object>)serial.stream;
                for(Object o : (List<?>)serial.stream) {
                    smsObjectList.add(o);
                }
            }
            
            for(Object smsObject : smsObjectList) {
                SerialMachineStream sms = gson.fromJson(gson.toJson(smsObject), SerialMachineStream.class);
                smsLists[0].add(sms);
            }
            
        }
        return smsLists;
    }

    private String dynamicSheetHash(String name) {
        return UID.hashCode() % 1000 + "_" + name;
    }

    private <T> List<T> asList(Collection<T> thing) {
        return new ArrayList<>(thing);
    }
}
