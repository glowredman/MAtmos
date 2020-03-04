package eu.ha3.matmos.core.sheet;

public class SheetEntry implements SheetIndex {
    private final String sheet;
    private final String index;

    public SheetEntry(String sheet, String index) {
        this.sheet = sheet;
        this.index = index;
    }

    @Override
    public String getSheet() {
        return sheet;
    }

    @Override
    public String getIndex() {
        return index;
    }
}
