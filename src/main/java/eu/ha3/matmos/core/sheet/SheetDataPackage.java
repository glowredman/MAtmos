package eu.ha3.matmos.core.sheet;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import eu.ha3.matmos.core.expansion.ExpansionManager;

/**
 * Represents a data package populated by data sheets.
 */
public class SheetDataPackage implements DataPackage {
    private final Map<String, Sheet> sheets;
    private final Class<? extends Sheet> sheetType;
    
    Set<String> referencedBlocks = new HashSet<String>();

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
    
    public String dealiasBlockMeta(String blockMeta) {
        String[] blockMetaArr = blockMeta.split("\\^");
        
        blockMetaArr[0] = ExpansionManager.dealias(blockMetaArr[0]);
        
        return String.join("^", blockMeta);
    }
    
    public void addReferencedBlocks(List<String> newReferencedBlocks) {
        newReferencedBlocks.stream().forEach(b -> referencedBlocks.add(b));
    }
}
