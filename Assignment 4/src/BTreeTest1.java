import static org.junit.Assert.*;

import org.junit.Test;

public class BTreeTest1 {

	@Test
	public void testBTree() {
		fail("Not yet implemented");
	}

	@Test
	public void testInorderPrint() {
		BTree btree = new BTree("2");
		btree.createFullTree("src/bad_passwords.txt");
		btree.InorderPrint(btree.root,0);
	}

}
