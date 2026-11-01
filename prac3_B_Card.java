import java.util.Objects;
class Card {
    private final String rank, suit;
    public Card(String rank, String suit) { this.rank = rank; this.suit = suit; }
    @Override public String toString() { return rank + " of " + suit; }
    @Override public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card)) return false;
        Card c = (Card) obj;
        return Objects.equals(rank, c.rank) && Objects.equals(suit, c.suit);
    }
    @Override public int hashCode() { return Objects.hash(rank, suit); }
}
