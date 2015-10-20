package eu.ha3.matmos.game.gui.editor.div;

import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags_ <dags@dags.me>
 */

public abstract class Div extends Gui
{
    private final List<Div> children = new ArrayList<Div>();
    private final float width;
    private final float height;
    private final float marginLeft;
    private final float marginTop;

    private boolean background = false;
    private boolean border = false;

    private int backgroundColor = 0;
    private int borderColor = 0;

    public Div(float width, float height, float marginLeft, float marginTop)
    {
        this.width = clamp(width, 0F, 1F);
        this.height = clamp(height, 0F, 1F);
        this.marginLeft = clamp(marginLeft, 0F, 1F);
        this.marginTop = clamp(marginTop, 0F, 1F);
    }

    public abstract void onMouseClick(int mouseX, int mouseY, int button);

    public abstract void onMouseRelease();

    public abstract void onKeyType(char c, int code);

    public abstract void onDraw(int mouseX, int mouseY, int left, int top, int right, int bottom);

    protected final void draw(int mouseX, int mouseY, int parentLeft, int parentTop, int parentRight, int parentBottom)
    {
        int parentWidth = parentRight - parentLeft;
        int parentHeight = parentBottom - parentTop;

        int currentWidth = round(parentWidth * width);
        int currentHeight = round(parentHeight * height);

        int left = parentLeft + round(parentWidth * marginLeft);
        int top = parentTop + round(parentHeight * marginTop);
        int right = left + currentWidth;
        int bottom = top + currentHeight;

        this.drawDiv(mouseX, mouseY, left, top, right, bottom);

        for (Div d : children)
        {
            d.draw(mouseX, mouseY, left, top, right, bottom);
        }
    }

    private void drawDiv(int mouseX, int mouseY, int left, int top, int right, int bottom)
    {
        if (background && border)
        {
            drawBorderedBox(left, top, right, bottom, backgroundColor, borderColor);
        }
        else if (background)
        {
            drawRect(left, top, right, bottom, backgroundColor);
        }
        else if (border)
        {
            drawHorizontalLine(left, right - 1, top, borderColor);
            drawHorizontalLine(left, right - 1, bottom - 1, borderColor);
            drawVerticalLine(left, top, bottom - 1, borderColor);
            drawVerticalLine(right - 1, top, bottom - 1, borderColor);
        }
        this.onDraw(mouseX, mouseY, left, top, right, bottom);
    }

    public final void mouseClicked(int mouseX, int mouseY, int button)
    {
        for (Div d : children)
            d.onMouseClick(mouseX, mouseY, button);
    }

    public final void mouseReleased()
    {
        for (Div d : children)
            d.onMouseRelease();
    }

    public final void keyTyped(char c, int code)
    {
        for (Div d : children)
            d.onKeyType(c, code);
    }

    public Div addChild(Div... child)
    {
        for (Div d : child)
        {
            children.add(d);
        }
        return this;
    }

    public Div background(int color)
    {
        background = true;
        backgroundColor = color;
        return this;
    }

    public Div border(int color)
    {
        border = true;
        borderColor = color;
        return this;
    }

    public void drawBorderedBox(int left, int top, int right, int bottom, int backgroundColor, int borderColor)
    {
        drawRect(left, top, right, bottom, backgroundColor);
        drawHorizontalLine(left, right - 1, top, borderColor);
        drawHorizontalLine(left, right - 1, bottom - 1, borderColor);
        drawVerticalLine(left, top, bottom - 1, borderColor);
        drawVerticalLine(right - 1, top, bottom - 1, borderColor);
    }

    public static boolean mouseOver(int mouseX, int mouseY, int left, int top, int right, int bottom)
    {
        return mouseX > left && mouseX < right && mouseY > top && mouseY < bottom;
    }

    protected static int round(float in)
    {
        return (int) (in > 0F ? in + 0.5F : in < 0F ? in - 0.5F : 0);
    }

    protected static float clamp(float val, float min, float max)
    {
        return val < min ? min : val > max ? max : val;
    }
}
