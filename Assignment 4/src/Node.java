
public class Node {
		int t;  //variable to determine order of tree

		int count; // number of keys in node

		char key[];  // array of key values

	    Node child[]; //array of references

		boolean leaf; //is node a leaf or not

		Node parent;  //parent of current node.

	// ----------------------------------------------------
	// this will be default constructor for new node      |
	// ----------------------------------------------------

		public Node()
		{}
	// ----------------------------------------------------
	// initial value constructor for new node             |
	// will be called from BTree.java                     |
	// ----------------------------------------------------

		public Node(int t, Node parent)
		{
			this.t = t;  //assign size

			this.parent = parent; //assign parent

			key = new char[2*t - 1];  // array of proper size

			child = new Node[2*t]; // array of refs proper size

			leaf = true; // everynode is leaf at first;

			count = 0; //until we add keys later.
		}

	// -----------------------------------------------------
	// this is method to return key value at index position|
	// -----------------------------------------------------

		public char getValue(int index)
		{
			return key[index];
		}

	// ----------------------------------------------------
	// this is method to get ith child of node            |
	// ----------------------------------------------------

		public Node getChild(int index)
		{
			return child[index];
		}


}


