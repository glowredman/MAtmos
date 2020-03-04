package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.ha3.matmos.editor.interfaces.IFlaggable;
import eu.ha3.matmos.serialisation.expansion.SerialDynamic;
import eu.ha3.matmos.serialisation.expansion.SerialDynamicSheetIndex;

public class DynamicRemoverPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    private final IFlaggable parent;
    private final SerialDynamic dynamic;
    private JList<String> list;

    private ArrayList<String> things;

    public DynamicRemoverPanel(IFlaggable parent, SerialDynamic original) {
        this.parent = parent;
        dynamic = original;

        setLayout(new BorderLayout(0, 0));

        JPanel panel = new JPanel();
        add(panel, BorderLayout.CENTER);
        panel.setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        panel.add(scrollPane, BorderLayout.CENTER);

        list = new JList<>();
        list.setVisibleRowCount(4);
        scrollPane.setViewportView(list);

        JPanel panel_1 = new JPanel();
        panel.add(panel_1, BorderLayout.EAST);

        JButton btnRemoveSelected = new JButton("Remove");
        btnRemoveSelected.addActionListener(arg -> removeSelected());
        panel_1.setLayout(new BorderLayout(0, 0));
        panel_1.add(btnRemoveSelected);
    }

    protected void removeSelected() {
        List<String> values = list.getSelectedValuesList();
        if (values.size() == 0) {
            return;
        }

        int removedCount = 0;
        for (Object o : values) {
            String value = (String)o;
            if (things.contains(value)) {
                dynamic.entries.remove(things.indexOf(value));
                removedCount = removedCount + 1;
            }
        }

        if (removedCount > 0) {
            parent.flagChange();
            // Flagging should cause a call to fillWithValues
        }
    }

    public void fillWithValues() {
        list.removeAll();

        things = new ArrayList<>();
        for (SerialDynamicSheetIndex si : dynamic.entries) {
            things.add(si.sheet + "@" + si.index);
        }

        list.setListData(things.toArray(new String[things.size()]));
    }

    public JList<String> getList() {
        return list;
    }
}
