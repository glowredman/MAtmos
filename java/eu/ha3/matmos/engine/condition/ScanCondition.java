package eu.ha3.matmos.engine.condition;

import eu.ha3.matmos.game.scanner.Scanner;

/**
 * @author dags_ <dags@dags.me>
 */

public class ScanCondition implements Checkable
{
    private final Scanner scanner;
    private final Check<Number> check;
    private final Number value;
    private final String lookUp;

    private Number lastLookup = 0;

    public ScanCondition(String valueLookup, Number val, Check<Number> chck, Scanner scan)
    {
        scanner = scan;
        check = chck;
        value = val;
        lookUp = valueLookup;
    }

    @Override
    public boolean active()
    {
        return check.isTrue(value, lastLookup = scanner.getCount(lookUp));
    }

    @Override
    public String getCurrentValue()
    {
        return "" + scanner.getCount(lookUp);
    }

    @Override
    public String serialize()
    {
        return scanner.displayId() + "." + lookUp + " " + check.asString() + " " + value;
    }

    @Override
    public String debugInfo()
    {
        return serialize() + " [" + lastLookup + "]";
    }
}
