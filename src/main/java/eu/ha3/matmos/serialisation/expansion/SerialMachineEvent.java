package eu.ha3.matmos.serialisation.expansion;

import com.google.gson.annotations.Expose;

public class SerialMachineEvent {
    @Expose
    public String event = "";

    @Expose
    public float vol_mod = 1;

    @Expose
    public float pitch_mod = 1;

    @Expose
    public float delay_min = 0;

    @Expose
    public float delay_max = 864000;

    @Expose
    public float delay_start = -1;
}
