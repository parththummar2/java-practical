import java.util.*;
public class prac12_B_UniqueVisitors {
    public static void main(String[] args) {
        List<String> logins = Arrays.asList("yug", "riya", "aarav", "yug", "meera", "riya", "kabir", "aarav", "nisha");
        Set<String> uniqueUsers = new HashSet<>(logins);
        Set<String> firstSeenOrder = new LinkedHashSet<>(logins);
        System.out.println("Login names: " + logins);
        System.out.println("Unique visitor count: " + uniqueUsers.size());
        System.out.println("First three distinct visitors:");
        int count = 0;
        for (String name : firstSeenOrder) {
            if (count == 3) break;
            System.out.println((count + 1) + ". " + name); count++;
        }
    }
}
