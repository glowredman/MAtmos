package eu.ha3.matmos;

import eu.ha3.mc.haddon.forge.ForgeBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

@Mod(modid = ForgeMatmos.MODID, name = ForgeMatmos.NAME, version = ForgeMatmos.VERSION)
public class ForgeMatmos extends ForgeBase
{
    public static final String MODID = "matmos";
    public static final String NAME = Matmos.NAME;
    public static final String VERSION = Matmos.FOR + "-" + Matmos.VERSION;
    
    public ForgeMatmos() {
        super(new Matmos());
    }
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        super.onClientTick(event);
    }
    
    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
        super.onRenderTick(event);
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event) {
        super.init(event, MODID, NAME, VERSION);
    }
    
}