import java.util.Scanner;
class DemoResource implements AutoCloseable {
    private final String name;
    private final boolean failOnClose;
    DemoResource(String name, boolean failOnClose) { this.name = name; this.failOnClose = failOnClose; }
    void perform(boolean fail) throws Exception {
        System.out.println("Performing operation using '" + name + "'...");
        if (fail) throw new Exception("original operation failed.");
        System.out.println("Operation completed successfully.");
    }
    public void close() throws Exception {
        System.out.println("Closing resource '" + name + "'...");
        if (failOnClose) throw new Exception("Exception occurred while closing the resource.");
    }
}
public class prac8_C_AutoCloseableResource {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter resource name: "); String name = sc.nextLine();
        System.out.print("Do you want the operation to fail? (yes/no): "); boolean fail = sc.nextLine().equalsIgnoreCase("yes");
        try (DemoResource resource = new DemoResource(name, fail)) {
            System.out.println("Resource '" + name + "' opened.");
            resource.perform(fail);
        } catch (Exception e) {
            System.out.println("\nMain exception reported:\n" + e.getMessage());
            for (Throwable suppressed : e.getSuppressed())
                System.out.println("\nSuppressed exception(s):\n" + suppressed.getMessage());
        }
        sc.close();
    }
}
