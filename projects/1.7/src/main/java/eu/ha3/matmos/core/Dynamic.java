package eu.ha3.matmos.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import eu.ha3.matmos.core.sheet.SheetCommander;
import eu.ha3.matmos.core.sheet.SheetIndex;
import eu.ha3.matmos.util.math.Numbers;

public class Dynamic extends Component implements Evaluated, InformationContainer<Long>, Dependable {
    public static final String DEDICATED_SHEET = "_DYNAMIC";

    private long value;

    private final List<SheetIndex> indexes;

    private final SheetCommander<String> sheetCommander;

    private Collection<String> dependencies;

    public Dynamic(String name, SheetCommander<String> sheetCommander, List<SheetIndex> indexes) {
        super(name);
        this.sheetCommander = sheetCommander;

        this.indexes = indexes;

        dependencies = new HashSet<>();
        for (SheetIndex index : indexes) {
            dependencies.add(index.getSheet());
        }
    }

    @Override
    public void evaluate() {
        long previous = value;

        value = 0;

        for (SheetIndex sheetIndex : indexes) {
            if (sheetCommander.exists(sheetIndex)) {
                Long value = Numbers.toLong(sheetCommander.get(sheetIndex));
                if (value != null) {
                    this.value = this.value + value;
                }
            }
        }

        if (previous != value) {
            incrementVersion();
        }
    }

    @Override
    public Long getInformation() {
        return value;
    }

    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }

    public List<SheetIndex> getIndexes() {
        return Collections.unmodifiableList(indexes);
    }
}
