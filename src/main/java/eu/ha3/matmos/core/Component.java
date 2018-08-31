package eu.ha3.matmos.core;

import java.util.HashSet;
import java.util.Set;

/* x-placeholder */

public abstract class Component implements Named, Versionned {
    private final String name;
    private final Set<VersionListener> listeners;
    private int version;

    public Component(String name) {
        this.name = name;
        listeners = new HashSet<>();
        version = -1;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[(" + this.getClass().toString() + ") " + name + "]";
    }

    @Override
    public int version() {
        return version;
    }

    @Override
    public void incrementVersion() {
        version = version + 1;

        for (VersionListener listener : listeners) {
            listener.onIncrement(this);
        }
    }

    @Override
    public void registerVersionListener(VersionListener listener) {
        listeners.add(listener);
    }
}
