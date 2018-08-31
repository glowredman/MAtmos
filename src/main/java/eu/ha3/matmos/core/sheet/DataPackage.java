package eu.ha3.matmos.core.sheet;

import java.util.Set;

/* x-placeholder */

public interface DataPackage {
    Sheet getSheet(String name);

    Set<String> getSheetNames();

    /**
     * Empties the data overall
     */
    void clear();

    /**
     * Empties the individual sheets
     */
    void clearContents();
}
