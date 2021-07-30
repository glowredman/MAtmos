package eu.ha3.matmos.serialisation.expansion;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.Expose;

public class SerialRoot {
    @Expose
    public Map<String, SerialCondition> condition = new TreeMap<>();

    @Expose
    public Map<String, SerialSet> set = new TreeMap<>();

    @Expose
    public Map<String, SerialMachine> machine = new TreeMap<>();

    @Expose
    public Map<String, SerialEvent> event = new TreeMap<>();

    @Expose
    public Map<String, SerialList> list = new TreeMap<>();

    @Expose
    public Map<String, SerialDynamic> dynamic = new TreeMap<>();

    @Expose
    public SerialSoundEffects soundeffects;
}
