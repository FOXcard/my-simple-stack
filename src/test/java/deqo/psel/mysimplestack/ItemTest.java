package deqo.psel.mysimplestack;

import org.junit.Test;

import static org.junit.Assert.*;

public class ItemTest {

    @Test
    public void construct(){
        assertNotEquals(null,new Item(5));
    }
    @Test
    public void getValue() {
        assertEquals(5,new Item(5).getValue());
    }

    @Test
    public void setValue() {
        Item val = new Item(1);
        val.setValue(5);
        assertEquals(5,val.getValue());
    }
}