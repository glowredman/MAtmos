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
    private StringBuilder key = new StringBuilder();
    private StringBuilder operator = new StringBuilder();
    private StringBuilder value = new StringBuilder();
    private List<String> values = new ArrayList<String>();

    public ConditionBuilder append(char c)
    {
        if (hasOperator())
        {
            if (c == '|')
            {
                addValue(value.toString());
                value.setLength(0);
            }
            else
            {
                value.append(c);
            }
        }
        else
        {
            key.append(c);
        }
        return this;
    }

    public ConditionBuilder appendOp(char c)
    {
        if (operator.length() < 2 && values.size() == 0 && value.length() == 0)
            operator.append(c);
        return this;
    }

    public ConditionBuilder finish()
    {
        if (value.length() > 0)
        {
            addValue(value.toString());
            value.setLength(0);
        }
        return this;
    }

    public ConditionBuilder addValue(String s)
    {
        if (s.length() > 0)
            values.add(s);
        return this;
    }

    public boolean valid()
    {
        return hasKey() && hasOperator() && hasValue();
    }

    public boolean hasKey()
    {
        return key.length() > 0;
    }

    public boolean hasOperator()
    {
        return operator.length() > 0;
    }

    public boolean hasValue()
    {
        return values.size() > 0;
    }

    public Optional<String> getCurrentValue()
    {
        return MAtmos.dataRegistry.getCurrentData(key.toString());
    }

    public Optional<Checkable> build()
    {
        if (valid())
        {
            String type = MAtmos.dataRegistry.dataTypeFromKey(key.toString());
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
        String key = this.key.toString();
        int index = key.startsWith("scan.block") ? key.indexOf('.', 12) : key.indexOf('.', 11);
        if (index > 0)
        {
            String scanType = key.substring(0, index);
            Optional<Scanner> scannerOptional = dataRegistry.getScanner(scanType);
            Optional<Check<Number>> checkOptional = Check.numCheck(operator.toString());
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
            return getCondition(dataRegistry.getNumData(key.toString()), Check.numCheck(operator.toString()), new Double[]{Double.valueOf(values.get(0))});
        }
        return Optional.absent();
    }

    private Optional<Checkable> getBoolCondition(DataRegistry dataRegistry)
    {
        String bool = values.get(0).toLowerCase();
        if (bool.equals("true") || bool.equals("false"))
        {
            Boolean[] bools = {Boolean.valueOf(bool)};
            return getCondition(dataRegistry.getBoolData(key.toString()), Check.boolCheck(operator.toString()), bools);
        }
        return Optional.absent();
    }

    private Optional<Checkable> getStringCondition(DataRegistry dataRegistry)
    {
        return getCondition(dataRegistry.getStringData(key.toString()), Check.stringCheck(operator.toString()), values.toArray(new String[values.size()]));
    }

    private <T> Optional<Checkable> getCondition(Optional<Data<T>> dataOptional, Optional<Check<T>> checkOptional, T[] vals)
    {
        if (checkOptional.isPresent() && dataOptional.isPresent())
        {
            Checkable c = new SimpleCondition<T>(key.toString(), vals, checkOptional.get(), dataOptional.get());
            return Optional.of(c);
        }
        return Optional.absent();
    }
}
