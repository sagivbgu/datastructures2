import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BTree {

		int order; // order of tree
		Node root;  //every tree has at least a root node


		public BTree(String order)
		{
			int Order;
			try {
				 Order=Integer.parseInt(order);
			}
			catch(NumberFormatException e)
			{
				throw new RuntimeException("BTree size isn't an integer", e);
			}
			this.order = Order;

			root = new Node(Order, null);

		}
		
		public void createFullTree(String filepath)
		{
			try {
	            Files.lines(Paths.get(filepath)).forEach(value -> {
	            	insert(value);
	            });
	        } catch (IOException e) {
	            throw new RuntimeException("Error reading file. Can't update Btree", e);
	        }

		}

	// --------------------------------------------------------
	// this will be method to search for a given node where   |
	// we want to insert a key value. this method is called   |
	// from SearchnPrintNode.  It returns a node with key     |
	// value in it                                            |
	// --------------------------------------------------------


		public Node search(Node root, String key)
		{
			int i = 0;//we always want to start searching the 0th index of node.

			while(i < root.count && key.compareTo(root.key[i]) >0)//keep incrementing
	                    							  //in node while key >
					                    			  //current value.
			{
				i++;
			}

			if(i <= root.count &&  key.compareTo(root.key[i])==0)//obviously if key is in node
								                     //we went to return node.
			{


				return root;
			}

			if(root.leaf)//since we've already checked root
	    			    //if it is leaf we don't have anything else to check
	        {

				return null ;

			}
			else//else if it is not leave recurse down through ith child
			{

				return search(root.getChild(i),key);

			}
		}
	//  --------------------------------------------------------
	//  this will be the split method.  It will split node we  |
	//  want to insert into if it is full.                     |
	//  --------------------------------------------------------

		public void split(Node x, int i, Node y)
		{
			Node z = new Node(order,null);//gotta have extra node if we are
		                					//to split.

			z.leaf = y.leaf;//set boolean to same as y

			z.count = order - 1;//this is updated size

			for(int j = 0; j < order - 1; j++)
			{
				z.key[j] = y.key[j+order]; //copy end of y into front of z

			}
			if(!y.leaf)//if not leaf we have to reassign child nodes.
			{
				for(int k = 0; k < order; k++)
				{
					z.child[k] = y.child[k+order]; //reassing child of y
				}
			}

			y.count = order - 1; //new size of y

			for(int j = x.count ; j> i ; j--)//if we push key into x we have
			{				 //to rearrange child nodes

				x.child[j+1] = x.child[j]; //shift children of x

			}
			x.child[i+1] = z; //reassign i+1 child of x

			for(int j = x.count; j> i; j--)
			{
				x.key[j + 1] = x.key[j]; // shift keys
			}
			x.key[i] = y.key[order-1];//finally push value up into root.

			y.key[order-1 ] = ""; //erase value where we pushed from

			for(int j = 0; j < order - 1; j++)
			{
				y.key[j + order] = ""; //'delete' old values
			}



			x.count ++;  //increase count of keys in x
		}

	// ----------------------------------------------------------
	// this will be insert method when node is not full.        |
	// ----------------------------------------------------------

		public void nonfullInsert(Node x, String key)
		{
			int i = x.count; //i is number of keys in node x

			if(x.leaf)
			{
				while(i >= 1 && key.compareTo(x.key[i-1]) < 0 )//here find spot to put key.
				{
					x.key[i] = x.key[i-1];//shift values to make room

					i--;//decrement
				}

				x.key[i] = key;//finally assign value to node
				x.count ++; //increment count of keys in this node now.

			}


			else
			{
				int j = 0;
				while(j < x.count  && key.compareTo(x.key[j]) > 0)//find spot to recurse
				{			             //on correct child  		
					j++;
				}

			//	i++;

				if(x.child[j].count == order*2 - 1)
				{
					split(x,j,x.child[j]);//call split on node x's ith child

					if(key.compareTo(x.key[j]) > 0)
					{
						j++;
					}
				}

				nonfullInsert(x.child[j],key);//recurse
			}
		}
	//--------------------------------------------------------------
	//this will be the method to insert in general, it will call    |
	//insert non full if needed.                                    |
	//--------------------------------------------------------------

		public void insert(BTree t, String key)
		{
			Node r = t.root;//this method finds the node to be inserted as 
					 //it goes through this starting at root node.
			if(r.count == 2*order - 1)//if is full
			{
				Node s = new Node(order,null);//new node

				t.root = s;    //\
		    			       // \	
				s.leaf = false;//  \
		    			       //   > this is to initialize node.
				s.count = 0;   //  /
		    			       // /	
				s.child[0] = r;///

				split(s,0,r);//split root

				nonfullInsert(s, key); //call insert method
			}
			else
				nonfullInsert(r,key);//if its not full just insert it
		}
		public void insert( String value)
		{
			Node r = root;//this method finds the node to be inserted as 
					 //it goes through this starting at root node.
			if(r.count == 2*order - 1)//if is full
			{
				Node s = new Node(order,null);//new node

				root = s;      //\
		    			       // \	
				s.leaf = false;//  \
		    			       //   > this is to initialize node.
				s.count = 0;   //  /
		    			       // /	
				s.child[0] = r;///

				split(s,0,r);//split root

				nonfullInsert(s, value); //call insert method
			}
			else
				nonfullInsert(r,value);//if its not full just insert it
		}


	// ---------------------------------------------------------------------------------
	// this will be method to print out a node, or recurses when root node is not leaf |
	// ---------------------------------------------------------------------------------


		public void print(Node n)
		{
			for(int i = 0; i < n.count; i++)
			{
				System.out.print(n.getValue(i)+" " );//this part prints root node
			}

			if(!n.leaf)//this is called when root is not leaf;
			{

				for(int j = 0; j <= n.count  ; j++)//in this loop we recurse
				{				  //to print out tree in
					if(n.getChild(j) != null) //preorder fashion.
					{			  //going from left most
					System.out.println();	  //child to right most
					print(n.getChild(j));     //child.
					}
				}
			}
		}

	// ------------------------------------------------------------
	// this will be method to print out a node                    |
	// ------------------------------------------------------------

		public void SearchPrintNode( BTree T,char x)
		{
			Node temp= new Node(order,null);

			temp= search(T.root,x);

			if (temp==null)
			{

			System.out.println("The Key does not exist in this tree");
			}

			else
			{

			print(temp);
			}


		}

	//--------------------------------------------------------------
	//this method will delete a key value from the leaf node it is |
	//in.  We will use the search method to traverse through the   |
	//tree to find the node where the key is in.  We will then     |
	//iterated through key[] array until we get to node and will   |
	//assign k[i] = k[i+1] overwriting key we want to delete and   |
	//keeping blank spots out as well.  Note that this is the most |
	//simple case of delete that there is and we will not have time|
	//to implement all cases properly.                             |
	//--------------------------------------------------------------

	       public void deleteKey(BTree t, String key)
	       {
				       
			Node temp = new Node(order,null);//temp Node
				
			temp = search(t.root,key);//call of search method on tree for key

			if(temp.leaf && temp.count > order - 1)
			{
				int i = 0;

				while( key > temp.getValue(i))
				{
					i++;
				}
				for(int j = i; j < 2*order - 2; j++)
				{
					temp.key[j] = temp.getValue(j+1);
				}
				temp.count --;
			
			}
			else
			{
				System.out.println("This node is either not a leaf or has less than order - 1 keys.");
			}
	       }
}
