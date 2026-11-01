import java.util.Random;
import java.util.Scanner;

enum Move { ROCK, PAPER, SCISSORS, LIZARD, SPOCK }

public class prac1_C_RockPaperScissorsLizardSpock {
    static int winner(Move a, Move b) {
        if (a == b) return 0;
        boolean wins = switch (a) {
            case ROCK -> b == Move.SCISSORS || b == Move.LIZARD;
            case PAPER -> b == Move.ROCK || b == Move.SPOCK;
            case SCISSORS -> b == Move.PAPER || b == Move.LIZARD;
            case LIZARD -> b == Move.SPOCK || b == Move.PAPER;
            case SPOCK -> b == Move.SCISSORS || b == Move.ROCK;
        };
        return wins ? 1 : -1;
    }
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random random = new Random();
        int playerScore = 0, computerScore = 0;
        System.out.println("Moves: ROCK, PAPER, SCISSORS, LIZARD, SPOCK");
        for (int round = 1; round <= 5; round++) {
            System.out.print("Round " + round + " - Your move: ");
            Move player = Move.valueOf(sc.next().toUpperCase());
            Move computer = Move.values()[random.nextInt(Move.values().length)];
            System.out.println("Computer move: " + computer);
            int result = winner(player, computer);
            if (result == 1) { playerScore++; System.out.println("You win this round!"); }
            else if (result == -1) { computerScore++; System.out.println("Computer wins this round."); }
            else System.out.println("Round is a tie.");
        }
        System.out.println("Final score: You " + playerScore + " - " + computerScore + " Computer");
        if (playerScore > computerScore) System.out.println("You win the game!");
        else if (computerScore > playerScore) System.out.println("Computer wins the game.");
        else System.out.println("The game is a tie.");
        sc.close();
    }
}
