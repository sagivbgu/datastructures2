import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
        this.elementsNum = 0;
        this.hashLists = getHashListsArray(this.size);
    }

    public void updateTable(String valuesFilePath) {
        try {
            Files.lines(Path.of(valuesFilePath))
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

    public int GetElementsNum() {
        return this.elementsNum;
    } // TODO: Remove this code?

    public boolean isEmpty() {
        return GetElementsNum() == 0;
    } // TODO: Remove this code?

    public HashListElement search(long key) {
        int index = hashFunction(key);
        return this.hashLists[index].get(key);
    }

    public void insert(long key) {
        if ((elementsNum + 1) / size >= 1) {
            size = size * 2; // TODO: Needed?
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
    // TODO
/*
    public String remove(String key)
    {
        int bucketIndex = hashFunction(key);

        // Get head of chain
        HashNode<K, V> head = hashLists.get(bucketIndex);

        // Search for key in its chain
        HashNode<K, V> prev = null;
        while (head != null)
        {
            // If Key found
            if (head.key.equals(key))
                break;

            // Else keep moving in chain
            prev = head;
            head = head.next;
        }

        // If key was not there
        if (head == null)
            return null;

        // Reduce elementsNum
        elementsNum--;

        // Remove key
        if (prev != null)
            prev.next = head.next;
        else
            hashLists.set(bucketIndex, head.next);

        return head.value;
    }
*/
}
