package eu.ha3.matmos.data.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.SheetDataPackage;
import eu.ha3.matmos.util.MAtUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

/*
--filenotes-placeholder
*/

/**
 * An abstract module that specializes in counting things in one pass.
 * 
 * @author Hurry
 */
public class BlockCountModule extends AbstractThingCountModule<Pair<Block, Integer>>
{
    public static final int MAX_ID = 4096;
    
    private boolean[] wasZero = new boolean[MAX_ID];
	private int[] counts = new int[MAX_ID];
	private int[] BLANK_COUNTS = new int[MAX_ID];
	
	private int[] zeroMetadataCounts = new int[MAX_ID];
	private TreeMap<Integer, Integer>[] metadatas = new TreeMap[MAX_ID];
	
	VirtualCountModule<Pair<Block, Integer>> thousand;
	
	SheetDataPackage sheetData; 
	
	public BlockCountModule(DataPackage data, String name)
	{
		this(data, name, false, null);
	}
	
	public BlockCountModule(DataPackage data, String name, boolean doNotUseDelta, VirtualCountModule<Pair<Block, Integer>> thousand)
	{
		super(data, name, doNotUseDelta);
		
		this.thousand = thousand;
		
		data.getSheet(name).setDefaultValue("0");
		if (!doNotUseDelta)
		{
			data.getSheet(name + DELTA_SUFFIX).setDefaultValue("0");
		}
		
		sheetData = (SheetDataPackage)data;
	}
	
	@Override
	protected void doProcess()
	{
		count();
		apply();
	}
	
	int blocksCounted = 0;
	
	public void increment(Pair<Block, Integer> blockMeta) {
	    increment(blockMeta, 1);
	}
	
	public void increment(Pair<Block, Integer> blockMeta, int amount)
	{	
	    Block block = blockMeta.getLeft();
	    int meta = blockMeta.getRight();
	    
		int id = sheetData.dealiasID(Block.getIdFromBlock(block));
		
		counts[id] += amount;
		
		if(meta != -1 && meta != 0) {
			if(metadatas[id] == null) {
				metadatas[id] = new TreeMap<Integer, Integer>();
			}
			Integer metaCount = metadatas[id].get(meta);
			metadatas[id].put(meta, metaCount == null ? 0 : metaCount + amount);
		} else if(meta == 0) {
		    zeroMetadataCounts[id] += amount;
		}
		
		blocksCounted += amount;
	}
	
	// for debugging
	public int get(Pair<Block, Integer> blockMeta) {
	    Block block = blockMeta.getLeft();
        int meta = blockMeta.getRight();
        
		int id=Block.getIdFromBlock(block);
		if(meta == -1) {
			return counts[id];
		} else {
			return metadatas[id].get(meta);
		}
	}
	
	public void count() {}
	
	public void apply()
	{
		for(int i = 0; i < counts.length; i++) {
		    boolean isZero = true;
			if(counts[i] > 0 || !wasZero[i]) {
			    isZero &= counts[i] == 0;
				
			    String name = MAtUtil.nameOf(Block.getBlockById(i));
				this.setValue(name, counts[i]);
				
				if(thousand != null) {
					float flaot = counts[i] / (float)blocksCounted * 1000f;
					thousand.setValue(name, (int)Math.ceil(flaot));
				}
			}
			if(zeroMetadataCounts[i] > 0 || !wasZero[i]) {
			    isZero &= zeroMetadataCounts[i] == 0;
			    
			    String name = MAtUtil.asPowerMeta(Block.getBlockById(i), 0);
                this.setValue(name, zeroMetadataCounts[i]);
                
                if(thousand != null) {
                    float flaot = zeroMetadataCounts[i] / (float)blocksCounted * 1000f;
                    thousand.setValue(name, (int)Math.ceil(flaot));
                }
            }
			if(metadatas[i] != null) {
				for(Entry<Integer, Integer> entry : metadatas[i].entrySet()) {
				    int value = entry.getValue();
				    
				    isZero &= value == 0;
				    
				    if(value > 0 || !wasZero[i]) {
    				    String name = MAtUtil.asPowerMeta(Block.getBlockById(i), entry.getKey());
    					this.setValue(name, value);
    					
    					if(thousand != null) {
    	                    float flaot = value / (float)blocksCounted * 1000f;
    	                    thousand.setValue(name, (int)Math.ceil(flaot));
    	                }
				    }
				}
			}
			wasZero[i] = isZero;
		}
		
		blocksCounted = 0;
		
		System.arraycopy(BLANK_COUNTS, 0, counts, 0, counts.length);
		System.arraycopy(BLANK_COUNTS, 0, zeroMetadataCounts, 0, counts.length);
		Arrays.stream(metadatas).forEach(m -> {if(m != null) m.replaceAll((k, v) -> 0);} );
		
	}
	
}