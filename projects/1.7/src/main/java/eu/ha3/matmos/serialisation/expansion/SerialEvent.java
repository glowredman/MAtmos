package eu.ha3.matmos.serialisation.expansion;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class SerialEvent {
    @Expose
    public float vol_min = 1;

    @Expose
    public float vol_max = 1;

    @Expose
    public float pitch_min = 1;

    @Expose
    public float pitch_max = 1;

    @Expose
    public int distance = 0;

    @Expose
    public List<String> path = new ArrayList<>();
}
