package eu.ha3.matmos.gui;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.text.TextFormatting;

public class MAtGuiExpansionInfo extends GuiScreen {
    private final MAtGuiMenu parentScreen;
    
    private final Expansion expansion;

    private final String[] info;

    public MAtGuiExpansionInfo(MAtGuiMenu menu, MAtMod mod, Expansion expansion) {
        this.parentScreen = menu;
        this.expansion = expansion;

        this.info = expansion.hasMoreInfo() ? expansion.getInfo().replace("\r", "").replace("�", "\u00A7").split("\n") : new String[] {"No info.txt available."};
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawGradientRect(0, 0, this.width, this.height, 0xF0000000, 0x90000000);

        drawCenteredString(this.fontRenderer, "About " + TextFormatting.YELLOW + TextFormatting.ITALIC + this.expansion.getFriendlyName() + TextFormatting.RESET + "...", this.width / 2, 4, 0xffffff);

        int lc = 0;
        for (String line : this.info) {
            this.fontRenderer.drawString(line, this.width / 2 - 200, 16 + 8 * lc, 0xFFFFFF);
            lc++;
        }

        super.drawScreen(par1, par2, par3);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        int h = new ScaledResolution(this.mc).getScaledHeight() - 22;

        this.buttonList.add(new GuiButton(200, 2, h, 70, 20, "Close"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Minecraft mc = Minecraft.getMinecraft();

        if (button.id == 200) {
            mc.displayGuiScreen(this.parentScreen);
        }
    }
}
