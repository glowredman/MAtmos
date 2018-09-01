package eu.ha3.matmos.editor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;

import javax.swing.UIManager;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;

import eu.ha3.matmos.core.expansion.JsonExpansionDebugUnit;
import eu.ha3.matmos.debug.PluggableIntoMinecraft;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.Window;
import eu.ha3.matmos.editor.tree.Selector;
import eu.ha3.matmos.serialisation.JsonExpansions_EngineDeserializer;
import eu.ha3.matmos.serialisation.expansion.SerialEvent;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;
import eu.ha3.matmos.util.Json;

public class EditorMaster implements Runnable, Editor {

    private Window window__EventQueue;

    private final PluggableIntoMinecraft minecraft;

    private File file;
    private File workingDirectory = new File(System.getProperty("user.dir"));
    private SerialRoot root = new SerialRoot();
    private boolean hasModifiedContents;

    public EditorMaster() {
        this(null);
    }

    public EditorMaster(PluggableIntoMinecraft minecraft) {
        this(minecraft, null);
    }

    public EditorMaster(PluggableIntoMinecraft minecraft, File fileIn) {
        File potentialFile = fileIn;

        this.minecraft = minecraft;
        if (minecraft != null) {
            File fileIF = minecraft.getFileIfAvailable();
            File workingDirectoryIF = minecraft.getWorkingDirectoryIfAvailable();
            if (fileIF != null && workingDirectoryIF != null) {
                potentialFile = fileIF;
                workingDirectory = workingDirectoryIF;
            }
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        root = new SerialRoot();
        hasModifiedContents = false;
        file = potentialFile;
    }

    @Override
    public void run() {
        System.out.println("Loading designer...");
        java.awt.EventQueue.invokeLater(() -> initializedWindow(new EditorWindow(EditorMaster.this)));
    }

    private void initializedWindow(EditorWindow editorWindow) {
        //this.__WINDOW = editorWindow;
        window__EventQueue = new WindowEventQueue(editorWindow);
        window__EventQueue.display();

        System.out.println("Loaded.");

        if (file != null) {
            trySetAndLoadFile(file);
        }

        if (minecraft instanceof JsonExpansionDebugUnit) {
            flushFileAndSerial();
            root = new JsonExpansions_EngineDeserializer().jsonToSerial(((JsonExpansionDebugUnit)minecraft)
                    .getJsonString());
            updateFileAndContentsState();
        }
    }

    public static void main(String args[]) {
        new EditorMaster().run();
    }

    @Override
    public boolean isMinecraftControlled() {
        return minecraft != null;
    }

    @Override
    public void trySetAndLoadFile(File potentialFile) {
        if (potentialFile == null) {
            showErrorPopup("Missing file pointer.");
            return;
        }

        if (potentialFile.isDirectory()) {
            showErrorPopup("The selected file is actually a directory.");
            return;
        }

        if (!potentialFile.exists()) {
            showErrorPopup("The selected file is inaccessible.");
            return;
        }

        try {
            loadFile(potentialFile);
            file = potentialFile;
        } catch (Exception e) {
            file = null;

            showErrorPopup("File could not be loaded:\n" + e.getLocalizedMessage());
            reset();
            updateFileAndContentsState();
        }
    }

    private void reset() {
        flushFileAndSerial();
        modelize();
    }

    private void flushFileAndSerial() {
        file = null;
        root = new SerialRoot();
        hasModifiedContents = false;
        window__EventQueue.setEditFocus(null, null, false);
    }

    private void modelize() {
        java.awt.EventQueue.invokeLater(() -> {});
    }

    private void loadFile(File potentialFile) throws IOException, MalformedJsonException {
        flushFileAndSerial();
        //Solly edit - resource leak
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(potentialFile));
            String jasonString = sc.useDelimiter("\\Z").next();
            System.out.println(jasonString);
            root = new JsonExpansions_EngineDeserializer().jsonToSerial(jasonString);
            hasModifiedContents = false;
            updateFileAndContentsState();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    private void mergeFile(File potentialFile) throws IOException, MalformedJsonException {
        //Solly edit - resource leak
        Scanner sc = null;
        try {
            sc = new Scanner(new FileInputStream(potentialFile));
            String jasonString = sc.useDelimiter("\\Z").next();
            System.out.println(jasonString);
            SerialRoot mergeFrom = new JsonExpansions_EngineDeserializer().jsonToSerial(jasonString);

            if (Collections.disjoint(root.condition.keySet(), mergeFrom.condition.keySet())
                    && Collections.disjoint(root.dynamic.keySet(), mergeFrom.dynamic.keySet())
                    && Collections.disjoint(root.event.keySet(), mergeFrom.event.keySet())
                    && Collections.disjoint(root.list.keySet(), mergeFrom.list.keySet())
                    && Collections.disjoint(root.machine.keySet(), mergeFrom.machine.keySet())
                    && Collections.disjoint(root.set.keySet(), mergeFrom.set.keySet())) {} else {
                showErrorPopup("The two expansions have elements in common.\n"
                        + "The elements in common will be overriden by the file you are currently importing for the merge.");
            }

            root.condition.putAll(mergeFrom.condition);
            root.dynamic.putAll(mergeFrom.dynamic);
            root.event.putAll(mergeFrom.event);
            root.list.putAll(mergeFrom.list);
            root.machine.putAll(mergeFrom.machine);
            root.set.putAll(mergeFrom.set);

            hasModifiedContents = true;
            updateFileAndContentsState();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
    }

    private void updateFileAndContentsState() {
        window__EventQueue.refreshFileState();
        window__EventQueue.updateSerial(EditorMaster.this.root);
    }

    private void updateFileState() {
        window__EventQueue.refreshFileState();
    }

    private void showErrorPopup(String error) {
        window__EventQueue.showErrorPopup(error);
    }

    @Override
    public void minecraftReloadFromDisk() {
        minecraft.reloadFromDisk();
    }

    @Override
    public boolean hasValidFile() {
        return file != null;
    }

    @Override
    public File getWorkingDirectory() {
        return workingDirectory;
    }

    @Override
    public boolean hasUnsavedChanges() {
        return hasModifiedContents;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public String generateJson(boolean pretty) {
        return pretty ? Json.toJsonPretty(root) : Json.toJson(root);
    }

    @Override
    public void minecraftPushCurrentState() {
        minecraft.pushJson(Json.toJson(root));
    }

    @Override
    public boolean longSave(File location, boolean setAsNewPointer) {
        boolean success = writeToFile(location);

        if (success && setAsNewPointer) {
            file = location;
            hasModifiedContents = false;
            updateFileState();
        }

        return success;
    }

    @Override
    public boolean quickSave() {
        if (!hasValidFile()) {
            return false;
        }

        boolean success = writeToFile(file);
        if (success) {
            hasModifiedContents = false;
            updateFileState();
        }
        return success;
    }

    private boolean writeToFile(File fileToWrite) {
        try {
            if (!fileToWrite.exists()) {
                fileToWrite.createNewFile();
            }

            FileWriter write = new FileWriter(fileToWrite);
            write.append(Json.toJsonPretty(root));
            write.close();
        } catch (Exception e) {
            e.printStackTrace();

            window__EventQueue.showErrorPopup("Writing to disk resulted in an error: " + e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    @Override
    public File getExpansionDirectory() {
        return new File(workingDirectory, "assets/matmos/expansions").exists() ? new File(
                workingDirectory, "assets/matmos/expansions") : workingDirectory;
    }

    @Override
    public File getSoundDirectory() {
        return new File(workingDirectory, "assets/minecraft/sounds").exists() ? new File(
                workingDirectory, "assets/minecraft/sounds") : workingDirectory;
    }

    @Override
    public void switchEditItem(Selector selector, String itemName) {
        Map<String, ? extends Object> map = null;

        switch (selector) {
            case CONDITION:
                map = root.condition;
                break;
            case SET:
                map = root.set;
                break;
            case MACHINE:
                map = root.machine;
                break;
            case LIST:
                map = root.list;
                break;
            case DYNAMIC:
                map = root.dynamic;
                break;
            case EVENT:
                map = root.event;
                break;
            case LOGIC:
                break;
            case SUPPORT:
                break;
            default:
                break;
        }

        if (map != null && map.containsKey(itemName)) {
            window__EventQueue.setEditFocus(itemName, map.get(itemName), false);
        }
    }

    @Override
    public void renameItem(String oldName, Object editFocus, String newName) {
        if (oldName.equals(newName)) {
            return;
        }

        if (newName == null || newName.equals("") || newName.contains("\"") || newName.contains("\\")) {
            showErrorPopup("Name must not be empty or include the characters \" and \\");
            return;
        }

        try {
            SerialManipulator.rename(root, editFocus, oldName, newName);
            flagChange(true);
            window__EventQueue.setEditFocus(newName, editFocus, true);
        } catch (ItemNamingException e) {
            showErrorPopup(e.getMessage());
        }
    }

    @Override
    public void deleteItem(String nameOfItem, Object editFocus) {
        try {
            SerialManipulator.delete(root, editFocus, nameOfItem);
            flagChange(true);
            window__EventQueue.setEditFocus(null, null, false);
        } catch (ItemNamingException e) {
            showErrorPopup(e.getMessage());
        }
    }

    private void flagChange(boolean treeWasDeeplyModified) {
        boolean previousStateIsFalse = !hasModifiedContents;
        hasModifiedContents = true;

        if (treeWasDeeplyModified) {
            updateFileAndContentsState();
        } else {
            if (previousStateIsFalse) {
                updateFileState();
            }
        }
    }

    @Override
    public boolean createItem(KnowledgeItemType choice, String name) {
        try {
            Object o = SerialManipulator.createNew(root, choice, name);
            flagChange(true);
            window__EventQueue.setEditFocus(name, o, true);
            return true;
        } catch (ItemNamingException e) {
            showErrorPopup(e.getMessage());
        }

        return false;
    }

    @Override
    public void informInnerChange() {
        flagChange(false);
    }

    @Override
    public SerialRoot getRootForCopyPurposes() {
        return root;
    }

    @Override
    public void duplicateItem(Selector selector, String name) {
        switch (selector) {
            case CONDITION:
                doDuplicateItem(selector, name, root.condition);
                break;
            case SET:
                doDuplicateItem(selector, name, root.set);
                break;
            case MACHINE:
                doDuplicateItem(selector, name, root.machine);
                break;
            case LIST:
                doDuplicateItem(selector, name, root.list);
                break;
            case DYNAMIC:
                doDuplicateItem(selector, name, root.dynamic);
                break;
            case EVENT:
                doDuplicateItem(selector, name, root.event);
                break;
            case LOGIC:
                break;
            case SUPPORT:
                break;
            default:
                break;
        }
    }

    private <T> void doDuplicateItem(Selector selector, String name, Map<String, T> map) {
        if (map == null) {
            return;
        }

        if (!map.containsKey(name)) {
            return;
        }

        Class<? extends Object> serialClass = map.get(name).getClass();
        @SuppressWarnings("unchecked")
        T duplicate = (T)new Gson().fromJson(new JsonParser().parse(Json.toJson(map.get(name))).getAsJsonObject(), serialClass);

        int add = 1;
        while (map.containsKey(name + " (" + add + ")")) {
            add = add + 1;
        }
        map.put(name + " (" + add + ")", duplicate);

        flagChange(true);
        window__EventQueue.setEditFocus(name + " (" + add + ")", duplicate, true);
    }

    @Override
    public void purgeLogic() {
        new PurgeOperator().purgeLogic(root);
        flagChange(true);
        window__EventQueue.setEditFocus(null, null, false);
    }

    @Override
    public void purgeSupports() {
        new PurgeOperator().purgeEvents(root);
        flagChange(true);
        window__EventQueue.setEditFocus(null, null, false);
    }

    @Override
    public void pushSound(SerialEvent event) {
    }

    @Override
    public void mergeFrom(File potentialFile) {
        if (potentialFile == null) {
            showErrorPopup("Missing file pointer.");
            return;
        }

        if (potentialFile.isDirectory()) {
            showErrorPopup("The selected file is actually a directory.");
            return;
        }

        if (!potentialFile.exists()) {
            showErrorPopup("The selected file is inaccessible.");
            return;
        }

        try {
            mergeFile(potentialFile);
        } catch (Exception e) {
            showErrorPopup("Merge error. The current state may be corrupt:\n" + e.getLocalizedMessage());
        }
    }
}
