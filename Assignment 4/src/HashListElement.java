public class HashListElement {
    private long key;
    private HashListElement next;

    public HashListElement(long key)
    {
        this.key = key;
        this.next = null;
    }

    public long getKey() {
        return key;
    }

    public HashListElement getNext() {
        return next;
    }

    public void setNext(HashListElement next) {
        this.next = next;
    }
}
