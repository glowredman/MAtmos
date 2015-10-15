package eu.ha3.matmos;

import eu.ha3.matmos.engine.DataRegistry;
import eu.ha3.matmos.engine.ExpansionRegistry;
import eu.ha3.matmos.engine.PackManager;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gatherer.PlayerGatherer;
import eu.ha3.matmos.game.gatherer.PositionGatherer;
import eu.ha3.matmos.game.gatherer.WeatherGatherer;
import eu.ha3.matmos.game.gatherer.WorldGatherer;
import eu.ha3.matmos.game.gui.editor.ScreenHolder;
import eu.ha3.matmos.game.scanner.EntityScanner;
import eu.ha3.matmos.game.scanner.VolumeScanner;
import eu.ha3.matmos.game.gui.GuiData;
import eu.ha3.matmos.util.Debug;
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

    public static final DataRegistry dataRegistry = new DataRegistry();
    public static final ExpansionRegistry expansionRegistry = new ExpansionRegistry();
    public static final Timer timer = new Timer();

    public final PackManager packManager = new PackManager(this);
    public File configFolder;

    private GuiData guiData;

    public String getName()
    {
        return "MAtmos";
    }

    public String getVersion()
    {
        return "2.0";
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
        expansionRegistry.wipe();
        dataRegistry.wipe();
        dataRegistry.addDataGatherer(new PlayerGatherer().register(dataRegistry));
        dataRegistry.addDataGatherer(new PositionGatherer().register(dataRegistry));
        dataRegistry.addDataGatherer(new WeatherGatherer().register(dataRegistry));
        dataRegistry.addDataGatherer(new WorldGatherer().register(dataRegistry));
        dataRegistry.registerScanner(new EntityScanner());
        dataRegistry.registerScanner(new VolumeScanner("small", 8, 8, 20 / 2)); // 17 * 17 * 17 blocks over 1 secs
        dataRegistry.registerScanner(new VolumeScanner("large", 28, 14, (20 * 5) / 2)); // 57 * 29 * 57 blocks over 5 secs
        guiData = new GuiData(this).display("active");
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
        ScreenHolder.checkToggle();

        timer.punchIn();
        checkLoadSoundPacks();
        MCGame.update();
        dataRegistry.process();
        expansionRegistry.process();
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
