import java.util.*;
class OutOfStockException extends Exception {
    OutOfStockException(String message) { super(message); }
}
class Warehouse {
    private final Map<String, Integer> stock = new LinkedHashMap<>();
    void addStock(String item, int quantity) { stock.put(item, quantity); }
    void issue(String item, int quantity) throws OutOfStockException {
        int available = stock.getOrDefault(item, 0);
        if (quantity > available) throw new OutOfStockException("Not enough stock for '" + item + "'. Shortfall = " + (quantity - available));
        stock.put(item, available - quantity);
        System.out.println("Successfully issued " + quantity + " " + item + "(s).");
        System.out.println("Remaining stock: " + stock.get(item));
    }
    void display() {
        System.out.println("\n--- Current Stock ---");
        stock.forEach((k, v) -> System.out.println(k + " : " + v));
    }
}
public class prac8_B_StockIssue {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); Warehouse warehouse = new Warehouse();
        System.out.print("Enter number of different items: "); int n = sc.nextInt(); sc.nextLine();
        System.out.println("\nEnter item name and quantity:");
        for (int i = 1; i <= n; i++) {
            System.out.print("Item " + i + " name: "); String name = sc.nextLine();
            System.out.print("Quantity: "); int qty = sc.nextInt(); sc.nextLine();
            warehouse.addStock(name, qty);
        }
        warehouse.display();
        System.out.print("Enter number of requests: "); int requests = sc.nextInt(); sc.nextLine();
        for (int i = 1; i <= requests; i++) {
            System.out.println("\n--- Request " + i + " ---");
            System.out.print("Enter item: "); String item = sc.nextLine();
            System.out.print("Enter quantity: "); int qty = sc.nextInt(); sc.nextLine();
            try { warehouse.issue(item, qty); }
            catch (OutOfStockException e) { System.out.println("Out of stock: " + e.getMessage()); }
        }
        warehouse.display(); sc.close();
    }
}
