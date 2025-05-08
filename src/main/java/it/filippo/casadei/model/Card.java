package it.filippo.casadei.model;

public class Card{

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    // Ritorna il nome del file .png associato alla carta
    public String toFileName() {
        return this.suit.getSuitName() + this.rank.getRankName();
    }

    public int getPoints() {
        return this.rank.getCardPoints();
    }

    public Suit getSuit() {
        return this.suit;
    }

    public Rank getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        return this.rank.getRankName() + " of " + this.suit.getSuitName();
    }
}
