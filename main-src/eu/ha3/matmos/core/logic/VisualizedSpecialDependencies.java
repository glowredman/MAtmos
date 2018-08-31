package eu.ha3.matmos.core.logic;

import java.util.Collection;

import eu.ha3.matmos.core.Dependable;

/*
 * --filenotes-placeholder
 */

public interface VisualizedSpecialDependencies extends Visualized, Dependable {
    public Collection<String> getSpecialDependencies(String type);
}
