package eu.ha3.matmos.core;

public class SystemClock implements ReferenceTime {
    @Override
    public long getMilliseconds() {
        return System.currentTimeMillis();
    }
}
