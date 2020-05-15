package eu.ha3.matmos.core.expansion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.ResourcePackDealer;
import eu.ha3.matmos.core.ducks.ISoundHandler;
import eu.ha3.matmos.core.expansion.agents.JsonLoadingAgent;
import eu.ha3.matmos.core.expansion.agents.LegacyXMLLoadingAgent;
import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.data.IDataCollector;
import eu.ha3.matmos.data.modules.BlockCountModule;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.util.ResourceLocation;

public class ExpansionManager implements VolumeUpdatable, SupportsTickEvents, SupportsFrameEvents {
    private final ISoundHandler accessor;
    private final File userconfigFolder;

    private final ResourcePackDealer dealer = new ResourcePackDealer();
    private final List<SoundpackIdentity> soundpackIdentities = new ArrayList<SoundpackIdentity>();
    private final Map<String, Expansion> expansions = new HashMap<>();
    private static int[] dealiasMap;

    private DataPackage data;

    private float volume = 1;

    private IDataCollector collector;

    public ExpansionManager(File userconfigFolder, File aliasFile, ISoundHandler accessor) {
        this.userconfigFolder = userconfigFolder;
        this.accessor = accessor;

        if (!this.userconfigFolder.exists()) {
            this.userconfigFolder.mkdirs();
        }
        
        if(dealiasMap == null) {
            dealiasMap = buildDealiasMap(aliasFile);
        }
    }
    
    private int[] buildDealiasMap(File aliasFile){
        Properties props = new Properties();
        
        try(FileReader reader = new FileReader(aliasFile)){
            props.load(reader);
        } catch (FileNotFoundException e) {
            Matmos.LOGGER.warn("Alias file (" + aliasFile.getPath() + ") is missing");
        } catch (IOException e) {
            Matmos.LOGGER.error("Error loading alias file (" + aliasFile.getPath() + "): " + e);
        }
        
        int[] dealiasMap = new int[BlockCountModule.MAX_ID];
        for(int i = 0; i < dealiasMap.length; i++) dealiasMap[i] = i;
        
        props.stringPropertyNames().forEach(k -> Arrays.stream(props.getProperty(k).split(",")).forEach(v -> {
            if(Block.blockRegistry.containsKey(k) && Block.blockRegistry.containsKey(v)) {
                Object keyObj = Block.blockRegistry.getObject(k);
                Object valueObj = Block.blockRegistry.getObject(v);
                if(keyObj instanceof Block && valueObj instanceof Block) {
                    dealiasMap[Block.getIdFromBlock((Block)valueObj)] = Block.getIdFromBlock((Block)keyObj);
                }
            }
        }));
        
        return dealiasMap;
    }

    public void loadExpansions() {
        dispose();

        dealer.findResourcePacks().forEach(this::readSoundPack);
    }
    
    private void readSoundPack(IResourcePack pack) {
        try (   InputStream matPackStream = dealer.openMatPackPointerFile(pack);
                InputStream expansionsStream = dealer.openExpansionsPointerFile(pack)){
            String matPackJSONString = IOUtils.toString(matPackStream, "UTF-8");
            String expansionsJSONString = IOUtils.toString(expansionsStream, "UTF-8");
            
            if(readMatPackFile(new JsonParser().parse(matPackJSONString).getAsJsonObject())) {
                readExpansionsFile(new JsonParser().parse(expansionsJSONString).getAsJsonObject(), pack);
            }
        } catch (Exception e) {
            Matmos.LOGGER.warn(pack + " " + "has failed with an error: " + e.getMessage());
        }
    }
    
    /*** Returns false if reading the pack should be cancelled */ 
    private boolean readMatPackFile(JsonObject matPackRoot) {
        soundpackIdentities.add(new SoundpackIdentity(matPackRoot));
        
        return true;
    }
    
    private void readExpansionsFile(JsonObject expansionsRoot, IResourcePack pack) {
        JsonArray expansions = expansionsRoot.get("expansions").getAsJsonArray();

        for (JsonElement element : expansions) {
            JsonObject o = element.getAsJsonObject();
            String uniqueName = MAtUtil.sanitizeUniqueName(o.get("uniquename").getAsString());
            String friendlyName = o.get("friendlyname").getAsString();
            float volumeModifier = o.has("volumemodifier") ? o.get("volumemodifier").getAsFloat() : 1;
            String pointer = o.get("pointer").getAsString();
            ResourceLocation location = new ResourceLocation("matmos", pointer);
            if (pack.resourceExists(location)) {
                addExpansion(new ExpansionIdentity(uniqueName, friendlyName, pack, location, volumeModifier));
            } else {
                Matmos.LOGGER.warn("An expansion pointer doesn't exist: " + pointer);
            }
        }
    }

    private void addExpansion(ExpansionIdentity identity) {
        Expansion expansion = new Expansion(identity, data, collector, accessor, this, new File(userconfigFolder, identity.getUniqueName() + ".cfg"));
        expansions.put(identity.getUniqueName(), expansion);

        if (identity.getLocation().getResourcePath().endsWith(".xml")) {
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
    
    public List<SoundpackIdentity> getSoundpackIdentities(){
        return soundpackIdentities;
    }
    
    public static int dealiasID(int alias) {
        return dealiasMap[alias];
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
        Minecraft.getMinecraft().mcProfiler.startSection("expansionmanager");
        expansions.values().forEach(Expansion::evaluate);
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    public void setData(DataPackage data) {
        this.data = data;
    }

    public void setCollector(IDataCollector collector) {
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
