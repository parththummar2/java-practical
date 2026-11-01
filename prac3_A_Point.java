import java.util.Objects;
class Point {
    private final int x, y;
    public Point(int x, int y) { this.x = x; this.y = y; }
    @Override public String toString() { return "(" + x + ", " + y + ")"; }
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point other = (Point) obj;
        return x == other.x && y == other.y;
    }
    @Override public int hashCode() { return Objects.hash(x, y); }
}
