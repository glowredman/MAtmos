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
    public List<SerialMachineEvent> event = null;

    @Expose
    public SerialMachineEventPair event_pair;

    @Expose
    public Object stream;

    @Expose
    public SerialMachineStreamPair stream_pair;

    @Expose
    public boolean play_deep_indoors;
}
