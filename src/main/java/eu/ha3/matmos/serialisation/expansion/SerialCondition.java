package eu.ha3.matmos.serialisation.expansion;

import com.google.gson.annotations.Expose;

public class SerialCondition {
    @Expose
    public String sheet = "";

    @Expose
    public String index = "";

    @Expose
    public String symbol = "EQUALS";

    @Expose
    public String value = "";
}
