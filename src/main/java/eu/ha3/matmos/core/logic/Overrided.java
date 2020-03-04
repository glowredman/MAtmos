package eu.ha3.matmos.core.logic;

public interface Overrided {
    /**
     * Bypass internal logic to force this on.
     */
    void overrideForceOn();

    /**
     * Bypass internal logic to force this off.
     */
    void overrideForceOff();

    /**
     * Stop bypassing internal logic.
     */
    void overrideFinish();
}
