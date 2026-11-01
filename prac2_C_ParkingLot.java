import java.util.Scanner;

public class prac2_C_ParkingLot {
    private int twoWheelers, fourWheelers;
    private final int twoCap, fourCap;
    private static long revenue = 0;

    public prac2_C_ParkingLot(int twoCap, int fourCap) {
        this.twoCap = twoCap; this.fourCap = fourCap;
    }
    public void park(String type) {
        if (type.equalsIgnoreCase("two")) {
            if (twoWheelers < twoCap) { twoWheelers++; revenue += 20; System.out.println("Two-wheeler parked"); }
            else System.out.println("Full");
        } else if (type.equalsIgnoreCase("four")) {
            if (fourWheelers < fourCap) { fourWheelers++; revenue += 40; System.out.println("Four-wheeler parked"); }
            else System.out.println("Full");
        }
    }
    public void leave(String type) {
        if (type.equalsIgnoreCase("two") && twoWheelers > 0) { twoWheelers--; System.out.println("Two-wheeler left"); }
        else if (type.equalsIgnoreCase("four") && fourWheelers > 0) { fourWheelers--; System.out.println("Four-wheeler left"); }
    }
    public void showStatus() {
        System.out.println("\nFinal Occupancy");
        System.out.println("Two-wheelers: " + twoWheelers + "/" + twoCap);
        System.out.println("Four-wheelers: " + fourWheelers + "/" + fourCap);
        System.out.println("Total Revenue: " + revenue);
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter two-wheeler capacity: "); int two = sc.nextInt();
        System.out.print("Enter four-wheeler capacity: "); int four = sc.nextInt();
        prac2_C_ParkingLot lot = new prac2_C_ParkingLot(two, four);
        System.out.print("Enter number of events: "); int events = sc.nextInt();
        for (int i = 1; i <= events; i++) {
            System.out.print("\nEnter action (park/leave): "); String action = sc.next();
            System.out.print("Enter vehicle type (two/four): "); String type = sc.next();
            if (action.equalsIgnoreCase("park")) lot.park(type);
            else if (action.equalsIgnoreCase("leave")) lot.leave(type);
            else System.out.println("Invalid action");
        }
        lot.showStatus(); sc.close();
    }
}
