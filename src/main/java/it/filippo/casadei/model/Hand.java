package it.filippo.casadei.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta le carte in mano a un giocatore.
 */
public class Hand {

    public static final int MAX_CARDS_IN_HAND = 3;
    private final List<Card> hand;

    // == COSTRUTTORE ==
    public Hand() {
        this.hand = new ArrayList<>();
    }

    // == METODI PUBBLICI ==
    /**
     * Aggiunge una carta alla mano.
     *
     * @param card carta aggiunta
     */
    public void addCard(Card card) {
        // Verifico che le carte in mano non siano mai più di 2 prima di pescare una carta
        if (hand.size() >= MAX_CARDS_IN_HAND) {
            throw new IllegalStateException("Il giocatore ha già "+ getCards() +" carte in mano. Impossibile aggiungere una nuova carta.");
        }
        hand.add(card);
    }

    /**
     * Rimuove una carta dalla mano.
     *
     * @param card la carta rimossa
     * @throws IllegalArgumentException se la carta non è presente nella mano
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

    public void clear() {
        this.hand.clear();
    }

    // == GETTER E SETTER ==
    public List<Card> getCards() {
        return Collections.unmodifiableList(hand);
    }

    // == ToSTRING ==
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
