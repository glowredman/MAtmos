package eu.ha3.matmos.core;

/* x-placeholder */

public class SystemClock implements ReferenceTime {
    @Override
    public long getMilliseconds() {
        return System.currentTimeMillis();
    }
}
