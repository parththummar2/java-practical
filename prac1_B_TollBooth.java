import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

record Vehicle(String number, String type) {}

public class prac1_B_TollBooth {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int total = 0;
        Map<String, Integer> counts = new HashMap<>();
        while (true) {
            System.out.print("Vehicle number (or done): ");
            String number = sc.next();
            if (number.equalsIgnoreCase("done")) break;
            System.out.print("Vehicle type (bike, car, truck): ");
            String type = sc.next().toLowerCase();
            Vehicle vehicle = new Vehicle(number, type);
            int toll = switch (vehicle.type()) {
                case "bike" -> 20;
                case "car" -> 50;
                case "truck" -> 150;
                default -> 0;
            };
            if (toll == 0) {
                System.out.println("Unknown vehicle type.");
                continue;
            }
            total += toll;
            counts.merge(type, 1, Integer::sum);
            System.out.println("Toll collected: " + toll);
        }
        System.out.println("Total toll: " + total);
        if (counts.isEmpty()) System.out.println("Most frequent: none");
        else {
            String frequent = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue()).orElseThrow().getKey();
            System.out.println("Most frequent: " + frequent);
        }
        sc.close();
    }
}
