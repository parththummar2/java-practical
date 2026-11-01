import java.util.Scanner;
class Counter {
    int value = 0;
    void increment() { value++; }
    synchronized void safeIncrement() { value++; }
}
public class prac9_A_CounterRace {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Number of threads: "); int threads = sc.nextInt();
        System.out.print("Increments per thread: "); int times = sc.nextInt();
        Counter counter = new Counter();
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> { for (int j = 0; j < times; j++) counter.increment(); });
        }
        for (Thread t : workers) t.start();
        for (Thread t : workers) t.join();
        int expected = threads * times;
        System.out.println("Without synchronization: " + counter.value);
        System.out.println("Expected total: " + expected);
        counter.value = 0;
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> { for (int j = 0; j < times; j++) counter.safeIncrement(); });
        }
        for (Thread t : workers) t.start();
        for (Thread t : workers) t.join();
        System.out.println("With synchronization: " + counter.value);
        sc.close();
    }
}
