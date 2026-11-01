public class prac10_C_DeadlockDemo {
    static class Account {
        String name;
        Account(String name) { this.name = name; }
    }
    static void transferDeadlock(Account a, Account b) {
        synchronized (a) {
            System.out.println(Thread.currentThread().getName() + " locked " + a.name);
            try { Thread.sleep(100); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
            synchronized (b) {
                System.out.println(Thread.currentThread().getName() + " locked " + b.name);
            }
        }
    }
    static void transferSafe(Account a, Account b) {
        Account first = a.name.compareTo(b.name) < 0 ? a : b;
        Account second = (first == a) ? b : a;
        synchronized (first) {
            System.out.println(Thread.currentThread().getName() + " locked " + first.name);
            synchronized (second) {
                System.out.println(Thread.currentThread().getName() + " locked " + second.name);
                System.out.println(Thread.currentThread().getName() + " completed transfer");
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Account a = new Account("AccountA"), b = new Account("AccountB");
        System.out.println("=== Deadlock Demonstration ===");
        Thread t1 = new Thread(() -> transferDeadlock(a, b), "Thread-1");
        Thread t2 = new Thread(() -> transferDeadlock(b, a), "Thread-2");
        t1.start(); t2.start(); t1.join(1000); t2.join(1000);
        if (t1.isAlive() && t2.isAlive()) System.out.println("Deadlock detected!");
        else System.out.println("Deadlock did not occur in this run.");
        System.out.println("\n=== Deadlock Prevention ===");
        Thread t3 = new Thread(() -> transferSafe(a, b), "Thread-3");
        Thread t4 = new Thread(() -> transferSafe(b, a), "Thread-4");
        t3.start(); t4.start(); t3.join(); t4.join();
        System.out.println("Both safe transfers completed.");
    }
}
