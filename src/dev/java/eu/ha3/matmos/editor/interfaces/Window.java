package eu.ha3.matmos.editor.interfaces;

import java.awt.Component;

public interface Window extends ISerialUpdate, NamedSerialEditor {
    Component asComponent();

    void display();

    void refreshFileState();

    void showErrorPopup(String error);

    void disableMinecraftCapabilitites();
}
