package deqo.psel.mysimplestack;

import java.util.EmptyStackException;
import java.util.Stack;

public class SimpleStackImpl implements SimpleStack {

    Stack<Object> wrappedStack = new Stack<Object>();

    @Override
    public boolean isEmpty() {
        return wrappedStack.isEmpty();
    }

    public int getSize() {
        return wrappedStack.size();
    }

    @Override
    public void push(Item item) {
        wrappedStack.push(item);
    }

    @Override
    public Item peek() throws EmptyStackException {
        return (Item) wrappedStack.peek();
    }

    @Override
    public Item pop() throws EmptyStackException {
        return (Item) wrappedStack.pop();
    }
}
