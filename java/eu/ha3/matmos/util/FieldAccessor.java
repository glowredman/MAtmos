package eu.ha3.matmos.util;

import com.google.common.base.Optional;

import java.lang.reflect.Field;

/**
 * @author dags_ <dags@dags.me>
 */

public class FieldAccessor<T>
{
    private final Object owner;
    private final Field target;
    private final Class<T> type;

    private FieldAccessor(Object object, Class<T> fieldType, Field field)
    {
        owner = object;
        target = field;
        type = fieldType;
    }

    public Optional<T> get()
    {
        try
        {
            Object o = target.get(owner);
            if (o != null)
                return Optional.of(type.cast(o));
            else
                return Optional.absent();
        }
        catch (Throwable t)
        {
            return Optional.absent();
        }
    }

    public static <T> FieldAccessor<T> of(Object owner, Class<T> type, String... names)
    {
        if (owner != null && type != null)
        {
            for (Field f : owner.getClass().getDeclaredFields())
            {
                for (String s : names)
                {
                    if (s.equals(f.getName()))
                    {
                        f.setAccessible(true);
                        return new FieldAccessor<T>(owner, type, f);
                    }
                }
            }
        }
        return null;
    }
}
