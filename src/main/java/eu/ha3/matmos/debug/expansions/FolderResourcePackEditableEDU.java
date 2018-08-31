package eu.ha3.matmos.debug.expansions;

import java.io.File;

/*
 * --filenotes-placeholder
 */

public interface FolderResourcePackEditableEDU extends ExpansionDebugUnit {
    File obtainExpansionFile();

    File obtainExpansionFolder();
}
