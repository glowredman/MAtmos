package eu.ha3.matmos.game.gui.editor.element;

import eu.ha3.matmos.util.NumberUtil;
import net.minecraft.client.gui.Gui;

/**
 * @author dags_ <dags@dags.me>
 */

public class Scrollbar
{
    private final int trackColor = 0xFF333333;
    private final int barColor = 0xFFAAAAAA;
    private final int hoveredBarColor = 0xFFFFFFFF;

    private final int length = 20;
    private final int width = 3;

    private int topPos = -1;
    private boolean selected = false;

    public void draw(int mouseX, int mouseY, int parentRight, int parentTop, int parentBottom)
    {
        Gui.drawRect(parentRight, parentTop, parentRight + width, parentBottom, trackColor);

        int color = selected || mouseOver(mouseX, mouseY, parentRight) ? hoveredBarColor : barColor;
        if (selected)
        {
            topPos = mouseY + length <= parentBottom ? mouseY : parentBottom - length;
            topPos = topPos >= parentTop ? topPos : parentTop;
        }
        else if (topPos < 0)
        {
            topPos = parentTop;
        }
        Gui.drawRect(parentRight, topPos, parentRight + width, topPos + length, color);
    }

    public void onClick(int mouseX, int mouseY, int parentRight)
    {
        selected = mouseOver(mouseX, mouseY, parentRight);
    }

    public void onMouseRelease()
    {
        selected = false;
    }

    public boolean mouseOver(int mouseX, int mouseY, int parentRight)
    {
        return mouseX >= parentRight && mouseX <= parentRight + width && mouseY >= topPos && mouseY <= topPos + length;
    }

    public long getContentTop(int contentBottom, int parentTop, int parentBottom)
    {
        if (contentBottom > parentBottom)
        {
            float contentD = contentBottom - parentBottom;
            float scrollD = parentBottom - parentTop - length;
            float progress = topPos - parentTop;
            return NumberUtil.round((contentD / scrollD) * progress);
        }
        return 0L;
    }
}
