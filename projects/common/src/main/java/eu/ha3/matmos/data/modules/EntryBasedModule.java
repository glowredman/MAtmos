package eu.ha3.matmos.data.modules;

import java.util.Map;

public interface EntryBasedModule extends Module {
    Map<String, EI> getModuleEntries();
}
