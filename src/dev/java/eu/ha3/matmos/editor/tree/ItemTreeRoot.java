package eu.ha3.matmos.editor.tree;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import eu.ha3.matmos.editor.KnowledgeItemType;
import eu.ha3.matmos.editor.interfaces.ISerialUpdate;
import eu.ha3.matmos.serialisation.expansion.SerialRoot;

@SuppressWarnings("serial")
public class ItemTreeRoot extends DefaultMutableTreeNode implements ISerialUpdate {
    
    private ItemTreeNode condition = new ItemTreeBranch("Conditions", Selector.CONDITION);
    private ItemTreeNode set = new ItemTreeBranch("Sets", Selector.SET);
    private ItemTreeNode machine = new ItemTreeBranch("Machines", Selector.MACHINE);

    private ItemTreeNode list = new ItemTreeBranch("Lists", Selector.LIST);
    private ItemTreeNode dynamic = new ItemTreeBranch("Dynamics", Selector.DYNAMIC);
    private ItemTreeNode event = new ItemTreeBranch("Events", Selector.EVENT);

    public ItemTreeRoot() {
        super("JTree");

        ItemTreeNode logic = new ItemTreeBranch("Logic", Selector.LOGIC);
        ItemTreeNode support = new ItemTreeBranch("Support", Selector.SUPPORT);
        add(logic);
        add(support);

        logic.add(condition);
        logic.add(set);
        logic.add(machine);

        support.add(list);
        support.add(dynamic);
        support.add(event);
    }

    @Override
    public void updateSerial(SerialRoot root) {
        updateSubSerial(root.condition.keySet(), condition);
        updateSubSerial(root.set.keySet(), set);
        updateSubSerial(root.machine.keySet(), machine);
        updateSubSerial(root.list.keySet(), list);
        updateSubSerial(root.dynamic.keySet(), dynamic);
        updateSubSerial(root.event.keySet(), event);
    }

    public ItemTreeNode getKnowledgeNode(KnowledgeItemType item) {
        switch (item) {
            case CONDITION:
                return condition;
            case DYNAMIC:
                return dynamic;
            case EVENT:
                return event;
            case LIST:
                return list;
            case MACHINE:
                return machine;
            case SET:
                return set;
            default:
                return null;

        }
    }

    private void updateSubSerial(Collection<String> keys, ItemTreeNode treeNode) {
        Set<String> names = new HashSet<>();
        Set<String> keysCopy = new HashSet<>(keys);

        @SuppressWarnings("unchecked")
        Enumeration<? extends ItemTreeNode> nenum = treeNode.children();
        while (nenum.hasMoreElements()) {
            ItemTreeNode next = nenum.nextElement();
            String name = next.getItemName();
            names.add(name);
        }

        keysCopy.addAll(names);
        if (keysCopy.size() != names.size()) {
            replaceSubSerial(keys, treeNode);
        } else {
            keysCopy.removeAll(names);
            if (keysCopy.size() != names.size()) {
                replaceSubSerial(keys, treeNode);
            }
        }
    }

    private void replaceSubSerial(Collection<String> keys, ItemTreeNode treeNode) {
        treeNode.removeAllChildren();
        TreeSet<String> treeSet = new TreeSet<>(keys);

        for (String name : treeSet) {
            treeNode.add(new ItemTreeNode(name));
        }
    }
}
