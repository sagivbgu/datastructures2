public class BTreeNode {
    private int currentKeysCount; // Number of keys in the node
    private String[] keys;
    private BTreeNode[] children;
    boolean isLeaf;

    public BTreeNode(int t) {
        keys = new String[2 * t - 1];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = "";
        }

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
}
