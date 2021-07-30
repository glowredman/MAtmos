package eu.ha3.matmos.editor.edit;

import java.awt.BorderLayout;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import eu.ha3.matmos.editor.interfaces.IFlaggable;

@SuppressWarnings("serial")
public class SetRemoverPanel extends JPanel {
    private final IFlaggable parent;
    private final Set<String> set;
    private JList<String> list;

    public SetRemoverPanel(IFlaggable parent, Set<String> original) {
        this.parent = parent;
        set = original;

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
        btnRemoveSelected.addActionListener(arg0 -> removeSelected());
        panel_1.setLayout(new BorderLayout(0, 0));
        panel_1.add(btnRemoveSelected);
    }

    protected void removeSelected() {
        //Solly edit list.getSelectedValues is deprecated
        List<String> values = list.getSelectedValuesList();
        if (values.size() == 0) {
            return;
        }

        int removedCount = 0;
        for (Object o : values) {
            String value = (String)o;
            if (set.contains(value)) {
                set.remove(value);
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
        list.setListData(new TreeSet<>(set).toArray(new String[set.size()]));
    }

    public JList<String> getList() {
        return list;
    }
}
