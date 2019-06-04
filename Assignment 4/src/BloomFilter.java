import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;


public class BloomFilter {
    private HashFunctionData[] functions;
    private boolean[] bloomFilterArray;

    public BloomFilter(String tableSize, String hashFunctionsFilePath) {
        int arraySize;
        try {
            arraySize = Integer.parseInt(tableSize);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Bloom filter table size isn't an integer", e);
        }
        bloomFilterArray = new boolean[arraySize];
        initFunctionsArray(hashFunctionsFilePath);
    }

    public void updateTable(String valuesFilePath) {
        try {
            Files.lines(Paths.get(valuesFilePath)).forEach(value -> {
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

    public String getFalsePositivePercentage(HashTable hashTable, String requestedPasswordsFilePath) {
        int falsePositives = 0;
        int totalPermittedPasswords = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(requestedPasswordsFilePath))) {
            String line = br.readLine();
            while (line != null) {
                long hornerValue = Utils.getHornerValue(line);
                if (hashTable.search(hornerValue) == null) {
                    totalPermittedPasswords++;

                    if (isKeyInBloomFilter(hornerValue)) {
                        falsePositives++;
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't get false positive percentage", e);
        }

        return String.valueOf((double) falsePositives / totalPermittedPasswords);
    }

    public String getRejectedPasswordsAmount(String requestedPasswordsFilePath) {
        try {
            long rejectedPasswords = Files.lines(Paths.get(requestedPasswordsFilePath))
                    .filter(pass -> isKeyInBloomFilter(Utils.getHornerValue(pass)))
                    .count();
            return String.valueOf(rejectedPasswords);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't get rejected passwords amount", e);
        }
    }

    private void initFunctionsArray(String hashFunctionsFilePath) {
        int numOfFunctions = countLinesInFile(hashFunctionsFilePath);
        this.functions = new HashFunctionData[numOfFunctions];
        updateFunctionsArray(hashFunctionsFilePath);
    }

    private int countLinesInFile(String filePath) {
        try {
            return (int) Files.lines(Paths.get(filePath)).count();
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

    private boolean isKeyInBloomFilter(long hornerValue) {
        for (HashFunctionData function : functions) {
            int index = Utils.getHash(hornerValue, function.getA(), function.getB(), bloomFilterArray.length);
            if (!bloomFilterArray[index]) {
                return false;
            }
        }
        return true;
    }
}
