package eu.ha3.matmos.game.scanner;

import eu.ha3.matmos.game.MCGame;
import net.minecraft.entity.EntityLiving;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dags_ <dags@dags.me>
 */

public class EntityScanner implements Scanner
{
    private final Map<String, Counter> counters = new HashMap<String, Counter>();

    private final int rad32 = 32 * 32;
    private final int rad16 = 16 * 16;
    private final int rad8 = 8 * 8;

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

    @Override
    public void scan()
    {
        counters.clear();
        for (Object o : MCGame.player.getEntityWorld().loadedEntityList)
        {
            if (o != null && o instanceof EntityLiving)
            {
                EntityLiving e = (EntityLiving) o;
                double dist = e.getDistanceSqToCenter(MCGame.playerPosition);
                if (dist <= rad32)
                    incCounter(e, 32);
                if (dist <= rad16)
                    incCounter(e, 16);
                if (dist <= rad8)
                    incCounter(e, 8);
            }
        }
    }

    @Override
    public String displayId()
    {
        return "scan.entity";
    }

    private void incCounter(EntityLiving e, int rad)
    {
        String id = "radius" + rad + "." + e.getClass().getSimpleName().substring("entity".length());
        if (!counters.containsKey(id))
            counters.put(id, new Counter());
        counters.get(id).count++;
    }
}
