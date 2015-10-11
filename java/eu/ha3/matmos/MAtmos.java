package eu.ha3.matmos;

import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.engine.ExpansionManager;
import eu.ha3.matmos.engine.PackManager;
import eu.ha3.matmos.engine.condition.ConditionParser;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gatherer.PlayerGatherer;
import eu.ha3.matmos.game.gatherer.PositionGatherer;
import eu.ha3.matmos.game.gatherer.WeatherGatherer;
import eu.ha3.matmos.game.gatherer.WorldGatherer;
import eu.ha3.matmos.game.scanner.EntityScanner;
import eu.ha3.matmos.game.scanner.VolumeScanner;
import eu.ha3.matmos.gui.GuiData;
import eu.ha3.matmos.util.Debug;
import eu.ha3.matmos.util.Dump;
import eu.ha3.matmos.util.Timer;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;


/**
 * @author dags_ <dags@dags.me>
 */

public class MAtmos
{
    private static final Logger logger = (Logger) LogManager.getLogger("MAtmos");

    public static Timer timer = new Timer();
    public static ConditionParser conditionParser;
    public final PackManager packManager = new PackManager(this);
    public final DataManager dataManager = new DataManager();
    public final ExpansionManager expansionManager = new ExpansionManager();
    public File configFolder;

    private GuiData guiData;

    public MAtmos()
    {
        conditionParser = new ConditionParser(this.dataManager);
    }

    public void setConfigDir(File configDir)
    {
        configFolder = configDir;
        if (!configFolder.exists() && configFolder.mkdirs())
        {
            log("Creating new directory: " + configFolder.getPath());
        }
    }

    public void reload()
    {
        log("Reloading engine...");
        expansionManager.wipe();
        dataManager.wipe();
        dataManager.addDataGatherer(new PlayerGatherer().register(dataManager));
        dataManager.addDataGatherer(new PositionGatherer().register(dataManager));
        dataManager.addDataGatherer(new WeatherGatherer().register(dataManager));
        dataManager.addDataGatherer(new WorldGatherer().register(dataManager));
        dataManager.registerScanner(new EntityScanner());
        // NB volume scanners scan on alternate ticks, hence the /2
        dataManager.registerScanner(new VolumeScanner("small", 8, 20 / 2 /*1 secs*/));
        dataManager.registerScanner(new VolumeScanner("large", 28, (20 * 5) / 2 /* 5 secs */));

        guiData = new GuiData(this).display("active");
        // debug stuff
        new Dump(dataManager).dumpToJson(configFolder);
        debug();
    }

    private void debug()
    {
        Debug.dummyExpansion(this);
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
        timer.punchIn();
        checkLoadSoundPacks();
        MCGame.update();
        dataManager.process();
        expansionManager.process();
        timer.punchOut();
    }

    public void draw()
    {
        if (Minecraft.getMinecraft().thePlayer != null && !MCGame.firstTick)
        {
            guiData.draw();
        }
    }
}
