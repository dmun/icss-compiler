package nl.han.ica.datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class HANLinkedListTest {
    @BeforeEach
    void setUp() {

    }

    @Test
    void test() {
        var list = new HANLinkedList<Integer>();
        list.addFirst(9);
        list.addFirst(5);
        list.addFirst(2);
        list.addFirst(8);

        System.out.println(list);
        list.insert(1, 7);
        System.out.println(list);
        System.out.println(list.getSize());
    }
}