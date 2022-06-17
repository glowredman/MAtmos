package eu.ha3.matmos.core.sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericSheet implements Sheet {
    protected final Map<String, String> values;
    protected final Map<String, Integer> versions;

    private String def = "_ENTRY_NOT_DEFINED";

    public GenericSheet() {
        values = new HashMap<>();
        versions = new HashMap<>();
    }

    @Override
    public String get(String key) {
        return values.containsKey(key) ? values.get(key) : def;
    }

    @Override
    public void set(String key, String value) {
        if (!value.equals(values.get(key))) {
            int ver = versions.containsKey(key) ? versions.get(key) : -1;
            values.put(key, value);
            versions.put(key, ver + 1);
        }
    }

    @Override
    public int version(String pos) {
        if (versions.containsKey(pos)) {
            return versions.get(pos);
        }

        return -1;
    }

    @Override
    public boolean exists(String key) {
        return values.containsKey(key);
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public void setDefaultValue(String def) {
        this.def = def;
    }

    @Override
    public String getDefaultValue() {
        return def;
    }
}
