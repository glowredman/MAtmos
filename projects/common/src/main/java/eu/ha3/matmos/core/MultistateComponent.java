package eu.ha3.matmos.core;

public abstract class MultistateComponent extends Component implements Stated, Evaluated {
    protected boolean isActive;

    public MultistateComponent(String name) {
        super(name);
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
}
