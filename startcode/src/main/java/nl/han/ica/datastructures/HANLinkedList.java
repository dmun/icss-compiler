package nl.han.ica.datastructures;

import java.util.NoSuchElementException;

public class HANLinkedList<T> implements IHANLinkedList<T> {
    HANLinkedListNode<T> first = null;

    @Override
    public void addFirst(T value) {
        var node = new HANLinkedListNode<T>();
        node.setValue(value);
        node.setNext(this.first);
        this.first = node;
    }

    @Override
    public void clear() {
        first = null;
    }

    @Override
    public void insert(int index, T value) {
        if (index == 0) {
            addFirst(value);
        } else {
            var current = this.first;
            while (index > 1) {
                if (!current.hasNext()) {
                    throw new NoSuchElementException();
                }
                current = current.getNext();
                index--;
            }
            var node = new HANLinkedListNode<T>();
            node.setValue(value);
            node.setNext(current.getNext());
            current.setNext(node);
        }
    }

    @Override
    public void delete(int pos) {
        var node = this.first;

        if (pos == 0) {
            this.first = node.getNext();
        }
        while (pos > 0) {
            if (!node.hasNext()) {
                throw new NoSuchElementException();
            }
            if (pos == 1) {
                // reset next of the prior node
                node.setNext(node.getNext().getNext());
            }
            node = node.getNext();
            pos--;
        }

    }

    @Override
    public T get(int pos) {
        var node = this.first;
        while (pos > 0) {
            if (!node.hasNext()) {
                throw new NoSuchElementException();
            }
            node = node.getNext();
            pos--;
        }
        return node.getValue();
    }

    @Override
    public void removeFirst() {
        delete(0);
    }

    @Override
    public T getFirst() {
        return this.first.getValue();
    }

    @Override
    public int getSize() {
        if (this.first == null) {
            return 0;
        }
        int size = 0;
        var node = this.first;
        boolean lastNode = false;
        while (!lastNode) {
            if (!node.hasNext()) {
                lastNode = true;
            }
            size++;
            node = node.getNext();
        }
        return size;
    }

    @Override
    public String toString() {
        var node = this.first;
        if (node != null) {
            var string = "[" + node.getValue();
            while (node.hasNext()) {
                node = node.getNext();
                string += ", " + node.getValue();
            }
            return string + "]";
        }
        return "[]";
    }

    public boolean contains(T value) {
        var node = this.first;
        if (node.getValue() == value) {
            return true;
        }
        while (node.hasNext()) {
            node = node.getNext();
            if (node.getValue() == value) {
                return true;
            }
        }
        return false;
    }
}
