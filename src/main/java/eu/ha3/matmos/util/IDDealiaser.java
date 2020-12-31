package eu.ha3.matmos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;
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
    
    private Map<String, String> loadEntries(Map<String, String> entries, String aliasDir, String path, Set<String> visited){
        try (FileReader reader = new FileReader(new File(aliasDir, path))) {
            int lineno = 0;
            for(String line : IOUtils.readLines(reader)) {
                line = line.trim();
                if(line.startsWith("#")|| line.equals("")) {
                    // do nothing
                } else if(line.startsWith(":")) {
                    String[] words = line.split(" ");
                    String directive = words[0].substring(1);
                    String argument = words[1];
                    
                    if(directive.equals("import")) {
                        if(!visited.contains(argument)){
                            loadEntries(entries, aliasDir, argument, visited);
                        } else {
                            Matmos.LOGGER.warn(String.format("%s:%d: Import cycle detected (%s->...->%s->%s)",
                                    path, lineno, argument, path, argument));
                        }
                    } else {
                        Matmos.LOGGER.warn(String.format("%s:%d: Invalid directive: %s", path, lineno, argument));
                    }
                } else {
                    String[] sides = line.split("=");
                    String key = sides[0];
                    if(sides.length == 2) 
                        if(!key.equals("") && !sides[1].equals("")) {    
                            String[] values = sides[1].split(",");
                            if(values.length > 0) {
                                for(String value: values) {
                                    entries.put(key, value);
                                }
                            }
                        } else {
                            Matmos.LOGGER.warn(String.format("%s:%d: Assignment has empty side(s)", path, lineno));
                    } else {
                        Matmos.LOGGER.warn(String.format("%s:%d: Invalid alias assignment. Excepted 1 '=', got %d", path, lineno, sides.length - 1));
                    }
                }
                lineno++;
            }
        } catch (FileNotFoundException e) {
            Matmos.LOGGER.warn("Alias file " + path + " is missing");
        } catch (IOException e) {
            Matmos.LOGGER.error("Error loading alias file " + path + ": " + e);
        }
        return entries;
    }

    private void loadAliasFile(File aliasFile) {
        ConfigManager.createDefaultConfigFileIfMissing(aliasFile);
        
        String aliasDir = aliasFile.getParent();
        Map<String, String> entries = loadEntries(new HashMap<String, String>(), aliasDir, aliasFile.getName(), new HashSet<String>());

        dealiasMap = new HashMap<Integer, Integer>();

        entries.entrySet().forEach(e -> {
            String k = e.getKey();
            String v = e.getValue();
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
        });
    }

    private void compile() {
        if(ConfigManager.getConfig().getBoolean("dealias.oredict")) {
            for(String oreName : OreDictionary.getOreNames()) { 
                List<Integer> ids = new ArrayList<>();
                for(ItemStack s : OreDictionary.getOres(oreName)) {
                    int id;
                    Item item = s.getItem();
                    if(item instanceof ItemBlock) {
                        id = Block.getIdFromBlock(((ItemBlock)item).blockInstance);
                    } else {
                        id = Item.getIdFromItem(item);
                    }
                    
                    ids.add(id);
                }
                
                int minBlockID = ids.stream().min(Integer::compare).orElse(-1);
                if(minBlockID != -1) {
                    ids.forEach(i -> {if(!dealiasMap.containsKey(i)) dealiasMap.put(i, minBlockID);});
                }
            }
        }
        
        dealiasMap.entrySet().removeIf(e -> {
            Integer i = e.getKey();
            int id = i;
            do {
                id = dealiasMap.get(id);

            } while (dealiasMap.containsKey(id) && dealiasMap.get(id) != id);

            if (id == i) {
                Matmos.LOGGER.warn("Circular dependency detected when dealiasing "
                        + Block.blockRegistry.getNameForObject(Block.getBlockById(i)) + ". Alias will be ignored.");
                return true;
            } else { // OK
                e.setValue(id);
                return false;
            }
        });
    }

    public int dealiasID(int alias) {
        return dealiasMap.getOrDefault(alias, alias);
    }

}
