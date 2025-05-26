package it.filippo.casadei.model;

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
    Rank(String rankName, int rankPoints) {
        this.rankName = rankName;
        this.rankPoints = rankPoints;
    }

    // == GETTER E SETTER ==
    public String getRankName() {
        return this.rankName;
    }

    public int getCardPoints() {
        return this.rankPoints;
    }

}
