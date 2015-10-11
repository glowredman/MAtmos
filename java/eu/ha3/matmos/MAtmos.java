package eu.ha3.matmos;

import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.engine.PackManager;
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

    public final PackManager packManager = new PackManager(this);
    public final DataManager dataManager = new DataManager();
    private GuiData guiData;

    public void reload()
    {
        log("Reloading engine...");
        dataManager.wipe();
        dataManager.addDataGatherer(new PlayerGatherer().register(dataManager));
        dataManager.addDataGatherer(new PositionGatherer().register(dataManager));
        dataManager.addDataGatherer(new WeatherGatherer().register(dataManager));
        dataManager.addDataGatherer(new WorldGatherer().register(dataManager));
        dataManager.registerScanner(new EntityScanner());
        // NB volume scanners scan on alternate ticks, hence the /2
        dataManager.registerScanner(new VolumeScanner("small", 8, 20 / 2 /*1 secs*/));
        dataManager.registerScanner(new VolumeScanner("large", 28, (20 * 5) / 2 /* 5 secs */));
        guiData = new GuiData(this).display("player");
        // create some test eventProcessors
        debug();
    }

    private void debug()
    {
        for (EventProcessor ep : Debug.getProcessors(this))
            dataManager.addEventProcessor(ep);
    }

    private void checkLoadSoundPacks()
    {
        if (MCGame.firstTick)
        {
            packManager.registerReloadable();
        }
    }

    public static void log(String message)
    {
        logger.info("(MAtmos) " + message);
    }

    public void onTick()
    {
        checkLoadSoundPacks();
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
