import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
class Booking {
    int seatsLeft = 5;
    int booked = 0;
    boolean bookWithoutSync(CountDownLatch start) {
        try { start.await(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return false; }
        if (seatsLeft > 0) {
            try { Thread.sleep(2); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            seatsLeft--; booked++; return true;
        }
        return false;
    }
    synchronized boolean book() {
        if (seatsLeft > 0) { seatsLeft--; booked++; return true; }
        return false;
    }
}
public class prac9_B_SeatBookingRace {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Number of booking threads (try 10): "); int count = sc.nextInt();
        Booking unsafe = new Booking();
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger unsafeSuccess = new AtomicInteger();
        Thread[] first = new Thread[count];
        for (int i = 0; i < count; i++) {
            first[i] = new Thread(() -> { if (unsafe.bookWithoutSync(start)) unsafeSuccess.incrementAndGet(); });
            first[i].start();
        }
        start.countDown();
        for (Thread t : first) t.join();
        System.out.println("Without synchronization, successful bookings: " + unsafeSuccess.get());
        Booking safe = new Booking(); AtomicInteger safeSuccess = new AtomicInteger();
        Thread[] second = new Thread[count];
        for (int i = 0; i < count; i++) {
            second[i] = new Thread(() -> { if (safe.book()) safeSuccess.incrementAndGet(); });
            second[i].start();
        }
        for (Thread t : second) t.join();
        System.out.println("With synchronized book(), successful bookings: " + safeSuccess.get());
        System.out.println("Seats left after safe booking: " + safe.seatsLeft);
        sc.close();
    }
}
