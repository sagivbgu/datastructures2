public class HashListElement {
    private int key;
    private HashListElement next;

    public HashListElement(int key)
    {
        this.key = key;
        this.next = null;
    }

    public int getKey() {
        return key;
    }

    public HashListElement getNext() {
        return next;
    }

    public void setNext(HashListElement next) {
        this.next = next;
    }
}
