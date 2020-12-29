package eu.ha3.matmos.core.sheet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.expansion.ExpansionManager;
import eu.ha3.matmos.data.modules.BlockCountModule;
import net.minecraft.block.Block;

/**
 * Represents a data package populated by data sheets.
 */
public class SheetDataPackage implements DataPackage {
    private final Map<String, Sheet> sheets;
    private final Class<? extends Sheet> sheetType;

    private boolean[] isReferenced = new boolean[Matmos.MAX_ID];

    public SheetDataPackage(Class<? extends Sheet> sheetType) {
        sheets = new TreeMap<>();
        this.sheetType = sheetType;
    }

    @Override
    public Sheet getSheet(String name) {
        if (!sheets.containsKey(name)) {
            try {
                sheets.put(name, sheetType.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return sheets.get(name);

    }

    @Override
    public Set<String> getSheetNames() {
        return sheets.keySet();
    }

    @Override
    public void clear() {
        sheets.clear();
    }

    @Override
    public void clearContents() {
        for (Sheet sheet : sheets.values()) {
            sheet.clear();
        }
    }

    public int dealiasID(int id) {
        if (isReferenced[id]) {
            return id;
        } else {
            return ExpansionManager.dealiasID(id);
        }
    }

    public void addReferencedBlocks(List<Block> newReferencedBlocks) {
        newReferencedBlocks.stream().filter(b -> b != null).forEach(b -> isReferenced[Block.getIdFromBlock(b)] = true);
    }
}
