package eu.ha3.matmos.editor.tree;

import java.awt.BorderLayout;
import java.util.Enumeration;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.interfaces.Editor;
import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.editor.interfaces.NamedSerialEditor;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;

@SuppressWarnings("serial")
public class ItemTreeViewPanel extends JPanel implements ISerialUpdate, NamedSerialEditor {

    private final Editor model;

    private ItemTreeModel itemTreeModel = new ItemTreeModel();
    private JTree itemTree;

    public ItemTreeViewPanel(Editor modelConstruct) {
        model = modelConstruct;

        setLayout(new BorderLayout(0, 0));

        JScrollPane scrollPane = new JScrollPane();
        add(scrollPane, BorderLayout.CENTER);

        itemTree = new JTree();
        itemTree.addTreeSelectionListener(event -> {
            try {
                Object component = event.getPath().getLastPathComponent();
                if (!(component instanceof ItemTreeNode)) {
                    return;
                }

                if (component instanceof ItemTreeBranch) {
                    return;
                }

                ItemTreeNode item = (ItemTreeNode)component;
                ItemTreeBranch parent = (ItemTreeBranch)item.getParent();

                model.switchEditItem(parent.getSelector(), item.getItemName());
            } catch (ClassCastException e) {}
        });
        itemTree.setShowsRootHandles(true);
        itemTree.setModel(itemTreeModel);
        itemTree.setRootVisible(false);
        scrollPane.setViewportView(itemTree);
    }

    @Override
    public void updateSerial(SerialRoot root) {
        itemTreeModel.updateSerial(root);
        itemTree.updateUI();
    }

    @Override
    public void setEditFocus(String name, Object item, boolean forceSelect) {
        if (item == null || name == null) {
            return;
        }

        if (!forceSelect) {
            return;
        }

        KnowledgeItemType k = KnowledgeItemType.fromSerialClass(item.getClass());
        if (k == null) {
            return;
        }

        if (itemTree.getSelectionPath().getLastPathComponent() instanceof ItemTreeNode
                && ((ItemTreeNode)itemTree.getSelectionPath().getLastPathComponent()).getItemName().equals(name)) {
            return;
        }

        @SuppressWarnings("unchecked")
        Enumeration<? extends ItemTreeNode> nenum = itemTreeModel.getItemTreeRoot().getKnowledgeNode(k).children();
        while (nenum.hasMoreElements()) {
            ItemTreeNode next = nenum.nextElement();
            String itemName = next.getItemName();

            if (itemName.equals(name)) {
                itemTree.setSelectionPath(new TreePath(itemTreeModel.getPathToRoot(next)));
                return;
            }
        }
    }

    public ItemTreeNode getSelectedITN() {
        return (ItemTreeNode)(itemTree.getSelectionPath().getLastPathComponent() instanceof ItemTreeNode
                ? itemTree.getSelectionPath().getLastPathComponent() : null);
    }
}
