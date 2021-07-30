package eu.ha3.matmos.data.scanners;

public interface ScanOperations {
    void begin();

    void finish();

    void input(int x, int y, int z);

    void input(int x, int y, int z, int weight);

    /**
     * Allows the scanner to set a value of the sheet, for example an overall score
     * calculated during scanning The key must begin with '.' to clarify that it is
     * not the name of a block type
     */
    void setValue(String key, int value);
}
