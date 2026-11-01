import java.util.Scanner;
public class prac3_B_Driver {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter number of cards: "); int n = scanner.nextInt(); scanner.nextLine();
        Card[] cards = new Card[n];
        for (int i = 0; i < n; i++) {
            System.out.print("Enter rank: "); String rank = scanner.nextLine();
            System.out.print("Enter suit: "); String suit = scanner.nextLine();
            Card card = new Card(rank, suit);
            for (int j = 0; j < i; j++) {
                if (card.equals(cards[j])) { System.out.println("Duplicate found: " + card); break; }
            }
            cards[i] = card;
        }
        scanner.close();
    }
}
