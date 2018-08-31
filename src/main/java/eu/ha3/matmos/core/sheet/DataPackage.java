package eu.ha3.matmos.core.sheet;

import java.util.Set;

/* x-placeholder */

public interface DataPackage {
    public Sheet getSheet(String name);

    public Set<String> getSheetNames();

    /**
     * Empties the data overall
     */
    public void clear();

    /**
     * Empties the individual sheets
     */
    public void clearContents();
}
