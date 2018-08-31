package eu.ha3.matmos.data;

import java.util.Set;

public interface Collector {

    void addModuleStack(String name, Set<String> requiredModules);

    void removeModuleStack(String name);

    /**
     * Tells if this collector requires a certain module.
     */
    boolean requires(String moduleName);
}
