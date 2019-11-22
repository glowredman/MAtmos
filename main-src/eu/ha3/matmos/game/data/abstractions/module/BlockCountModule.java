package eu.ha3.matmos.game.data.abstractions.module;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import eu.ha3.matmos.engine.core.interfaces.Data;
import eu.ha3.matmos.game.system.MAtmosUtility;
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
public class BlockCountModule extends ModuleProcessor implements Module
{
	private int[] counts = new int[4096]; // TODO metadata
	private TreeMap<Integer, Integer>[] metadatas = new TreeMap[4096];
	private HashMap<Class, Integer> countsMap = new HashMap<Class, Integer>();
	
	ModuleProcessor thousand;
	
	public BlockCountModule(Data data, String name)
	{
		this(data, name, false, null);
	}
	
	public BlockCountModule(Data data, String name, boolean doNotUseDelta, ModuleProcessor thousand)
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
	
	Class lastClass = null;
	int lastID = -1;
	int blocksCounted = 0;
	
	public void increment(Block block, int meta)
	{
		//Integer count = cunts.get(block.getClass());
		//cunts.put(block.getClass(), count == null ? 0 : count + 1);
		
		int id = -1;
		if(lastID == -1 || block.getClass() != lastClass) {
			lastClass = block.getClass();
			lastID = Block.getIdFromBlock(block);
		}
		
		id = lastID;
		
		counts[id]++;
		
		if(metadatas[id] == null) {
			metadatas[id] = new TreeMap<Integer, Integer>();
		}
		
		if(meta != -1) {
			Integer metaCount = metadatas[id].get(meta);
			metadatas[id].put(meta, metaCount == null ? 0 : metaCount + 1);
		}
		
		blocksCounted++;
		
	}
	
	protected void count() {}
	
	public void apply()
	{
		for(int i = 0; i < counts.length; i++) {
			if(counts[i] > 0) {
				String name = MAtmosUtility.nameOf(Block.getBlockById(i));
				this.setValue(name, counts[i]);
				if(thousand != null) {
					float flaot = counts[i] / (float)blocksCounted * 1000f;
					thousand.setValue(name, (int)Math.ceil(flaot));
				}
				
				counts[i] = 0;
			}
			if(metadatas[i] != null) {
				for(Entry<Integer, Integer> entry : metadatas[i].entrySet()) {
					this.setValue(MAtmosUtility.asPowerMeta(Block.getBlockById(i), entry.getKey()), entry.getValue());
				}
				metadatas[i].clear();
			}
		}
		/*for(Entry<Class, Integer> entry : cunts.entrySet()) {
			Block inst = null;
			try {
				inst = (Block)entry.getKey().newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(inst != null) {
				String name = MAtmosUtility.nameOf(inst);
				this.setValue(name, entry.getValue());
			}
		}*/
		countsMap.clear();
		blocksCounted = 0;
	}
	
}