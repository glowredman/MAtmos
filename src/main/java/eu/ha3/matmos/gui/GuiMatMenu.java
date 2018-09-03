package eu.ha3.matmos.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import eu.ha3.matmos.core.expansion.VolumeUpdatable;
import eu.ha3.mc.gui.HGuiSliderControl;
import eu.ha3.mc.gui.HSliderListener;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class GuiMatMenu extends GuiScreen {
    private GuiScreen parentScreen;

    private MAtMod mod;

    private int buttonId;

    private int pageFromZero;
    private final int IDS_PER_PAGE = 5;

    private final List<Expansion> expansionList = new ArrayList<>();

    // Keep the active page in memory. Globally... (herpderp)
    private static int in_memory_page = 0;

    public GuiMatMenu(GuiScreen par1GuiScreen, MAtMod matmos) {
        this(par1GuiScreen, matmos, in_memory_page);
    }

    public GuiMatMenu(GuiScreen par1GuiScreen, MAtMod matmos, int pageFromZero) {
        buttonId = -1;
        parentScreen = par1GuiScreen;
        mod = matmos;
        this.pageFromZero = pageFromZero;

        in_memory_page = this.pageFromZero;
    }

    @Override
    public void initGui() {
        final int _GAP = 2;
        final int _UNIT = 20;
        final int _ELEMENT_WIDTH = 155 * 2;

        final int _MIX = _GAP + _UNIT;

        final int _LEFT = width / 2 - _ELEMENT_WIDTH / 2;
        final int _RIGHT = width / 2 + _ELEMENT_WIDTH / 2;

        final int _PREVNEWTWIDTH = _ELEMENT_WIDTH / 3;
        final int _ASPLIT = 2;
        final int _AWID = _ELEMENT_WIDTH / _ASPLIT - _GAP * (_ASPLIT - 1) / 2;

        final int _SEPARATOR = 10;

        Map<String, Expansion> expansions = mod.getExpansionList();
        int id = 0;

        {
            final VolumeUpdatable globalVolumeControl = mod.getGlobalVolumeControl();

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

            buttonList.add(sliderControl);
            id++;

        }

        List<String> sortedNames = new ArrayList<>(expansions.keySet());
        Collections.sort(sortedNames);

        for (int expansionIndex = pageFromZero * IDS_PER_PAGE; expansionIndex < pageFromZero
                * IDS_PER_PAGE + IDS_PER_PAGE
                && expansionIndex < sortedNames.size(); expansionIndex++) {
            final String uniqueIdentifier = sortedNames.get(expansionIndex);
            final Expansion expansion = expansions.get(uniqueIdentifier);
            expansionList.add(expansion);

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

            buttonList.add(sliderControl);

            if (!mod.isDebugMode()) {
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
                buttonList.add(new GuiButton(Make.make(() -> {
                    mc.displayGuiScreen(new GuiExpansionDetails(this, mod, expansion));
                }), _RIGHT - _UNIT, _MIX * (id + 1), _UNIT, _UNIT, TextFormatting.GOLD + "+"));
            }

            id++;

        }

        if (!mod.isDebugMode()) {
            buttonList.add(new GuiButton(220, _RIGHT - _UNIT, _MIX * (IDS_PER_PAGE + 2), _UNIT, _UNIT, isAutopreviewEnabled() ? "^o^" : "^_^"));
        } else {
            buttonList.add(new GuiButton(230, _RIGHT - _UNIT, _MIX * (IDS_PER_PAGE + 2), 40, _UNIT, TextFormatting.GOLD + "OSD"));
        }

        if (pageFromZero != 0) {
            buttonList.add(new GuiButton(201, _LEFT + _MIX, _MIX * (IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, I18n.format("mat.options.prev")));
        }
        if (pageFromZero * IDS_PER_PAGE + IDS_PER_PAGE < sortedNames.size()) {
            buttonList.add(new GuiButton(202, _RIGHT - _MIX - _PREVNEWTWIDTH, _MIX * (IDS_PER_PAGE + 2), _PREVNEWTWIDTH, _UNIT, I18n.format("mat.options.next")));
        }

        buttonList.add(new GuiButton(210, _LEFT, _SEPARATOR + _MIX * (IDS_PER_PAGE + 3), _AWID, _UNIT, I18n.format("mat.options.start." + mod.getConfig().getBoolean("start.enabled"))));
        buttonList.add(new GuiButton(211, _LEFT + _AWID + _GAP, _SEPARATOR + _MIX * (IDS_PER_PAGE + 3), _AWID, _UNIT, (mod.isDebugMode() ? TextFormatting.GOLD : "") + I18n.format("mat.options.advanced")));

        final int _TURNOFFWIDTH = _ELEMENT_WIDTH / 5;

        buttonList.add(new GuiButton(200, _LEFT + _MIX, _SEPARATOR + _MIX * (IDS_PER_PAGE + 4), _ELEMENT_WIDTH - _MIX * 2 - _GAP - _TURNOFFWIDTH, _UNIT, "Done"));
        buttonList.add(new GuiButton(212, _RIGHT - _TURNOFFWIDTH - _MIX, _SEPARATOR + _MIX * (IDS_PER_PAGE + 4), _TURNOFFWIDTH, _UNIT, "Turn Off"));

        if (!mod.hasResourcePacksLoaded()) {
            buttonList.add(new GuiButton(199, _LEFT + _MIX, _SEPARATOR + _MIX * (IDS_PER_PAGE + 1), _ELEMENT_WIDTH - _MIX * 2, _UNIT, (mod.hasNonethelessResourcePacksInstalled() ? "Enable" : "Install") + " MAtmos Resource Pack"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton) {
        if (par1GuiButton.id == 200) {
            // This triggers onGuiClosed
            mc.displayGuiScreen(parentScreen);
        } else if (par1GuiButton.id == 199) {
            // This triggers onGuiClosed
            this.mc.displayGuiScreen(new GuiScreenResourcePacks(this));
        } else if (par1GuiButton.id == 201) {
            mc.displayGuiScreen(new GuiMatMenu(parentScreen, mod, pageFromZero - 1));
        } else if (par1GuiButton.id == 202) {
            mc.displayGuiScreen(new GuiMatMenu(parentScreen, mod, pageFromZero + 1));
        } else if (par1GuiButton.id == 210) {
            boolean newEnabledState = !mod.getConfig().getBoolean("start.enabled");
            mod.getConfig().setProperty("start.enabled", newEnabledState);
            par1GuiButton.displayString = I18n.format("mat.options.start." + newEnabledState);
            mod.saveConfig();
        } else if (par1GuiButton.id == 211) {
            mc.displayGuiScreen(new GuiMore(this, mod));
        } else if (par1GuiButton.id == 212) {
            mc.displayGuiScreen(parentScreen);
            mod.deactivate();
        } else if (par1GuiButton.id == 220) {
            mod.getConfig().setProperty("sound.autopreview", !isAutopreviewEnabled());
            par1GuiButton.displayString = isAutopreviewEnabled() ? "^o^" : "^_^";
            mod.saveConfig();
        } else if (par1GuiButton.id == 230) {
            mc.displayGuiScreen(new GuiModules(this, mod));
        } else {
            Make.perform(par1GuiButton.id);
        }

    }

    private boolean isAutopreviewEnabled() {
        return mod.getConfig().getBoolean("sound.autopreview");
    }

    private void aboutToClose() {
        mod.synchronize();
        mod.saveExpansions();
        mod.saveConfig();
    }

    @Override
    public void onGuiClosed() {
        aboutToClose();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (buttonId < 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        final int _GAP = 2;
        final int _UNIT = 20;
        final int _MIX = _GAP + _UNIT;
        final int _SEPARATOR = 10;

        if (!mod.isDebugMode()) {
            drawGradientRect(0, 0, width, height, 0xC0000000, 0x60000000);
            drawCenteredString(fontRenderer, I18n.format("mat.title.expansions"), width / 2, 8, 0xffffff);
        } else {
            drawGradientRect(0, 0, width, height, 0xC0C06000, 0x60C06000);
            drawCenteredString(fontRenderer, I18n.format("mat.title.expansions") + TextFormatting.GOLD + "(Dev mode)", width / 2, 8, 0xffffff);
            drawCenteredString(fontRenderer, I18n.format("mat.title.devmode"), width / 2, _SEPARATOR + _MIX * (IDS_PER_PAGE + 3) - 9, 0xffffff);
        }

        mod.util().prepareDrawString();
        mod.util().drawString(I18n.format("mat.resources.lag", mod.getLag().getMilliseconds()), 1f, 1f, 0, 0, '3', 0, 0, 0, 0, true);

        if (!mod.hasResourcePacksLoaded()) {
            if (mod.hasNonethelessResourcePacksInstalled()) {
                drawCenteredString(fontRenderer, I18n.format("mat.resources.off.1"), width / 2, 10 + 22 * 6 - 40 + 20, 0xff0000);
                drawCenteredString(fontRenderer, I18n.format("mat.resources.off.2"), width / 2, 10 + 22 * 6 - 40 + 28, 0xff0000);
            } else {
                drawCenteredString(fontRenderer, I18n.format("mat.resources.none.1"), width / 2, 10 + 22 * 6 - 40 + 20, 0xff0000);
                drawCenteredString(fontRenderer, I18n.format("mat.resources.none.2"), width / 2, 10 + 22 * 6 - 40 + 28, 0xff0000);
            }
        }

        super.drawScreen(par1, par2, par3);

    }

    @Deprecated
    private static class Make {
        private static int makeIn = 1000;
        private static Map<Integer, ActionPerformed> actions = new HashMap<>();

        public static int make(ActionPerformed callback) {
            int make = makeIn;
            makeIn = makeIn + 1;

            Make.actions.put(make, callback);

            return make;
        }

        public static void perform(int action) {
            if (!Make.actions.containsKey(action)) {
                return;
            }

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
