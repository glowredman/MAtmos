package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.storage.WorldInfo;

public class ModuleWorld extends ModuleProcessor implements Module {
    float rainStrengthThreshold;
    
    int[] celestialAngleToTime = constructCelestialAngleToTimeTable();
    
    public ModuleWorld(DataPackage data) {
        super(data, "w_general");
        rainStrengthThreshold = ConfigManager.getConfig().getFloat("rain.strengththreshold");
    }
    
    private static int[] constructCelestialAngleToTimeTable() {
        int[] angleToTime = new int[1000];
        
        WorldProviderSurface p = new WorldProviderSurface();
        for(int t = 23999; t >= 0; t--) {
            float angle = p.calculateCelestialAngle(t, 0);
            angleToTime[(int)(angle * angleToTime.length)] = t;
        }
        
        return angleToTime;
    }

    @Override
    protected void doProcess() {
        World w = Minecraft.getMinecraft().world;
        EntityPlayer player = getPlayer();
        WorldInfo info = w.getWorldInfo();
        BlockPos pos = MAtUtil.getPlayerPos();
        Biome biome = w.getBiome(pos);

        setValue("time_modulo24k", invCalculateCelestialAngle(w.provider.calculateCelestialAngle(info.getWorldTime(), 0)));
        setValue("rain", rainStrengthThreshold == -1 ? w.isRaining() 
                : w.getRainStrength(0f) > rainStrengthThreshold);
        setValue("thunder", info.isThundering());
        setValue("thunder", w.getThunderStrength(0f) > 0.9f);
        setValue("dimension", player.dimension);
        setValue("light_subtracted", w.getSkylightSubtracted());
        setValue("remote", w.isRemote);
        setValue("moon_phase", w.getMoonPhase());
        setValue("can_rain_on", MAtUtil.canSeeSky(pos));
        setValue("can_snow_here", w.provider.canSnowAt(pos, false));

        setValue("biome_can_rain", biome.canRain());
        setValue("biome_is_snowy", biome.getEnableSnow());
        setValue("biome_temperature", Math.round(biome.getTemperature(pos) * 1000));
        setValue("biome_rainfall", Math.round(biome.getRainfall() * 1000));

        setValue("can_rain_here", biome.canRain() && biome.getTemperature(pos) > 0.15f);

        setValue("rain_force1k", Math.round(w.getRainStrength(0f) * 1000));
        setValue("thunder_force1k", Math.round(w.getThunderStrength(0f) * 1000));

    }
    
    private int invCalculateCelestialAngle(float x) {
        /* Approximation of the inverse of calculateCelestialAngle, with a max error of 11 */
        
        x = x % 1f;
        
        int prevIndex = (int)(x * celestialAngleToTime.length);
        float alpha = ((x * celestialAngleToTime.length) - (float)Math.floor(x * celestialAngleToTime.length));
        int nextIndex = prevIndex + 1 < celestialAngleToTime.length ? prevIndex + 1 : 0;
        
        int prev = celestialAngleToTime[prevIndex];
        int next = celestialAngleToTime[nextIndex];
        if(next < prev) {
            next = 24000;
        }
        
        return (int)((1f - alpha) * prev + alpha * next);
    }
}
