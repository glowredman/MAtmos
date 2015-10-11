package eu.ha3.matmos.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class SoundSet
{
    private String name;
    private final List<String> sounds = new ArrayList<String>();

    public SoundSet(){}

    public SoundSet(String s)
    {
        name = s;
    }

    public List<String> getSounds()
    {
        return sounds;
    }

    public SoundSet add(String... sound)
    {
        for (String s : sound)
            sounds.add(s);
        return this;
    }

    public SoundSet remove(String sound)
    {
        sounds.remove(sound);
        return this;
    }

    public String getName()
    {
        return name;
    }
}
