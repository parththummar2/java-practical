class PasswordChecker {
    public String strength(String pw) {
        int count = 0;
        if (pw.length() >= 8) count++;
        if (pw.matches(".*[A-Z].*")) count++;
        if (pw.matches(".*\\d.*")) count++;
        if (pw.matches(".*[^a-zA-Z0-9].*")) count++;
        if (count <= 1) return "Weak";
        else if (count <= 3) return "Medium";
        return "Strong";
    }
}
