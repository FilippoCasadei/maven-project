package org.filippo.casadei.model;

public enum Rank {
    TWO("2", 0),
    FOUR("4", 0),
    FIVE("5", 0),
    SIX("6", 0),
    SEVEN("7", 0),
    KNAVE("Knave", 2),
    KNIGHT("Knight", 3),
    KING("King", 4),
    THREE("3", 10),
    ACE("Ace", 11);

    private String rankName;
    private int rankPoints;

    private Rank(String rankName, int rankPoints) {
        this.rankName = rankName;
        this.rankPoints = rankPoints;
    }

    public String getRankName() {
        return this.rankName;
    }

    public int getCardPoints() {
        return this.rankPoints;
    }

}
