package eu.ha3.matmos.core;

import java.util.Collection;

public interface Dependable {
    Collection<String> getDependencies();
}
