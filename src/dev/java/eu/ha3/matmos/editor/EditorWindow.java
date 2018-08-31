package eu.ha3.matmos.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import eu.ha3.matmos.editor.edit.EditPanel;
import eu.ha3.matmos.editor.filechooser.JsonFileChooser;
import eu.ha3.matmos.editor.filechooser.OverwriteWarningJsonFileChooser;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.Window;
import eu.ha3.matmos.editor.tree.ItemTreeBranch;
import eu.ha3.matmos.editor.tree.ItemTreeNode;
import eu.ha3.matmos.editor.tree.ItemTreeViewPanel;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;

public class EditorWindow extends JFrame implements Window {
    private static final long serialVersionUID = 1L;

    private final Editor model;

    private static final String WINDOW_TITLE = "MAtmos Editor";

    private String windowTitle = WINDOW_TITLE;
    private JMenuItem mntmFDiscardChanges;
    private JMenuItem mntmFSave;
    private JMenuItem mntmFSaveAs;
    private JMenuItem mntmOpenFile;
    private JMenu mnMinecraft;
    private JMenuItem mntmStartLiveCapture;
    private JMenuItem mntmStopLiveCapture;
    private JMenuItem mntmReplaceCurrentFile;
    private JPanel omniPanel;
    private JMenuItem mntmMCSaveAndPush;

    private JLabel specialWarningLabel;
    private ItemTreeViewPanel panelTree;
    private EditPanel editPanel;

    public EditorWindow(Editor modelConstruct) {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent arg0) {
                if (model.hasUnsavedChanges()) {
                    if (!continueUnsavedChangesWarningIfNecessary()) {
                        return;
                    }
                }

                if (model.isMinecraftControlled()) {
                    setVisible(false);
                } else {
                    dispose();
                }
            }
        });
        model = modelConstruct;

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnFile = new JMenu("File");
        mnFile.setMnemonic('f');
        menuBar.add(mnFile);

        mntmFSave = new JMenuItem("Save");
        mntmFSave.addActionListener(e -> model.quickSave());

        mntmOpenFile = new JMenuItem("Open file...");
        mntmOpenFile.addActionListener(e -> {
            if (!continueUnsavedChangesWarningIfNecessary()) {
                return;
            }

            JFileChooser fc = new JsonFileChooser(EditorWindow.this.model.getExpansionDirectory());
            int returnValue = fc.showOpenDialog(EditorWindow.this);
            if (returnValue != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = fc.getSelectedFile();
            if (file == null || !file.exists() || file.isDirectory()) {
                if (file.isDirectory()) {
                    showErrorPopup("Unexpected error: The file is a directory.");
                } else {
                    showErrorPopup("Unexpected error: The file does not exist.");
                }
                return;
            }

            model.trySetAndLoadFile(file);
        });
        mnFile.add(mntmOpenFile);

        JSeparator separator_1 = new JSeparator();
        mnFile.add(separator_1);
        mntmFSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        mnFile.add(mntmFSave);

        mntmFSaveAs = new JMenuItem("Save as...");
        mntmFSaveAs.addActionListener(e -> {
            while (true) {
                JFileChooser fc = new OverwriteWarningJsonFileChooser(model.getExpansionDirectory());
                int returnValue = fc.showSaveDialog(EditorWindow.this);
                if (returnValue != JFileChooser.APPROVE_OPTION) {
                    return;
                }
    
                File file = fc.getSelectedFile();
                if (file == null || file.isDirectory()) {
                    if (file.isDirectory()) {
                        showErrorPopup("Unexpected error: The file is a directory.");
                    } else {
                        showErrorPopup("Unexpected error: No file pointer.");
                    }
                    return;
                }
    
                if (model.longSave(file, true)) {
                    return;
                }
            }
        });
        mnFile.add(mntmFSaveAs);

        JMenuItem mntmFSaveACopy = new JMenuItem("Save a backup copy...");
        mntmFSaveACopy.addActionListener(e -> {
            
            while (true) {
                JFileChooser fc = new OverwriteWarningJsonFileChooser(model.getExpansionDirectory());
                int returnValue = fc.showSaveDialog(EditorWindow.this);
                if (returnValue != JFileChooser.APPROVE_OPTION) {
                    return;
                }
    
                File file = fc.getSelectedFile();
                if (file == null || file.isDirectory()) {
                    if (file.isDirectory()) {
                        showErrorPopup("Unexpected error: The file is a directory.");
                    } else {
                        showErrorPopup("Unexpected error: No file pointer.");
                    }
                    return;
                }
    
                if (model.longSave(file, false)) {
                    return;
                }
            }
        });
        mntmFSaveACopy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK | InputEvent.ALT_MASK));
        mnFile.add(mntmFSaveACopy);

        JSeparator separator_2 = new JSeparator();
        mnFile.add(separator_2);

        mntmReplaceCurrentFile = new JMenuItem("Replace current file with backup... (NOT IMPLEMENTED)");
        mnFile.add(mntmReplaceCurrentFile);

        mntmFDiscardChanges = new JMenuItem("Discard changes and reload (NOT IMPLEMENTED)");
        mnFile.add(mntmFDiscardChanges);

        JSeparator separator_7 = new JSeparator();
        mnFile.add(separator_7);

        JMenuItem mntmMergeAnotherFile = new JMenuItem("Merge another file in...");
        mntmMergeAnotherFile.addActionListener(arg0 -> {
            if (!continueUnsavedChangesWarningIfNecessary()) {
                return;
            }

            JFileChooser fc = new JsonFileChooser(model.getExpansionDirectory());
            int returnValue = fc.showOpenDialog(EditorWindow.this);
            if (returnValue != JFileChooser.APPROVE_OPTION) {
                return;
            }

            File file = fc.getSelectedFile();
            if (file == null || !file.exists() || file.isDirectory()) {
                if (file.isDirectory()) {
                    showErrorPopup("Unexpected error: The file is a directory.");
                } else {
                    showErrorPopup("Unexpected error: The file does not exist.");
                }
                return;
            }

            model.mergeFrom(file);
        });
        mnFile.add(mntmMergeAnotherFile);

        JSeparator separator = new JSeparator();
        mnFile.add(separator);

        JMenuItem mntmClose = new JMenuItem("Close");
        mnFile.add(mntmClose);

        JMenu mnCreate = new JMenu("Create");
        menuBar.add(mnCreate);

        JMenuItem mntmAdd = new JMenuItem("Create new item...");
        mntmAdd.addActionListener(arg0 -> {
            new PopupHelper();
            KnowledgeItemType choice = PopupHelper.askForType(EditorWindow.this, "Create new item...");

            String name = "New item";
            while (true) {
                name = PopupHelper.askForName(EditorWindow.this, "Create new item...", name);

                if (name == null || model.createItem(choice, name)) {
                    return;
                }
            }
        });
        mnCreate.add(mntmAdd);

        JMenuItem mntmNewMenuItem = new JMenuItem("Duplicate item...");
        mntmNewMenuItem.addActionListener(arg0 -> {
            ItemTreeNode node = getSelectedITN();
            if (node == null) {
                return;
            }

            model.duplicateItem(
                    ((ItemTreeBranch)node.getParent()).getSelector(), node.getItemName());
        });
        mnCreate.add(mntmNewMenuItem);

        JSeparator separator_6 = new JSeparator();
        mnCreate.add(separator_6);

        JMenuItem mntmPurgeUnusedLogic = new JMenuItem("Purge unused logic");
        mntmPurgeUnusedLogic.addActionListener(e -> {
            int saveOption = JOptionPane.showConfirmDialog(
                    this,
                    "This is going to remove all set and conditions that are recursively unused by any machine.\n"
                            + "Make sure you have backups before appenting this. Confirm?", "Purging logic",
                    JOptionPane.CANCEL_OPTION);

            if (saveOption == JOptionPane.YES_OPTION) {
                model.purgeLogic();
            }
        });
        mnCreate.add(mntmPurgeUnusedLogic);

        JMenuItem mntmPurgeUnusedSupports = new JMenuItem("Purge unused supports");
        mntmPurgeUnusedSupports.addActionListener(e -> {
            int saveOption = JOptionPane.showConfirmDialog(
                    this,
                    "This is going to remove all supports items that are unused by any machine.\n"
                            + "Make sure you have backups before appenting this. Confirm?", "Purging supports",
                    JOptionPane.CANCEL_OPTION);

            if (saveOption == JOptionPane.YES_OPTION) {
                model.purgeSupports();
            }
        });
        mnCreate.add(mntmPurgeUnusedSupports);

        JMenu mnOptions = new JMenu("Options");
        mnOptions.setMnemonic('o');
        menuBar.add(mnOptions);

        JMenuItem mntmOpenDefinitionsFile = new JMenuItem("Open definitions file...");
        mnOptions.add(mntmOpenDefinitionsFile);

        JMenuItem mntnLoadDefaultDefinitions = new JMenuItem("Load default definitions");
        mnOptions.add(mntnLoadDefaultDefinitions);

        JSeparator separator_4 = new JSeparator();
        mnOptions.add(separator_4);

        JMenuItem mntmOpenDatavaluesFile = new JMenuItem("Open blocks and items file...");
        mnOptions.add(mntmOpenDatavaluesFile);

        JMenuItem mntmLoadDefaultDatavalues = new JMenuItem("Load last generated data values");
        mnOptions.add(mntmLoadDefaultDatavalues);

        mnMinecraft = new JMenu("Minecraft");
        mnMinecraft.setMnemonic('m');
        menuBar.add(mnMinecraft);

        JMenuItem mntmMCPushEditorState = new JMenuItem("Push editor state to Minecraft");
        mntmMCPushEditorState.addActionListener(e -> model.minecraftPushCurrentState());
        mntmMCPushEditorState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        mnMinecraft.add(mntmMCPushEditorState);

        mntmMCSaveAndPush = new JMenuItem("Save file and push to Minecraft");
        mntmMCSaveAndPush.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_MASK));
        mntmMCSaveAndPush.addActionListener(event -> {
            // Should never happen hopefully...
            if (!EditorWindow.this.model.isMinecraftControlled()) {
                return;
            }

            if (attemptToQuickSave()) {
                EditorWindow.this.model.minecraftReloadFromDisk();
            } else {
                showErrorPopup("Saving was unsuccessful.");
            }
        });
        mnMinecraft.add(mntmMCSaveAndPush);

        JSeparator separator_3 = new JSeparator();
        mnMinecraft.add(separator_3);

        JMenuItem mntmCaptureCurrentState = new JMenuItem("Capture current state");
        mntmCaptureCurrentState.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F12, 0));
        mnMinecraft.add(mntmCaptureCurrentState);

        mntmStartLiveCapture = new JMenuItem("Start live capture");
        mnMinecraft.add(mntmStartLiveCapture);

        mntmStopLiveCapture = new JMenuItem("Stop live capture");
        mntmStopLiveCapture.setEnabled(false);
        mnMinecraft.add(mntmStopLiveCapture);

        JSeparator separator_5 = new JSeparator();
        mnMinecraft.add(separator_5);

        JMenuItem mntmGenerateDataValues = new JMenuItem("Generate data values file");
        mnMinecraft.add(mntmGenerateDataValues);

        omniPanel = new JPanel();
        omniPanel.setBorder(new EmptyBorder(0, 4, 4, 4));
        getContentPane().add(omniPanel, BorderLayout.CENTER);
        omniPanel.setLayout(new BorderLayout(0, 0));

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        omniPanel.add(tabbedPane, BorderLayout.CENTER);

        JPanel treeTab = new JPanel();
        tabbedPane.addTab("Knowledge", null, treeTab, null);
        treeTab.setLayout(new BorderLayout(0, 0));

        JSplitPane splitPane = new JSplitPane();
        treeTab.add(splitPane, BorderLayout.CENTER);
        splitPane.setResizeWeight(0.5);

        panelTree = new ItemTreeViewPanel(model);
        splitPane.setLeftComponent(panelTree);

        JScrollPane scrollPane = new JScrollPane();
        splitPane.setRightComponent(scrollPane);

        editPanel = new EditPanel(model);
        scrollPane.setViewportView(editPanel);
        editPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

        JPanel sheetsTab = new JPanel();
        tabbedPane.addTab("Sheets", null, sheetsTab, null);

        JPanel jsonTab = new JPanel();
        tabbedPane.addTab("Json", null, jsonTab, null);
        jsonTab.setLayout(new BorderLayout(0, 0));

        JsonPanel jsonPanel = new JsonPanel(model);
        jsonTab.add(jsonPanel, BorderLayout.CENTER);

        init();
    }

    private ItemTreeNode getSelectedITN() {
        return panelTree.getSelectedITN();
    }

    private void init() {
        this.setSize(600, 400);
        setLocationRelativeTo(this);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        if (model.isMinecraftControlled()) {
            windowTitle = WINDOW_TITLE + " - Minecraft integration (WILL CLOSE IF MINECRAFT CLOSES)";

            specialWarningLabel = new JLabel(
                    "Integrated with Minecraft. If Minecraft closes/crashes, this window will close. SAVE FREQUENTLY (CTRL-F5 / CTRL-S)");
            omniPanel.add(specialWarningLabel, BorderLayout.NORTH);
            specialWarningLabel.setForeground(Color.RED);

            mntmOpenFile.setEnabled(false);
            mntmFSaveAs.setEnabled(false);
        } else {
            windowTitle = WINDOW_TITLE;

            mnMinecraft.setEnabled(false);
            mntmReplaceCurrentFile.setEnabled(false);
        }
        setTitle(windowTitle);

        refreshFileState();
    }

    protected boolean continueUnsavedChangesWarningIfNecessary() {
        if (!model.hasUnsavedChanges()) {
            return true;
        }

        int saveOption = JOptionPane.showConfirmDialog(
                this, "You have unsaved changes. Are you sure you want to continue?", "Unsaved changes",
                JOptionPane.CANCEL_OPTION);

        return saveOption == JOptionPane.YES_OPTION;
    }

    @Override
    public void refreshFileState() {
        boolean hasValidFile = model.hasValidFile();
        boolean hasUnsavedChanges = model.hasUnsavedChanges();

        mntmFSave.setEnabled(hasValidFile && hasUnsavedChanges);
        mntmFDiscardChanges.setEnabled(hasValidFile && !hasUnsavedChanges);
        mntmMCSaveAndPush.setEnabled(hasValidFile && hasUnsavedChanges);

        if (hasValidFile) {
            setTitle((model.hasUnsavedChanges() ? "*" : "")
                    + model.getFile().getName() + " - " + windowTitle);
        } else {
            if (model.hasUnsavedChanges()) {
                setTitle("*(no file) - " + windowTitle);
            } else {
                setTitle(windowTitle);
            }
        }
    }

    @Override
    public void display() {
        setVisible(true);
        if (model.isMinecraftControlled()) {
            toFront();
            repaint();
        }
    }

    @Override
    public Component asComponent() {
        return this;
    }

    @Override
    public void updateSerial(SerialRoot root) {
        panelTree.updateSerial(root);
    }

    @Override
    public void showErrorPopup(String error) {
        JOptionPane.showMessageDialog(this, error, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Returns true even if the file has no changes.
     *
     * @return
     */
    private boolean attemptToQuickSave() {
        if (!model.hasValidFile()) {
            showErrorPopup("Cannot Quick Save: No file is open.");
            return false;
        }

        if (model.hasUnsavedChanges()) {
            return model.quickSave();
        }

        return true;
    }

    @Override
    public void disableMinecraftCapabilitites() {
        if (specialWarningLabel != null) {
            specialWarningLabel.setText("MINECRAFT CONNECTION LOST. If Minecraft closes/crashes, this window will close. YOU SHOULD SAVE (CTRL-S)");
        }

        mnMinecraft.setEnabled(false);

        showErrorPopup("Minecraft connection lost!\nThis may be due to Resource Packs being reloaded.\nYou should save!");
    }

    @Override
    public void setEditFocus(String name, Object item, boolean forceSelect) {
        panelTree.setEditFocus(name, item, forceSelect);
        editPanel.setEditFocus(name, item, forceSelect);
    }
}
