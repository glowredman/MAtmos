package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

public class ModuleWorld extends ModuleProcessor implements Module {
    public ModuleWorld(DataPackage data) {
        super(data, "w_general");
    }

    @Override
    protected void doProcess() {
        World w = Minecraft.getMinecraft().world;
        EntityPlayer player = getPlayer();
        WorldInfo info = w.getWorldInfo();

        setValue("time_modulo24k", (int)(info.getWorldTime() % 24000L));
        setValue("rain", w.isRaining());
        setValue("thunder", info.isThundering());
        setValue("thunder", w.getThunderStrength(0f) > 0.9f);
        setValue("dimension", player.dimension);
        setValue("light_subtracted", w.getSkylightSubtracted());
        setValue("remote", w.isRemote);
        setValue("moon_phase", w.getMoonPhase());
        setValue("can_rain_on", w.canSeeSky(MAtUtil.getPlayerPos()));
        setValue("biome_can_rain", w.getBiome(MAtUtil.getPlayerPos()).canRain());
        setValue("rain_force1k", Math.round(w.getRainStrength(0f) * 1000));
        setValue("thunder_force1k", Math.round(w.getThunderStrength(0f) * 1000));

    }
}
