package eu.ha3.matmos.engine.condition;

import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionSet
{
    private final List<Checkable> conditions;
    private final String name;

    public ConditionSet(String n, List<Checkable> checkables)
    {
        conditions = checkables;
        name = n;
    }

    public List<Checkable> getConditions()
    {
        return conditions;
    }

    public boolean active()
    {
        for (Checkable c : conditions)
            if (!c.active())
                return false;
        return true;
    }

    public String getName()
    {
        return name;
    }

    public void printDebug()
    {
        for (Checkable c : conditions)
            System.out.println(c.serialize());
    }
}
