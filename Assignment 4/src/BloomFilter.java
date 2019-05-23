import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.*;


public class BloomFilter {
    private HashFunctionData[] functions;
    private boolean[] bloomFilterArray;
    private static int p = 15486907;

    public BloomFilter(String tableSize, String hashFunctionsFilePath) {
        int arraySize = Integer.parseInt(tableSize);  // TODO: Check input
        bloomFilterArray = new boolean[arraySize];
        initFunctionsArray(hashFunctionsFilePath);
    }
    public boolean ConfirmPassword(String PassWord) {
    	
        for (HashFunctionData function : functions) {
            int index = Utils.getHash(Utils.getHornerValue(PassWord), function.getA(), function.getB(), bloomFilterArray.length);
            if(bloomFilterArray[index] == false)
            {
            	return false;
            }
        }
    	return true;
    
    }

    public void updateTable(String valuesFilePath) {
        try {
            Files.lines(Path.of(valuesFilePath)).forEach(value -> {
                long hornerValue = Utils.getHornerValue(value);
                InsertToFilter(hornerValue);
            });
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't update bloom filter table", e);
        }
    }

    public void InsertToFilter(long hornerValue) {
        for (HashFunctionData function : functions) {
            int index = Utils.getHash(hornerValue, function.getA(), function.getB(), bloomFilterArray.length);
            bloomFilterArray[index] = true;
        }
    }

    private void initFunctionsArray(String hashFunctionsFilePath) {
        int numOfFunctions = countLinesInFile(hashFunctionsFilePath);
        this.functions = new HashFunctionData[numOfFunctions];
        updateFunctionsArray(hashFunctionsFilePath);
    }

    private int countLinesInFile(String filePath) {
        try {
            return (int) Files.lines(Path.of(filePath)).count();
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't count lines in file", e);
        }
    }

    private void updateFunctionsArray(String hashFunctionsFilePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(hashFunctionsFilePath))) {
            int i = 0;
            String line = br.readLine();
            while (line != null) {
                String[] str = line.split("_");
                this.functions[i] = new HashFunctionData(Integer.parseInt(str[0]), Integer.parseInt(str[1]));
                i++;
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't initiate bloom filter functions array", e);
        }
    }
}
