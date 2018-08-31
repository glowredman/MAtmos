package eu.ha3.matmos.data;

import java.util.Set;

/*
 * --filenotes-placeholder
 */

public interface Collector {

    void addModuleStack(String name, Set<String> requiredModules);

    void removeModuleStack(String name);

    /**
     * Tells if this collector requires a certain module.
     *
     * @param  moduleName
     * @return
     */
    boolean requires(String moduleName);
}
