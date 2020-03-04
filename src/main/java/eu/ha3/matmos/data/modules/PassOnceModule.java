package eu.ha3.matmos.data.modules;

import java.util.Set;

/*
 * --filenotes-placeholder
 */

public interface PassOnceModule extends Module {
    /**
     * Returns a set of modules this pass-once module is capable to handle.
     *
     * @return
     */
    Set<String> getSubModules();
}
