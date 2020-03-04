package eu.ha3.matmos.data.modules;

public interface RegistryBasedModule extends Module {
    /**
     * Gets the name of the registry used to back this module.
     */
    String getRegistryName();
}
