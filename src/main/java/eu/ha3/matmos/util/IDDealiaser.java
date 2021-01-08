package eu.ha3.matmos.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.hash.Hashing;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.sheet.DataPackage;
import net.minecraft.block.Block;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraftforge.oredict.OreDictionary;

import static eu.ha3.matmos.util.MAtUtil.getParentSafe;

public class IDDealiaser {
    
    // the hash of alias.map in versions before we started bundling builtin alias maps
    private static final String OLD_ALIAS_MAP_SHA256 = 
            "e2fbd8793250808dc2816ab4aba16a2f150a918cb02670b6dca4ec3be4f63469";
    
    private Map<Integer, Integer> dealiasMap = new HashMap<>();

    public IDDealiaser(File configFolder) {
        ConfigManager.createDefaultConfigFileIfMissing(new File(configFolder, "builtin_aliases"), true);
        
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
        
        if(ConfigManager.getConfig().getBoolean("dealias.guessfromclass")) {
            guessAliases();
        }
        
        loadAliasFile(new File(configFolder, "alias.map"));
        
        compile();
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
                        e.warn("Ignoring invalid oredict name in alias map: " + v);
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
                                e.warn("No name matched pattern " + v);
                            }
                        } catch (Exception e2) {
                            e.warn("Invalid pattern: " + v + " (" + e2 + ")");
                        }
                    } else {
                        int vi = getIDFromName(v);
                        if(vi > 0) {
                            dealiasMap.put(vi, ki);
                        } else {
                            e.warn("Ignoring invalid name in alias map: " + v);
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
    
    private void guessAliases() {
        int swordID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("iron_sword"));
        int wswordID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("wooden_sword"));
        int pickID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("iron_pickaxe"));
        int wpickID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("wooden_pickaxe"));
        int shovelID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("iron_shovel"));
        int wshovelID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("wooden_shovel"));
        int axeID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("iron_axe"));
        int waxeID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("wooden_axe"));
        int hoeID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("iron_hoe"));
        int whoeID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("wooden_hoe"));
        int bowID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("bow"));
        int appleID = Item.itemRegistry.getIDForObject(Item.itemRegistry.getObject("apple"));
        
        for(Object o : Item.itemRegistry) {
            int id = Item.itemRegistry.getIDForObject(o);
            String name = Item.itemRegistry.getNameForObject(o);
            if(name.startsWith("minecraft:")) {
                continue;
            }
            
            boolean wooden = name.toLowerCase().contains("wood");
            int target = -1;
            
            Set<String> toolClasses = o instanceof ItemTool ? ((ItemTool)o).getToolClasses(new ItemStack((Item)o)) : new TreeSet<String>();
            
            if(o instanceof ItemSword || toolClasses.contains("sword")) {
                target = wooden ? wswordID : swordID;
            } else if(o instanceof ItemPickaxe || toolClasses.contains("pickaxe")) {
                target = wooden ? wpickID : pickID;
            } else if(o instanceof ItemSpade || toolClasses.contains("shovel")) {
                target = wooden ? wshovelID : shovelID;
            } else if(o instanceof ItemAxe || toolClasses.contains("axe")) {
                target = wooden ? waxeID : axeID;
            } else if(o instanceof ItemHoe || toolClasses.contains("hoe")) {
                target = wooden ? whoeID : hoeID;
            } else if(o instanceof ItemBow || toolClasses.contains("bow")) {
                target = bowID;
            } else if(o instanceof Item && ((Item)o).getItemUseAction(new ItemStack((Item)o)) == EnumAction.eat) {
                target = appleID;
            }
            
            if(target != -1) {
                Matmos.LOGGER.debug("Guessing alias " + 
                    Item.itemRegistry.getNameForObject(Item.itemRegistry.getObjectById(target)) + 
                    " for " + name);
                dealiasMap.put(id, target);
            }
        }
    }

    private void compile() {
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
    
    class AliasEntry implements Entry<String, String>{
        private String key;
        private String value;
        private String path;
        private boolean showWarnings;
        private int lineno;
        
        public AliasEntry(String key, String value, String path, int lineno, boolean showWarnings) {
            this.key = key;
            this.value = value;
            this.path = path;
            this.lineno = lineno;
            this.showWarnings = showWarnings;
        }
        
        public AliasEntry(String key, String path, int lineno, String value) {
            this(key, value, path, lineno, true);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public String getValue() {
            return value;
        }

        @Override
        public String setValue(String value) {
            String oldValue = this.value;
            this.value = value;
            return oldValue;
        }
        
        public boolean doesShowWarnings() {
            return showWarnings;
        }
        
        public void warn(String msg) {
            if(showWarnings || ConfigManager.getConfig().getBoolean("debug.verbosealiasparsing")) {
                Matmos.LOGGER.warn(path + ":" + lineno + ": " + msg);
            }
        }
        
        public String getPath() {
            return path;
        }
        
        public int getLineNumber() {
            return lineno;
        }
    }
    
}
