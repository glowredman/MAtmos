package eu.ha3.matmos.serialisation.expansion;

import java.util.Set;
import java.util.TreeSet;

import com.google.gson.annotations.Expose;

public class SerialSet {
    @Expose
    public Set<String> yes = new TreeSet<>();

    @Expose
    public Set<String> no = new TreeSet<>();
}
