package eu.ha3.matmos.game.gatherer;

import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.util.NumberUtil;

/**
 * @author dags_ <dags@dags.me>
 */

public class WeatherGatherer implements DataGatherer
{
    private Data<Number> rainStrength = new Data<Number>();
    private Data<Boolean> raining = new Data<Boolean>();
    private Data<Boolean> rainCanReach = new Data<Boolean>();
    private Data<Boolean> snowing = new Data<Boolean>();
    private Data<Boolean> canRain = new Data<Boolean>();
    private Data<Boolean> canSnow = new Data<Boolean>();

    @Override
    public DataGatherer register(DataManager manager)
    {
        manager.registerBool("weather.canRain", canRain);
        manager.registerBool("weather.canSnow", canSnow);
        manager.registerNum("weather.rainStrength", rainStrength);
        manager.registerBool("weather.rainCanReach", rainCanReach);
        manager.registerBool("weather.raining", raining);
        manager.registerBool("weather.snowing", snowing);
        return this;
    }

    @Override
    public void update()
    {
        canRain.value = MCGame.currentBiome.canSpawnLightningBolt();
        canSnow.value = canSnow();
        rainCanReach.value = MCGame.currentWorld.canBlockSeeSky(MCGame.playerPosition);
        rainStrength.value = NumberUtil.round2dp(MCGame.currentWorld.getRainStrength(1L));
        raining.value = MCGame.currentWorld.isRaining();
        snowing.value = raining.value && canSnow.value;
    }

    private boolean canSnow()
    {
        float temp = MCGame.currentBiome.getFloatTemperature(MCGame.playerPosition);
        float f = MCGame.currentWorld.getWorldChunkManager().getTemperatureAtHeight(temp, MCGame.playerPosition.getY());
        return f < 0.15F;
    }
}
