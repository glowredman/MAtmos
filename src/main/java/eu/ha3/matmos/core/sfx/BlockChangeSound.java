package eu.ha3.matmos.core.sfx;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import eu.ha3.matmos.core.Named;
import eu.ha3.matmos.core.expansion.ExpansionManager;
import eu.ha3.matmos.util.BlockPos;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.haddon.supporting.SupportsBlockChangeEvents.ClickType;
import eu.ha3.mc.haddon.supporting.event.BlockChangeEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;

public class BlockChangeSound implements Named {
    
    private static final Random random = new Random();
    
    private String name;
    
    private boolean onPlace, onBreak;
    
    private List<Integer> blockIDs = new LinkedList<>();
    
    private String sound;
    
    private float volMin, volMax, pitchMin, pitchMax;
    
    public BlockChangeSound(String name, List<String> when, List<String> blocks, String sound, float volMin, float volMax, float pitchMin, float pitchMax) {
        this.name = name;
        
        this.onPlace = when.contains("onplace");
        this.onBreak = when.contains("onbreak");
        
        this.pitchMin = pitchMin;
        this.pitchMax = pitchMax;
        this.volMin = volMin;
        this.volMax = volMax;
        
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
    
    public void onBlockChange(BlockChangeEvent e) {
        boolean accept = false;
        
        if(onPlace && e.oldBlock instanceof BlockAir) {
            int placedID = ExpansionManager.dealiasID(Block.getIdFromBlock(e.newBlock));
            
            if(blockIDs.contains(placedID)) {
                accept = true;
            }   
        }
        if(onBreak && e.newBlock instanceof BlockAir) {
            int brokeID = ExpansionManager.dealiasID(Block.getIdFromBlock(e.oldBlock));
            if(blockIDs.contains(brokeID)) {
                accept = true;
            }
        }
        
        if(accept) {
            play(e.x, e.y, e.z);
        }
    }
    
    public void play(int x, int y, int z) {
        MAtUtil.playSound(sound, x, y, z, MAtUtil.randomFloatRange(volMin, volMax), MAtUtil.randomFloatRange(pitchMin, pitchMax));
    }

}
