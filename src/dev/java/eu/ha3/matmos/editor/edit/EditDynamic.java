package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.serialisation.expansion.SerialDynamic;
import eu.ha3.matmos.serialisation.expansion.SerialDynamicSheetIndex;

@SuppressWarnings("serial")
public class EditDynamic extends JPanel implements IFlaggable {
    private final EditPanel edit;
    private final SerialDynamic serialDynamic;
    private DynamicRemoverPanel listRemover;
    private JTextField textFieldSheet;
    private JTextField textFieldIndex;

    public EditDynamic(EditPanel parentConstruct, SerialDynamic serialDynamicConstruct) {
        edit = parentConstruct;
        serialDynamic = serialDynamicConstruct;
        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        panel.setBorder(new TitledBorder(null, "Internal", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel, BorderLayout.CENTER);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] {100, 0};
        gbl_panel.rowHeights = new int[] {14, 130, 0};
        gbl_panel.columnWeights = new double[] {1.0, Double.MIN_VALUE};
        gbl_panel.rowWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
        panel.setLayout(gbl_panel);

        JLabel lblMustBeActive = new JLabel("List contents:");
        GridBagConstraints gbc_lblMustBeActive = new GridBagConstraints();
        gbc_lblMustBeActive.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblMustBeActive.insets = new Insets(0, 0, 5, 0);
        gbc_lblMustBeActive.gridx = 0;
        gbc_lblMustBeActive.gridy = 0;
        panel.add(lblMustBeActive, gbc_lblMustBeActive);

        listRemover = new DynamicRemoverPanel(this, serialDynamic);
        GridBagConstraints gbc_listRemover = new GridBagConstraints();
        gbc_listRemover.fill = GridBagConstraints.BOTH;
        gbc_listRemover.gridx = 0;
        gbc_listRemover.gridy = 1;
        panel.add(listRemover, gbc_listRemover);

        JPanel panel_1 = new JPanel();
        panel_1.setBorder(new TitledBorder(null, "Add", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(panel_1, BorderLayout.SOUTH);
        panel_1.setLayout(new BorderLayout(0, 0));

        JPanel panel_2 = new JPanel();
        panel_1.add(panel_2, BorderLayout.CENTER);
        GridBagLayout gbl_panel_2 = new GridBagLayout();
        gbl_panel_2.columnWidths = new int[] {86, 0, 0};
        gbl_panel_2.rowHeights = new int[] {20, 0};
        gbl_panel_2.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
        gbl_panel_2.rowWeights = new double[] {0.0, Double.MIN_VALUE};
        panel_2.setLayout(gbl_panel_2);

        textFieldSheet = new JTextField();
        GridBagConstraints gbc_textField = new GridBagConstraints();
        gbc_textField.fill = GridBagConstraints.BOTH;
        gbc_textField.insets = new Insets(0, 0, 0, 5);
        gbc_textField.gridx = 0;
        gbc_textField.gridy = 0;
        panel_2.add(textFieldSheet, gbc_textField);
        textFieldSheet.addActionListener(arg0 -> addLine());
        textFieldSheet.setColumns(10);

        textFieldIndex = new JTextField();
        GridBagConstraints gbc_textField_1 = new GridBagConstraints();
        gbc_textField_1.fill = GridBagConstraints.BOTH;
        gbc_textField_1.gridx = 1;
        gbc_textField_1.gridy = 0;
        panel_2.add(textFieldIndex, gbc_textField_1);
        textFieldIndex.setColumns(10);

        JButton btnAddenter = new JButton("Add (ENTER)");
        btnAddenter.addActionListener(e -> addLine());
        panel_1.add(btnAddenter, BorderLayout.EAST);

        listRemover.getList().addListSelectionListener(arg0 -> pickupSelection());

        updateValues();
    }

    private void pickupSelection() {
        int value = listRemover.getList().getSelectedIndex();
        if (value == -1 || value >= serialDynamic.entries.size()) {
            return;
        }

        textFieldSheet.setText(serialDynamic.entries.get(value).sheet);
        textFieldIndex.setText(serialDynamic.entries.get(value).index);
    }

    private void addLine() {
        String ctsOfSheet = textFieldSheet.getText();
        if (ctsOfSheet.equals("")) {
            return;
        }

        String ctsOfIndex = textFieldIndex.getText();
        if (ctsOfIndex.equals("")) {
            return;
        }

        serialDynamic.entries.add(new SerialDynamicSheetIndex(ctsOfSheet, ctsOfIndex));
        flagChange();
        listRemover.getList().setSelectedValue(ctsOfSheet + "@" + ctsOfIndex, true);
    }

    @Override
    public void flagChange() {
        edit.flagChange();
        updateValues();
    }

    private void updateValues() {
        listRemover.fillWithValues();
    }
}
