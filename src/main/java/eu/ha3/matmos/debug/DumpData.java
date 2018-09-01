package eu.ha3.matmos.debug;

import eu.ha3.matmos.core.sheet.DataPackage;
import eu.ha3.matmos.core.sheet.Sheet;

// TODO: Unused
@Deprecated
public class DumpData {
    public static String dumpData(DataPackage data) {
        StringBuilder s = new StringBuilder();

        for (String sheetName : data.getSheetNames()) {
            Sheet sheet = data.getSheet(sheetName);

            s.append(sheetName + "\n");

            for (String index : sheet.keySet()) {
                s.append("  " + index + ":" + sheet.get(index) + "\n");
            }
        }
        return s.toString();
    }
}
