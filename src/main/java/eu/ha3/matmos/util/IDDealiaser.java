package eu.ha3.matmos.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

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
    
    private List<Pair<String, String>> loadEntries(List<Pair<String, String>> entries, String aliasDir, String path, Set<String> visited){
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
                        Matmos.LOGGER.warn(String.format("%s:%d: Invalid directive: %s", path, lineno, directive));
                    }
                } else {
                    String[] sides = line.split("=");
                    String key = sides[0];
                    if(sides.length == 2) 
                        if(!key.equals("") && !sides[1].equals("")) {    
                            String[] values = sides[1].split(",");
                            if(values.length > 0) {
                                for(String value: values) {
                                    entries.add(Pair.of(key, value));
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
        List<Pair<String, String>> entries = loadEntries(new LinkedList<Pair<String, String>>(), aliasDir, aliasFile.getName(), new HashSet<String>());

        dealiasMap = new HashMap<Integer, Integer>();

        entries.forEach(e -> {
            String k = e.getKey();
            String v = e.getValue();
            int ki = getIDFromName(k);
            if(ki > 0) {
                if(v.startsWith(":")) {
                    String oreName = v.substring(1);
                    List<ItemStack> ores = OreDictionary.getOres(oreName);
                    if(!ores.isEmpty()) {
                        for(ItemStack is : ores) {
                            int id = getItemID(is.getItem());
                            if(id > 0) {
                                dealiasMap.put(id, ki);
                            }
                        }
                    } else {
                        Matmos.LOGGER.warn("Ignoring invalid oredict name in alias map: " + k);
                    }
                } else {
                    if(v.contains("*")) {
                        try {
                            Pattern pattern = makePattern(v);
                            boolean matchedName = false;
                            for(Object ko : Item.itemRegistry.getKeys()){
                                String name = (String)ko;
                                if(pattern.matcher(name).matches() ||
                                        (name.startsWith("minecraft:") && pattern.matcher(name.substring("minecraft:".length())).matches())) {
                                    matchedName = true;
                                    int vi = getIDFromName(name);
                                    if(vi > 0) {
                                        dealiasMap.put(vi, ki);
                                    }
                                }
                            }
                            if(!matchedName) {
                                Matmos.LOGGER.warn("No name matched pattern " + v);
                            }
                        } catch (Exception e2) {
                            Matmos.LOGGER.warn("Invalid pattern: " + v + " (" + e2 + ")");
                        }
                    } else {
                        int vi = getIDFromName(v);
                        if(vi > 0) {
                            dealiasMap.put(vi, ki);
                        } else {
                            Matmos.LOGGER.warn("Ignoring name in alias map: " + k);
                        }
                    }
                }
            } else {
                Matmos.LOGGER.warn("Ignoring invalid name in alias map: " + k);
            }
        });
    }
    
    Pattern makePattern(String str) throws Exception {
        str = str.replace(".", "\\.").replace("*", ".*");
        
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(str);
        } catch (PatternSyntaxException e) {
            throw e;
        }
        return pattern;
    }
    
    private int getItemID(Item item) {
        if(item instanceof ItemBlock) {
            return Block.getIdFromBlock(((ItemBlock)item).blockInstance);
        } else {
            return Item.getIdFromItem(item);
        }
    }
    
    private int getIDFromName(String name) {
        if(Block.blockRegistry.containsKey(name)) {
            return Block.getIdFromBlock(Block.getBlockFromName(name));
        } else {
            return Item.getIdFromItem((Item)Item.itemRegistry.getObject(name));
        }
    }

    private void compile() {
        if(ConfigManager.getConfig().getBoolean("dealias.oredict")) {
            for(String oreName : OreDictionary.getOreNames()) { 
                List<Integer> ids = new ArrayList<>();
                for(ItemStack s : OreDictionary.getOres(oreName)) {
                    ids.add(getItemID(s.getItem()));
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
            int hops = 0;
            do {
                id = dealiasMap.get(id);
                hops++;
            } while (dealiasMap.containsKey(id) && dealiasMap.get(id) != id);

            if (id == i && hops > 1) {
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
