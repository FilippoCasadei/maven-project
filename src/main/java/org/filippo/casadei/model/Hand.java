package org.filippo.casadei.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a player's hand of cards in a card game.
 */
public class Hand {

    private final List<Card> hand;
    public static final int MAX_CARDS_IN_HAND = 3;

    /**
     * Initializes an empty hand.
     */
    public Hand() {
        this.hand = new ArrayList<>();
    }

    /**
     * Adds a card to the hand.
     *
     * @param card the card to be added
     */
    public void addCard(Card card) {
        // Verifico che le carte in mano non siano mai più di 2 prima di pescare una carta
        if (hand.size() >= MAX_CARDS_IN_HAND) {
            throw new IllegalStateException("Hand contains "+ hand.size() +" cards");
        }

        hand.add(card);
    }

    /**
     * Removes a card from the hand.
     *
     * @param card the card to be removed
     * @throws IllegalArgumentException if the card is not in the hand
     */
    public void removeCard(Card card) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Card " + card + " is not in the hand!");
        }

        hand.remove(card);
    }

    public boolean isEmpty() {
        return hand.isEmpty();
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(hand);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Card card : this.hand) {
            sb.append(card).append(", ");
        }
        // Remove the last ','
        if (!hand.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }

}
