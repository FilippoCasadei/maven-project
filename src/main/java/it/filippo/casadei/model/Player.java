package it.filippo.casadei.model;

public abstract class Player {
    // TODO: SERVE IL NOME???
    private String name;
    private Hand hand;
    private int points;

    public Player(String name) {
        this.name = name;
        this.hand = new Hand();
        this.points = 0;
    }

    public void addCardToHand(Card card) {
        hand.addCard(card);
    }

    public void playCard(Card card) {
        hand.removeCard(card);
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }

    // === GETTER ===
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
