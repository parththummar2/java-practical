import java.util.Arrays;
class GenericStack<T> {
    private Object[] items; private int size;
    GenericStack(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        items = new Object[capacity];
    }
    void push(T item) {
        if (size == items.length) items = Arrays.copyOf(items, items.length * 2);
        items[size++] = item;
    }
    @SuppressWarnings("unchecked") T pop() {
        if (size == 0) throw new IllegalStateException("Stack is empty");
        T value = (T) items[--size]; items[size] = null; return value;
    }
    boolean isEmpty() { return size == 0; }
}
public class prac13_C_GenericStack {
    public static void main(String[] args) {
        GenericStack<String> words = new GenericStack<>(2);
        words.push("Java"); words.push("Python"); words.push("C++");
        System.out.println(words.pop()); System.out.println(words.pop()); System.out.println(words.pop());
        GenericStack<Integer> numbers = new GenericStack<>(2);
        numbers.push(10); numbers.push(20); System.out.println(numbers.pop());
        try { numbers.pop(); } catch (IllegalStateException ex) { System.out.println(ex.getMessage()); }
    }
}
