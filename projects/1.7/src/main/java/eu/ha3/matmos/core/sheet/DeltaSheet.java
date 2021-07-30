package eu.ha3.matmos.core.sheet;

import eu.ha3.matmos.util.math.Numbers;

public class DeltaSheet extends GenericSheet implements VirtualSheet {
    private final DataPackage data;

    private final String actualSheet;
    private final String deltaSheet;

    public DeltaSheet(DataPackage data, String actualSheet, String deltaSheet) {
        this.data = data;
        this.actualSheet = actualSheet;
        this.deltaSheet = deltaSheet;
    }

    @Override
    public void apply() {
        for (String key : values.keySet()) {

            Sheet actual = data.getSheet(actualSheet);

            String previousValue = actual.getOrDefault(key, "0");
            String newValue = values.get(key);

            actual.set(key, newValue);
            data.getSheet(deltaSheet).set(key, computeDelta(previousValue, newValue));
        }

        clear();
    }

    protected String computeDelta(String previousValue, String newValue) {
        Long newLong = Numbers.toLong(newValue);

        if (newLong != null) {
            Long previousLong = Numbers.toLong(previousValue);

            if (previousLong != null) {
                return Long.toString(newLong - previousLong);
            }
        }

        return newValue.equals(previousValue) ? "NOT_MODIFIED" : "MODIFIED";
    }

}
