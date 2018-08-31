package eu.ha3.matmos.editor;

import java.util.HashMap;
import java.util.Map;

import eu.ha3.matmos.serialisation.expansion.SerialCondition;
import eu.ha3.matmos.serialisation.expansion.SerialDynamic;
import eu.ha3.matmos.serialisation.expansion.SerialEvent;
import eu.ha3.matmos.serialisation.expansion.SerialList;
import eu.ha3.matmos.serialisation.expansion.SerialMachine;
import eu.ha3.matmos.serialisation.expansion.SerialSet;

public enum KnowledgeItemType {
    CONDITION("Condition", SerialCondition.class),
    SET("Set", SerialSet.class),
    MACHINE("Machine", SerialMachine.class),
    LIST("List", SerialList.class),
    DYNAMIC("Dynamic", SerialDynamic.class),
    EVENT("Event", SerialEvent.class);

    private String name;
    private Class<?> serialClass;

    private static final Map<Class<?>, KnowledgeItemType> fromSerialClass = new HashMap<>();

    static {
        for (KnowledgeItemType kit : KnowledgeItemType.values()) {
            fromSerialClass.put(kit.getSerialClass(), kit);
        }
    }

    public static KnowledgeItemType fromSerialClass(Class<?> klass) {
        if (!fromSerialClass.containsKey(klass)) {
            return null;
        }

        return fromSerialClass.get(klass);
    }

    private KnowledgeItemType(String name, Class<?> serialClass) {
        this.name = name;
        this.serialClass = serialClass;
    }

    public Class<?> getSerialClass() {
        return serialClass;
    }

    @Override
    public String toString() {
        return name;
    }
}
