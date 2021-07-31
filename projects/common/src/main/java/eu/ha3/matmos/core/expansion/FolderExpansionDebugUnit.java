package eu.ha3.matmos.core.expansion;

import java.io.File;

public interface FolderExpansionDebugUnit extends ExpansionDebugUnit {
    File getExpansionFile();

    File getExpansionFolder();
}
