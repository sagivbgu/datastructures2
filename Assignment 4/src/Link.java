public class Link<E> {
    // ---------------------- fields ----------------------
    private E data;
    private Link<E> next;

    // ---------------------- constructors ----------------------
    public Link(E data, Link<E> next) {
        this.data = data;
        this.next = next;
    }

    public Link(E data) {
        this(data, null);
    }

    // ---------------------- Methods ----------------------
    public Link<E> getNext() {
        return next;
    }

    public void setNext(Link<E> next) {
        this.next = next;
    }

    public E getData() {
        return data;
    }

    public E setData(E data) {
        E tmp = this.data;
        this.data = data;
        return tmp;
    }

    public String toString() {
        return data.toString();
    }
}