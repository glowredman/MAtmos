package eu.ha3.matmos.serialisation.expansion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.google.gson.annotations.Expose;

public class SerialMachine {
    @Expose
    public Set<String> allow = new TreeSet<>();

    @Expose
    public Set<String> restrict = new TreeSet<>();

    @Expose
    public float fadein;

    @Expose
    public float fadeout;

    @Expose
    public float delay_fadein;

    @Expose
    public float delay_fadeout;

    @Expose
    public List<SerialMachineEvent> event = new ArrayList<>();

    @Expose
    public SerialMachineStream stream;
}
