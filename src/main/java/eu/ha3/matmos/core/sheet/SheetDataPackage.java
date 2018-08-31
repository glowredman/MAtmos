package eu.ha3.matmos.core.sheet;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Represents a data package populated by data sheets.
 */
public class SheetDataPackage implements DataPackage {
    private final Map<String, Sheet> sheets;
    private final Class<? extends Sheet> sheetType;

    public SheetDataPackage(Class<? extends Sheet> sheetType) {
        this.sheets = new TreeMap<String, Sheet>();
        this.sheetType = sheetType;
    }

    @Override
    public Sheet getSheet(String name) {
        if (!this.sheets.containsKey(name)) {
            try {
                this.sheets.put(name, this.sheetType.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return this.sheets.get(name);

    }

    @Override
    public Set<String> getSheetNames() {
        return this.sheets.keySet();
    }

    @Override
    public void clear() {
        this.sheets.clear();
    }

    @Override
    public void clearContents() {
        for (Sheet sheet : this.sheets.values()) {
            sheet.clear();
        }
    }

}
