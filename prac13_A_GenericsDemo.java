class Box<T> {
    private T value;
    Box(T value) { this.value = value; }
    T get() { return value; }
    void set(T value) { this.value = value; }
}
class Pair<A, B> {
    private final A first; private final B second;
    Pair(A first, B second) { this.first = first; this.second = second; }
    A getFirst() { return first; }
    B getSecond() { return second; }
}
public class prac13_A_GenericsDemo {
    static <T> T first(T[] values) {
        if (values == null || values.length == 0) throw new IllegalArgumentException("Array must not be empty");
        return values[0];
    }
    public static void main(String[] args) {
        Box<String> name = new Box<>("Yug");
        Box<Integer> marks = new Box<>(95);
        Pair<String, Integer> student = new Pair<>("Riya", 91);
        String[] languages = {"Java", "Python"};
        System.out.println("Name: " + name.get());
        System.out.println("Marks: " + marks.get());
        System.out.println("Pair: " + student.getFirst() + ", " + student.getSecond());
        System.out.println("First language: " + first(languages));
    }
}
