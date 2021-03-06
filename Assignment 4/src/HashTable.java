import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

class HashTable {
    private HashList[] hashLists;
    private int size;
    private int elementsNum;

    public HashTable(String size) {
        try {
            this.size = Integer.parseInt(size);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Hash table size isn't an integer", e);
        }
        if (this.size < 1) {
            throw new RuntimeException("Hash table size is invalid");
        }

        this.elementsNum = 0;
        this.hashLists = getHashListsArray(this.size);
    }

    public void updateTable(String valuesFilePath) {
        try {
            Files.lines(Paths.get(valuesFilePath))
                    .forEach(value -> insert(Utils.getHornerValue(value)));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file. Can't update hash table", e);
        }
    }

    public String getSearchTime(String requestedPasswordsFilePath) {
        return Utils.getElapsedTimeInMs(() -> {
            try {
                Files.lines(Paths.get(requestedPasswordsFilePath))
                        .forEach(password -> search(Utils.getHornerValue(password)));
            } catch (IOException e) {
                throw new RuntimeException("Error reading file. Can't get hash table search time", e);
            }
        });
    }

    public HashListElement search(long key) {
        int index = hashFunction(key);
        return this.hashLists[index].get(key);
    }

    public void insert(long key) {
        if ((elementsNum + 1) / size >= 1) {
            size = size * 2;
            rehash(size);
        }

        insertToCorrectList(key, hashLists);
        elementsNum++;
    }

    private int hashFunction(long key) {
        return Utils.getHash(key, 5, 9, this.size);
    }

    private HashList[] getHashListsArray(int size) {
        HashList[] hashLists = new HashList[size];
        for (int i = 0; i < hashLists.length; i++)
            hashLists[i] = new HashList();

        return hashLists;
    }

    private void rehash(int newSize) {
        HashList[] newHashLists = getHashListsArray(newSize);
        int newElementsNum = 0;

        for (HashList list : hashLists) {
            HashListElement element = list.getHead();
            while (element != null) {
                insertToCorrectList(element.getKey(), newHashLists);
                newElementsNum++;
                element = element.getNext();
            }
        }

        hashLists = newHashLists;
        elementsNum = newElementsNum;
    }

    private void insertToCorrectList(long key, HashList[] hashLists) {
        int index = hashFunction(key);
        hashLists[index].insert(key);
    }
}
