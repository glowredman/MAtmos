package eu.ha3.matmos.data.modules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.expansion.ExpansionManager;
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
public class BlockCountModule extends AbstractThingCountModule<Pair<Block, Integer>> {
    
	private static final int INITIAL_SIZE = 4096;
	
	private boolean[] wasZero = new boolean[INITIAL_SIZE];
    private int[] counts = new int[INITIAL_SIZE];
    private int[] zeroMetadataCounts = new int[INITIAL_SIZE];
    private TreeMap<Integer, Integer>[] metadatas = new TreeMap[INITIAL_SIZE];

    int size;
    
    VirtualCountModule<Pair<Block, Integer>> thousand;

    SheetDataPackage sheetData;
    
    public BlockCountModule(DataPackage data, String name) {
        this(data, name, false);
    }

    public BlockCountModule(DataPackage data, String name, boolean doNotUseDelta) {
        this(data, name, doNotUseDelta, null);
    }

    public BlockCountModule(DataPackage data, String name, boolean doNotUseDelta,
            VirtualCountModule<Pair<Block, Integer>> thousand) {
        super(data, name, doNotUseDelta);

        this.thousand = thousand;

        data.getSheet(name).setDefaultValue("0");
        if (!doNotUseDelta) {
            data.getSheet(name + DELTA_SUFFIX).setDefaultValue("0");
        }

        sheetData = (SheetDataPackage) data;
    }

    @Override
    protected void doProcess() {
        count();
        apply();
    }

    int blocksCounted = 0;

    public void increment(Pair<Block, Integer> blockMeta) {
        increment(blockMeta, 1);
    }

    public void increment(Pair<Block, Integer> blockMeta, int amount) {
        Block block = blockMeta.getLeft();
        int meta = blockMeta.getRight();

        int id = ExpansionManager.dealiasToID(block, sheetData);
        
        if(id >= size) {
        	resize(id + 1);
        }
        
        counts[id] += amount;

        if (meta != -1 && meta != 0) {
            if (metadatas[id] == null) {
                metadatas[id] = new TreeMap<Integer, Integer>();
            }
            Integer metaCount = metadatas[id].get(meta);
            metadatas[id].put(meta, metaCount == null ? 0 : metaCount + amount);
        } else if (meta == 0) {
            zeroMetadataCounts[id] += amount;
        }

        blocksCounted += amount;
    }

    // for debugging
    public int get(Pair<Block, Integer> blockMeta) {
        Block block = blockMeta.getLeft();
        int meta = blockMeta.getRight();

        int id = Block.getIdFromBlock(block);
        if(id >= size) {
        	return 0;
        } else if (meta == -1) {
            return counts[id];
        } else {
            return metadatas[id].get(meta);
        }
    }

    public void count() {
    }

    public void apply() {
        for (int i = 0; i < counts.length; i++) {
            boolean isZero = true;
            if (counts[i] > 0 || !wasZero[i]) {
                isZero &= counts[i] == 0;

                String name = MAtUtil.nameOf(Block.getBlockById(i));
                this.setValue(name, counts[i]);

                if (thousand != null) {
                    float flaot = counts[i] / (float) blocksCounted * 1000f;
                    thousand.setValue(name, (int) Math.ceil(flaot));
                }
            }
            if (zeroMetadataCounts[i] > 0 || !wasZero[i]) {
                isZero &= zeroMetadataCounts[i] == 0;

                String name = MAtUtil.asPowerMeta(Block.getBlockById(i), 0);
                this.setValue(name, zeroMetadataCounts[i]);

                if (thousand != null) {
                    float flaot = zeroMetadataCounts[i] / (float) blocksCounted * 1000f;
                    thousand.setValue(name, (int) Math.ceil(flaot));
                }
            }
            if (metadatas[i] != null) {
                for (Entry<Integer, Integer> entry : metadatas[i].entrySet()) {
                    int value = entry.getValue();

                    isZero &= value == 0;

                    if (value > 0 || !wasZero[i]) {
                        String name = MAtUtil.asPowerMeta(Block.getBlockById(i), entry.getKey());
                        this.setValue(name, value);

                        if (thousand != null) {
                            float flaot = value / (float) blocksCounted * 1000f;
                            thousand.setValue(name, (int) Math.ceil(flaot));
                        }
                    }
                }
            }
            wasZero[i] = isZero;
        }

        blocksCounted = 0;

        Arrays.fill(counts, 0);
        Arrays.fill(zeroMetadataCounts, 0);
        Arrays.stream(metadatas).forEach(m -> {
            if (m != null)
                m.replaceAll((k, v) -> 0);
        });

    }
    
    private void resize(int newSize) {
    	int stepSize = 1024;
    	newSize = ((int)Math.ceil(newSize / (double)stepSize)) * stepSize;
    	wasZero = Arrays.copyOf(wasZero, newSize);
    	counts = Arrays.copyOf(counts, newSize);
    	zeroMetadataCounts = Arrays.copyOf(zeroMetadataCounts, newSize);
    	metadatas = Arrays.copyOf(metadatas, newSize);
    }

}