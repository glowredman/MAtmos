package eu.ha3.matmos.core.logic;

import java.util.Collection;
import java.util.HashSet;

import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.core.Dependable;
import eu.ha3.matmos.core.MultistateComponent;
import eu.ha3.matmos.core.Operator;
import eu.ha3.matmos.core.sheet.SheetCommander;
import eu.ha3.matmos.core.sheet.SheetIndex;
import eu.ha3.matmos.util.math.Numbers;

public class Condition extends MultistateComponent implements Dependable, Visualized {
    private final SheetIndex indexX;

    private final Operator operatorX;

    private final String constantX;

    private final Long constantLongX;

    private final SheetCommander<String> sheetCommander;

    // Fixes a bug where conditions don't evaluate for sheet indexes that don't
    // exist
    // Required for ALWAYS_TRUE / ALWAYS_FALSE
    // This was caused by default value for undefined sheet indexes being -1 (equal
    // to initial siVersion)
    private int siVersion = Integer.MIN_VALUE;

    private final Collection<String> dependencies;

    public Condition(String name, SheetCommander<String> sheetCommander, SheetIndex index, Operator operator,
            String constant) {
        super(name);
        this.sheetCommander = sheetCommander;

        indexX = index;
        operatorX = operator;
        constantX = constant;

        constantLongX = Numbers.toLong(constant);

        dependencies = new HashSet<>();
        dependencies.add(index.getSheet());
    }

    @Override
    public void evaluate() {
        if (sheetCommander.version(indexX) == siVersion) {
            return;
        }

        boolean pre = isActive;
        isActive = testIfTrue();

        if (pre != isActive) {
            incrementVersion();

            Matmos.DEBUGLOGGER.debug("C: " + getName() + " -> " + isActive);
        }
    }

    private boolean testIfTrue() {
        try {
            String value = sheetCommander.get(indexX);
            switch (operatorX) {
            case IN_LIST:
                return sheetCommander.listHas(constantX, value);
            case NOT_IN_LIST:
                return !sheetCommander.listHas(constantX, value);
            default:
                if (constantLongX != null) {
                    Long longValue = Numbers.toLong(value);
                    if (longValue != null) {
                        return operatorX.test(longValue, constantLongX);
                    }
                }
                return operatorX.test(value, constantX);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the required sheet modules of this condition.
     */
    @Override
    public Collection<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String getFeed() {
        String value = sheetCommander.get(indexX);
        String op = operatorX.getSymbol();

        return indexX.getSheet() + ">" + indexX.getIndex() + ":[" + value + "] " + op + " " + constantX;
    }

    public SheetIndex getIndex() {
        return indexX;
    }
}
