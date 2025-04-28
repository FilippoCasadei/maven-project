package org.filippo.casadei.model;

public enum Suit {
    BATONS("Batons"),
    COINS("Coins"),
    CUPS("Cups"),
    SWORDS("Swords");

    private final String suitName;

    private Suit(String suitName) {
        this.suitName = suitName;
    }

    public String getSuitName() {
        return this.suitName;
    }
}
