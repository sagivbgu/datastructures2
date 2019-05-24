import static org.junit.Assert.*;

import org.junit.Test;

public class BTreeTest {

	@Test
	public void testBTree() {
		

	}

	@Test
	public void testCreateFullTree() {
		BTree btree = new BTree("4");
		btree.createFullTree("src/bad_passwords.txt");
		btree.print(btree.root);
	}

	@Test
	public void testSearch() {
		fail("Not yet implemented");
	}

	@Test
	public void testSplit() {
		fail("Not yet implemented");
	}

	@Test
	public void testNonfullInsert() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertBTreeString() {
		fail("Not yet implemented");
	}

	@Test
	public void testInsertString() {
		fail("Not yet implemented");
	}

	@Test
	public void testPrint() {
		fail("Not yet implemented");
	}

	@Test
	public void testSearchPrintNode() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteKey() {
		fail("Not yet implemented");
	}

}
