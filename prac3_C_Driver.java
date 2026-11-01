public class prac3_C_Driver {
    public static void main(String[] args) {
        Fraction a = new Fraction(1, 2), b = new Fraction(2, 4), c = new Fraction(3, 6);
        System.out.println(a); System.out.println(b); System.out.println(c);
        System.out.println("All equal: " + (a.equals(b) && b.equals(c)));
    }
}
