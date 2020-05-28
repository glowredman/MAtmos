package eu.ha3.matmos.serialisation.expansion;

import com.google.gson.annotations.Expose;

public class SerialMachineStream {
    @Expose
    public String path = "";

    @Expose
    public float vol = 1;

    @Expose
    public float pitch = 1;

    @Expose
    public boolean looping;

    @Expose
    public boolean pause;

    @Expose
    public boolean underwater;
}
