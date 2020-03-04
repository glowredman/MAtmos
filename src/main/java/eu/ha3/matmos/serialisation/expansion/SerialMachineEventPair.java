package eu.ha3.matmos.serialisation.expansion;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class SerialMachineEventPair {
    @Expose
    public List<SerialMachineEvent> indoor = new ArrayList<>();
    
    @Expose
    public List<SerialMachineEvent> outdoor = new ArrayList<>();
}
