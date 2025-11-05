package it.filippo.casadei.model.card;

/**
 * Rappresenta i valori delle carte nel gioco della Briscola.
 * Ogni carta ha un valore (da 1 a 10) e un punteggio associato per il calcolo del punteggio finale.
 */
public enum Rank {
    TWO("2", 0),
    FOUR("4", 0),
    FIVE("5", 0),
    SIX("6", 0),
    SEVEN("7", 0),
    KNAVE("8", 2),
    KNIGHT("9", 3),
    KING("10", 4),
    THREE("3", 10),
    ACE("1", 11);

    private final String rankName;
    private final int rankPoints;

    // == COSTRUTTORE ==

    /**
     * Crea un nuovo valore di carta con il nome e i punti specificati.
     *
     * @param rankName   il nome/valore della carta (es. "1" per Asso)
     * @param rankPoints i punti che vale la carta nel gioco
     */
    Rank(String rankName, int rankPoints) {
        this.rankName = rankName;
        this.rankPoints = rankPoints;
    }

    // == GETTER E SETTER ==

    /**
     * Restituisce il nome/valore della carta.
     *
     * @return il nome della carta come stringa
     */
    public String getRankName() {
        return this.rankName;
    }

    /**
     * Restituisce i punti che vale la carta nel gioco.
     *
     * @return il valore in punti della carta
     */
    public int getCardPoints() {
        return this.rankPoints;
    }

}