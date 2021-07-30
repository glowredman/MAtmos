package eu.ha3.matmos.core.expansion;

public interface Stable {
    boolean isActivated();

    void activate();

    void deactivate();

    void dispose();

    void interrupt();
}
