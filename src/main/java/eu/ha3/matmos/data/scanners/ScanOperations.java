package eu.ha3.matmos.data.scanners;

public interface ScanOperations {
    void begin();

    void finish();

    void input(int x, int y, int z);
}
