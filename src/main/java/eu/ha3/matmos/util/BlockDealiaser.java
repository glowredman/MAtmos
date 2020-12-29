package eu.ha3.matmos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class BlockDealiaser {

    private int[] dealiasMap;

    public BlockDealiaser(File aliasFile) {
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

        dealiasMap = new int[Matmos.MAX_ID];
        for (int i = 0; i < dealiasMap.length; i++)
            dealiasMap[i] = i;

        props.stringPropertyNames().forEach(k -> Arrays.stream(props.getProperty(k).split(",")).forEach(v -> {
            if (Block.blockRegistry.containsKey(k) && Block.blockRegistry.containsKey(v)) {
                Object keyObj = Block.blockRegistry.getObject(k);
                Object valueObj = Block.blockRegistry.getObject(v);
                if (keyObj instanceof Block && valueObj instanceof Block) {
                    dealiasMap[Block.getIdFromBlock((Block) valueObj)] = Block.getIdFromBlock((Block) keyObj);
                }
            }
        }));
    }

    private void compile() {
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
                    IntStream.of(ids).forEach(i -> dealiasMap[i] = minBlockID);
                }
            }
        }
        
        for (int i = 0; i < dealiasMap.length; i++) {
            if (dealiasMap[i] != i) {
                int id = dealiasMap[i];
                do {
                    id = dealiasMap[id];

                } while (dealiasMap[id] != dealiasMap[id] && id != i);

                if (id == i) {
                    Matmos.LOGGER.warn("Circular dependency detected in alias file when dealiasing "
                            + Block.blockRegistry.getNameForObject(Block.getBlockById(i)) + ". Alias will be ignored.");
                    dealiasMap[i] = i;
                } else { // OK
                    dealiasMap[i] = id;
                }
            }
        }
    }

    public int dealiasID(int alias) {
        return dealiasMap[alias];
    }

}
