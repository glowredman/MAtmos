package eu.ha3.matmos.core;

/*
 * --filenotes-placeholder
 */

public interface Versionned {
    /**
     * Gets the version of this. Starts at -1.
     *
     * @return
     */
    int version();

    /**
     * Increments the version.
     */
    void incrementVersion();

    /**
     * Registers a version listener that is called after incrementation.
     *
     * @param listener
     */
    void registerVersionListener(VersionListener listener);
}
