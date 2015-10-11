package eu.ha3.matmos.engine.processor;

import eu.ha3.matmos.util.NumberUtil;

/**
 * @author dags_ <dags@dags.me>
 */

public class DelayTimer
{
    private final int min;
    private final int max;
    private final int after;

    private int ticker = 0;
    private int delay = 0;

    public DelayTimer(int mi, int ma, int af)
    {
        min = mi;
        max = ma;
        after = af;
        delay = NumberUtil.nextInt(min, max);
    }

    public boolean complete()
    {
        if(ticker++ >= delay)
        {
            delay = after + NumberUtil.nextInt(min, max);
            ticker = 0;
            return true;
        }
        return false;
    }
}
