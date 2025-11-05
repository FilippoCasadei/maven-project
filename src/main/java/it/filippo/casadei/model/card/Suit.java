package it.filippo.casadei.model.card;

/**
 * Rappresenta i semi delle carte nel gioco della Briscola.
 * I semi disponibili sono bastoni, denara, coppe e spade.
 */
public enum Suit {
    BATONS("bastoni"),
    COINS("denara"),
    CUPS("coppe"),
    SWORDS("spade");

    private final String suitName;

    // == COSTRUTTORE ==

    /**
     * Crea un nuovo seme di carta con il nome specificato.
     *
     * @param suitName il nome del seme della carta
     */
    Suit(String suitName) {
        this.suitName = suitName;
    }

    // == GETTER E SETTER ==

    /**
     * Restituisce il nome del seme della carta.
     *
     * @return il nome del seme come stringa
     */
    public String getSuitName() {
        return this.suitName;
    }
}