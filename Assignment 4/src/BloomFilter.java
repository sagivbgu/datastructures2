import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
public class BloomFilter {
	private String Hashfunncshens="hash_functions.txt";
	FuncshenLink[] funcshens;
    private byte[] BloomFilterArray;
    private int  ArraySize, size;
	
	
public BloomFilter(int capacity) {
	InitFuncshensArray();
	ArraySize = capacity;
    BloomFilterArray = new byte[ArraySize];
    size = 0;
	}
	
	





private  void InitFuncshensArray() {
	String input;
	int NumOfFuncshens=0;
	try {
        // FileReader reads text files in the default encoding.
        FileReader fileReader =  new FileReader(Hashfunncshens);
        
        // Always wrap FileReader in BufferedReader.
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while((input=bufferedReader.readLine()) != null) {   //get the number of funcshens
            NumOfFuncshens++;
        }   
        bufferedReader.close();    
        
        funcshens=new FuncshenLink[NumOfFuncshens]; //init the funcshens array;
        BufferedReader bufferedReader1 = new BufferedReader(fileReader);
        int i=0;
        while((input=bufferedReader1.readLine()) != null) {
            String[] str=input.split("_");
            funcshens[i]=new FuncshenLink(Integer.parseInt(str[0]),Integer.parseInt(str[1]));
            i++;
        }
        bufferedReader1.close();      
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
