public class prac2_B_CinemaShow {
    private String title;
    private int seatsAvailable;
    private final int capacity;
    private static int totalBooked = 0;

    public prac2_B_CinemaShow(String title, int capacity) {
        this.title = title;
        this.capacity = capacity;
        seatsAvailable = capacity;
    }
    public prac2_B_CinemaShow(String title) { this(title, 100); }
    public boolean book(int n) {
        if (n <= seatsAvailable && n >= 0) {
            seatsAvailable -= n;
            totalBooked += n;
            return true;
        }
        return false;
    }
    public void cancel(int n) {
        seatsAvailable = Math.min(capacity, seatsAvailable + Math.max(0, n));
    }
    public int getSeatsAvailable() { return seatsAvailable; }
    public static int getTotalBooked() { return totalBooked; }
    public static void main(String[] args) {
        prac2_B_CinemaShow movie = new prac2_B_CinemaShow("Interstellar", 50);
        boolean first = movie.book(20);
        System.out.println("20 seats booking: " + first);
        System.out.println("Remaining seats = " + movie.getSeatsAvailable());
        boolean second = movie.book(35);
        System.out.println("\n35 seats booking: " + second);
        System.out.println("Remaining seats = " + movie.getSeatsAvailable());
        movie.cancel(5);
        System.out.println("\n5 seats cancelled");
        System.out.println("Seats now available = " + movie.getSeatsAvailable());
        System.out.println("\nTotal successful bookings = " + getTotalBooked());
    }
}
