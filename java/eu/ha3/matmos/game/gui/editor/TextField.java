package eu.ha3.matmos.game.gui.editor;

import eu.ha3.matmos.game.MCGame;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public class TextField
{
    private List<String> tabComplete = new ArrayList<String>();
    private String partMatch = "";
    protected StringBuilder text = new StringBuilder();
    protected boolean active = false;
    protected boolean hovered = false;
    protected int tabCycle = -1;

    public void onKeyType(char c, int keyCode)
    {
        if (keyCode == Keyboard.KEY_TAB)
        {
            if (tabCycle == -1)
            {
                tabCycle = 0;
                partMatch = text.toString();
                tabComplete = tabComplete(partMatch);
            }
            String match;
            if (tabCycle < tabComplete.size())
            {
                match = partMatch + tabComplete.get(tabCycle++);
            }
            else
            {
                match = partMatch;
                tabCycle = 0;
            }
            text = new StringBuilder(match);
            return;
        }
        tabCycle = -1;
        if (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_ESCAPE)
        {
            active = false;
            return;
        }
        if (keyCode == Keyboard.KEY_BACK)
        {
            delete(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL));
            return;
        }
        append(c);
    }

    protected void append(char c)
    {
        text.append(c);
    }

    public void setActive(boolean isActive)
    {
        active = isActive;
    }

    public void setHovered(boolean isHovered)
    {
        hovered = isHovered;
    }

    public void setText(String string)
    {
        text.setLength(0);
        for (char c : string.toCharArray())
        {
            onKeyType(c, -1);
        }
    }

    public void delete(boolean ctrl)
    {
        if (ctrl)
        {
            delete(new Rule()
            {
                public boolean check(char c) { return length() > 0; }
            });
        }
        else
        {
            deleteLast();
        }
    }

    public void delete(Rule rule)
    {
        char c = deleteLast();
        while (rule.check(c))
        {
            c = deleteLast();
            if (length() == 0)
                break;
        }
    }

    public char deleteLast()
    {
        if (length() > 0)
        {
            text.deleteCharAt(length() - 1);
        }
        return charAt(length() - 1);
    }

    public char charAt(int index)
    {
        if (index < text.length() && index >= 0)
        {
            return text.charAt(index);
        }
        return (char) -1;
    }

    public boolean active()
    {
        return active;
    }

    public boolean hovered()
    {
        return hovered;
    }

    public boolean empty()
    {
        return length() == 0;
    }

    public int length()
    {
        return text.length();
    }

    public String getString()
    {
        return text.toString();
    }

    public String getString(boolean showCursor)
    {
        return getString() + (showCursor ? "|" : "");
    }

    protected List<String> tabComplete(String match)
    {
        return new ArrayList<String>();
    }

    protected static abstract class Rule
    {
        public abstract boolean check(char c);
    }

    public void drawBox(int left, int top, int right, int bottom)
    {
        if (hovered || active)
        {
            ScreenHolder.drawRect(left, top, right, bottom, active ? 0x33FFFFFF : 0x22FFFFFF);
        }
    }

    public void draw(boolean showCursor, int left, int top, int right, int color)
    {
        MCGame.drawString(getString(showCursor), left, top, color);
    }
}