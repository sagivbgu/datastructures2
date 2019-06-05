public class HashList {
    private HashListElement head;

    public HashList() {
    }

    public void insert(long key) {
        HashListElement newElement = new HashListElement(key);
        newElement.setNext(this.head);
        this.head = newElement;
    }

    public HashListElement get(long key) {
        HashListElement currentElement = this.head;
        while (currentElement != null) {
            if (currentElement.getKey() == key) {
                return currentElement;
            }
            currentElement = currentElement.getNext();
        }
        return null;
    }

    public HashListElement getHead() {
        return this.head;
    }
}
