package eu.ha3.matmos.serialisation.expansion;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

public class SerialDynamic {
    @Expose
    public List<SerialDynamicSheetIndex> entries = new ArrayList<>();
}
