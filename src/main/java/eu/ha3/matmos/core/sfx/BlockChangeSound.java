package eu.ha3.matmos.core.sfx;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import eu.ha3.matmos.core.Named;
import eu.ha3.matmos.core.expansion.ExpansionManager;
import eu.ha3.matmos.util.BlockPos;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.supporting.SupportsBlockChangeEvents.ClickType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;

public class BlockChangeSound implements Named {
    
    private String name;
    
    private boolean onPlace, onBreak;
    
    private List<Integer> blockIDs = new LinkedList<>();
    
    private String sound;
    
    public BlockChangeSound(String name, List<String> when, List<String> blocks, String sound) {
        this.name = name;
        
        this.onPlace = when.contains("onplace");
        this.onBreak = when.contains("onbreak");
        
        blocks.forEach(b -> {
            if(Block.blockRegistry.containsKey(b)) {
                this.blockIDs.add(Block.getIdFromBlock((Block)(Block.blockRegistry.getObject(b))));
            }
        });
        
        this.sound = sound;
    }

    @Override
    public String getName() {
        return name;
    }
    
    public void onBlockChange(int x, int y, int z, Block oldBlock, Block newBlock) {
        boolean accept = false;
        
        if(onPlace && oldBlock instanceof BlockAir) {
            int placedID = ExpansionManager.dealiasID(Block.getIdFromBlock(newBlock));
            
            if(blockIDs.contains(placedID)) {
                accept = true;
            }   
        }
        if(onBreak && newBlock instanceof BlockAir) {
            int brokeID = ExpansionManager.dealiasID(Block.getIdFromBlock(oldBlock));
            if(blockIDs.contains(brokeID)) {
                accept = true;
            }
        }
        
        if(accept) {
            play(x, y, z);
        }
    }
    
    public void play(int x, int y, int z) {
        MAtUtil.playSound(sound, x, y, z, 1, 1);
    }

}
