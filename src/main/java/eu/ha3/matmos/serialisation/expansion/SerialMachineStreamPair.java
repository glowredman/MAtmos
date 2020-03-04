package eu.ha3.matmos.serialisation.expansion;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class SerialMachineStreamPair {
    @Expose
    public List<SerialMachineStream> indoor = new ArrayList<>();
    
    @Expose
    public List<SerialMachineStream> outdoor = new ArrayList<>();
}
