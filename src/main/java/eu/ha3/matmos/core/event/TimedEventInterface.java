package eu.ha3.matmos.core.event;

import eu.ha3.matmos.core.ReferenceTime;

/*
 * --filenotes-placeholder
 */

public interface TimedEventInterface {
    void restart(ReferenceTime time);

    void play(ReferenceTime time, float fadeFactor);
}
