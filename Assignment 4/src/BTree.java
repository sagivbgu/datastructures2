import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;

public class BTree {
    private int t;
    BTreeNode root;

    public BTree(String t) {
        int tVal;
        try {
            tVal = Integer.parseInt(t);
        } catch (NumberFormatException e) {
            throw new RuntimeException("BTree size isn't an integer", e);
        }
        this.t = tVal;

        root = new BTreeNode(tVal, null);
    }

    @Override
    public String toString() {
        StringJoiner treeStringJoiner = new StringJoiner(",");
        buildInorderRepresentation(root, 0, treeStringJoiner);
        return treeStringJoiner.toString();
    }

    public void createFullTree(String filepath) {
        try {
            Files.lines(Paths.get(filepath)).forEach(this::insert);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't update Btree", e);
        }
    }

    public void deleteKeysFromTree(String keysToDeleteFilePath) {
        try {
            Files.lines(Paths.get(keysToDeleteFilePath)).forEach(key -> delete(root, key));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't delete keys from Btree", e);
        }
    }

    public String getSearchTime(String requestedPasswordsFilePath) {
        return Utils.getElapsedTimeInMs(() -> {
            try {
                Files.lines(Paths.get(requestedPasswordsFilePath))
                        .forEach(password -> search(root, password));
            } catch (IOException e) {
                throw new RuntimeException("Error reading file. Can't get B-Tree search time", e);
            }
        });
    }

    public boolean search(BTreeNode root, String key) {
        int i = indexOfNotGreater(root, key);

        // TODO: Check it's OK that we removed "i <= root.getNumOfKeys() - 1 &&"
        if (key.equals(root.getValue(i))) {
            return true;
        }

        if (root.isLeaf || root.getChild(i) == null) {
            return false;
        }
        return search(root.getChild(i), key);
    }

    public void split(BTreeNode parentNode, int i) {
        BTreeNode nodeToSplit = parentNode.getChild(i);
        BTreeNode rightNode = new BTreeNode(t, parentNode);
        rightNode.isLeaf = nodeToSplit.isLeaf;
        rightNode.setNumOfKeys(t - 1);

        // Copy the last elements from the node to split to the right node
        for (int j = 0; j < t - 1; j++) {
            rightNode.setValue(j, nodeToSplit.getValue(j + t));
        }
        if (!nodeToSplit.isLeaf) {
            for (int j = 0; j < t; j++) {
                rightNode.setChild(j, nodeToSplit.getChild(j + t));
            }
        }

        // Shift children from the parent node
        for (int j = parentNode.getNumOfKeys(); j > i; j--) {
            parentNode.setChild(j + 1, parentNode.getChild(j));
        }
        parentNode.setChild(i + 1, rightNode);

        // Shift keys from the parent node
        for (int j = parentNode.getNumOfKeys() - 1; j > i - 1; j--) {
            parentNode.setValue(j + 1, parentNode.getValue(j));
        }
        parentNode.setValue(i, nodeToSplit.getValue(t - 1));
        parentNode.setNumOfKeys(parentNode.getNumOfKeys() + 1);
        nodeToSplit.setNumOfKeys(t - 1);

        // Delete old values and children
        for (int j = t - 1; j < 2 * t - 1; j++) {
            nodeToSplit.setValue(j, "");
            nodeToSplit.setChild(j + 1, null);
        }
    }

    public void insert(String value) {
        value = value.toLowerCase();
        BTreeNode oldRoot = this.root;
        if (oldRoot.getNumOfKeys() == 2 * t - 1) {
            BTreeNode newRoot = new BTreeNode(t, null);
            this.root = newRoot;
            newRoot.isLeaf = false;
            newRoot.setChild(0, oldRoot);
            split(newRoot, 0);
            insertNonFull(newRoot, value);
        } else
            insertNonFull(oldRoot, value);
    }

    public void insertNonFull(BTreeNode node, String key) {
        int i = node.getNumOfKeys() - 1;

        if (node.isLeaf) {
            while (i >= 0 && key.compareTo(node.getValue(i)) < 0) {
                node.setValue(i + 1, node.getValue(i));
                i--;
            }

            node.setValue(i + 1, key);
            node.setNumOfKeys(node.getNumOfKeys() + 1);

        } else {
            while (i >= 0 && key.compareTo(node.getValue(i)) < 0) {
                i--;
            }
            i++;

            if (node.getChild(i).getNumOfKeys() == 2 * t - 1) {
                split(node, i);

                if (key.compareTo(node.getValue(i)) > 0) {
                    i++;
                }
            }
            insertNonFull(node.getChild(i), key);
        }
    }

    // Get the index of the first key in the node that is not greater than the key parameter,
    // or node.getNumOfKeys()-1 if not found
    private int indexOfNotGreater(BTreeNode node, String key) {
        int i = 0;
        while (i < node.getNumOfKeys() - 1 && key.compareTo(node.getValue(i)) > 0) {
            i++;
        }
        return i;
    }


    private void buildInorderRepresentation(BTreeNode root, int depth, StringJoiner treeStringJoiner) {
        if (root.isLeaf) {
            for (int i = 0; i < root.getNumOfKeys() && !root.getValue(i).equals(""); i++) {
                if (root.getValue(i) != null)
                    treeStringJoiner.add(root.getValue(i) + "_" + depth);
            }
        } else {
            for (int i = 0; i < root.getNumOfKeys() + 1; i++) {
                if (root.getChild(i) != null) {
                    buildInorderRepresentation(root.getChild(i), depth + 1, treeStringJoiner);
                    if (!root.getValue(i).equals("") & root.getValue(i) != null)
                        treeStringJoiner.add(root.getValue(i) + "_" + depth);
                    else
                        break;
                }
            }
        }
    }

    // Delete a key from this sub-tree, assuming this node has more at least t keys, or it's the tree root
    public void delete(BTreeNode root, String key) {
        int i = indexOfNotGreater(root, key);

        if (!key.equals(root.getValue(i))) {
            if (root.isLeaf) {
                return;
            }
            if (key.compareTo(root.getValue(i)) > 0) {
                i++;
            }

            BTreeNode child = prepareChildForDeletion(root, i);
            delete(child, key);

        } else {
            if (root.isLeaf) {
                deleteKeyFromNode(root, i);
            }

            BTreeNode rightChild = root.getChild(i + 1);
            BTreeNode leftChild = root.getChild(i);
            if (leftChild.getNumOfKeys() > t - 1) {
                String predecessor = getPredecessor(root, i);
                delete(leftChild, predecessor);
                root.setValue(i, predecessor);
            } else if (rightChild.getNumOfKeys() > t - 1) {
                String successor = getSuccessor(root, i);
                delete(rightChild, successor);
                root.setValue(i, successor);
            } else {
                BTreeNode mergedChild = merge(root, i);
                delete(mergedChild, key);
            }
        }
    }

    // Make sure the next node has at least t elements
    private BTreeNode prepareChildForDeletion(BTreeNode parent, int childIndex) {
        BTreeNode child = parent.getChild(childIndex);
        BTreeNode prevChild = childIndex == 0 ? null : parent.getChild(childIndex - 1);
        BTreeNode nextChild = childIndex == parent.getNumOfKeys() - 1 ? null : parent.getChild(childIndex + 1);
        if (child.getNumOfKeys() >= t) {
            return child;
        }
        if (prevChild != null && prevChild.getNumOfKeys() > t - 1) {
            shiftRight(parent, prevChild, child, childIndex - 1);
        } else if (nextChild != null && nextChild.getNumOfKeys() > t - 1) {
            shiftLeft(parent, child, nextChild, childIndex);
        } else {
            child = merge(parent, childIndex);
        }
        return child;
    }

    private BTreeNode decideNodeToDeleteFrom(BTreeNode root, String key, int i) {
        BTreeNode childNode;
        if (key.compareTo(root.getValue(i)) > 0) {
            childNode = root.getChild(i + 1);
        } else {
            childNode = root.getChild(i);
        }
        return childNode;
    }

    private void deleteKeyFromNode(BTreeNode node, int index) {
        shiftKeysBackward(node, index);
        shiftChildrenBackward(node, index);
        node.setNumOfKeys(node.getNumOfKeys() - 1);
    }

    private void shiftRight(BTreeNode parent, BTreeNode left, BTreeNode right, int leftChildIndex) {
        String leftKeyToShift = left.getValue(left.getNumOfKeys() - 1);
        BTreeNode childToShift = left.getChild(left.getNumOfKeys());

        shiftKeysForward(right);
        shiftChildrenForward(right);

        // Add key and child to the right node
        String parentKeyToShift = parent.getValue(leftChildIndex);
        right.setValue(0, parentKeyToShift);
        right.setChild(0, childToShift);
        right.setNumOfKeys(right.getNumOfKeys() + 1);

        parent.setValue(leftChildIndex, leftKeyToShift);

        deleteKeyFromNode(left, left.getNumOfKeys() - 1);
    }

    private void shiftKeysForward(BTreeNode node) {
        for (int i = node.getNumOfKeys(); i > 0; i--) {
            node.setValue(i, node.getValue(i - 1));
        }
    }

    private void shiftChildrenForward(BTreeNode node) {
        for (int i = node.getNumOfKeys() + 1; i > 0; i--) {
            node.setChild(i, node.getChild(i - 1));
        }
    }

    private void shiftLeft(BTreeNode parent, BTreeNode left, BTreeNode right, int leftChildIndex) {
        String rightKeyToShift = right.getValue(0);
        BTreeNode childToShift = right.getChild(0);

        // Add key and child to the left node
        String parentKeyToShift = parent.getValue(leftChildIndex);
        left.setValue(left.getNumOfKeys() - 1, parentKeyToShift);
        left.setChild(left.getNumOfKeys(), childToShift);
        left.setNumOfKeys(right.getNumOfKeys() + 1);

        parent.setValue(leftChildIndex, rightKeyToShift);

        deleteKeyFromNode(right, 0);
    }

    private void shiftKeysBackward(BTreeNode node, int fromIndex) {
        for (int i = fromIndex; i < node.getNumOfKeys() - 1; i++) {
            node.setValue(i, node.getValue(i + 1));
        }
        node.setValue(node.getNumOfKeys() - 1, "");
    }

    private void shiftChildrenBackward(BTreeNode node, int fromIndex) {
        for (int i = fromIndex; i < node.getNumOfKeys(); i++) {
            node.setChild(i, node.getChild(i + 1));
        }
        node.setChild(node.getNumOfKeys(), null);
    }

    private BTreeNode merge(BTreeNode root, int i) {
        BTreeNode mergingChild = root.getChild(i);
        BTreeNode mergedChild = getMergedChild(root, i);

        // Insert the value from the parent to the end of mergedChild
        mergedChild.setValue(mergedChild.getNumOfKeys(), root.getValue(i));
        mergedChild.setNumOfKeys(mergedChild.getNumOfKeys() + 1);

        // Append keys from mergingChild to mergedChild
        for (int j = 0; j < mergingChild.getNumOfKeys(); j++) {
            mergedChild.setValue(mergedChild.getNumOfKeys() + j, mergingChild.getValue(j));
        }

        // Append children from mergingChild to mergedChild
        for (int j = 0; j < mergingChild.getNumOfKeys() + 1; j++) {
            mergedChild.setChild(mergedChild.getNumOfKeys() + j, mergingChild.getChild(j));
        }

        mergedChild.setNumOfKeys(mergedChild.getNumOfKeys() + mergingChild.getNumOfKeys());
        deleteKeyFromNode(root, i);
        return mergedChild;
    }

    private BTreeNode getMergedChild(BTreeNode root, int i) {
        BTreeNode mergedChild;
        if (i > 0) {
            mergedChild = root.getChild(i - 1);
        } else {
            mergedChild = root.getChild(i + 1);
        }
        return mergedChild;
    }

    private String getSuccessor(BTreeNode root, int keyIndex) {
        BTreeNode child = root.getChild(keyIndex + 1);
        while (!child.isLeaf) {
            child = child.getChild(0);
        }
        return child.getValue(0);
    }

    private String getPredecessor(BTreeNode root, int keyIndex) {
        BTreeNode child = root.getChild(keyIndex);
        while (!child.isLeaf) {
            child = child.getChild(child.getNumOfKeys());
        }
        return child.getValue(child.getNumOfKeys() - 1);
    }
}