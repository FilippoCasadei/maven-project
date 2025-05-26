package it.filippo.casadei.model;

public enum Suit {
    BATONS("bastoni"),
    COINS("denara"),
    CUPS("coppe"),
    SWORDS("spade");

    private final String suitName;

    // == COSTRUTTORE ==
    Suit(String suitName) {
        this.suitName = suitName;
    }

    // == GETTER E SETTER ==
    public String getSuitName() {
        return this.suitName;
    }
}
