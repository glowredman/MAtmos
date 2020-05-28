package eu.ha3.matmos.core.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.Dependable;
import eu.ha3.matmos.core.MultistateComponent;
import eu.ha3.matmos.core.Provider;
import eu.ha3.matmos.core.Simulated;
import eu.ha3.matmos.core.StreamInformation;
import eu.ha3.matmos.core.event.TimedEventInformation;

public class Machine extends MultistateComponent
        implements Dependable, Simulated, Overrided, VisualizedSpecialDependencies {
    private final List<String> allow;
    private final List<String> restrict;
    private final TimedEventInformation timed;
    private final List<StreamInformation> streams;

    private final Provider<Junction> provider;

    private boolean overrideUnderway;
    private boolean overrideState;

    private final Collection<String> dependencies;

    public Machine(String name, Provider<Junction> provider, List<String> allow, List<String> restrict,
            TimedEventInformation timed, List<StreamInformation> streams) {
        super(name);
        this.provider = provider;

        this.allow = allow;
        this.restrict = restrict;
        this.timed = timed;
        this.streams = streams;

        dependencies = new TreeSet<>();
        dependencies.addAll(allow);
        dependencies.addAll(restrict);
    }

    @Override
    public void simulate() {
        if (timed != null) {
            timed.simulate();
        }
        for (StreamInformation stream : streams) {
            stream.simulate();
        }
    }

    @Override
    public void evaluate() {
        boolean previous = isActive;
        isActive = testIfTrue();

        if (previous != isActive) {
            incrementVersion();
            if (timed != null) {
                timed.evaluate();
            }
            for (StreamInformation stream : streams) {
                stream.evaluate();
            }

            Matmos.LOGGER.debug("M: " + getName() + " -> " + isActive);
        }
    }

    private boolean testIfTrue() {
        if (overrideUnderway) {
            return overrideState;
        }

        boolean isTrue = false;

        Iterator<String> iterAllow = allow.iterator();
        while (!isTrue && iterAllow.hasNext()) {
            String junction = iterAllow.next();

            if (provider.exists(junction) && provider.get(junction).isActive()) {
                // If any Allows is true, it's true (exit the loop)
                isTrue = true;
            }
        }

        if (!isTrue) {
            return false;
        }

        /// Unless...

        Iterator<String> iterRestrict = restrict.iterator();
        while (isTrue && iterRestrict.hasNext()) {
            String junction = iterRestrict.next();

            if (provider.exists(junction) && provider.get(junction).isActive()) {
                // If any Restrict is true, it's false
                isTrue = false;
            }
        }

        return isTrue;
    }

    @Override
    public void overrideForceOn() {
        overrideUnderway = true;
        overrideState = true;
        evaluate();
    }

    @Override
    public void overrideForceOff() {
        overrideUnderway = true;
        overrideState = false;
        evaluate();
    }

    @Override
    public void overrideFinish() {
        overrideUnderway = false;
        evaluate();
    }

    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String getFeed() {
        return "";
    }

    @Override
    public Collection<String> getSpecialDependencies(String type) {
        if (type.equals("allow")) {
            return allow;
        } else if (type.equals("restrict")) {
            return restrict;
        }

        return new HashSet<>();
    }
}
