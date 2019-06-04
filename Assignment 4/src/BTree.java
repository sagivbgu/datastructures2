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
        // TODO
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
    // or node.getNumOfKeys() if not found
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

        if (key.equals(root.getValue(i))) {
            deleteKeyFromNode(root, i);
            return;
        }

        BTreeNode nextNode = root.getChild(i);
        if (nextNode == null) {
            return;
        }
        if (!nextNode.isLeaf) {
            // TODO: Make sure the next node has at least t elements
        }

        delete(nextNode, key);
    }

    private void deleteKeyFromNode(BTreeNode node, int index) {
        shiftKeysBackward(node, index);
        shiftChildrenBackward(node, index);
        node.setNumOfKeys(node.getNumOfKeys() - 1);
    }

    private void shiftRight(BTreeNode parent, BTreeNode left, BTreeNode right, int keyIndex) {
        String leftKeyToShift = left.getValue(left.getNumOfKeys() - 1);
        BTreeNode childToShift = left.getChild(left.getNumOfKeys());

        shiftKeysForward(right);
        shiftChildrenForward(right);

        // Add key and child to the right node
        String parentKeyToShift = parent.getValue(keyIndex);
        right.setValue(0, parentKeyToShift);
        right.setChild(0, childToShift);
        right.setNumOfKeys(right.getNumOfKeys() + 1);

        parent.setValue(keyIndex, leftKeyToShift);

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

    private void shiftLeft(BTreeNode parent, BTreeNode left, BTreeNode right, int keyIndex) {
        String rightKeyToShift = right.getValue(0);
        BTreeNode childToShift = right.getChild(0);

        // Add key and child to the left node
        String parentKeyToShift = parent.getValue(keyIndex);
        left.setValue(left.getNumOfKeys() - 1, parentKeyToShift);
        left.setChild(left.getNumOfKeys(), childToShift);
        left.setNumOfKeys(right.getNumOfKeys() + 1);

        parent.setValue(keyIndex, rightKeyToShift);

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
    private void Merge(BTreeNode root,int i) {
    	BTreeNode MergingChiled=root.getChild(i);
    	if(i>0 && root.getChild(i-1).getNumOfKeys()<t)
    	{
    		BTreeNode LeftChiled=root.getChild(i-1);
    		int numofkeys=root.getChild(i-1).getNumOfKeys();
    		LeftChiled.setValue(numofkeys, root.getValue(i-1));
    		LeftChiled.setNumOfKeys(LeftChiled.getNumOfKeys()+1);
    		root.setValue(i-1, "");
    		numofkeys++;
    		root.setNumOfKeys(root.getNumOfKeys()-1);
    		for(int j=0;j<numofkeys+MergingChiled.getNumOfKeys();j++)
    		{
    			LeftChiled.setValue(numofkeys+j, MergingChiled.getValue(j));
    			LeftChiled.setNumOfKeys(LeftChiled.getNumOfKeys()+1);
    		}
    		for(int j=0;j<2*t;j++)
    		{
    			LeftChiled.setChild(LeftChiled.getNumOfKeys(), MergingChiled.getChild(j));
    		}
    		root.setChild(i, null);
    	}
    	else if(i+1<2*t){
    		BTreeNode RightChiled=root.getChild(i+1);
    		int numofkeys=RightChiled.getNumOfKeys();
    		RightChiled.setValue(numofkeys, root.getValue(i));
    		RightChiled.setNumOfKeys(RightChiled.getNumOfKeys()+1);
    		root.setValue(i, "");
    		numofkeys++;
    		root.setNumOfKeys(root.getNumOfKeys()-1);
    		for(int j=0;j<numofkeys+MergingChiled.getNumOfKeys();j++)
    		{
    			RightChiled.setValue(numofkeys+j, MergingChiled.getValue(j));
    			RightChiled.setNumOfKeys(RightChiled.getNumOfKeys()+1);
    		}
    		for(int j=0;j<2*t;j++)
    		{
    			RightChiled.setChild(RightChiled.getNumOfKeys(), MergingChiled.getChild(j));
    		}
    		root.setChild(i, null);
    	}
    }
    
}