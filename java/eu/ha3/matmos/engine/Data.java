package eu.ha3.matmos.engine;

/**
 * @author dags_ <dags@dags.me>
 */

public class Data<T>
{
    public T value;

    public Data(){}

    public Data(T t)
    {
        value = t;
    }

    public boolean present()
    {
        return value != null;
    }
}
