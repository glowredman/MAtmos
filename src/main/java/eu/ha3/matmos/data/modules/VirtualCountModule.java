package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.core.sheet.DataPackage;

public class VirtualCountModule<T> extends AbstractThingCountModule<T> {

    public VirtualCountModule(DataPackage data, String name) {
        super(data, name);
    }

    public VirtualCountModule(DataPackage data, String name, boolean doNotUseDelta) {
        super(data, name, doNotUseDelta);
    }
    
    @Override
    protected void doProcess() {
    }

    @Override
    public void increment(T thing) {
        
    }
    
    @Override
    public void increment(T thing, int amount) {
        
    }

    @Override
    public int get(T thing) {
        return 0;
    }

    @Override
    public void count() {
        
    }

    @Override
    public void apply() {
        
    }
}
