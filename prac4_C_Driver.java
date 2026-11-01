import java.util.Scanner;
public class prac4_C_Driver {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter template: "); String template = sc.nextLine();
        System.out.print("Enter number of placeholders: "); int n = sc.nextInt(); sc.nextLine();
        String[] names = new String[n], values = new String[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter placeholder name: "); names[i] = sc.nextLine();
            System.out.print("Enter value: "); values[i] = sc.nextLine();
        }
        System.out.println("\nFilled Template:");
        System.out.println(new TemplateFiller().fillTemplate(template, names, values));
        sc.close();
    }
}
