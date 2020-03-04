package eu.ha3.matmos.editor.tree;

import javax.swing.tree.DefaultMutableTreeNode;

@SuppressWarnings("serial")
public class ItemTreeNode extends DefaultMutableTreeNode {
    private String itemName;

    public ItemTreeNode(String name) {
        super(name);
        itemName = name;
    }

    public String getItemName() {
        return itemName;
    }
}
