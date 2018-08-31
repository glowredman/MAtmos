package eu.ha3.matmos.serialisation.expansion;

import java.util.Map;
import java.util.TreeMap;

/*
 * --filenotes-placeholder
 */

public class SerialRoot {
    public Map<String, SerialCondition> condition = new TreeMap<>();
    public Map<String, SerialSet> set = new TreeMap<>();
    public Map<String, SerialMachine> machine = new TreeMap<>();
    public Map<String, SerialEvent> event = new TreeMap<>();
    public Map<String, SerialList> list = new TreeMap<>();
    public Map<String, SerialDynamic> dynamic = new TreeMap<>();
}
