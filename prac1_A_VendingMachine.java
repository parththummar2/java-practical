import java.util.Scanner;

enum Coin { ONE, TWO, FIVE, TEN }

public class prac1_A_VendingMachine {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int price = 15;
        int total = 0;

        while (total < price) {
            System.out.print("Insert coin (ONE, TWO, FIVE, TEN): ");
            Coin coin = Coin.valueOf(sc.next().toUpperCase());
            int value = switch (coin) {
                case ONE -> 1;
                case TWO -> 2;
                case FIVE -> 5;
                case TEN -> 10;
            };
            total += value;
            System.out.println("Total paid: " + total);
        }
        System.out.println("Paid. Change: " + (total - price));
        sc.close();
    }
}
