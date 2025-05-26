package it.filippo.casadei.model;

public abstract class Player {
    private final String name;
    private final Hand hand;
    private int points;

    // === COSTRUTTORE ===
    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.points = 0;
    }

    // == METODI PUBBLICI ==
    public void addCardToHand(Card card) {
        hand.addCard(card);
    }

    public void playCard(Card card) {
        hand.removeCard(card);
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    public void resetPoints() {
        this.points = 0;
    }

    // == GETTER E SETTER ==
    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getPoints() {
        return points;
    }
}
