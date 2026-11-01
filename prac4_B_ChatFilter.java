class ChatFilter {
    public String filterChats(String[] logs, String keyword) {
        StringBuilder report = new StringBuilder();
        int matches = 0;
        for (String line : logs) {
            String[] parts = line.split(" ", 3);
            if (parts.length < 3) continue;
            if (parts[2].toLowerCase().contains(keyword.toLowerCase())) {
                matches++;
                report.append(parts[0]).append(" ").append(parts[1]).append(": ").append(parts[2]).append("\n");
            }
        }
        return "Matches: " + matches + "\n" + report;
    }
}
