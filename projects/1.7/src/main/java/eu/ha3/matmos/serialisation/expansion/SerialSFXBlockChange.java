package eu.ha3.matmos.serialisation.expansion;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class SerialSFXBlockChange {
    @Expose
    public List<String> when = new LinkedList<String>();

    @Expose
    public List<String> blocks = new LinkedList<String>();

    @Expose
    public String sound;

    @Expose
    public float pitch_min = 1, pitch_max = 1, vol_min = 1, vol_max = 1;
}
