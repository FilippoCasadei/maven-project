package it.filippo.casadei.model;

public enum Suit {
    BATONS("bastoni"),
    COINS("denara"),
    CUPS("coppe"),
    SWORDS("spade");

    private final String suitName;

    private Suit(String suitName) {
        this.suitName = suitName;
    }

    public String getSuitName() {
        return this.suitName;
    }
}
