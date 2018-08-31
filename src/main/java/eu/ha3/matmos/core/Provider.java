package eu.ha3.matmos.core;

import java.util.Set;

/*
 * --filenotes-placeholder
 */

public interface Provider<T> {
    /**
     * Returns whether a named object exists.
     *
     * @param  name
     * @return
     */
    boolean exists(String name);

    /**
     * Returns a named object version from the provider, or -1 if it doesn't exist.
     *
     * @param  name
     * @return
     */
    int version(String name);

    /**
     * Returns an named object from the provider, or null if it doesn't exist.
     *
     * @param  name
     * @return
     */
    T get(String name);

    /**
     * Returns a commanding instance of the provider.
     *
     * @return
     */
    T instance();

    /**
     * Returns the keySet. Use with care, preferably for debugging purposes.
     *
     * @return
     */
    Set<String> keySet();
}
