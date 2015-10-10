package eu.ha3.matmos.engine.condition;

import com.google.common.base.Optional;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public abstract class Check<T>
{
    public abstract String asString();
    public abstract boolean isTrue(T value, T check);

    private static Map<String, Check<Number>> numChecks = populate();
    private static Check<String> stringEquals = new StringEquals();
    private static Check<String> stringNotEquals = new StringNotEquals();
    private static Check<Boolean> boolEquals = new BoolEquals();
    private static Check<Boolean> boolNotEquals = new BoolNotEquals();

    private static Map<String, Check<Number>> populate()
    {
        Map<String, Check<Number>> map = new HashMap<String, Check<Number>>();
        map.put("=", new NumEqual());
        map.put("==", map.get("="));
        map.put("!=", new NumNotEqual());
        map.put("=!", map.get("!="));
        map.put(">", new GreaterThan());
        map.put(">=", new EqualGreater());
        map.put("=>", map.get(">="));
        map.put("<", new LessThan());
        map.put("<=", new EqualLess());
        map.put("=<", map.get("<="));
        return map;
    }

    public static boolean validOperator(String s)
    {
        return numChecks.containsKey(s);
    }

    public static Optional<Check<Number>> numCheck(String operator)
    {
        if (numChecks.containsKey(operator))
        {
            return Optional.of(numChecks.get(operator));
        }
        return Optional.absent();
    }

    public static Optional<Check<String>> stringCheck(String operator)
    {
        if ("=".equals(operator) || "==".equals(operator))
            return Optional.of(stringEquals);
        if ("!=".equals(operator) || "=!".equals(operator))
            return Optional.of(stringNotEquals);
        return Optional.absent();
    }

    public static Optional<Check<Boolean>> boolCheck(String operator)
    {
        if ("=".equals(operator) || "==".equals(operator))
            return Optional.of(boolEquals);
        if ("!=".equals(operator) || "=!".equals(operator))
            return Optional.of(boolNotEquals);
        return Optional.absent();
    }

    static class StringEquals extends Check<String>
    {
        public String asString()
        {
            return "=";
        }

        public boolean isTrue(String value, String check)
        {
            return check.equalsIgnoreCase(value);
        }
    }

    static class StringNotEquals extends Check<String>
    {
        public String asString()
        {
            return "!=";
        }

        public boolean isTrue(String value, String check)
        {
            return !check.equalsIgnoreCase(value);
        }
    }

    static class BoolEquals extends Check<Boolean>
    {
        @Override
        public String asString()
        {
            return "=";
        }

        @Override
        public boolean isTrue(Boolean value, Boolean check)
        {
            return check.booleanValue() == value.booleanValue();
        }
    }

    static class BoolNotEquals extends Check<Boolean>
    {
        @Override
        public String asString()
        {
            return "=";
        }

        @Override
        public boolean isTrue(Boolean value, Boolean check)
        {
            return check.booleanValue() != value.booleanValue();
        }
    }

    static class NumEqual extends Check<Number>
    {
        public String asString()
        {
            return "=";
        }

        public boolean isTrue(Number value, Number check)
        {
            return check.doubleValue() == value.doubleValue();
        }
    }

    static class NumNotEqual extends Check<Number>
    {
        public String asString()
        {
            return "!=";
        }

        public boolean isTrue(Number value, Number check)
        {
            return check.doubleValue() != value.doubleValue();
        }
    }

    static class GreaterThan extends Check<Number>
    {
        public String asString()
        {
            return ">";
        }

        public boolean isTrue(Number value, Number check)
        {
            return check.doubleValue() > value.doubleValue();
        }
    }

    static class EqualGreater extends Check<Number>
    {
        public String asString()
        {
            return ">=";
        }

        public boolean isTrue(Number value, Number check)
        {
            return check.doubleValue() >= value.doubleValue();
        }
    }

    static class LessThan extends Check<Number>
    {
        public String asString()
        {
            return "<";
        }

        public boolean isTrue(Number value, Number check)
        {
            return check.doubleValue() < value.doubleValue();
        }
    }

    static class EqualLess extends Check<Number>
    {
        public String asString()
        {
            return "<=";
        }

        public boolean isTrue(Number value, Number check)
        {
            return check.doubleValue() <= value.doubleValue();
        }
    }
}
