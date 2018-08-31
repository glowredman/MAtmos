package eu.ha3.matmos.core.sheet;

import eu.ha3.matmos.util.math.LongFloatSimplificator;

/*
 * --filenotes-placeholder
 */

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
        for (String key : this.values.keySet()) {
            String newValue = this.values.get(key);
            Long newLong = LongFloatSimplificator.longOf(newValue);

            String previousValue = this.data.getSheet(this.actualSheet).exists(key) ? this.data.getSheet(this.actualSheet).get(key) : "0";

            if (newLong != null) {
                Long previousLong = LongFloatSimplificator.longOf(previousValue);

                // Set it here, we needed to retreive previous value first
                this.data.getSheet(this.actualSheet).set(key, newValue);
                if (previousLong != null) {
                    this.data.getSheet(this.deltaSheet).set(key, Long.toString(newLong - previousLong));
                } else {
                    this.data.getSheet(this.deltaSheet).set(
                            key, newValue.equals(previousValue) ? "NOT_MODIFIED" : "MODIFIED");
                }
            } else {
                this.data.getSheet(this.actualSheet).set(key, newValue);
                this.data.getSheet(this.deltaSheet).set(
                        key, newValue.equals(previousValue) ? "NOT_MODIFIED" : "MODIFIED");
            }
        }

        clear();
    }

}
