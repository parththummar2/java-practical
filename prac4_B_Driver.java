import java.util.Scanner;
public class prac4_B_Driver {
    public static void main(String[] args) {
        String[] logs = {"10:05 alice Hello there", "10:06 bob Good morning", "10:07 carol hello everyone", "malformed"};
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter keyword: "); String keyword = sc.nextLine();
        System.out.println(new ChatFilter().filterChats(logs, keyword));
        sc.close();
    }
}
