import java.util.Scanner;
public class prac3_A_Driver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter no. of coordinates: ");
        int n = scanner.nextInt(), count = 0;
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter x-coordinate: "); int x = scanner.nextInt();
            System.out.print("Enter y-coordinate: "); int y = scanner.nextInt();
            points[i] = new Point(x, y);
        }
        for (int i = 0; i < n; i++) {
            boolean appeared = false;
            for (int j = 0; j < i; j++) if (points[i].equals(points[j])) { appeared = true; break; }
            if (!appeared) count++;
        }
        System.out.println("Distinct: " + count);
        scanner.close();
    }
}
