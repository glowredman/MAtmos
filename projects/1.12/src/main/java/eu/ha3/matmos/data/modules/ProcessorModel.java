package eu.ha3.matmos.data.modules;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.DeltaSheet;
import eu.ha3.matmos.core.sheet.Sheet;
import eu.ha3.matmos.core.sheet.VirtualSheet;
import eu.ha3.matmos.data.IDataGatherer;

/**
 * A processor that contains a sheet, or a virtual delta sheet if the deltaName
 * is not null. Calling doProcess() will set the values of the sheet, and
 * automatically apply them at the end of the call if the sheet provided by the
 * Data is a virtual sheet, or if the sheet is a delta sheet.
 *
 * @author Hurry
 */
public abstract class ProcessorModel implements IDataGatherer {
    protected Sheet sheet;

    private int interval;
    private int callsRemaining;

    public ProcessorModel(DataPackage data, String normalName, String deltaName) {
        if (deltaName == null) {
            sheet = data.getSheet(normalName);
        } else {
            sheet = new DeltaSheet(data, normalName, deltaName);
        }

        interval = 1;
        callsRemaining = 0;
    }

    /**
     * Runs this module's value discovery processing.
     */
    protected abstract void doProcess();

    @Override
    public void process() {
        if (callsRemaining <= 0) {
            doProcess();

            if (sheet instanceof VirtualSheet) {
                ((VirtualSheet) sheet).apply();
            }

            if (interval != 0) {
                callsRemaining = interval;
            }
        } else {
            callsRemaining = callsRemaining - 1;
        }
    }

    /**
     * Sets the number of calls where nothing happens before the process is executed
     * again. Defaults to 0 (call every time).
     *
     * @param value
     */
    public void setInterval(int value) {
        interval = Math.min(0, value);
        callsRemaining = 0;
    }

    public void setValueIntIndex(int index, boolean value) {
        setValue(Integer.toString(index), value);
    }

    public void setValueIntIndex(int index, long value) {
        setValue(Integer.toString(index), value);
    }

    public void setValueIntIndex(int index, String value) {
        setValue(Integer.toString(index), value);
    }

    public void setValue(String index, boolean newValue) {
        setValue(index, newValue ? "1" : "0");
    }

    public void setValue(String index, long newValue) {
        setValue(index, Long.toString(newValue));
    }

    public void setValue(String index, String value) {
        sheet.set(index, value == null ? "NULL" : value);
    }
}
