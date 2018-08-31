package eu.ha3.matmos.debug;

import java.io.File;

public interface PluggableIntoMinecraft {
    boolean isReadOnly();

    File getFileIfAvailable();

    File getWorkingDirectoryIfAvailable();

    void pushJason(String jason);

    void reloadFromDisk();

    void onEditorClosed();
}
