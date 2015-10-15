package eu.ha3.matmos.engine.condition;

import com.google.common.base.Optional;
import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataRegistry;
import eu.ha3.matmos.game.scanner.Scanner;
import eu.ha3.matmos.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionBuilder
{
    private String key = "";
    private String operator = "";
    private List<String> values = new ArrayList<String>();

    public ConditionBuilder setKey(String s)
    {
        if (s.length() > 0)
            key = s;
        return this;
    }

    public ConditionBuilder setOperator(String s)
    {
        if (s.length() > 0)
            operator = s;
        return this;
    }

    public ConditionBuilder addValue(String s)
    {
        if (s.length() > 0)
            values.add(s);
        return this;
    }

    public boolean validKey()
    {
        return !"invalid".equals(MAtmos.dataRegistry.dataTypeFromKey(key));
    }

    public boolean validOperator()
    {
        return Check.validOperator(operator);
    }

    public boolean valid()
    {
        return validKey() && validOperator() && values.size() > 0;
    }

    public Optional<String> getCurrentValue()
    {
        return MAtmos.dataRegistry.getCurrentData(key);
    }

    public Optional<Checkable> build()
    {
        if (valid())
        {
            String type = MAtmos.dataRegistry.dataTypeFromKey(key);
            if ("number".equals(type))
                return getNumericCondition(MAtmos.dataRegistry);
            else if (type.startsWith("scan"))
                return getScanCondition(MAtmos.dataRegistry);
            else if ("boolean".equals(type))
                return getBoolCondition(MAtmos.dataRegistry);
            else if ("string".equals(type))
                return getStringCondition(MAtmos.dataRegistry);
        }
        return Optional.absent();
    }

    private Optional<Checkable> getScanCondition(DataRegistry dataRegistry)
    {
        int index = key.startsWith("scan.block") ? key.indexOf('.', 12) : key.indexOf('.', 11);
        if (index > 0)
        {
            String scanType = key.substring(0, index);
            Optional<Scanner> scannerOptional = dataRegistry.getScanner(scanType);
            Optional<Check<Number>> checkOptional = Check.numCheck(operator);
            if (scannerOptional.isPresent() && checkOptional.isPresent() && NumberUtil.isInt(values.get(0)))
            {
                String lookUp = key.substring(scanType.length() + 1, key.length());
                int value = Integer.valueOf(values.get(0));
                Checkable c = new ScanCondition(lookUp, value, checkOptional.get(), scannerOptional.get());
                return Optional.of(c);
            }
        }
        return Optional.absent();
    }

    private Optional<Checkable> getNumericCondition(DataRegistry dataRegistry)
    {
        if (NumberUtil.isNumber(values.get(0)))
        {
            return getCondition(dataRegistry.getNumData(key), Check.numCheck(operator), new Double[]{Double.valueOf(values.get(0))});
        }
        return Optional.absent();
    }

    private Optional<Checkable> getBoolCondition(DataRegistry dataRegistry)
    {
        String bool = values.get(0).toLowerCase();
        if (bool.equals("true") || bool.equals("false"))
        {
            Boolean[] bools = {Boolean.valueOf(bool)};
            return getCondition(dataRegistry.getBoolData(key), Check.boolCheck(operator), bools);
        }
        return Optional.absent();
    }

    private Optional<Checkable> getStringCondition(DataRegistry dataRegistry)
    {
        return getCondition(dataRegistry.getStringData(key), Check.stringCheck(operator), values.toArray(new String[values.size()]));
    }

    private <T> Optional<Checkable> getCondition(Optional<Data<T>> dataOptional, Optional<Check<T>> checkOptional, T[] vals)
    {
        if (checkOptional.isPresent() && dataOptional.isPresent())
        {
            Checkable c = new SimpleCondition<T>(key, vals, checkOptional.get(), dataOptional.get());
            return Optional.of(c);
        }
        return Optional.absent();
    }
}
