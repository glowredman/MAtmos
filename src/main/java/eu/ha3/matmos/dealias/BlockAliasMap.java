package eu.ha3.matmos.dealias;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class BlockAliasMap extends ObjectAliasMap {

    @Override
    public String getLogPrefix() {
        return "[BlockAliasMap] ";
    }

    @Override
    public int getIDFromName(String s) {
        return Block.REGISTRY.getIDForObject(Block.REGISTRY.getObject(new ResourceLocation(s)));
    }

    @Override
    public int getItemID(Item i) {
        if(i instanceof ItemBlock) {
            return Block.getIdFromBlock(((ItemBlock)i).getBlock());
        } else {
            return -1;
        }
    }

    @Override
    public String getNameFromID(int i) {
        return String.valueOf(Block.REGISTRY.getNameForObject(Block.getBlockById(i)));
    }

}
