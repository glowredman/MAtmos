package eu.ha3.matmos.debug.expansions;

import eu.ha3.matmos.core.Knowledge;
import eu.ha3.matmos.core.sheet.DataPackage;

/*
 * --filenotes-placeholder
 */

public interface ExpansionDebugUnit {
    Knowledge obtainKnowledge();

    DataPackage obtainData();
}
