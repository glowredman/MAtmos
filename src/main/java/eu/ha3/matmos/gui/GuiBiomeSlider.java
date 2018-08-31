package eu.ha3.matmos.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import eu.ha3.matmos.MAtMod;
import eu.ha3.mc.gui.HDisplayStringProvider;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;
import net.minecraft.world.biome.Biome;

public class GuiBiomeSlider implements HDisplayStringProvider, HSliderListener {
    final protected MAtMod mod;

    final protected int maxBiomes = calculateMaxBiomes();
    protected int definedBiomeID;

    private List<Integer> validBiomes = new ArrayList<>();

    public GuiBiomeSlider(MAtMod mod, int define) {
        this.mod = mod;
        definedBiomeID = define;

        computeBiomes();
    }

    private void computeBiomes() {
        for (Biome i : Biome.REGISTRY) {
            validBiomes.add(Biome.REGISTRY.getIDForObject(i));
        }
    }

    @Override
    public void sliderValueChanged(HGuiSliderControl slider, float value) {
        int biomeID = (int)(Math.floor(value * validBiomes.size()) - 1);

        definedBiomeID = biomeID >= 0 && biomeID < validBiomes.size() ? validBiomes.get(biomeID) : biomeID;

        slider.updateDisplayString();
    }

    @Override
    public void sliderReleased(HGuiSliderControl hGuiSliderControl) {
        mod.getConfig().setProperty("useroptions.biome.override", definedBiomeID);
        mod.saveConfig();
    }

    @Override
    public String provideDisplayString() {
        String desc = getDescriptorString();

        if (desc == null) {
            return "";
        }

        return I18n.format("mat.biome.override", desc);
    }

    @Nullable
    protected String getDescriptorString() {
        if (definedBiomeID >= 0 && definedBiomeID <= maxBiomes) {
            Biome biome = Biome.getBiomeForId(definedBiomeID);

            if (biome == null) {
                return I18n.format("mat.biome.undef", definedBiomeID);
            }

            if (StringUtils.isNullOrEmpty(biome.getBiomeName())) {
                return I18n.format("mat.biome.unamed", definedBiomeID);
            }

            return I18n.format("mat.biome.biome", biome.getBiomeName(), definedBiomeID);
        }

        if (definedBiomeID == -1) {
            return I18n.format("mat.biome.disabled");
        }

        return null;
    }

    public float calculateSliderLocation(int biomeID) {

        if (biomeID == -1) {
            return 0;
        }

        if (validBiomes.contains(biomeID)) {
            return (validBiomes.indexOf(biomeID) + 1) / validBiomes.size();
        }

        return 1;
    }

    private int calculateMaxBiomes() {
        int max = 0;

        for (Biome i : Biome.REGISTRY) {
            int id = Biome.REGISTRY.getIDForObject(i);

            if (id > max) {
                max = id;
            }
        }

        return max;
    }

}
