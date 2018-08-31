package eu.ha3.matmos.editor;

import eu.ha3.matmos.editor.interfaces.Window;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;

import java.awt.*;

public class WindowEventQueue implements Window {
    private Window window;

    public WindowEventQueue(Window window) {
        this.window = window;
    }

    @Override
    public void updateSerial(final SerialRoot root) {
        EventQueue.invokeLater(() -> window.updateSerial(root));
    }

    @Override
    public void setEditFocus(final String name, final Object item, boolean forceSelect) {
        EventQueue.invokeLater(() -> window.setEditFocus(name, item, false));
    }

    @Override
    public Component asComponent() {
        return this.window.asComponent();
    }

    @Override
    public void display() {
        EventQueue.invokeLater(window::display);
    }

    @Override
    public void refreshFileState() {
        EventQueue.invokeLater(window::refreshFileState);
    }

    @Override
    public void showErrorPopup(String error) {
        EventQueue.invokeLater(() -> window.showErrorPopup(error));
    }

    @Override
    public void disableMinecraftCapabilitites() {
        EventQueue.invokeLater(window::disableMinecraftCapabilitites);
    }
}
