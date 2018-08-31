package eu.ha3.matmos.gui;

import eu.ha3.matmos.MAtMod;
import eu.ha3.matmos.core.expansion.Expansion;
import eu.ha3.matmos.debug.expansions.ExpansionDebugUnit;
import eu.ha3.matmos.debug.expansions.FolderResourcePackEditableEDU;
import eu.ha3.matmos.debug.expansions.ReadOnlyJasonStringEDU;
import eu.ha3.matmos.debug.game.PluggableIntoMAtmos;
import eu.ha3.matmos.debug.game.SoundsJsonGenerator;
import eu.ha3.matmos.game.user.VisualExpansionDebugging;
import eu.ha3.matmos.util.IDontKnowHowToCode;

import java.io.File;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

public class MAtGuiExpansionDetails extends GuiScreen {
    private final MAtGuiMenu parentScreen;
    private final MAtMod mod;
    private final Expansion expansion;
    private final VisualExpansionDebugging debug;

    public MAtGuiExpansionDetails(MAtGuiMenu menu, MAtMod mod, Expansion expansion) {
        this.parentScreen = menu;
        this.mod = mod;
        this.expansion = expansion;
        this.debug = new VisualExpansionDebugging(this.mod, expansion.getName());
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawGradientRect(0, 0, this.width, this.height, 0xF0000000, 0x90000000);

        drawCenteredString(this.fontRenderer, I18n.format("mat.mode.dev", this.expansion.getFriendlyName(), this.expansion.getName()), this.width / 2, 4, 0xffffff);

        this.debug.onFrame(0f);

        super.drawScreen(par1, par2, par3);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        int h = new ScaledResolution(this.mc).getScaledHeight() - 22;

        buttonList.add(new GuiButton(200, 2, h, 70, 20, I18n.format("mat.options.close")));
        buttonList.add(new GuiButton(201, 4 + 70, h, 70, 20, I18n.format("mat.options.osd")));
        buttonList.add(new GuiButton(202, 6 + 70 * 2, h, 70, 20, I18n.format("mat.options.reload")));
        
        String editor = I18n.format(mod.isEditorAvailable() ? "mat.options.editor" : "mat.options.editor.disabled");
        
        buttonList.add(new GuiButton(203, 8 + 70 * 3, h, 110, 20, editor));
        buttonList.add(new GuiButton(204, 10 + 70 * 3 + 110, h, 96, 20, I18n.format("mat.options.sounds")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 200) {
            // This triggers onGuiClosed
            mc.displayGuiScreen(this.parentScreen);
        } else if (button.id == 201) {
            this.mod.getVisualDebugger().debugModeExpansion(this.debug);
            mc.displayGuiScreen(null);
        } else if (button.id == 202) {
            this.expansion.refreshKnowledge();
        } else if (button.id == 203 && this.mod.isEditorAvailable()) {
            final ExpansionDebugUnit debugUnit = this.expansion.obtainDebugUnit();
            if (debugUnit != null) {
                PluggableIntoMAtmos plug = new PluggableIntoMAtmos(this.mod, this.expansion);

                Runnable editor = this.mod.instantiateRunnableEditor(plug);
                if (editor != null) {
                    new Thread(editor, "EditorWindow_for_" + expansion.getName()).start();

                    if (debugUnit instanceof ReadOnlyJasonStringEDU) {
                        // XXX Read only mode
                        mod.getChatter().printChat(TextFormatting.RED, I18n.format("mat.zip.unsupported"));
                        mod.getChatter().printChatShort(TextFormatting.RED, I18n.format("mat.zip.unzip"));
                    }
                } else {
                    mod.getChatter().printChat(TextFormatting.RED, "Could not start editor for an unknown reason.");
                }
            }
        } else if (button.id == 204) {
            final ExpansionDebugUnit debugUnit = expansion.obtainDebugUnit();
            
            if (debugUnit instanceof FolderResourcePackEditableEDU) {
                
                File expFolder = ((FolderResourcePackEditableEDU)debugUnit).obtainExpansionFolder();
                File minecraftFolder = new File(expFolder, "assets/minecraft/");
                
                if (minecraftFolder.exists()) {
                    
                    File soundsFolder = new File(minecraftFolder, "sounds/");
                    File jsonFile = new File(minecraftFolder, "sounds.json");
                    
                    if (soundsFolder.exists()) {
                        try {
                            new SoundsJsonGenerator(soundsFolder, jsonFile).run();
                            
                            this.mod.getChatter().printChat(I18n.format("mat.generator.done", jsonFile.getAbsolutePath()));
                            this.mod.getChatter().printChatShort(I18n.format("mat.generator.plswait"));
                        } catch (Exception e) {
                            e.printStackTrace(System.out);
                            IDontKnowHowToCode.whoops__printExceptionToChat(mod.getChatter(), e, this);
                        }
                    } else {
                        mod.getChatter().printChat(TextFormatting.RED, I18n.format("mat.folders.sounds"));
                    }
                } else {
                    mod.getChatter().printChat(TextFormatting.RED, I18n.format("mat.folders.mc"));
                }
            }
        }
    }
}
