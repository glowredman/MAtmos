package eu.ha3.matmos.engine.serialize;

import eu.ha3.matmos.MAtmos;

/**
 * @author dags_ <dags@dags.me>
 */

public class StreamEventSerialize extends EventSerialize
{
    public boolean repeatable = true;
    public int fadeIn = 0;
    public int fadeOut = 0;

    public StreamEventSerialize(String name)
    {
        super(name);
    }

    public boolean valid(MAtmos m)
    {
        if (fadeIn < 0)
            MAtmos.log("FadeIn must be greater than or equal to 0!");
        if (fadeOut < 0)
            MAtmos.log("FadeOut must be greater than or equal to 0!");
        return fadeIn >= 0 && fadeOut >= 0 && super.valid(m);
    }
}
