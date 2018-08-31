package eu.ha3.matmos.core;

import java.util.Collection;

/*
 * --filenotes-placeholder
 */

public interface Dependable {
    public Collection<String> getDependencies();
}
