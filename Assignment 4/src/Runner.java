import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Runner {

	//Arguments you program should expect:
	//1. m1 (the size of the Bloom-Filter's table).
	//2. m2 (the size of expected hash table with chaining).
	//3. t (the value for BTree).
	//Note that the files used (hash_functions.txt,bad_passwords.txt,requested_passwords.txt) 
	//should be located in the same location as your src folder.
	
	public static void main(String[] args) {

		//Create the Bloom Filter.
		BloomFilter bloomFilter = contructBloomFilter(args[0]);
				
		//Create the Hash Table.
		HashTable hashTable = contructHashTable(args[1]);

		//Find the percentage of false-positives
		String falsePositivesPercent = bloomFilter.getFalsePositivePercentage(hashTable, System.getProperty("user.dir")+"/requested_passwords.txt");

		//Find the number of rejected passwords
		String rejectedPasswordsAmount = bloomFilter.getRejectedPasswordsAmount(System.getProperty("user.dir")+"/requested_passwords.txt");

		//Create the B tree using the t value and the path to the bad_passwords file.
		BTree btree = createTree(args[2]);


		//Get the DFS representation of the btree
		String treeLayout = btree.toString();

		//Get the time required to perform a search of all requested words when using the B tree and hash table.
		String searchTime = getSearchTime(hashTable, btree);

		//Get the DFS representation of the btree, after performing deletions
		String treeLayoutAfterDeletions = deleteKeysFromTree(btree);

		//Create a file with the program's expected output
		createOutputFile(falsePositivesPercent, rejectedPasswordsAmount, treeLayout, searchTime, treeLayoutAfterDeletions, System.getProperty("user.dir")+"/output.txt");
	    
	}
	

	private static BloomFilter contructBloomFilter(String m1) {
		BloomFilter bloomFilter = new BloomFilter(m1, System.getProperty("user.dir")+"/hash_functions.txt");
		//update the Bloom Filter's table with the bad passwords
		bloomFilter.updateTable(System.getProperty("user.dir")+"/bad_passwords.txt");
		return bloomFilter;
	}

	private static HashTable contructHashTable(String m2) {
		HashTable hashTable = new HashTable(m2);
		//update the Hash Table with the bad passwords
		hashTable.updateTable(System.getProperty("user.dir")+"/bad_passwords.txt");
		return hashTable;
	}

	// Create the BTree using the t value, and the friends file.
	// Insert the bad passwords into the tree.
	private static BTree createTree(String tVal) {
		BTree btree = new BTree(tVal);
		btree.createFullTree(System.getProperty("user.dir")+"/bad_passwords.txt");
		return btree;
	}

	private static String getSearchTime(HashTable hashTable, BTree btree) {
		//Get the time required to perform a search of all requested words when using the B tree.
		String btreeSearchTime = btree.getSearchTime(System.getProperty("user.dir")+"/requested_passwords.txt");
		//Get the time required to perform a search of all requested words when using the hash table.
		String hashTableSearchTime = hashTable.getSearchTime(System.getProperty("user.dir")+"/requested_passwords.txt");
		return btreeSearchTime+"_"+hashTableSearchTime;
	}

	private static String deleteKeysFromTree(BTree btree) {
		btree.deleteKeysFromTree(System.getProperty("user.dir")+"/delete_keys.txt");
		return btree.toString();
	}

	//Creates a file with the program's expected output
	private static void createOutputFile(String falsePositivesPercent, String rejectedPasswordsAmount, String treeLayout, String searchTime, String treeLayoutAfterDeletions, String pathToOutput) {
		Path path = Paths.get(pathToOutput);
		StringBuilder sb = new StringBuilder();
		sb.append(falsePositivesPercent).append(System.lineSeparator()).append(rejectedPasswordsAmount).append(System.lineSeparator()).append(treeLayout).append(System.lineSeparator()).append(searchTime).append(System.lineSeparator()).append(treeLayoutAfterDeletions);
	    byte[] strToBytes = sb.toString().getBytes();
	    try {
			Files.write(path, strToBytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
