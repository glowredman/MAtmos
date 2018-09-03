package eu.ha3.matmos.core.expansion;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.ResourcePackDealer;
import eu.ha3.matmos.core.expansion.agents.JsonLoadingAgent;
import eu.ha3.matmos.core.expansion.agents.LegacyXMLLoadingAgent;
import eu.ha3.matmos.core.mixin.ISoundHandler;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.Collector;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

public class ExpansionManager implements VolumeUpdatable, SupportsTickEvents, SupportsFrameEvents {
    private final ISoundHandler accessor;
    private final File userconfigFolder;

    private final ResourcePackDealer dealer = new ResourcePackDealer();
    private final Map<String, Expansion> expansions = new HashMap<>();

    private DataPackage data;

    private float volume = 1;

    private Collector collector;

    public ExpansionManager(File userconfigFolder, ISoundHandler accessor) {
        this.userconfigFolder = userconfigFolder;
        this.accessor = accessor;

        if (!this.userconfigFolder.exists()) {
            this.userconfigFolder.mkdirs();
        }
    }

    public void loadExpansions() {
        dispose();

        dealer.findResourcePacks().forEach(this::readExpansionsFile);
    }

    private void readExpansionsFile(IResourcePack pack) {
        try {
            InputStream is = dealer.openExpansionsPointerFile(pack);
            String jasonString = IOUtils.toString(is, "UTF-8");

            JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
            JsonArray expansions = jason.get("expansions").getAsJsonArray();

            for (JsonElement element : expansions) {
                JsonObject o = element.getAsJsonObject();
                String uniqueName = MAtUtil.sanitizeUniqueName(o.get("uniquename").getAsString());
                String friendlyName = o.get("friendlyname").getAsString();
                String pointer = o.get("pointer").getAsString();
                ResourceLocation location = new ResourceLocation("matmos", pointer);
                if (pack.resourceExists(location)) {
                    addExpansion(new ExpansionIdentity(uniqueName, friendlyName, pack, location));
                } else {
                    Matmos.LOGGER.warn("An expansion pointer doesn't exist: " + pointer);
                }
            }
        } catch (Exception e) {
            Matmos.LOGGER.warn(pack + " " + "has failed with an error: " + e.getMessage());
        }
    }

    private void addExpansion(ExpansionIdentity identity) {
        Expansion expansion = new Expansion(identity, data, collector, accessor, this, new File(userconfigFolder, identity.getUniqueName() + ".cfg"));
        expansions.put(identity.getUniqueName(), expansion);

        if (identity.getLocation().getPath().endsWith(".xml")) {
            File folder = new File(userconfigFolder, "DO NOT EDIT UNLESS COPIED/");
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String filename = identity.getUniqueName() + ".json";
            if (filename.startsWith("legacy__")) {
                filename = filename.substring("legacy__".length());
            }
            expansion.setLoadingAgent(new LegacyXMLLoadingAgent(new File(folder, filename)));
        } else {
            expansion.setLoadingAgent(new JsonLoadingAgent());
        }

        expansion.updateVolume();
    }

    public Map<String, Expansion> getExpansions() {
        return expansions;
    }

    private void synchronizeStable(Expansion expansion) {
        if (expansion == null) {
            return;
        }

        if (expansion.isActivated()) {
            if (expansion.getVolume() <= 0) {
                expansion.deactivate();
            }
        } else if (expansion.getVolume() > 0) {
            expansion.activate();
        }
    }

    public void synchronize() {
        expansions.values().forEach(this::synchronizeStable);
    }

    @Override
    public void onFrame(float f) {
        expansions.values().forEach(Expansion::simulate);
    }

    @Override
    public void onTick() {
        expansions.values().forEach(Expansion::evaluate);
    }

    public void setData(DataPackage data) {
        this.data = data;
    }

    public void setCollector(Collector collector) {
        this.collector = collector;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVolumeAndUpdate(float volume) {
        this.volume = volume;
        updateVolume();
    }

    @Override
    public void updateVolume() {
        expansions.values().forEach(Expansion::updateVolume);
    }

    public void interrupt() {
        expansions.values().forEach(Expansion::interrupt);
    }

    public void dispose() {
        expansions.values().forEach(Expansion::dispose);
    }

    public void saveConfig() {
        expansions.values().forEach(Expansion::saveConfig);
    }
}
