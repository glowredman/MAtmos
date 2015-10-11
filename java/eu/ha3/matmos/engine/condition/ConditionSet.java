package eu.ha3.matmos.engine.condition;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionSet
{
    private final List<Checkable> conditions;
    private final String name;

    public ConditionSet(String setName)
    {
        conditions = new ArrayList<Checkable>();
        name = setName;
    }

    public ConditionSet(String n, List<Checkable> checkables)
    {
        conditions = checkables;
        name = n;
    }

    public void addCondition(Checkable checkable)
    {
        conditions.add(checkable);
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

    public List<String> serialize()
    {
        List<String> lines = new ArrayList<String>();
        for (Checkable c : conditions)
        {
            lines.add(c.serialize());
        }
        return lines;
    }
}
