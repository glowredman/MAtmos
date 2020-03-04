package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.editor.interfaces.NamedSerialEditor;
import eu.ha3.matmos.serialisation.expansion.SerialCondition;
import eu.ha3.matmos.serialisation.expansion.SerialDynamic;
import eu.ha3.matmos.serialisation.expansion.SerialEvent;
import eu.ha3.matmos.serialisation.expansion.SerialList;
import eu.ha3.matmos.serialisation.expansion.SerialMachine;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;
import eu.ha3.matmos.serialisation.expansion.SerialSet;

@SuppressWarnings("serial")
public class EditPanel extends JPanel implements NamedSerialEditor, IFlaggable {
    private final Editor model;

    private boolean noPane = true;
    private String nameOfItem = "";
    private Object editFocus = null;

    //

    private JTextField textField;
    private JButton btnRename;
    private JButton btnDelete;
    private JPanel editor;

    private JPanel currentEdit = null;

    public EditPanel(Editor modelConstruct) {
        model = modelConstruct;
        setLayout(new BorderLayout(0, 0));

        JPanel name = new JPanel();
        add(name, BorderLayout.NORTH);
        name.setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Editing item", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        name.add(panel);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {86, 71, 63, 0};
        gridBagLayout.rowHeights = new int[] {23, 0};
        gridBagLayout.columnWeights = new double[] {1.0, 0.0, 0.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights = new double[] {0.0, Double.MIN_VALUE};
        panel.setLayout(gridBagLayout);

        textField = new InstantTextField() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void editEvent() {
                evaluateRename();
            }
        };
        textField.setFont(new Font("Tahoma", Font.BOLD, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(textField, gbc);
        textField.setColumns(10);
        textField.addActionListener(arg0 -> model.renameItem(
                nameOfItem, editFocus, textField.getText()));

        btnRename = new JButton("Rename");
        GridBagConstraints gbc_1 = new GridBagConstraints();
        gbc_1.fill = GridBagConstraints.VERTICAL;
        gbc_1.anchor = GridBagConstraints.WEST;
        gbc_1.insets = new Insets(0, 0, 0, 5);
        gbc_1.gridx = 1;
        gbc_1.gridy = 0;
        panel.add(btnRename, gbc_1);
        btnRename.addActionListener(arg0 -> model.renameItem(
                nameOfItem, editFocus, textField.getText()));

        btnDelete = new JButton("Delete");
        GridBagConstraints gbc_2 = new GridBagConstraints();
        gbc_2.fill = GridBagConstraints.VERTICAL;
        gbc_2.anchor = GridBagConstraints.WEST;
        gbc_2.gridx = 2;
        gbc_2.gridy = 0;
        panel.add(btnDelete, gbc_2);
        btnDelete.addActionListener(e -> {
            int saveOption = JOptionPane.showConfirmDialog(
                    EditPanel.this, "Are you sure you want to delete the following item:\n"
                            + nameOfItem + "\n("
                            + editFocus.getClass().getSimpleName().replace("Serial", "") + ")",
                    "Deleting item", JOptionPane.CANCEL_OPTION);

            if (saveOption == JOptionPane.YES_OPTION) {
                model.deleteItem(nameOfItem, editFocus);
            }

        });

        editor = new JPanel();
        add(editor, BorderLayout.CENTER);
        editor.setLayout(new BorderLayout(0, 0));

        refreshPane();
    }

    protected boolean isValidName(String text) {
        return !text.equals("");
    }

    @Override
    public void setEditFocus(String name, Object item, boolean forceSelect) {
        nameOfItem = name;
        editFocus = item;

        refreshPane();
    }

    private void refreshPane() {
        if (nameOfItem == null || editFocus == null) {
            noPane();
            return;
        }

        noPane = false;
        textField.setEditable(true);
        textField.setText(nameOfItem);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(true);

        BorderLayout lay = (BorderLayout)editor.getLayout();
        Component c = lay.getLayoutComponent(BorderLayout.CENTER);
        if (c != null) {
            editor.remove(c);
        }

        if (editFocus instanceof SerialCondition) {
            showEdit(new EditCondition(this, (SerialCondition)editFocus));
        } else if (editFocus instanceof SerialSet) {
            showEdit(new EditSet(this, (SerialSet)editFocus));
        } else if (editFocus instanceof SerialList) {
            showEdit(new EditList(this, (SerialList)editFocus));
        } else if (editFocus instanceof SerialEvent) {
            showEdit(new EditEvent(this, (SerialEvent)editFocus));
        } else if (editFocus instanceof SerialMachine) {
            showEdit(new EditMachine(this, (SerialMachine)editFocus));
        } else if (editFocus instanceof SerialDynamic) {
            showEdit(new EditDynamic(this, (SerialDynamic)editFocus));
        } else {
            showEdit(null);
        }
    }

    private void showEdit(JPanel panel) {
        if (currentEdit != null) {
            editor.remove(currentEdit);
        }

        currentEdit = panel;

        if (currentEdit != null) {
            editor.add(currentEdit, BorderLayout.CENTER);
        }
        revalidate();
        repaint();
    }

    private void noPane() {
        noPane = true;
        textField.setText("<no item selected>");
        textField.setEditable(false);
        btnRename.setEnabled(false);
        btnDelete.setEnabled(false);

        showEdit(null);
    }

    private void evaluateRename() {
        if (noPane) {
            btnRename.setEnabled(false);
            return;
        }

        if (textField.getText().equals(nameOfItem) || !isValidName(textField.getText())) {
            btnRename.setEnabled(false);
        } else {
            btnRename.setEnabled(true);
        }
    }

    @Override
    public void flagChange() {
        model.informInnerChange();
    }

    public SerialRoot getSerialRoot() {
        return model.getRootForCopyPurposes();
    }

    public File getSoundDirectory() {
        return model.getSoundDirectory();
    }

    public Editor getModel() {
        return model;
    }
}
