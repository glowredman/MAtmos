package eu.ha3.matmos.dealias;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static eu.ha3.matmos.util.MAtUtil.getParentSafe;

public class IDDealiaser {
    
    // the hash of alias.map in versions before we started bundling builtin alias maps
    private static final String OLD_ALIAS_MAP_SHA256 = 
            "e2fbd8793250808dc2816ab4aba16a2f150a918cb02670b6dca4ec3be4f63469";
    
    //private Map<Integer, Integer> dealiasMap = new HashMap<>();
    private BlockAliasMap blockAliasMap = new BlockAliasMap();
    private ItemAliasMap itemAliasMap = new ItemAliasMap();

    public IDDealiaser(File configFolder) {
        ConfigManager.createDefaultConfigFileIfMissing(new File(configFolder, "builtin_aliases"), true);
        
        if(ConfigManager.getConfig().getBoolean("dealias.oredict")) {
            for(String oreName : OreDictionary.getOreNames()) {
                List<String> names = new ArrayList<>();
                for(ItemStack s : OreDictionary.getOres(oreName)) {
                    names.add(getItemName(s.getItem()));
                }
                
                if(ConfigManager.getConfig().getInteger("debug.mode") == 1) {
                    Matmos.LOGGER.debug("Items with oredict name " + oreName + ": " + Arrays.toString(names.toArray()));
                }
                
                blockAliasMap.dealiasItemGroupToMinID(names);
                itemAliasMap.dealiasItemGroupToMinID(names);
            }
        }
        
        if(ConfigManager.getConfig().getBoolean("dealias.guessfromclass")) {
            itemAliasMap.guessAliases();
        }
        
        loadAliasFile(new File(configFolder, "alias.map"));
        
        blockAliasMap.compile();
        itemAliasMap.compile();
    }
    
    private List<AliasEntry> loadEntries(List<AliasEntry> entries, Path aliasDir, Path path, Set<Path> visited, boolean showWarnings){
        try (FileReader reader = new FileReader(aliasDir.resolve(path).toFile())) {
            int lineno = 0;
            for(String line : IOUtils.readLines(reader)) {
                line = line.trim();
                if(line.startsWith("#")|| line.equals("")) {
                    // do nothing
                } else if(line.startsWith(":")) {
                    String[] words = line.split(" ");
                    String directive = words[0].substring(1);
                    String argument = words.length > 1 ? words[1] : null;
                    
                    switch(directive) {
                    case "import":
                        Path linkedPath = getParentSafe(path).resolve(Paths.get(argument)).normalize();
                        if(!visited.contains(linkedPath)){
                            loadEntries(entries, aliasDir, linkedPath, visited, showWarnings);
                        } else {
                            Matmos.LOGGER.warn(String.format("%s:%d: Import cycle detected (%s->...->%s->%s)",
                                    path, lineno, linkedPath, path, linkedPath));
                        }
                        break;
                    case "disableWarnings":
                        showWarnings = false;
                        break;
                    default:
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
                                    entries.add(new AliasEntry(key, value, path.toString(), lineno, showWarnings));
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
        Matmos.LOGGER.info("Loading alias map " + aliasFile + "...");
        
        ConfigManager.createDefaultConfigFileIfMissing(aliasFile,
                bytes -> {
                    try {
                        return DigestUtils.sha256Hex(String.join("\n", IOUtils.readLines(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8))).equals(OLD_ALIAS_MAP_SHA256);
                    } catch (IOException e) {
                        // the file is corrupt, whatever
                        return false;
                    }
                });
        
        Path aliasDir = getParentSafe(aliasFile.toPath());
        List<AliasEntry> entries = loadEntries(new LinkedList<AliasEntry>(), aliasDir, aliasDir.relativize(aliasFile.toPath()), new HashSet<Path>(), true);
        
        blockAliasMap.addMappings(entries);
        itemAliasMap.addMappings(entries);
    }
    
    private String getItemName(Item item) {
        if(item instanceof ItemBlock) {
            return Block.blockRegistry.getNameForObject(((ItemBlock)item).blockInstance).toString();
        } else {
            return Item.itemRegistry.getNameForObject(item).toString();
        }
    }
    
    public int dealiasID(int alias, boolean isItem) {
        return isItem ? dealiasItemID(alias) : dealiasBlockID(alias);
    }

    public int dealiasBlockID(int alias) {
        return blockAliasMap.dealiasID(alias);
    }
    
    public int dealiasItemID(int alias) {
        return itemAliasMap.dealiasID(alias);
    }
}
