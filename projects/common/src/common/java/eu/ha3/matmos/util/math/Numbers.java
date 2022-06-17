package eu.ha3.matmos.util.math;

import com.google.common.primitives.Longs;

public class Numbers {
    public static Long toLong(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Long) {
            return (Long) o;
        }

        if (o instanceof String) {
            return Longs.tryParse((String)o);
        }

        return null;
    }

    public static Float toFloat(Object o) {
        if (o == null) {
            return null;
        }

        if (o instanceof Float) {
            return (Float) o;
        }

        if (o instanceof String) {
            try {
                return Float.parseFloat((String) o);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
