package eu.ha3.matmos.serialisation.expansion;

import java.util.Set;
import java.util.TreeSet;

import com.google.gson.annotations.Expose;

public class SerialList {
    @Expose
    public Set<String> entries = new TreeSet<>();
}
