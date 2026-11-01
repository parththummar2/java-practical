import java.util.LinkedList;
import java.util.Queue;
class SharedBuffer {
    private final Queue<Integer> buffer = new LinkedList<>();
    private final int capacity = 3;
    public synchronized void produce(int item) throws InterruptedException {
        while (buffer.size() == capacity) wait();
        buffer.add(item);
        System.out.println("Produced: " + item);
        notifyAll();
    }
    public synchronized int consume() throws InterruptedException {
        while (buffer.isEmpty()) wait();
        int item = buffer.remove();
        System.out.println("Consumed: " + item);
        notifyAll();
        return item;
    }
}
public class prac10_B_ProducerConsumer {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer();
        int totalItems = 10;
        Thread producer = new Thread(() -> {
            try { for (int i = 1; i <= totalItems; i++) buffer.produce(i); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread consumer = new Thread(() -> {
            try { for (int i = 1; i <= totalItems; i++) buffer.consume(); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        producer.start(); consumer.start();
        try { producer.join(); consumer.join(); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        System.out.println("All items produced and consumed.");
    }
}
