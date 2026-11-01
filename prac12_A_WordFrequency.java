import java.util.*;
public class prac12_A_WordFrequency {
    public static void main(String[] args) {
        String paragraph = "Java makes coding fun. Java helps us learn coding. " + "Coding with Java is fun.";
        Map<String, Integer> frequency = new HashMap<>();
        String[] words = paragraph.toLowerCase().replaceAll("[^a-z\\s]", "").split("\\s+");
        for (String word : words) frequency.put(word, frequency.getOrDefault(word, 0) + 1);
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(frequency.entrySet());
        entries.sort(Comparator.comparing(Map.Entry<String, Integer>::getValue).reversed().thenComparing(Map.Entry::getKey));
        System.out.println("Word Frequency (highest first):");
        for (Map.Entry<String, Integer> entry : entries)
            System.out.println(entry.getKey() + " : " + entry.getValue());
    }
}
