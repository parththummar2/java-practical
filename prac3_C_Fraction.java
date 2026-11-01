import java.util.Objects;
class Fraction {
    private final int num, den;
    public Fraction(int num, int den) {
        if (den == 0) throw new IllegalArgumentException("Denominator cannot be zero");
        int sign = den < 0 ? -1 : 1;
        int g = gcd(Math.abs(num), Math.abs(den));
        this.num = sign * num / g;
        this.den = sign * den / g;
    }
    private static int gcd(int a, int b) { while (b != 0) { int t = b; b = a % b; a = t; } return a == 0 ? 1 : a; }
    @Override public String toString() { return num + "/" + den; }
    @Override public boolean equals(Object o) { return o instanceof Fraction f && num == f.num && den == f.den; }
    @Override public int hashCode() { return Objects.hash(num, den); }
}
