package eu.ha3.matmos.dealias;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;

public class ItemAliasMap extends ObjectAliasMap {
    
    protected void guessAliases() {
        int swordID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("iron_sword")));
        int wswordID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("wooden_sword")));
        int pickID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("iron_pickaxe")));
        int wpickID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("wooden_pickaxe")));
        int shovelID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("iron_shovel")));
        int wshovelID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("wooden_shovel")));
        int axeID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("iron_axe")));
        int waxeID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("wooden_axe")));
        int hoeID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("iron_hoe")));
        int whoeID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("wooden_hoe")));
        int bowID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("bow")));
        int appleID = Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation("apple")));
        
        for(Object o : Item.REGISTRY) {
            if(!(o instanceof Item)) continue;
            
            int id = Item.REGISTRY.getIDForObject((Item)o);
            String name = Item.REGISTRY.getNameForObject((Item)o).toString();
            if(name.startsWith("minecraft:")) {
                continue;
            }
            
            boolean wooden = name.toLowerCase().contains("wood");
            int target = -1;
            
            Set<String> toolClasses = ((Item)o).getToolClasses(new ItemStack((Item)o));
            if(ConfigManager.getConfig().getInteger("debug.mode") == 1) {
                Matmos.LOGGER.debug("Tool classes for " + name + " (" + id + "): " + Arrays.toString(toolClasses.toArray()));
            }
            
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
            } else if(((Item)o).getItemUseAction(new ItemStack((Item)o)) == EnumAction.EAT) {
                target = appleID;
            }
            
            if(target != -1) {
                if(ConfigManager.getConfig().getInteger("debug.mode") == 1) {
                    Matmos.LOGGER.debug("Guessing alias " + 
                        Item.REGISTRY.getNameForObject(Item.REGISTRY.getObjectById(target)) + 
                        " for " + name);
                }
                dealiasMap.put(id, target);
            }
        }
    }

    @Override
    public String getLogPrefix() {
        return "[ItemAliasMap] ";
    }

    @Override
    public int getIDFromName(String s) {
        return Item.REGISTRY.getIDForObject(Item.REGISTRY.getObject(new ResourceLocation(s)));
    }

    @Override
    public int getItemID(Item i) {
        return Item.getIdFromItem(i);
    }

    @Override
    public String getNameFromID(int i) {
        return String.valueOf(Item.REGISTRY.getNameForObject(Item.REGISTRY.getObjectById(i)));
    }

}
