import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
class SharedTotal {
    long total = 0;
    synchronized void safeAdd(long value) { total += value; }
}
public class prac9_C_ParallelArraySum {
    static void joinAll(Thread[] threads) throws InterruptedException { for (Thread t : threads) t.join(); }
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Array size: "); int size = sc.nextInt();
        System.out.print("Number of threads: "); int count = sc.nextInt();
        long[] numbers = new long[size];
        for (int i = 0; i < size; i++) numbers[i] = i + 1L;
        long expected = (long) size * (size + 1) / 2;
        Thread[] workers = new Thread[count];
        long unsafeStart = System.nanoTime();
        long[] unsafeTotal = {0};
        for (int i = 0; i < count; i++) {
            final int from = i * size / count, to = (i + 1) * size / count;
            workers[i] = new Thread(() -> { for (int j = from; j < to; j++) unsafeTotal[0] += numbers[j]; });
            workers[i].start();
        }
        joinAll(workers);
        long unsafeTime = System.nanoTime() - unsafeStart;
        SharedTotal shared = new SharedTotal();
        long syncStart = System.nanoTime();
        for (int i = 0; i < count; i++) {
            final int from = i * size / count, to = (i + 1) * size / count;
            workers[i] = new Thread(() -> { long local = 0; for (int j = from; j < to; j++) local += numbers[j]; shared.safeAdd(local); });
            workers[i].start();
        }
        joinAll(workers);
        long syncTime = System.nanoTime() - syncStart;
        AtomicLong atomic = new AtomicLong();
        long atomicStart = System.nanoTime();
        for (int i = 0; i < count; i++) {
            final int from = i * size / count, to = (i + 1) * size / count;
            workers[i] = new Thread(() -> { long local = 0; for (int j = from; j < to; j++) local += numbers[j]; atomic.addAndGet(local); });
            workers[i].start();
        }
        joinAll(workers);
        long atomicTime = System.nanoTime() - atomicStart;
        System.out.println("Expected sum: " + expected);
        System.out.println("Without synchronization: " + unsafeTotal[0]);
        System.out.println("Synchronized sum: " + shared.total);
        System.out.println("AtomicLong sum: " + atomic.get());
        System.out.printf("Synchronized time (ms): %.3f%n", syncTime / 1_000_000.0);
        System.out.printf("AtomicLong time (ms): %.3f%n", atomicTime / 1_000_000.0);
        sc.close();
    }
}
