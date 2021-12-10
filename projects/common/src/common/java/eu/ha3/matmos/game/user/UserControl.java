package eu.ha3.matmos.game.user;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;

import eu.ha3.easy.TimeStatistic;
import eu.ha3.matmos.ConfigManager;
import eu.ha3.matmos.Matmos;
import eu.ha3.matmos.gui.GuiMatMenu;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.mc.convenience.Ha3HoldActions;
import eu.ha3.mc.convenience.Ha3KeyHolding;
import eu.ha3.mc.convenience.Ha3KeyManager_2;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import eu.ha3.mc.abstraction.util.ATextFormatting;

public class UserControl implements Ha3HoldActions, SupportsTickEvents, SupportsFrameEvents {
    private final Matmos mod;
    private final Ha3KeyManager_2 keyManager = new Ha3KeyManager_2();

    private KeyBinding keyBindingMain;
    private VolumeScroller scroller;
    private int tickRound;

    private int loadingCount;

    public UserControl(Matmos mod) {
        this.mod = mod;
    }

    public void load() {
        keyBindingMain = new KeyBinding("MAtmos", ConfigManager.DEFAULT_KEY, "key.categories.misc");
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils
                .addAll(Minecraft.getMinecraft().gameSettings.keyBindings, keyBindingMain);
        keyBindingMain.setKeyCode(mod.getConfig().getInteger("key.code"));
        KeyBinding.resetKeyBindingArrayAndHash();

        keyManager.addKeyBinding(keyBindingMain, new Ha3KeyHolding(this, 7));

        scroller = new VolumeScroller(mod);
    }

    private String getKeyBindingMainFriendlyName() {
        if (keyBindingMain == null) {
            return "undefined";
        }

        return Keyboard.getKeyName(keyBindingMain.getKeyCode());
    }

    @Override
    public void onTick() {
        keyManager.onTick();

        // Copied from Sollace's Presence Footsteps solution to bindings disappearing
        if (tickRound == 0) {
            int keyCode = keyBindingMain.getKeyCode();
            if (keyCode != mod.getConfig().getInteger("key.code")) {
                // PFLog.log("Key binding changed. Saving...");
                mod.getConfig().setProperty("key.code", keyCode);
                mod.saveConfig();
            }
        }
        tickRound = (tickRound + 1) % 100;

        scroller.routine();
        if (scroller.isRunning()) {
            mod.getGlobalVolumeControl().setVolumeAndUpdate(scroller.getValue());
        }
    }

    @Override
    public void onFrame(float fspan) {
        scroller.draw(fspan);
    }

    private void printUnusualMessages() {
        if (!mod.isInitialized()) {
            mod.getChatter().printChat(ATextFormatting.RED, "Unknown error: MAtmos isn't initialized");
        } else {
            if (!MAtUtil.isSoundMasterEnabled()) {
                mod.getChatter().printChat(ATextFormatting.RED, "Warning: ", ATextFormatting.WHITE,
                        "Sounds are turned off in your game settings!");
            }
            if (!MAtUtil.isSoundAmbientEnabled()) {
                mod.getChatter().printChat(ATextFormatting.RED, "Warning: ", ATextFormatting.WHITE,
                        "Ambient sounds are at 0%% volume in the Minecraft options menu!");
            }
        }
    }

    @Override
    public void beginHold() {
        if (mod.isActivated()) {
            scroller.start();
        }
    }

    @Override
    public void shortPress() {
        if (!mod.isActivated()) {
            whenWantsToggle();
        } else {
            displayMenu();
        }

        printUnusualMessages();
    }

    @Override
    public void endHold() {
        if (scroller.isRunning()) {
            scroller.stop();
            mod.getConfig().setProperty("globalvolume.scale", mod.getGlobalVolumeControl().getVolume());
            mod.saveConfig();
        }

        whenWantsForcing();
        printUnusualMessages();

    }

    private void whenWantsToggle() {
        if (mod.isActivated()) {
            mod.deactivate();
            mod.getChatter().printChat(ATextFormatting.YELLOW, "Stopped. Press ", ATextFormatting.WHITE,
                    getKeyBindingMainFriendlyName(), ATextFormatting.YELLOW, " to re-enable.");

        } else if (mod.isInitialized()) {
            if (loadingCount != 0) {
                mod.getChatter().printChat(ATextFormatting.GREEN, "Loading...");
            } else {
                mod.getChatter().printChat(ATextFormatting.GREEN, "Loading...", ATextFormatting.YELLOW, " (Hold ",
                        ATextFormatting.WHITE, getKeyBindingMainFriendlyName() + " down", ATextFormatting.YELLOW,
                        " to tweak the volume)");
            }

            loadingCount++;
            mod.activate();

        } else if (!mod.isInitialized()) {
            whenUninitializedAction();
        }

    }

    private void whenUninitializedAction() {
        if (mod.isInitialized()) {
            return;
        }

        TimeStatistic stat = new TimeStatistic();
        mod.start();
        mod.getChatter().printChat(ATextFormatting.GREEN,
                "Loading for the first time (" + stat.getSecondsAsString(2) + "s)");
    }

    private void whenWantsForcing() {
        if (!mod.isActivated() && mod.isInitialized()) {
            TimeStatistic stat = new TimeStatistic();
            mod.refresh();
            mod.activate();
            mod.getChatter().printChat(ATextFormatting.GREEN,
                    "Reloading expansions (" + stat.getSecondsAsString(2) + "s)");
        } else if (!mod.isInitialized()) {
            whenUninitializedAction();
        }
    }

    private void displayMenu() {
        if (mod.isActivated() && mod.util().isCurrentScreen(null)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiMatMenu((GuiScreen) mod.util().getCurrentScreen(), mod));
        }
    }

    @Override
    public void beginPress() {
    }

    @Override
    public void endPress() {
    }

}
