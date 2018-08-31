package eu.ha3.matmos.gui;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import eu.ha3.matmos.core.expansion.VolumeUpdatable;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.*;

public class GuiMatMenu extends GuiScreen {
    private GuiScreen parentScreen;

    private MAtMod mod;

    private int buttonId;

    private int pageFromZero;
    private final int IDS_PER_PAGE = 5;

    private final List<Expansion> expansionList = new ArrayList<Expansion>();

    // Keep the active page in memory. Globally... (herpderp)
    private static int in_memory_page = 0;

    public GuiMatMenu(GuiScreen par1GuiScreen, MAtMod matmos) {
        this(par1GuiScreen, matmos, in_memory_page);
    }

    public GuiMatMenu(GuiScreen par1GuiScreen, MAtMod matmos, int pageFromZero) {
        this.buttonId = -1;
        this.parentScreen = par1GuiScreen;
        this.mod = matmos;
        this.pageFromZero = pageFromZero;

        in_memory_page = this.pageFromZero;
    }

    @Override
    public void initGui() {
        final int _GAP = 2;
        final int _UNIT = 20;
        final int _ELEMENT_WIDTH = 155 * 2;

        final int _MIX = _GAP + _UNIT;

        final int _LEFT = this.width / 2 - _ELEMENT_WIDTH / 2;
        final int _RIGHT = this.width / 2 + _ELEMENT_WIDTH / 2;

        final int _PREVNEWTWIDTH = _ELEMENT_WIDTH / 3;
        final int _ASPLIT = 2;
        final int _AWID = _ELEMENT_WIDTH / _ASPLIT - _GAP * (_ASPLIT - 1) / 2;

        final int _SEPARATOR = 10;

        Map<String, Expansion> expansions = this.mod.getExpansionList();
        int id = 0;

        {
            final VolumeUpdatable globalVolumeControl = this.mod.getGlobalVolumeControl();

            HGuiSliderControl sliderControl = new HGuiSliderControl(id, _LEFT, _MIX, _ELEMENT_WIDTH, _UNIT, "", globalVolumeControl.getVolume() * 0.5f);
            sliderControl.setListener((slider, value) -> {
                globalVolumeControl.setVolumeAndUpdate(value * 2);
                slider.updateDisplayString();
                GuiMatMenu.this.mod.getConfig().setProperty("globalvolume.scale", globalVolumeControl.getVolume());
            });
            sliderControl.setDisplayStringProvider(() -> {
                return I18n.format("mat.options.volume", (int)Math.floor(globalVolumeControl.getVolume() * 100) + "%");
            });
            sliderControl.updateDisplayString();

            this.buttonList.add(sliderControl);
            id++;

        }

        List<String> sortedNames = new ArrayList<String>(expansions.keySet());
        Collections.sort(sortedNames);

        for (int expansionIndex = this.pageFromZero * this.IDS_PER_PAGE; expansionIndex < this.pageFromZero
                * this.IDS_PER_PAGE + this.IDS_PER_PAGE
                && expansionIndex < sortedNames.size(); expansionIndex++) {
            final String uniqueIdentifier = sortedNames.get(expansionIndex);
            final Expansion expansion = expansions.get(uniqueIdentifier);
            this.expansionList.add(expansion);

            HGuiSliderControl sliderControl = new HGuiSliderControl(
                    id, _LEFT + _MIX, _MIX * (id + 1), _ELEMENT_WIDTH - _MIX * 2, _UNIT, "",
                    expansion.getVolume() * 0.5f);
            sliderControl.setListener(new HSliderListener() {
                @Override
                public void sliderValueChanged(HGuiSliderControl slider, float value) {
                    expansion.setVolumeAndUpdate(value * 2);
                    if (value > 0f && !expansion.isActivated()) {
                        expansion.activate();
                    }
                    slider.updateDisplayString();
                }

                @Override
                public void sliderReleased(HGuiSliderControl hGuiSliderControl) {
                    if (GuiMatMenu.this.isAutopreviewEnabled()) {
                        expansion.playSample();
                    }
                }
            });

            sliderControl.setDisplayStringProvider(() -> {
                String display = expansion.getFriendlyName() + ": ";
                if (expansion.getVolume() == 0f) {
                    if (expansion.isActivated()) {
                        display = display + "Will be disabled";
                    } else {
                        display = display + "Disabled";
                    }
                } else {
                    display = display + (int)Math.floor(expansion.getVolume() * 100) + "%";
                }

                return TextFormatting.ITALIC + display;
            });
            sliderControl.updateDisplayString();

            this.buttonList.add(sliderControl);

            if (!this.mod.isDebugMode()) {
                buttonList.add(new GuiButton(Make.make(() -> {
                    if (expansion.isActivated()) {
                        expansion.playSample();
                    }
                }), _RIGHT - _UNIT, _MIX * (id + 1), _UNIT, _UNIT, "?"));

                if (expansion.hasMoreInfo()) {
                    buttonList.add(new GuiButton(Make.make(() -> {
                        mc.displayGuiScreen(new GuiExpansionInfo(this, mod, expansion));
                    }), _RIGHT + _GAP, _MIX * (id + 1), _UNIT, _UNIT, "..."));
                }
            } else {
                this.buttonList.add(new GuiButton(Make.make(() -> {
                    mc.displayGuiScreen(new GuiExpansionDetails(this, mod, expansion));
                }), _RIGHT - _UNIT, _MIX * (id + 1), _UNIT, _UNIT, TextFormatting.GOLD + "+"));
            }

            id++;

        }

        if (!this.mod.isDebugMode()) {
            this.buttonList.add(new GuiButton(220, _RIGHT - _UNIT, _MIX * (this.IDS_PER_PAGE + 2), _UNIT, _UNIT, isAutopreviewEnabled() ? "^o^" : "^_^"));
        } else {
            this.buttonList.add(new GuiButton(230, _RIGHT - _UNIT, _MIX * (this.IDS_PER_PAGE + 2), 40, _UNIT, TextFormatting.GOLD + "OSD"));
        }

        if (this.pageFromZero != 0) {
            this.buttonList.add(new GuiButton(201, _LEFT + _MIX, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, I18n.format("mat.options.prev")));
        }
        if (this.pageFromZero * this.IDS_PER_PAGE + this.IDS_PER_PAGE < sortedNames.size()) {
            this.buttonList.add(new GuiButton(202, _RIGHT - _MIX - _PREVNEWTWIDTH, _MIX * (this.IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, I18n.format("mat.options.next")));
        }

        this.buttonList.add(new GuiButton(210, _LEFT, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT, I18n.format("mat.options.start." + mod.getConfig().getBoolean("start.enabled"))));
        this.buttonList.add(new GuiButton(211, _LEFT + _AWID + _GAP, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3), _AWID, _UNIT, (this.mod.isDebugMode() ? TextFormatting.GOLD : "") + I18n.format("mat.options.advanced")));

        final int _TURNOFFWIDTH = _ELEMENT_WIDTH / 5;

        this.buttonList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 4), _ELEMENT_WIDTH - _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
        this.buttonList.add(new GuiButton(212, _RIGHT - _TURNOFFWIDTH - _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 4), _TURNOFFWIDTH, _UNIT, "Turn Off"));

        if (!this.mod.hasResourcePacksLoaded()) {
            this.buttonList.add(new GuiButton(199, _LEFT + _MIX, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 1), _ELEMENT_WIDTH - _MIX * 2, _UNIT, (this.mod.hasNonethelessResourcePacksInstalled() ? "Enable" : "Install") + " MAtmos Resource Pack"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        Minecraft mc = Minecraft.getMinecraft();

        if (par1GuiButton.id == 200) {
            // This triggers onGuiClosed
            mc.displayGuiScreen(this.parentScreen);
        } else if (par1GuiButton.id == 199) {
            // This triggers onGuiClosed
            this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
        } else if (par1GuiButton.id == 201) {
            mc.displayGuiScreen(new GuiMatMenu(parentScreen, mod, pageFromZero - 1));
        } else if (par1GuiButton.id == 202) {
            mc.displayGuiScreen(new GuiMatMenu(parentScreen, mod, pageFromZero + 1));
        } else if (par1GuiButton.id == 210) {
            boolean newEnabledState = !this.mod.getConfig().getBoolean("start.enabled");
            this.mod.getConfig().setProperty("start.enabled", newEnabledState);
            par1GuiButton.displayString = I18n.format("mat.options.start." + newEnabledState);
            this.mod.saveConfig();
        } else if (par1GuiButton.id == 211) {
            mc.displayGuiScreen(new GuiMore(this, this.mod));
        } else if (par1GuiButton.id == 212) {
            mc.displayGuiScreen(this.parentScreen);
            this.mod.deactivate();
        } else if (par1GuiButton.id == 220) {
            this.mod.getConfig().setProperty("sound.autopreview", !isAutopreviewEnabled());
            par1GuiButton.displayString = isAutopreviewEnabled() ? "^o^" : "^_^";
            this.mod.saveConfig();
        } else if (par1GuiButton.id == 230) {
            mc.displayGuiScreen(new GuiModules(this, this.mod));
        } else {
            Make.perform(par1GuiButton.id);
        }

    }

    private boolean isAutopreviewEnabled() {
        return this.mod.getConfig().getBoolean("sound.autopreview");
    }

    private void aboutToClose() {
        this.mod.synchronize();
        this.mod.saveExpansions();
        this.mod.saveConfig();
    }

    @Override
    public void onGuiClosed() {
        aboutToClose();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (this.buttonId < 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        final int _GAP = 2;
        final int _UNIT = 20;
        final int _MIX = _GAP + _UNIT;
        final int _SEPARATOR = 10;

        if (!this.mod.isDebugMode()) {
            drawGradientRect(0, 0, this.width, this.height, 0xC0000000, 0x60000000);
            drawCenteredString(this.fontRenderer, I18n.format("mat.title.expansions"), this.width / 2, 8, 0xffffff);
        } else {
            drawGradientRect(0, 0, this.width, this.height, 0xC0C06000, 0x60C06000);
            drawCenteredString(this.fontRenderer, I18n.format("mat.title.expansions") + TextFormatting.GOLD + "(Dev mode)", this.width / 2, 8, 0xffffff);
            drawCenteredString(this.fontRenderer, I18n.format("mat.title.devmode"), this.width / 2, _SEPARATOR + _MIX * (this.IDS_PER_PAGE + 3) - 9, 0xffffff);
        }

        mod.util().prepareDrawString();
        mod.util().drawString(I18n.format("mat.resources.lag", mod.getLag().getMilliseconds()), 1f, 1f, 0, 0, '3', 0, 0, 0, 0, true);

        if (!this.mod.hasResourcePacksLoaded()) {
            if (this.mod.hasNonethelessResourcePacksInstalled()) {
                drawCenteredString(this.fontRenderer, I18n.format("mat.resources.off.1"), width / 2, 10 + 22 * 6 - 40 + 20, 0xff0000);
                drawCenteredString(this.fontRenderer, I18n.format("mat.resources.off.2"), width / 2, 10 + 22 * 6 - 40 + 28, 0xff0000);
            } else {
                drawCenteredString(this.fontRenderer, I18n.format("mat.resources.none.1"), width / 2, 10 + 22 * 6 - 40 + 20, 0xff0000);
                drawCenteredString(this.fontRenderer, I18n.format("mat.resources.none.2"), width / 2, 10 + 22 * 6 - 40 + 28, 0xff0000);
            }
        }

        super.drawScreen(par1, par2, par3);

    }

    @Deprecated
    private static class Make {
        private static int makeIn = 1000;
        private static Map<Integer, ActionPerformed> actions = new HashMap<Integer, ActionPerformed>();

        public static int make(ActionPerformed callback) {
            int make = makeIn;
            makeIn = makeIn + 1;

            Make.actions.put(make, callback);

            return make;
        }

        public static void perform(int action) {
            if (!Make.actions.containsKey(action)) return;

            Make.actions.get(action).actionPerformed();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @FunctionalInterface
    private interface ActionPerformed {
        void actionPerformed();
    }
}
