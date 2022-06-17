package eu.ha3.matmos.core.sheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a data package populated by data sheets.
 */
public class SheetDataPackage implements DataPackage {
    private final Map<String, Sheet> sheets;
    private final Class<? extends Sheet> sheetType;

    Set<Integer> referencedBlockIDs = new HashSet<Integer>();
    Set<Integer> referencedItemIDs = new HashSet<Integer>();

    public SheetDataPackage(Class<? extends Sheet> sheetType) {
        sheets = new HashMap<>();
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

    public void addReferencedIDs(List<Integer> newReferencedBlockIDs, List<Integer> newReferencedItemIDs) {
        referencedBlockIDs.addAll(newReferencedBlockIDs);
        referencedItemIDs.addAll(newReferencedItemIDs);
    }
    
    public boolean isIDReferenced(int id, boolean isItem) {
        return (isItem ? referencedItemIDs : referencedBlockIDs).contains(id);
    }
}
