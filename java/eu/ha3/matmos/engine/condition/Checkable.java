package eu.ha3.matmos.engine.condition;

/**
 * @author dags_ <dags@dags.me>
 */

public interface Checkable
{
    public boolean active();

    public String getCurrentValue();

    public String serialize();

    public String debugInfo();
}
