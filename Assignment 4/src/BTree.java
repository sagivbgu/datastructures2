import java.io.BufferedWriter;
import java.io.FileWriter;
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

    public void createFullTree(String filepath) {
        try {
            Files.lines(Paths.get(filepath)).forEach(this::insert);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't update Btree", e);
        }
    }

    public boolean search(BTreeNode root, String key) {
        int i = 0;

        while (i < root.getNumOfKeys() && key.compareTo(root.getValue(i)) > 0) {
            i++;
        }

        if (i <= root.getNumOfKeys() && key.equals(root.getValue(i))) {
            return true;
        }

        if (root.isLeaf) {
            return false;
        }
        return search(root.getChild(i), key);
    }

    public void split(BTreeNode parentNode, int i) {
        BTreeNode nodeToSplit = parentNode.getChild(i);
        BTreeNode rightNode = new BTreeNode(t, null);
        rightNode.isLeaf = nodeToSplit.isLeaf;
        rightNode.setNumOfKeys(t - 1);

        // Copy the last elements from the node to split to the right node
        for (int j = 0; j < t - 1; j++) {
            rightNode.setValue(j, nodeToSplit.getValue(j + t));
        }
        if (!nodeToSplit.isLeaf) {
            for (int j = 0; j < t; j++) {
                rightNode.setChild(i, nodeToSplit.getChild(j + t));
            }
        }

        nodeToSplit.setNumOfKeys(t - 1);

        // Shift children from the parent node
        for (int j = parentNode.getNumOfKeys(); j > i; j--) {
            parentNode.setChild(j + 1, parentNode.getChild(j));
        }
        parentNode.setChild(i + 1, rightNode);

        // Shift keys from the parent node
        for (int j = parentNode.getNumOfKeys(); j > i; j--) {
            parentNode.setValue(j + 1, parentNode.getValue(j));
        }
        parentNode.setValue(i, nodeToSplit.getValue(t - 1));

        // Delete old values
        nodeToSplit.setValue(t - 1, "");
        for (int j = 0; j < t - 1; j++) {
            nodeToSplit.setValue(j + t, "");
        }

        parentNode.setNumOfKeys(parentNode.getNumOfKeys() + 1);
    }

    public void insert(String value) {
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
        int i = node.getNumOfKeys();

        if (node.isLeaf) {
            while (i > 0 && key.compareTo(node.getValue(i - 1)) < 0) {
                node.setValue(i, node.getValue(i - 1));
                i--;
            }

            node.setValue(i, key);
            node.setNumOfKeys(node.getNumOfKeys() + 1);

        } else {
            int j = 0;
            while (j < node.getNumOfKeys() && key.compareTo(node.getValue(j)) > 0) {
                j++;
            }

            if (node.getChild(j).getNumOfKeys() == t * 2 - 1) {
                split(node, j);

                if (key.compareTo(node.getValue(j)) > 0) {
                    j++;
                }
            }

            insertNonFull(node.getChild(j), key);
        }
    }


    // TODO: Delete
    public void print(BTreeNode n) {
        for (int i = 0; i < n.getNumOfKeys(); i++) {
            System.out.print(n.getValue(i) + " ");//this part prints root node
        }

        if (!n.isLeaf)//this is called when root is not leaf;
        {

            for (int j = 0; j <= n.getNumOfKeys(); j++)//in this loop we recurse
            {                  //to print out tree in
                if (n.getChild(j) != null) //preorder fashion.
                {              //going from left most
                    System.out.println();      //child to right most
                    print(n.getChild(j));     //child.
                }
            }
        }
    }

    @Override
    public String toString() {
        StringJoiner treeStringJoiner = new StringJoiner(",");
        buildInorderRepresentation(root, 0, treeStringJoiner);
        return treeStringJoiner.toString();
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

    // TODO: Delete
    private void PrintToFile(String FileName, String[] Data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FileName));
            writer.newLine();
            writer.newLine();
            Data[0] = Data[0].substring(0, Data[0].length() - 1);
            writer.write(Data[0]);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error Writing file", e);
        }
    }
}