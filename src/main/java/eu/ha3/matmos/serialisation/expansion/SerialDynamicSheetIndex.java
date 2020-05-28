package eu.ha3.matmos.serialisation.expansion;

import com.google.gson.annotations.Expose;

public class SerialDynamicSheetIndex {
    @Expose
    public String sheet = "";

    @Expose
    public String index = "";

    public SerialDynamicSheetIndex() {
    }

    public SerialDynamicSheetIndex(String sheet, String index) {
        this.sheet = sheet;
        this.index = index;
    }
}
