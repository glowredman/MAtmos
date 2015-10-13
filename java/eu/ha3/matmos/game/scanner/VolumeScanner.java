package eu.ha3.matmos.game.scanner;

import eu.ha3.matmos.game.MCGame;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public class VolumeScanner implements Scanner
{
    private final Map<String, Counter> counters = new HashMap<String, Counter>();
    private final Map<String, Counter> current = new HashMap<String, Counter>();
    private final String id;

    private final int countPer;
    private final int radius;
    private final int height;

    private boolean reset = true;

    private int xPos = 0;
    private int yPos = 0;
    private int zPos = 0;

    private int dx = 0;
    private int dy = 0;
    private int dz = 0;


    public VolumeScanner(String name, int horizontalRadius, int scanHeight,int scanDuration)
    {
        id = name;
        radius = horizontalRadius;
        height = scanHeight;
        int w = 1 + horizontalRadius + horizontalRadius;
        int h = 1 + height + height;
        int vol = w * h * w;
        countPer = 1 + (vol / scanDuration);
    }

    @Override
    public void scan()
    {
        if (reset)
        {
            reset = false;
            counters.clear();
            counters.putAll(current);
            current.clear();
            dx = -radius;
            dy = -height;
            dz = -radius;
            xPos = MCGame.playerPosition.getX();
            yPos = MCGame.playerPosition.getY();
            zPos = MCGame.playerPosition.getZ();
        }
        else
        {
            int counter = countPer;
            outer:
            for (; dx < radius; dx++)
            {
                for (; dy < height; dy++)
                {
                    for (; dz < radius; dz++)
                    {
                        String name = MCGame.getBlockName(xPos + dx, yPos + dy, zPos + dz);
                        if (!current.containsKey(name))
                            current.put(name, new Counter());
                        current.get(name).count++;
                        if (counter-- <= 0)
                            break outer;
                    }
                    if (dz == radius)
                        dz = -radius;
                }
                if (dy == height)
                    dy = -height;
            }
            reset = dx == radius;
        }
    }

    @Override
    public String displayId()
    {
        return "scan.block." + id;
    }

    @Override
    public Map<String, Counter> getCounts()
    {
        return counters;
    }

    @Override
    public int getCount(String lookUp)
    {
        if (counters.containsKey(lookUp))
            return counters.get(lookUp).count;
        return 0;
    }
}
