package it.filippo.casadei.model.player;

import it.filippo.casadei.model.card.Card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Rappresenta le carte in mano a un giocatore.
 * La mano può contenere al massimo 3 carte.
 * Le carte possono essere aggiunte e rimosse ed è possibile controllarne lo stato.
 */
public class Hand {

    public static final int MAX_CARDS_IN_HAND = 3;

    private final List<Card> hand;

    // == COSTRUTTORE ==

    /**
     * Crea una nuova mano vuota che può contenere fino a 3 carte.
     */
    public Hand() {
        this.hand = new ArrayList<>();
    }

    // == METODI PUBBLICI ==

    /**
     * Aggiunge una carta alla mano del giocatore.
     *
     * @param card la carta da aggiungere alla mano
     * @throws IllegalStateException se la mano contiene già il numero massimo di carte (3)
     */
    public void addCard(Card card) {
        // Verifico che le carte in mano non siano mai più di 2 prima di pescare una carta
        if (hand.size() >= MAX_CARDS_IN_HAND) {
            throw new IllegalStateException("Il giocatore ha già " + getCards() + " carte in mano. Impossibile aggiungere una nuova carta.");
        }
        hand.add(card);
    }

    /**
     * Rimuove una carta specifica dalla mano del giocatore.
     *
     * @param card la carta da rimuovere dalla mano
     * @throws IllegalArgumentException se la carta specificata non è presente nella mano
     */
    public void removeCard(Card card) {
        if (!hand.contains(card)) {
            throw new IllegalArgumentException("Card " + card + " is not in the hand!");
        }

        hand.remove(card);
    }

    /**
     * Verifica se la mano è vuota.
     *
     * @return true se la mano non contiene carte, false altrimenti
     */
    public boolean isEmpty() {
        return hand.isEmpty();
    }

    /**
     * Rimuove tutte le carte dalla mano.
     */
    public void clear() {
        this.hand.clear();
    }

    // == GETTER E SETTER ==

    /**
     * Restituisce una lista immutabile delle carte nella mano.
     *
     * @return lista non modificabile delle carte presenti nella mano
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(hand);
    }

    // == ToSTRING ==

    /**
     * Restituisce una rappresentazione testuale della mano.
     *
     * @return stringa che elenca tutte le carte presenti nella mano tra parentesi quadre
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Card card : this.hand) {
            sb.append(card).append(", ");
        }

        if (!hand.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        return sb.toString();
    }
}