package nl.han.ica.datastructures;

import java.util.ArrayList;
import java.util.List;

public class HANStack<T> implements IHANStack<T> {
    IHANLinkedList<T> stack;

    public HANStack() {
        this.stack = new HANLinkedList<>();
    }

    @Override
    public void push(T value) {
        stack.addFirst(value);
    }

    @Override
    public T pop() {
        var popped = stack.getFirst();
        stack.removeFirst();
        return popped;
    }

    @Override
    public T peek() {
        return stack.getFirst();
    }

    @Override
    public String toString() {
        return stack.toString();
    }
}
