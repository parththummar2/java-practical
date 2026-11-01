import java.util.Scanner;
abstract class Shape { abstract double area(); }
class Circle extends Shape {
    double radius; Circle(double radius) { this.radius = radius; }
    double area() { return Math.PI * radius * radius; }
}
class Rectangle extends Shape {
    double length, width; Rectangle(double length, double width) { this.length = length; this.width = width; }
    double area() { return length * width; }
}
class Triangle extends Shape {
    double base, height; Triangle(double base, double height) { this.base = base; this.height = height; }
    double area() { return 0.5 * base * height; }
}
public class prac5_A_ShapeAreas {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of shapes: "); int n = sc.nextInt();
        Shape[] shapes = new Shape[n];
        for (int i = 0; i < n; i++) {
            System.out.println("\nShape " + (i + 1) + "\n1. Circle\n2. Rectangle\n3. Triangle");
            System.out.print("Enter choice: "); int choice = sc.nextInt();
            switch (choice) {
                case 1 -> { System.out.print("Enter radius: "); shapes[i] = new Circle(sc.nextDouble()); }
                case 2 -> { System.out.print("Enter length: "); double l = sc.nextDouble(); System.out.print("Enter width: "); shapes[i] = new Rectangle(l, sc.nextDouble()); }
                case 3 -> { System.out.print("Enter base: "); double b = sc.nextDouble(); System.out.print("Enter height: "); shapes[i] = new Triangle(b, sc.nextDouble()); }
                default -> { System.out.println("Invalid choice!"); i--; }
            }
        }
        double total = 0, largest = 0;
        for (Shape shape : shapes) {
            double a = shape.area(); System.out.printf("Area = %.2f%n", a);
            total += a; if (a > largest) largest = a;
        }
        System.out.printf("Total Area = %.2f%nLargest Area = %.2f%n", total, largest);
        sc.close();
    }
}
