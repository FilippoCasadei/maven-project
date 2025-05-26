package it.filippo.casadei.model;

/**
 * Rappresenta una carta da gioco di Briscola, con seme e valore.
 * Ogni carta ha un seme (bastoni, denara, coppe, spade) e un valore (da 1 a 10).
 */
public class Card {

    private final Suit suit;
    private final Rank rank;

    // == COSTRUTTORE ==

    /**
     * Crea una nuova carta con il seme e valore specificati.
     *
     * @param suit il seme della carta (es., BASTONI, DENARA, COPPE, SPADE)
     * @param rank il valore della carta (es., DUE, RE, ASSO)
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    // == METODI PUBBLICI ==

    /**
     * Restituisce il nome del file associato alla carta per caricare l'immagine corrispondente.
     *
     * @return nome del file della carta nel formato "seme+valore" (es. "coppe1")
     */
    public String toFileName() {
        return this.suit.getSuitName() + this.rank.getRankName();
    }

    // == GETTER E SETTER ==

    /**
     * Restituisce il punteggio della carta.
     *
     * @return punteggio della carta nel gioco della briscola
     */
    public int getPoints() {
        return this.rank.getCardPoints();
    }

    /**
     * Restituisce il seme della carta.
     *
     * @return seme della carta
     */
    public Suit getSuit() {
        return this.suit;
    }

    /**
     * Restituisce il valore della carta.
     *
     * @return valore della carta
     */
    public Rank getRank() {
        return this.rank;
    }

    // == ToSTRING ==

    /**
     * Restituisce una rappresentazione testuale della carta.
     *
     * @return stringa nel formato "valore di seme"
     */
    @Override
    public String toString() {
        return this.rank.getRankName() + " di " + this.suit.getSuitName();
    }
}