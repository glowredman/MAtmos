package eu.ha3.matmos.gui.editor;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.condition.Check;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class ConditionBuilder
{
    private final MAtmos mAtmos;

    private List<String> matches = new ArrayList<String>();
    private String partMatch = "";

    private StringBuilder line = new StringBuilder();
    private String operator = "";
    private String type = "";
    private String key = "";

    private int cursorPos = 0;
    private int operatorPos = 0;
    private int tabCycle = -1;

    public boolean hovered = false;
    public boolean active = false;
    private boolean validCondition = false;

    public ConditionBuilder(MAtmos instance)
    {
        mAtmos = instance;
    }

    public void onKeyType(char keyChar, int keyCode)
    {
        keyType(keyChar, keyCode);
        checkValidKey();
    }

    private void keyType(char keyChar, int keyCode)
    {
        if (keyCode == Keyboard.KEY_TAB) // tabComplete
        {
            tabComplete();
            return;
        }
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_ESCAPE)
        {
            active = false;
            return;
        }
        tabCycle = -1;
        if (keyChar == ' ')
        {
            return;
        }
        if (keyCode == Keyboard.KEY_BACK) // delete
        {
            delete(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL));
            return;
        }
        if (isOpChar(keyChar))
        {
            if (operatorPos == 0)
            {
                operatorPos = cursorPos;
            }
            if (operator.length() < 2)
            {
                operator += keyChar;
            }
            return;
        }
        if (isValidChar(keyChar))
        {
            line.append(keyChar);
            cursorPos++;
        }
    }

    private void tabComplete()
    {
        if (operatorPos == 0)
        {
            if (tabCycle == -1)
            {
                partMatch = line.toString();
                matches = mAtmos.dataManager.findMatches(partMatch);
                tabCycle = 0;
            }
            String match;
            if (tabCycle < matches.size())
            {
                match = partMatch + matches.get(tabCycle++);
            }
            else
            {
                match = partMatch;
                tabCycle = 0;
            }
            line = new StringBuilder(match);
            cursorPos = line.length();
        }
    }

    private void delete(boolean all)
    {
        if (cursorPos == operatorPos)
        {
            operator = all ? "" : operator.length() == 2 ? "" + operator.charAt(0) : "";
            operatorPos = operator.length() == 0 ? 0 : operatorPos;
            return;
        }
        if (all && operatorPos > 0 && cursorPos > operatorPos)
        {
            line.delete(operatorPos, line.length());
            cursorPos = line.length();
            return;
        }
        if (cursorPos > 0)
        {
            if (all)
                line.setLength(cursorPos = 0);
            else
                line.deleteCharAt(--cursorPos);
        }
    }

    public String get(boolean withCursor)
    {
        StringBuilder result = new StringBuilder(line.toString());
        if (withCursor && active)
            result.insert(cursorPos, '|');
        if (operator.length() > 0)
            result.insert(operatorPos, " " + operator + " ");
        return result.toString();
    }

    public boolean empty()
    {
        return line.length() == 0;
    }

    public void drawBox(int left, int top, int right, int bottom)
    {
        if (hovered || active)
        {
            EditorGuiScreen.drawRect(left, top, right, bottom, active ? 0x33FFFFFF : 0x22FFFFFF);
        }
    }

    public void draw(boolean showCursor, int left, int top, int right)
    {
        String displayString = get(showCursor);
        MCGame.drawString(displayString, left, top, getTextColor(validCondition));
        if (key.length() > 0)
        {
            String data = getData(type, key);
            int x = Minecraft.getMinecraft().fontRendererObj.getStringWidth("[" + data + "]");
            MCGame.drawString("[" + data + "]", right - x - 1, top, 0xFFFFFF);
        }
    }

    private int getTextColor(boolean valid)
    {
        if (active)
        {
            return valid ? 0x8CD156 : 0xD15E56;
        }
        return valid ? 0xFFFFFFFF : 0xD15E56;
    }

    private void checkValidKey()
    {
        key = "";
        String[] split = get(false).split(" " + operator + " ");
        if (split.length >= 1)
        {
            String dataType = mAtmos.dataManager.dataKeyType(split[0]);
            if (!"invalid".equals(dataType))
            {
                key = split[0];
                type = dataType;
            }
        }
        validCondition = split.length == 2 && validOperator() && validValue(split[1]);
    }

    private String getData(String type, String key)
    {
        if ("number".equals(type))
            return mAtmos.dataManager.getNumData(key).get().value.toString();
        if ("boolean".equals(type))
            return mAtmos.dataManager.getBoolData(key).get().value.toString();
        if ("string".equals(type))
            return mAtmos.dataManager.getStringData(key).get().value;
        return "";
    }

    private boolean validOperator()
    {
        if ("number".equals(type))
            return Check.numCheck(operator).isPresent();
        if ("boolean".equals(type))
            return Check.boolCheck(operator).isPresent();
        return "string".equals(type) && Check.stringCheck(operator).isPresent();
    }

    private boolean validValue(String value)
    {
        if ("number".equals(type))
            return NumberUtil.isNumber(value);
        if ("boolean".equals(type))
            return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
        return "string".equals(type) && value.length() > 0;
    }

    private static boolean isOpChar(char c)
    {
        return c == '<' || c == '>' || c == '=' || c == '!';
    }

    private static boolean isValidChar(char c)
    {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '.' || c == '-' || c == '_' || c ==':';
    }
}
