package eu.ha3.matmos.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import eu.ha3.matmos.editor.interfaces.ILister;

public abstract class ListerPanel extends JPanel implements ILister {
    private static final long serialVersionUID = 1L;
    
    private JLabel titleLabel;
    private JList<String> list;
    private JPanel panel;
    private JButton create;
    private JPanel panel_1;

    public ListerPanel() {
        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        list = new JList<>();
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setModel(new AbstractListModel<String>() {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            String[] values = new String[] {};

            @Override
            public int getSize() {
                return values.length;
            }

            @Override
            public String getElementAt(int index) {
                return values[index];
            }
        });
        scrollPane.setViewportView(list);

        panel = new JPanel();
        add(panel, BorderLayout.SOUTH);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

        create = new JButton("Create new");
        panel.add(create);

        panel_1 = new JPanel();
        add(panel_1, BorderLayout.NORTH);

        titleLabel = new JLabel("no label");
        panel_1.add(titleLabel);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    @Override
    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    protected void updateWith(Map<String, ?> listing) {
        list.removeAll();
        list.setListData(new ArrayList<>(listing.keySet()).toArray(new String[listing.keySet().size()]));
    }
}
