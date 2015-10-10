package eu.ha3.matmos.game.gatherer;

import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.game.MCGame;

/**
 * @author dags_ <dags@dags.me>
 */

public class WorldGatherer implements DataGatherer
{
    // world
    private Data<String> worldName = new Data<String>();
    private Data<Number> dimensionId = new Data<Number>();
    private Data<String> worldTime = new Data<String>();
    private Data<Number> worldTimeTicks = new Data<Number>();

    @Override
    public DataGatherer register(DataManager manager)
    {
        manager.registerString("world.name", worldName);
        manager.registerNum("world.dimensionId", dimensionId);
        manager.registerNum("world.timeTicks", worldTimeTicks);
        manager.registerString("world.time", worldTime);
        return this;
    }

    @Override
    public void update()
    {
        worldName.value = MCGame.worldName;
        dimensionId.value = MCGame.currentWorld.getWorldType().getWorldTypeID();
        worldTimeTicks.value = MCGame.currentWorld.getWorldInfo().getWorldTime() % 24000;
        worldTime.value = worldTime();
    }

    private String worldTime()
    {
        short ticks = worldTimeTicks.value.shortValue();
        if (ticks >= 23000 && ticks < 24000)
            return "sunrise";
        if (ticks >= 0 && ticks < 5000)
            return "morning";
        if (ticks >= 5000 && ticks < 7000)
            return "midday";
        if (ticks >= 7000 && ticks < 9000)
            return "afternoon";
        if (ticks >= 9000 && ticks < 11700)
            return "evening";
        if (ticks >= 11700 && ticks < 13200)
            return "sunset";
        return "night";
    }
}
