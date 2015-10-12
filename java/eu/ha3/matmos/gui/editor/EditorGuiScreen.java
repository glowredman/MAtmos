package eu.ha3.matmos.gui.editor;

import eu.ha3.matmos.MAtmos;
import eu.ha3.matmos.gui.editor.div.Div;
import eu.ha3.matmos.gui.editor.div.EditorDiv;
import eu.ha3.matmos.gui.editor.div.ScreenDiv;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author dags_ <dags@dags.me>
 */

public class EditorGuiScreen extends GuiScreen
{
    private final ScreenDiv screen;

    public EditorGuiScreen(MAtmos mAtmos)
    {
        width = Minecraft.getMinecraft().displayWidth;
        height = Minecraft.getMinecraft().displayHeight;
        ScaledResolution r = new ScaledResolution(Minecraft.getMinecraft(), width, height);

        screen = new ScreenDiv(r.getScaledWidth(), r.getScaledHeight());
        screen.background(0xCC333333);
        screen.border(0xFFFFFFFF);
        Div editor = new EditorDiv(mAtmos, 0.49F, 0.98F, 0.005F, 0.01F).background(0xFF000000).border(0xFFFFFFFF);
        screen.addChild(editor);
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        screen.mouseMoved(x, y);
        screen.drawScreen();
    }

    public void mouseClicked(int x, int y, int mouseButton) throws IOException
    {
        screen.mouseClicked(x, y, mouseButton);
    }

    public void keyTyped(char typedChar, int keyCode) throws IOException
    {
        screen.keyTyped(typedChar, keyCode);
    }

    public void onResize(Minecraft mcIn, int w, int h)
    {
        width = w;
        height = h;
        ScaledResolution r = new ScaledResolution(Minecraft.getMinecraft(), w, h);
        screen.setDimensions(r.getScaledWidth(), r.getScaledHeight());
    }

    private static boolean toggle = false;
    private static boolean latch = false;

    public static void checkToggle(MAtmos mAtmos)
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_END))
        {
            if (!latch)
            {
                latch = true;
                toggle = !toggle;
                if (toggle)
                {
                    Minecraft.getMinecraft().displayGuiScreen(new EditorGuiScreen(mAtmos));
                }
                else
                {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                }
            }
        }
        else
        {
            latch = false;
        }
    }
}
