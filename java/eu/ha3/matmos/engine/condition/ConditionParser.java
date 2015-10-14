package eu.ha3.matmos.engine.condition;

import com.google.common.base.Optional;
import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.game.scanner.Scanner;
import eu.ha3.matmos.util.NumberUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionParser
{
    private final DataManager data;

    public ConditionParser(DataManager dm)
    {
        data = dm;
    }

    private boolean isOp(char c)
    {
        return c == '=' || c == '<' || c == '>' || c == '!';
    }

    public ConditionSet parse(String name, List<String> lines)
    {
        return new ConditionSet(name, parseLines(lines));
    }

    public List<Checkable> parseLines(List<String> lines)
    {
        List<Checkable> list = new ArrayList<Checkable>();
        for (String s : lines)
        {
            Optional<Checkable> o = parse(s);
            if (o.isPresent())
                list.add(o.get());
        }
        return list;
    }

    public Optional<Checkable> parse(String line)
    {
        String operator = "";
        StringBuilder keyBuilder = new StringBuilder();
        List<String> vals = new ArrayList<String>();
        for (int i = 0, length = line.length(), v = 0; i < length; i++)
        {
            char c = line.charAt(i);
            if (isOp(c))
            {
                operator = "" + c;
                if (i < length - 1 && isOp(line.charAt(i + 1)))
                    operator += line.charAt(++i);
                StringBuilder sb = new StringBuilder();
                while (i < length - 1)
                {
                    c = line.charAt(++i);
                    if (c == '|')
                    {
                        if (sb.length() > 0)
                        {
                            vals.add(sb.toString());
                            sb.setLength(0);
                        }
                        continue;
                    }
                    if (c != ' ')
                        sb.append(c);
                }
                if (sb.length() > 0)
                    vals.add(sb.toString());
                break;
            }
            if (c != ' ')
                keyBuilder.append(c);
        }
        if (operator.length() > 0 && vals.size() > 0)
        {
            String key = keyBuilder.toString();
            if (NumberUtil.isNumber(vals.get(0)))
            {
                return getNumericCondition(key, vals, operator);
            }
            else if (vals.get(0).equalsIgnoreCase("true") || vals.get(0).equalsIgnoreCase("false"))
            {
                return getBoolCondition(key, vals, operator);
            }
            else
            {
                return getStringCondition(key, vals, operator);
            }
        }
        return Optional.absent();
    }

    private Optional<Checkable> getNumericCondition(String key, List<String> values, String operator)
    {
        key = key.toLowerCase();
        if (key.startsWith("scan.") && values.size() == 1 && NumberUtil.isInt(values.get(0)))
        {
            int index = key.startsWith("scan.block") ? key.indexOf('.', 12) : key.indexOf('.', 11);
            if (index > 0)
            {
                String scanType = key.substring(0, index);
                Optional<Scanner> scannerOptional = data.getScanner(scanType);
                if (scannerOptional.isPresent())
                {
                    Optional<Check<Number>> checkOptional = Check.numCheck(operator);
                    if (checkOptional.isPresent())
                    {
                        String lookUp = key.substring(scanType.length() + 1, key.length());
                        int value = Integer.valueOf(values.get(0));
                        Checkable c = new ScanCondition(lookUp, value, checkOptional.get(), scannerOptional.get());
                        return Optional.of(c);
                    }
                }
            }
        }
        else
        {
            Optional<Data<Number>> d = data.getNumData(key);
            Optional<Check<Number>> op = Check.numCheck(operator);
            if (op.isPresent() && d.isPresent())
            {
                List<Number> numbers = new ArrayList<Number>();
                for (String s : values)
                    if (NumberUtil.isNumber(s))
                        numbers.add(Double.valueOf(s));
                Checkable c = new SimpleCondition<Number>(key, numbers.toArray(new Number[numbers.size()]), op.get(), d.get());
                return Optional.of(c);
            }
        }
        return Optional.absent();
    }

    private Optional<Checkable> getBoolCondition(String key, List<String> values, String operator)
    {
        Optional<Data<Boolean>> d = data.getBoolData(key);
        Optional<Check<Boolean>> op = Check.boolCheck(operator);
        if (op.isPresent() && d.isPresent())
        {
            Boolean[] b = {Boolean.valueOf(values.get(0))};
            Checkable c = new SimpleCondition<Boolean>(key, b, op.get(), d.get());
            return Optional.of(c);
        }
        return Optional.absent();
    }

    private Optional<Checkable> getStringCondition(String key, List<String> values, String operator)
    {
        Optional<Data<String>> d = data.getStringData(key);
        Optional<Check<String>> op = Check.stringCheck(operator);
        if (op.isPresent() && d.isPresent())
        {
            String[] s = new String[values.size()];
            for (int i = 0; i < values.size(); i++)
                s[i] = values.get(i);
            Checkable c = new SimpleCondition<String>(key, s, op.get(), d.get());
            return Optional.of(c);
        }
        return Optional.absent();
    }
}
