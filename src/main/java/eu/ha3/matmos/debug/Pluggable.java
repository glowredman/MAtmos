package eu.ha3.matmos.debug;

import java.io.File;

public interface Pluggable {
    boolean isReadOnly();

    File getFileIfAvailable();

    File getWorkingDirectoryIfAvailable();

    void pushJson(String json);

    void reloadFromDisk();

    void onEditorClosed();
}
