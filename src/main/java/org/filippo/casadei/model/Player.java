package org.filippo.casadei.model;

import java.util.Collections;

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

    /**
     * TODO: Gioca una carta: rimuove una carta dalla mano
     * // TODO: PER ORA NON MI INTERESSA IL CONTESTO COME PARAMETRO (GIOCA CARTE A CASO)
     */
    public void playCard(Card card) {
        hand.removeCard(card);
    }

    public Hand getHand() {
        return hand;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
    }
}
