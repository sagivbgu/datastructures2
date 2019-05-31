import static org.junit.Assert.*;

import org.junit.Test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BTreeTest1 {

    @Test
    public void testBTree() {
        fail("Not yet implemented");
    }

    @Test
    public void testInorderPrint() {
        BTree btree = new BTree("2");
        btree.createFullTree("src/bad_passwords.txt");
        printToFile("test_inorder_print_output.txt", btree.toString());
    }

    private void printToFile(String FileName, String data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FileName));
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Error Writing file", e);
        }
    }
}
