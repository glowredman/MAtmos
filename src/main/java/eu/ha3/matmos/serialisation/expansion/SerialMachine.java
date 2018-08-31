package eu.ha3.matmos.serialisation.expansion;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/*
 * --filenotes-placeholder
 */

public class SerialMachine {
    public Set<String> allow = new TreeSet<>();
    public Set<String> restrict = new TreeSet<>();

    public float fadein;
    public float fadeout;
    public float delay_fadein;
    public float delay_fadeout;

    public List<SerialMachineEvent> event = new ArrayList<>();
    public SerialMachineStream stream = null;
}
