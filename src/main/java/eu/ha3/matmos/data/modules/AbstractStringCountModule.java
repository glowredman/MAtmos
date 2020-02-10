package eu.ha3.matmos.data.modules;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import eu.ha3.matmos.core.sheet.DataPackage;

/**
 * An abstract module that specializes in counting things in one pass.
 *
 * @author Hurry
 */
public abstract class AbstractStringCountModule extends AbstractThingCountModule<String> {
    private Set<String> oldThings = new LinkedHashSet<>();
    private Set<String> newThings = new LinkedHashSet<>();

    protected Map<String, Integer> things = new HashMap<>();

    public AbstractStringCountModule(DataPackage data, String name) {
        this(data, name, false);
    }

    public AbstractStringCountModule(DataPackage data, String name, boolean doNotUseDelta) {
        super(data, name, doNotUseDelta);

        data.getSheet(name).setDefaultValue("0");
        if (!doNotUseDelta) {
            data.getSheet(name + DELTA_SUFFIX).setDefaultValue("0");
        }
    }

    @Override
    protected void doProcess() {
        count();
        apply();
    }

    public void increment(String thing) {
        things.put(thing, things.containsKey(thing) ? things.get(thing) + 1 : 1);
    }
    
    // for debugging
    public int get(String thing) {
            return this.things.get(thing);
    }

    protected abstract void count();

    public void apply() {
        for (Entry<String, Integer> entry : things.entrySet()) {
            this.setValue(entry.getKey(), entry.getValue());
        }

        newThings.addAll(things.keySet()); // add all new string
        things.clear();

        // Reset all missing string to zero
        oldThings.removeAll(newThings);
        for (String missing : oldThings) {
            setValue(missing, 0);
        }
        oldThings.clear();

        // The following code means
        // oldThings <- newThings
        // newThings <- (empty set)
        Set<String> anEmptySet = oldThings;
        oldThings = newThings;
        newThings = anEmptySet;
    }

}
