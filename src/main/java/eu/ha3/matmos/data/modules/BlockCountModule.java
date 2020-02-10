package eu.ha3.matmos.data.modules;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import eu.ha3.matmos.core.sheet.DataPackage;
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
	private int[] counts = new int[4096];
	private TreeMap<Integer, Integer>[] metadatas = new TreeMap[4096];
	
	VirtualCountModule<Pair<Block, Integer>> thousand;
	
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
	}
	
	@Override
	protected void doProcess()
	{
		count();
		apply();
	}
	
	int blocksCounted = 0;
	
	public void increment(Pair<Block, Integer> blockMeta)
	{	
	    Block block = blockMeta.getLeft();
	    int meta = blockMeta.getRight();
	    
		int id = Block.getIdFromBlock(block);
		
		counts[id]++;
		
		if(meta != -1 && meta != 0) {
			if(metadatas[id] == null) {
				metadatas[id] = new TreeMap<Integer, Integer>();
			}
			Integer metaCount = metadatas[id].get(meta);
			metadatas[id].put(meta, metaCount == null ? 0 : metaCount + 1);
		}
		
		blocksCounted++;
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
			if(counts[i] > 0) {
				String name = MAtUtil.nameOf(Block.getBlockById(i));
				this.setValue(name, counts[i]);
				if(thousand != null) {
					float flaot = counts[i] / (float)blocksCounted * 1000f;
					thousand.setValue(name, (int)Math.ceil(flaot));
				}
				
				counts[i] = 0;
			}
			if(metadatas[i] != null) {
				for(Entry<Integer, Integer> entry : metadatas[i].entrySet()) {
					this.setValue(MAtUtil.asPowerMeta(Block.getBlockById(i), entry.getKey()), entry.getValue());
				}
				metadatas[i].clear();
			}
		}
		
		blocksCounted = 0;
	}
	
}