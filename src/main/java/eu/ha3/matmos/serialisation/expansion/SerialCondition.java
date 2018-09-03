package eu.ha3.matmos.serialisation.expansion;

import com.google.gson.annotations.Expose;

import eu.ha3.matmos.core.sheet.SheetIndex;

public class SerialCondition {
    @Expose
    public String sheet = "";

    @Expose
    public String index = "";

    @Expose
    public String symbol = "EQUALS";

    @Expose
    public String value = "";

    public SerialCondition() {}

    public SerialCondition(SheetIndex sheet, String symbol, String value) {
        this.sheet = sheet.getSheet();
        this.index = sheet.getIndex();
        this.symbol = symbol;
        this.value = value;
    }
}
