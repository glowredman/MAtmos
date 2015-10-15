package eu.ha3.matmos.game.gui.editor;

import eu.ha3.matmos.game.gui.editor.div.ScreenDiv;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

/**
 * @author dags_ <dags@dags.me>
 */

public class ScreenHolder extends GuiScreen
{
    // TODO: Save on close
    private ScreenDiv screen;

    public ScreenHolder()
    {
        ScaledResolution r = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        width = r.getScaledWidth();
        height = r.getScaledHeight();
        screen = Screens.buildConditionsScreen(width, height);
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        screen.drawScreen(x, y);
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

    public static void checkToggle()
    {
        if (Keyboard.isKeyDown(Keyboard.KEY_END))
        {
            if (!latch)
            {
                latch = true;
                toggle = !toggle;
                if (toggle)
                {
                    Minecraft.getMinecraft().displayGuiScreen(new ScreenHolder());
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
