import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class BloomFilter {
	private String Hashfunncshens="hash_functions.txt";
	private LinkedList<int[]> FuncshenList;
    private byte[] set;
    private int keySize, setSize, size;
	
	
public BloomFilter(int capacity,int k) {
	
	FuncshenList=new LinkedList<int[]>();
    setSize = capacity;
    set = new byte[setSize];
    keySize = k;
    size = 0;
	
	}
	
	
private  void LoudHashFuncshens() {
	String input;
	try {
        // FileReader reads text files in the default encoding.
        FileReader fileReader = 
            new FileReader(Hashfunncshens);

        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = 
            new BufferedReader(fileReader);

        while((input=bufferedReader.readLine()) != null) {
            String[] str=input.split("_");
            int[] array=new int[2];
            array[0]= Integer.parseInt(str[0]);
            array[1]= Integer.parseInt(str[1]);
            FuncshenList.add(array);
        }   

        // Always close files.
        bufferedReader.close();         
    }
    catch(FileNotFoundException ex) {
        System.out.println(
            "Unable to open file");                
    }
    catch(IOException ex) {
        System.out.println("Error reading file");                  

    }
}
}
