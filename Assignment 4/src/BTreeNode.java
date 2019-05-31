public class BTreeNode {
    private int currentKeysCount; // Number of keys in the node
    private String[] keys;
    private BTreeNode[] children;
    private BTreeNode parent;
    boolean isLeaf;

    public BTreeNode(int t, BTreeNode parent) {
        this.parent = parent;
        keys = new String[2 * t - 1];
        children = new BTreeNode[2 * t];
        isLeaf = true;
        currentKeysCount = 0;
    }

    public String getValue(int index) {
        return keys[index];
    }

    public void setValue(int index, String value) {
        keys[index] = value;
    }

    public BTreeNode getChild(int index) {
        return children[index];
    }

    public void setChild(int index, BTreeNode child) {
        children[index] = child;
    }

    public int getNumOfKeys() {
        return currentKeysCount;
    }

    public void setNumOfKeys(int keys) {
        currentKeysCount = keys;
    }

    public BTreeNode getParent() {
        return parent;
    }

    public void setParent(BTreeNode parent) {
        this.parent = parent;
    }
    
    public int indexOf(String value) {
        for (int i = 0; i < getNumOfKeys(); i++) {
            if (keys[i].equals(value)) return i;
        }
        return -1;
    }
    public String removeKey(String value) {
        String removed = null;
        boolean found = false;
        if (currentKeysCount == 0) return null;
        for (int i = 0; i < currentKeysCount; i++) {
            if (keys[i].equals(value)) {
                found = true;
                removed = keys[i];
            } else if (found) {
                // shift the rest of the keys down
                keys[i - 1] = keys[i];
            }
        }
        if (found) {
        	currentKeysCount--;
            keys[currentKeysCount] = null;
        }
        return removed;
    }
    
}
