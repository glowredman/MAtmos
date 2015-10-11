package eu.ha3.matmos.util;

import eu.ha3.matmos.MAtmos;
import io.netty.util.internal.ThreadLocalRandom;

/**
 * @author dags_ <dags@dags.me>
 */

public class NumberUtil
{
    public static boolean validMinMax(MAtmos mAtmos, String name, Number min, Number max)
    {
        if (min.doubleValue() < 0)
            MAtmos.log(name + "Min must be greater than or equal to 0!");
        if (max.doubleValue() < 0)
            MAtmos.log(name + "Max must be greater than or equal to 0!");
        if (max.doubleValue() < min.doubleValue())
            MAtmos.log(name + "Max must be greater than " + name + "Min!");
        return min.doubleValue() >= 0 && max.doubleValue() >= min.doubleValue();
    }

    public static double round1dp(double in)
    {
        return round(in * 10D) / 10D;
    }

    public static double round2dp(double in)
    {
        return round(in * 100D) / 100D;
    }

    public static long round(double in)
    {
        return in > 0 ? (long) (in + 0.5D) : (long) (in - 0.5D);
    }

    public static int nextRanInt(int min, int max)
    {
        return ThreadLocalRandom.current().nextBoolean() ? nextInt(min, max) : -nextInt(min, max);
    }

    public static int nextInt(int min, int max)
    {
        if (min >= max)
            return min;
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static float nextFloat(float min, float max)
    {
        if (max == min)
            return max;
        if (min > max)
            return (float) ThreadLocalRandom.current().nextDouble(max, min);
        return (float) ThreadLocalRandom.current().nextDouble(min, max);
    }

    public static boolean isInt(String s)
    {
        return isNumber(s, true);
    }

    public static boolean isNumber(String s)
    {
        return isNumber(s, false);
    }

    private static boolean isNumber(String s, boolean ignoreDecimal)
    {
        if (s.isEmpty())
            return false;
        for (int i = 0, c, length = s.length(); i < length; i++)
        {
            c = s.charAt(i);
            if (i == 0 && c == '-' && length > 1 || (!ignoreDecimal && (ignoreDecimal = c == '.')))
                continue;
            if (Character.digit(c, 10) < 0)
                return false;
        }
        return true;
    }
}
