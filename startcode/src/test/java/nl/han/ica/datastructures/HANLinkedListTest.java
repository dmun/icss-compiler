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
        var stack = new HANStack<Integer>();
        stack.push(1);
        stack.push(10);
        stack.push(9);
        stack.push(2);
    }
}