package eu.ha3.matmos.data.modules.world;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.modules.Module;
import eu.ha3.matmos.data.modules.ModuleProcessor;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

public class ModuleBiome extends ModuleProcessor implements Module {
    private final Matmos mod;

    public ModuleBiome(DataPackage data, Matmos mod) {
        super(data, "w_biome");
        this.mod = mod;
    }

    @Override
    protected void doProcess() {
        int biomej = mod.getConfig().getInteger("useroptions.biome.override");

        if (biomej <= -1) {
            Biome biome = calculateBiome();
            setValue("id", Biome.getIdForBiome(biome));
            setValue("biome_name", biome.getBiomeName());
        } else {
            setValue("id", biomej);
            setValue("biome_name", "");
        }
    }

    private Biome calculateBiome() {
        Minecraft mc = Minecraft.getMinecraft();
        BlockPos playerPos = MAtUtil.getPlayerPos();

        return mc.world.getChunk(playerPos).getBiome(playerPos, mc.world.getBiomeProvider());
    }
}
