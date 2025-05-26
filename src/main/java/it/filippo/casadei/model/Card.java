package it.filippo.casadei.model;

public class Card{

    private final Suit suit;
    private final Rank rank;

    // == COSTRUTTORE ==
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    // == METODI PUBBLICI ==
    // Ritorna il nome del file associato alla carta
    public String toFileName() {
        return this.suit.getSuitName() + this.rank.getRankName();
    }

    // == GETTER E SETTER ==
    public int getPoints() {
        return this.rank.getCardPoints();
    }

    public Suit getSuit() {
        return this.suit;
    }

    public Rank getRank() {
        return this.rank;
    }

    // == ToSTRING ==
    @Override
    public String toString() {
        return this.rank.getRankName() + " di " + this.suit.getSuitName();
    }
}
