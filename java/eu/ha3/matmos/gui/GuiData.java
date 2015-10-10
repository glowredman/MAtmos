package eu.ha3.matmos.gui;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.engine.Data;
import eu.ha3.matmos.engine.DataManager;
import eu.ha3.matmos.engine.condition.Checkable;
import eu.ha3.matmos.engine.condition.ConditionSet;
import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.scanner.Scanner;

import java.util.*;

/**
 * @author dags_ <dags@dags.me>
 */

public class GuiData
{
    private static final int BLUE = 0x0066FF;
    private static final int GREEN = 0x00FF1A;
    private static final int WHITE = 0xFFFFFF;

    private static final int xInc = 250;
    private static final int yInc = 10;
    private static final int maxLineCount = 20;

    private final MAtmos mAtmos;
    private final Map<String, Data<?>> data = new LinkedHashMap<String, Data<?>>();
    private final Set<String> toDisplay = new HashSet<String>();

    private int xPos = 1;
    private int yPos = 1;
    private int lineCount = 0;

    public GuiData(MAtmos instance)
    {
        mAtmos = instance;
        Map<String, Data<?>> all = new HashMap<String, Data<?>>();

        all.putAll(mAtmos.dataManager.boolData);
        all.putAll(mAtmos.dataManager.numData);
        all.putAll(mAtmos.dataManager.stringData);

        List<String> keys = new ArrayList<String>(all.keySet());
        Collections.sort(keys);
        for (String s : keys)
        {
            data.put(s, all.get(s));
        }
    }

    private void newLine()
    {
        yPos += yInc;
        lineCount++;
        if (lineCount > maxLineCount)
        {
            lineCount = 0;
            yPos = 1;
            xPos += xInc;
        }
    }

    long lastUpdate = 0L;
    float tickPercentage = 0L;

    public void drawTimer()
    {
        long current = System.currentTimeMillis();
        if (current - lastUpdate > 1000)
        {
            tickPercentage = ((float) DataManager.processTime) / 50F;
            lastUpdate = current;
        }
        MCGame.drawString("process time: " + tickPercentage + "% tick", 1, 1, 0xFFFFFF);
    }

    public void draw()
    {
        xPos = 1;
        yPos = 1;
        lineCount = 0;
        drawTimer();
        newLine();
        for  (Map.Entry<String, Data<?>> e : data.entrySet())
        {
            if (checkAndDraw(e.getKey(), e.getKey(), e.getValue().value, xPos, yPos))
            {
                newLine();
            }
        }
        for (Scanner s : mAtmos.dataManager.scanners.values())
        {
            List<String> list = new ArrayList<String>(s.getCounts().keySet());
            Collections.sort(list);
            for (String id : list)
            {
                if (checkAndDraw(s.displayId(), s.displayId() + "." + id, s.getCounts().get(id).count, xPos, yPos))
                {
                    newLine();
                }
            }
        }
        for (Map.Entry<String, ConditionSet> e : mAtmos.dataManager.conditions.entrySet())
        {
            final boolean active = e.getValue().active();
            int color = active ? GREEN : WHITE;
            if (checkAndDraw("conditions", e.getKey(), xPos, yPos, color))
            {
                newLine();
                for (Checkable c : e.getValue().getConditions())
                {
                    color = active ? GREEN : c.active() ? BLUE : WHITE;
                    MCGame.drawString(c.debugInfo(), xPos, yPos, color);
                    newLine();
                }
            }
        }
    }

    private boolean checkAndDraw(String id, String displayName, Object value,int x, int y)
    {
        return checkAndDraw(id, displayName, value, x, y, 0xFFFFFF);
    }

    private boolean checkAndDraw(String id, String displayName, Object value,int x, int y, int color)
    {
        if (toDisplay.contains(id))
        {
            String line = displayName + " = " + value;
            MCGame.drawString(line, x, y, color);
            return true;
        }
        return false;
    }

    public GuiData displayAll()
    {
        toDisplay.addAll(data.keySet());
        return this;
    }

    public GuiData display(String s)
    {
        if ("active".equals(s))
        {
            toDisplay.add("%active%");
            return this;
        }
        for (String key : data.keySet())
        {
            if (key.startsWith(s))
            {
                toDisplay.add(key);
            }
        }
        for (Scanner sc : mAtmos.dataManager.scanners.values())
        {
            if (sc.displayId().startsWith(s))
            {
                toDisplay.add(sc.displayId());
            }
        }
        return this;
    }

    public GuiData hide(String s)
    {
        if ("active".equals(s))
        {
            toDisplay.remove("%active%");
            return this;
        }
        for (String key : data.keySet())
        {
            if (key.startsWith(s))
            {
                toDisplay.remove(key);
            }
        }
        for (Scanner sc : mAtmos.dataManager.scanners.values())
        {
            if (sc.displayId().startsWith(s))
            {
                toDisplay.remove(sc.displayId());
            }
        }
        return this;
    }
}
