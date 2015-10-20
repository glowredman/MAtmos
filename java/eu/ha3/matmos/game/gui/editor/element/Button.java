package eu.ha3.matmos.game.gui.editor.element;

import eu.ha3.matmos.game.MCGame;
import eu.ha3.matmos.game.gui.editor.div.Div;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

/**
 * @author dags_ <dags@dags.me>
 */

public class Button extends Gui
{
    public int width;
    public int height;
    private int halfWidth;
    private int marginTop;

    private String text = "";
    private int textColor = 0xFFFFFF;
    private int backGroundColor = 0xFFCCCCCC;
    private int hoveredColor = 0xAA333333;

    private boolean mouseOver = false;

    public Button(int width, int height, int marginTop)
    {
        setDims(width, height);
        this.marginTop = marginTop;
    }

    public Button(){}

    public Button setMarginTop(int top)
    {
        marginTop = top;
        return this;
    }

    public Button setDims(int width, int height)
    {
        this.width = width;
        this.height = height;
        halfWidth = width / 2;
        return this;
    }

    public Button setDisplayString(String s)
    {
        text = s;
        return this;
    }

    public Button setTextColor(int i)
    {
        textColor = i;
        return this;
    }

    public Button setBackgroundColor(int i)
    {
        backGroundColor = i;
        return this;
    }

    public void draw(int left, int top)
    {
        drawBorderedBox(left, top, left + width, top + height, mouseOver ? hoveredColor : backGroundColor, 0xFFFFFFFF);
        int boxCenter = left + halfWidth;
        int halfLength = Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2;
        MCGame.drawString(text, boxCenter - halfLength, top + marginTop, textColor);
    }

    public void drawBorderedBox(int left, int top, int right, int bottom, int backgroundColor, int borderColor)
    {
        drawRect(left, top, right, bottom, backgroundColor);
        drawHorizontalLine(left, right - 1, top, borderColor);
        drawHorizontalLine(left, right - 1, bottom - 1, borderColor);
        drawVerticalLine(left, top, bottom - 1, borderColor);
        drawVerticalLine(right - 1, top, bottom - 1, borderColor);
    }

    public boolean onClick(int mouseX, int mouseY, int left, int top)
    {
        return mouseOver = Div.mouseOver(mouseX, mouseY, left, top, left + width, top + height);
    }

    public void onRelease()
    {
        mouseOver = false;
    }
}
