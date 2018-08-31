package eu.ha3.matmos.editor;

import eu.ha3.matmos.serialisation.expansion.*;

import java.util.HashSet;
import java.util.Set;

public class PurgeOperator {
    public void purgeLogic(SerialRoot root) {
        Set<String> unusedSets = new HashSet<String>(root.set.keySet());
        for (SerialMachine machine : root.machine.values()) {
            unusedSets.removeAll(machine.allow);
            unusedSets.removeAll(machine.restrict);
        }

        for (String unusedSet : unusedSets) {
            root.set.remove(unusedSet);
        }

        Set<String> unusedConditions = new HashSet<String>(root.condition.keySet());

        for (SerialSet set : root.set.values()) {
            unusedConditions.removeAll(set.yes);
            unusedConditions.removeAll(set.no);
        }

        for (String unusedCondition : unusedConditions) {
            root.condition.remove(unusedCondition);
        }
    }

    public void purgeEvents(SerialRoot root) {
        Set<String> unusedEvents = new HashSet<String>(root.event.keySet());
        for (SerialMachine machine : root.machine.values()) {
            for (SerialMachineEvent machineEvent : machine.event) {
                unusedEvents.remove(machineEvent.event);
            }
        }

        for (String unusedEvent : unusedEvents) {
            root.event.remove(unusedEvent);
        }
    }
}
