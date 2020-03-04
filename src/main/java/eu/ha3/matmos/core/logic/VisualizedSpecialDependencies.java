package eu.ha3.matmos.core.logic;

import java.util.Collection;

import eu.ha3.matmos.core.Dependable;

public interface VisualizedSpecialDependencies extends Visualized, Dependable {
    Collection<String> getSpecialDependencies(String type);
}
