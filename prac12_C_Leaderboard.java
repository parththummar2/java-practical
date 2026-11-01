import java.util.*;
public class prac12_C_Leaderboard {
    public static void main(String[] args) {
        Map<String, Integer> scores = new HashMap<>();
        scores.put("Aarav", 950); scores.put("Kabir", 950);
        scores.put("Riya", 900); scores.put("Meera", 870); scores.put("Yug", 870);
        List<Map.Entry<String, Integer>> leaderboard = new ArrayList<>(scores.entrySet());
        leaderboard.sort(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue)
            .reversed().thenComparing(Map.Entry::getKey));
        int topN = 5;
        System.out.println("TOP " + topN + " PLAYERS");
        System.out.printf("%-5s %-10s %s%n", "Rank", "Player", "Score");
        for (int i = 0; i < Math.min(topN, leaderboard.size()); i++) {
            Map.Entry<String, Integer> player = leaderboard.get(i);
            System.out.printf("%-5d %-10s %d%n", i + 1, player.getKey(), player.getValue());
        }
    }
}
