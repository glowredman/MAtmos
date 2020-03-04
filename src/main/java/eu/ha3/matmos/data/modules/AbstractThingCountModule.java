package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.core.sheet.DataPackage;

public abstract class AbstractThingCountModule<T> extends ModuleProcessor {

    public AbstractThingCountModule(DataPackage data, String name) {
        super(data, name);
        // TODO Auto-generated constructor stub
    }

    public AbstractThingCountModule(DataPackage data, String name, boolean doNotUseDelta) {
        super(data,  name, doNotUseDelta);
    }

    public abstract void increment(T thing);
    
    public abstract void increment(T thing, int amount);
    
    // for debugging
    public abstract int get(T thing);

    protected abstract void count();

    public abstract void apply();
    
}
