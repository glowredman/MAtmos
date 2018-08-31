package eu.ha3.matmos.core.logic;

import eu.ha3.matmos.core.Named;
import eu.ha3.matmos.core.Stated;

/*
 * --filenotes-placeholder
 */

public interface Visualized extends Named, Stated {
    /**
     * Represents the feed of this visualizable. It's a chain of characters that says what is happening.
     * 
     * @return
     */
    public String getFeed();
}
