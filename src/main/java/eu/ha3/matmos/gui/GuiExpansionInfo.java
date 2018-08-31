package eu.ha3.matmos.gui;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiExpansionInfo extends GuiScreen {
    private final GuiMatMenu parentScreen;

    private final Expansion expansion;

    private final String[] info;

    public GuiExpansionInfo(GuiMatMenu menu, MAtMod mod, Expansion expansion) {
        parentScreen = menu;
        this.expansion = expansion;

        String info = expansion.hasMoreInfo() ? expansion.getInfo() : I18n.format("mat.expansion.noinfo");
        this.info = info.replace("\r", "").replace("�", "\u00A7").split("\n");
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawGradientRect(0, 0, width, height, 0xF0000000, 0x90000000);

        drawCenteredString(fontRenderer, "About " + TextFormatting.YELLOW + TextFormatting.ITALIC + expansion.getFriendlyName() + TextFormatting.RESET + "...", width / 2, 4, 0xffffff);

        int lc = 0;
        for (String line : info) {
            fontRenderer.drawString(line, width / 2 - 200, 16 + 8 * lc, 0xFFFFFF);
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
        int h = new ScaledResolution(mc).getScaledHeight() - 22;

        buttonList.add(new GuiButton(200, 2, h, 70, 20, I18n.format("mat.options.close")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Minecraft mc = Minecraft.getMinecraft();

        if (button.id == 200) {
            mc.displayGuiScreen(parentScreen);
        }
    }
}
