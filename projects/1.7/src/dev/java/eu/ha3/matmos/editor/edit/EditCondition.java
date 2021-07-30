package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import eu.ha3.matmos.core.Operator;
import eu.ha3.matmos.editor.InstantTextField;
import eu.ha3.matmos.serialisation.expansion.SerialCondition;

@SuppressWarnings("serial")
public class EditCondition extends JPanel {
    private final EditPanel edit;
    private final SerialCondition condition;

    private InstantTextField sheet;
    private InstantTextField index;
    private InstantTextField value;
    private JComboBox<Operator> comboBox;

    public EditCondition(EditPanel parentConstruct, SerialCondition conditionConstruct) {
        edit = parentConstruct;
        condition = conditionConstruct;
        setLayout(new BorderLayout(0, 0));

        JPanel activation = new JPanel();
        activation.setBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Activation", TitledBorder.LEADING, TitledBorder.TOP, null,
                null));
        add(activation);
        GridBagLayout gbl_activation = new GridBagLayout();
        gbl_activation.columnWidths = new int[] {50, 0, 0};
        gbl_activation.rowHeights = new int[] {0, 0, 0, 0, 0};
        gbl_activation.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
        gbl_activation.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        activation.setLayout(gbl_activation);

        JLabel lblSheet = new JLabel("Sheet");
        GridBagConstraints gbc_lblSheet = new GridBagConstraints();
        gbc_lblSheet.anchor = GridBagConstraints.EAST;
        gbc_lblSheet.insets = new Insets(0, 0, 5, 5);
        gbc_lblSheet.gridx = 0;
        gbc_lblSheet.gridy = 0;
        activation.add(lblSheet, gbc_lblSheet);

        sheet = new InstantTextField() {
            @Override
            protected void editEvent() {
                String text = getText();
                if (!condition.sheet.equals(text)) {
                    condition.sheet = text;
                    edit.flagChange();
                }
            };
        };
        GridBagConstraints gbc_sheet = new GridBagConstraints();
        gbc_sheet.fill = GridBagConstraints.HORIZONTAL;
        gbc_sheet.insets = new Insets(0, 0, 5, 0);
        gbc_sheet.gridx = 1;
        gbc_sheet.gridy = 0;
        activation.add(sheet, gbc_sheet);

        JLabel lblIndex = new JLabel("Index");
        GridBagConstraints gbc_lblIndex = new GridBagConstraints();
        gbc_lblIndex.anchor = GridBagConstraints.EAST;
        gbc_lblIndex.insets = new Insets(0, 0, 5, 5);
        gbc_lblIndex.gridx = 0;
        gbc_lblIndex.gridy = 1;
        activation.add(lblIndex, gbc_lblIndex);

        index = new InstantTextField() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void editEvent() {
                String text = getText();
                if (!condition.index.equals(text)) {
                    condition.index = text;
                    edit.flagChange();
                }
            };
        };
        GridBagConstraints gbc_index = new GridBagConstraints();
        gbc_index.fill = GridBagConstraints.HORIZONTAL;
        gbc_index.insets = new Insets(0, 0, 5, 0);
        gbc_index.gridx = 1;
        gbc_index.gridy = 1;
        activation.add(index, gbc_index);

        comboBox = new JComboBox<>();
        comboBox.addActionListener(arg0 -> {
            if (Operator.fromSerializedForm(condition.symbol) != comboBox
                    .getSelectedItem()) {
                condition.symbol = ((Operator)comboBox.getSelectedItem()).getSerializedForm();
                edit.flagChange();
            }
        });
        GridBagConstraints gbc_comboBox = new GridBagConstraints();
        gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
        gbc_comboBox.insets = new Insets(0, 0, 5, 0);
        gbc_comboBox.gridx = 1;
        gbc_comboBox.gridy = 2;
        activation.add(comboBox, gbc_comboBox);
        comboBox.setModel(new DefaultComboBoxModel<>(Operator.values()));

        JLabel lblValue = new JLabel("Value");
        GridBagConstraints gbc_lblValue = new GridBagConstraints();
        gbc_lblValue.anchor = GridBagConstraints.EAST;
        gbc_lblValue.insets = new Insets(0, 0, 0, 5);
        gbc_lblValue.gridx = 0;
        gbc_lblValue.gridy = 3;
        activation.add(lblValue, gbc_lblValue);

        value = new InstantTextField() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;

            @Override
            protected void editEvent() {
                String text = getText();
                if (!condition.value.equals(text)) {
                    condition.value = text;
                    edit.flagChange();
                }
            };
        };
        GridBagConstraints gbc_value = new GridBagConstraints();
        gbc_value.fill = GridBagConstraints.HORIZONTAL;
        gbc_value.gridx = 1;
        gbc_value.gridy = 3;
        activation.add(value, gbc_value);

        JPanel options = new JPanel();
        options.setBorder(new TitledBorder(
                UIManager.getBorder("TitledBorder.border"), "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        add(options, BorderLayout.SOUTH);
        options.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JButton btnSheet = new JButton("Sheet...");
        options.add(btnSheet);

        JButton btnDynamic = new JButton("Dynamic...");
        options.add(btnDynamic);

        JButton btnSelectList = new JButton("List...");
        options.add(btnSelectList);

        updateValues();
    }

    private void updateValues() {
        sheet.setText(condition.sheet);
        index.setText(condition.index);
        comboBox.setSelectedItem(Operator.fromSerializedForm(condition.symbol));
        value.setText(condition.value);
    }
}
