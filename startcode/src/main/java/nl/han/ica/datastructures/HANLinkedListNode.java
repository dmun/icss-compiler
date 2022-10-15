package nl.han.ica.datastructures;

public class HANLinkedListNode<T> {
    T value;
    HANLinkedListNode<T> next;

    public T getValue() {
        return value;
    }

    public void setValue(T item) {
        this.value = item;
    }

    public HANLinkedListNode<T> getNext() {
        return next;
    }

    public void setNext(HANLinkedListNode<T> next) {
        this.next = next;
    }

    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public String toString() {
        return "HANLinkedListNode{" +
                "item=" + value +
                ", next=" + next +
                '}';
    }
}
