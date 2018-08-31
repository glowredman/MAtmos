package eu.ha3.matmos.editor.interfaces;

import java.io.File;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.tree.Selector;
import eu.ha3.matmos.serialisation.expansion.SerialEvent;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;

public interface Editor {
    File getExpansionDirectory();

    File getSoundDirectory();

    File getWorkingDirectory();

    void minecraftReloadFromDisk();

    void minecraftPushCurrentState();

    boolean isMinecraftControlled();

    boolean hasValidFile();

    boolean hasUnsavedChanges();

    File getFile();

    String generateJson(boolean pretty);

    void trySetAndLoadFile(File file);

    boolean quickSave();

    boolean longSave(File location, boolean setAsNewPointer);

    void switchEditItem(Selector selector, String itemName);

    void renameItem(String nameOfItem, Object editFocus, String text);

    void deleteItem(String nameOfItem, Object editFocus);

    boolean createItem(KnowledgeItemType choice, String name);

    void informInnerChange();

    SerialRoot getRootForCopyPurposes();

    void duplicateItem(Selector selector, String name);

    void purgeLogic();

    void purgeSupports();

    void pushSound(SerialEvent event);

    void mergeFrom(File file);
}
