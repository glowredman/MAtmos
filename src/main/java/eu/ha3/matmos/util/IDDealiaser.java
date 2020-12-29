package eu.ha3.matmos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.IntStream;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class IDDealiaser {

    private Map<Integer, Integer> dealiasMap;

    public IDDealiaser(File aliasFile) {
        loadAliasFile(aliasFile);

        compile();
    }

    private void loadAliasFile(File aliasFile) {
        ConfigManager.createDefaultConfigFileIfMissing(aliasFile);

        Properties props = new Properties();

        try (FileReader reader = new FileReader(aliasFile)) {
            props.load(reader);
        } catch (FileNotFoundException e) {
            Matmos.LOGGER.warn("Alias file (" + aliasFile.getPath() + ") is missing");
        } catch (IOException e) {
            Matmos.LOGGER.error("Error loading alias file (" + aliasFile.getPath() + "): " + e);
        }

        dealiasMap = new HashMap<Integer, Integer>();

        props.stringPropertyNames().forEach(k -> Arrays.stream(props.getProperty(k).split(",")).forEach(v -> {
            if (Block.blockRegistry.containsKey(k) && Block.blockRegistry.containsKey(v)) {
                Object keyObj = Block.blockRegistry.getObject(k);
                Object valueObj = Block.blockRegistry.getObject(v);
                if (keyObj instanceof Block && valueObj instanceof Block) {
                    dealiasMap.put(Block.getIdFromBlock((Block) valueObj), Block.getIdFromBlock((Block) keyObj));
                }
            }
            if (Item.itemRegistry.containsKey(k) && Item.itemRegistry.containsKey(v)) {
                Object keyObj = Item.itemRegistry.getObject(k);
                Object valueObj = Item.itemRegistry.getObject(v);
                if (keyObj instanceof Item && valueObj instanceof Item) {
                    dealiasMap.put(Item.getIdFromItem((Item) valueObj), Item.getIdFromItem((Item) keyObj));
                }
            }
        }));
    }

    private void compile() {
        for (Integer i : dealiasMap.keySet()) {
            if (dealiasMap.containsKey(i)) {
                int id = i;
                do {
                    id = dealiasMap.get(id);

                } while (dealiasMap.containsKey(id) && dealiasMap.get(id) != id);

                if (id == i) {
                    Matmos.LOGGER.warn("Circular dependency detected in alias file when dealiasing "
                            + Block.blockRegistry.getNameForObject(Block.getBlockById(i)) + ". Alias will be ignored.");
                    dealiasMap.remove(i);
                } else { // OK
                    dealiasMap.put(i, id);
                }
            }
        }
        
        if(ConfigManager.getConfig().getBoolean("dealias.oredict")) {
            for(String oreName : OreDictionary.getOreNames()) {
                List<Block> blocks = new ArrayList<>();
                for(ItemStack s : OreDictionary.getOres(oreName)) {
                    if(s.getItem() instanceof ItemBlock) {
                        blocks.add(((ItemBlock)s.getItem()).blockInstance);
                    }
                }
                
                int[] ids = blocks.stream().mapToInt(b -> Block.getIdFromBlock(b)).toArray();
                int minBlockID = IntStream.of(ids).min().orElse(-1);
                if(minBlockID != -1) {
                    IntStream.of(ids).forEach(i -> {if(!dealiasMap.containsKey(i)) dealiasMap.put(i, minBlockID);});
                }
            }
        }
    }

    public int dealiasID(int alias) {
        return dealiasMap.getOrDefault(alias, alias);
    }

}
