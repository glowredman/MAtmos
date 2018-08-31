package eu.ha3.matmos.core.sheet;

/*
 * --filenotes-placeholder
 */

public interface SheetCommander<T> {
    /**
     * Tells if the sheet index exists
     *
     * @param  sheetIndex
     * @return
     */
    boolean exists(SheetIndex sheetIndex);

    /**
     * Gets the version of the sheet index
     *
     * @param  sheetIndex
     * @return
     */
    int version(SheetIndex sheetIndex);

    /**
     * Returns the value of the sheet index
     *
     * @param  sheetIndex
     * @return
     */
    T get(SheetIndex sheetIndex);

    /**
     * Tells if a list has a certain value.
     *
     * @param  constantX
     * @param  value
     * @return
     */
    boolean listHas(String constantX, String value);
}
