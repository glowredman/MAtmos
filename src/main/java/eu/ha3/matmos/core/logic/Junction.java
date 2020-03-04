package eu.ha3.matmos.core.logic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.Dependable;
import eu.ha3.matmos.core.MultistateComponent;
import eu.ha3.matmos.core.Named;
import eu.ha3.matmos.core.Provider;

public class Junction extends MultistateComponent implements Dependable, VisualizedSpecialDependencies {
    private final List<String> yes;
    private final List<String> no;

    private final Provider<Condition> provider;

    private final Collection<String> dependencies;
    
    private final boolean inverted;

    public Junction(String name, Provider<Condition> provider, List<String> yes, List<String> no, boolean inverted) {
        super(name);
        this.provider = provider;

        this.yes = yes;
        this.no = no;
        this.inverted = inverted;

        dependencies = new TreeSet<>();
        dependencies.addAll(yes);
        dependencies.addAll(no);
    }
    
    public Junction(String name, Provider<Condition> provider, List<String> yes, List<String> no) {
        this(name, provider, yes, no, false);
    }
    
    @Override
    public void evaluate() {
        boolean pre = isActive;
        isActive = testIfTrue();
        
        if(inverted) {
            isActive = !isActive;
        }

        if (pre != isActive) {
            Matmos.LOGGER.debug("S: " + getName() + " -> " + isActive);

            incrementVersion();
        }
    }

    private boolean testIfTrue() {
        boolean isTrue = true;

        Iterator<String> iterYes = yes.iterator();
        while (isTrue && iterYes.hasNext()) {
            String yes = iterYes.next();
            if (!provider.exists(yes) || !provider.get(yes).isActive()) {
                isTrue = false;
            }
        }

        if (!isTrue) {
            return false;
        }

        Iterator<String> iterNo = no.iterator();
        while (isTrue && iterNo.hasNext()) {
            String no = iterNo.next();
            if (!provider.exists(no) || provider.get(no).isActive()) {
                isTrue = false;
            }
        }
        return isTrue;
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
        if (type.equals("yes")) {
            return yes;
        } else if (type.equals("no")) {
            return no;
        }

        return new HashSet<>();
    }

    public Named getInverted() {
        return new Junction("!" + getName(), provider, yes, no, !inverted);
    }
}
