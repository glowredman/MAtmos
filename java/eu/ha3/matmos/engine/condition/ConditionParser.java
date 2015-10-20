package eu.ha3.matmos.engine.condition;

import com.google.common.base.Optional;

import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionParser
{
    private final String input;
    private final boolean ignoreSpace;
    private int pos = 0;

    public ConditionParser(String in)
    {
        input = in;
        ignoreSpace = true;
    }

    public ConditionParser(String in, boolean ignoreSpaceChar)
    {
        input = in;
        ignoreSpace = ignoreSpaceChar;
    }

    public char current()
    {
        return hasNext() ? input.charAt(pos) : (char) -1;
    }

    public void skip()
    {
        pos++;
    }

    public boolean hasNext()
    {
        return pos < input.length();
    }

    public String parseIf(ConditionParser.Rule rule)
    {
        if (hasNext())
        {
            StringBuilder result = new StringBuilder();
            char c;
            while (rule.accept(c = input.charAt(pos)) || c == ' ')
            {
                if (!ignoreSpace || c != ' ')
                    result.append(c);
                pos++;
                if (!hasNext())
                    break;
            }
            return result.toString();
        }
        return "";
    }

    public static abstract class Rule
    {
        abstract boolean accept(char in);
    }

    public static ConditionParser.Rule string()
    {
        return new ConditionParser.Rule()
        {
            boolean accept(char c)
            {
                return Character.isLetterOrDigit(c) || c == '.' || c == ':' || c == '_' || c == '-';
            }
        };
    }

    public static ConditionParser.Rule operator()
    {
        return new ConditionParser.Rule()
        {
            int count = 0;
            boolean accept(char c)
            {
                if (count < 2 && c == '=' || c == '<' || c == '>' || c == '!')
                {
                    count++;
                    return true;
                }
                return false;
            }
        };
    }

    public static ConditionSet parse(String name, List<String> rules)
    {
        ConditionSet conditionSet = new ConditionSet(name);
        for (String s : rules)
        {
            ConditionBuilder builder = parse(s);
            Optional<Checkable> checkableOptional = builder.build();
            if (checkableOptional.isPresent())
            {
                conditionSet.addCondition(checkableOptional.get());
            }
        }
        return conditionSet;
    }

    public static ConditionBuilder parse(String in)
    {
        ConditionBuilder builder = new ConditionBuilder();
        for (int i = 0; i < in.length(); i++)
        {
            char c = in.charAt(i);
            if (Character.isLetterOrDigit(c) || c == '.' || c == '_' || c == '-' || c == ':' || c == '|')
            {
                builder.append(c);
            }
            else if (c == '<' || c == '>' || c == '=' || c == '!')
            {
                builder.appendOp(c);
            }
        }
        return builder.finish();
    }
}
