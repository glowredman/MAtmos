package eu.ha3.matmos.util;

/**
 * @author dags_ <dags@dags.me>
 */

public class Timer
{
    private long punchIn = 0L;
    private long punchOut = 0L;

    public Timer punchIn()
    {
        punchIn = System.nanoTime();
        return this;
    }

    public Timer punchOut()
    {
        punchOut = System.nanoTime();
        return this;
    }

    public long getPeriodNs()
    {
        return punchOut - punchIn;
    }

    public float getPeriodMs()
    {
        return getPeriodNs() * 1e-6F;
    }

    public String getTimeNs()
    {
        return getPeriodNs() + "ns";
    }

    public String getTimeMs()
    {
        return NumberUtil.round1dp(getPeriodMs()) + "ms";
    }

    public String getTickPercent()
    {
        // (time / 50) * 100
        return NumberUtil.round1dp((getPeriodMs() / 0.5)) + "%";
    }
}
