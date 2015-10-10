package eu.ha3.matmos;

import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.engine.event.EventProcessor;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gatherer.PlayerGatherer;
import eu.ha3.matmos.game.gatherer.PositionGatherer;
import eu.ha3.matmos.game.gatherer.WeatherGatherer;
import eu.ha3.matmos.game.gatherer.WorldGatherer;
import eu.ha3.matmos.game.scanner.EntityScanner;
import eu.ha3.matmos.game.scanner.VolumeScanner;
import eu.ha3.matmos.gui.GuiData;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * @author dags_ <dags@dags.me>
 */

public class MAtmos
{
    private static final Logger logger = (Logger) LogManager.getLogger("MAtmos");

    public final DataManager dataManager = new DataManager();
    private GuiData guiData;

    public void init()
    {
        dataManager.addDataGatherer(new PlayerGatherer().register(dataManager));
        dataManager.addDataGatherer(new PositionGatherer().register(dataManager));
        dataManager.addDataGatherer(new WeatherGatherer().register(dataManager));
        dataManager.addDataGatherer(new WorldGatherer().register(dataManager));
        dataManager.registerScanner(new EntityScanner());
        dataManager.registerScanner(new VolumeScanner("small", 8, 10));
        dataManager.registerScanner(new VolumeScanner("large", 28, 10 * 5 /* 5 secs */));

        guiData = new GuiData(this).display("position");
        // create some test eventProcessors
        debug();
    }

    private void debug()
    {
        for (EventProcessor ep : Debug.getProcessors(this))
            dataManager.addEventProcessor(ep);
    }

    public static void log(String message)
    {
        logger.info("(MAtmos) " + message);
    }

    public void onTick()
    {
        MCGame.update();
        dataManager.process();
    }

    public void draw()
    {
        if (Minecraft.getMinecraft().thePlayer != null && !MCGame.firstTick)
        {
            guiData.draw();
        }
    }
}
