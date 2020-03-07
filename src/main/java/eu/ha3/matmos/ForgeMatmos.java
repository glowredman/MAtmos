package eu.ha3.matmos;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;
import eu.ha3.mc.haddon.forge.ForgeBase;


@Mod(modid = ForgeMatmos.MODID, name = ForgeMatmos.NAME, version = ForgeMatmos.VERSION/*, updateJSON = Matmos.UPDATE_JSON*/) // 1.12.2 only
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