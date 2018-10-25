package deqo.psel.mysimplestack;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SimpleStackImplTest {
    SimpleStack simpleStack;

    @Before
    public void setUp() throws Exception {
        simpleStack = new SimpleStackImpl();
    }


    @Test
    public void isEmpty() throws Exception {
        assertTrue(simpleStack.isEmpty());
        simpleStack.push(new Item(new String("toto")));
        assertFalse(simpleStack.isEmpty());
    }

    @Test
    public void getSize() {
    }

    @Test
    public void push() {
    }

    @Test
    public void peek() {
    }

    @Test
    public void pop() {
    }
}