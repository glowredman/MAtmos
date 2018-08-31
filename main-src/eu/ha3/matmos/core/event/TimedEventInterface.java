package eu.ha3.matmos.core.event;

import eu.ha3.matmos.core.ReferenceTime;

/*
 * --filenotes-placeholder
 */

public interface TimedEventInterface {
    public void restart(ReferenceTime time);

    public void play(ReferenceTime time, float fadeFactor);
}
