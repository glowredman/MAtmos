package eu.ha3.matmos.serialisation.module;

import java.util.Map;
import java.util.TreeMap;

import com.google.gson.annotations.Expose;

//TODO: Unused
@Deprecated
public class MSerialRoot {
    @Expose
    public Map<String, Module> module = new TreeMap<String, Module>();
    
    @Expose
    public Map<String, Agency> agency = new TreeMap<String, Agency>();
    
    public static class Agency {
        @Expose
        public Map<String, String> entries = new TreeMap<String, String>();
    }
    
    public static class Module {
        
        @Expose
        public String reference;
        
        @Expose
        public boolean delta;
        
        @Expose
        public String agency;
    }
}
