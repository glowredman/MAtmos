package eu.ha3.matmos.engine.condition;

import eu.ha3.matmos.engine.Data;

/**
 * @author dags_ <dags@dags.me>
 */

public class SimpleCondition<T> implements Checkable
{
    private String key;
    private T[] values;
    private Data<T> data;
    private Check<T> check;

    public SimpleCondition(String s, T[] vals, Check<T> c, Data<T> d)
    {
        key = s;
        values = vals;
        check = c;
        data = d;
    }

    public boolean active()
    {
        for (T v : values)
            if (data.present() && check.isTrue(v, data.value))
                return true;
        return false;
    }

    @Override
    public String getCurrentValue()
    {
        return data.present() ? data.value.toString() : "";
    }

    @Override
    public String serialize()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(key).append(' ').append(check.asString()).append(' ');
        for (T t : values)
            sb.append(t).append(" | ");
        return sb.delete(sb.length() - 3, sb.length()).toString();
    }

    @Override
    public String debugInfo()
    {
        return serialize() + " [" + data.value + "]";
    }
}
