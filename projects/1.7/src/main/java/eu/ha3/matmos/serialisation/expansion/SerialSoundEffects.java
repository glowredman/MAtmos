package eu.ha3.matmos.serialisation.expansion;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.Expose;

public class SerialSoundEffects {
    @Expose
    public Map<String, SerialSFXBlockChange> blockchange = new TreeMap<>();

}