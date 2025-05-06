package org.filippo.casadei.model;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private final Stack<Card> deck;

    // Costruttore privato per utilizzare il Factory Pattern
    private Deck(Stack<Card> deck) {
        this.deck = deck;
    }

    // Factory method per creare un mazzo standard di 40 carte ordinate
    public static Deck createDeck() {
        Stack<Card> deck = new Stack<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.push(new Card(suit, rank));
            }
        }

        return new Deck(deck);
    }

    // Mescolo il mazzo
    public Deck shuffle() {
        Collections.shuffle(this.deck);
        return this;
    }

    // Pescare una carta
    public Card draw() {
        return deck.isEmpty() ? null :
                                deck.pop();
    }
//    TODO: Scegliere tra Optional e non Optional
//    public Optional<Card> draw() {
//        return deck.isEmpty() ? Optional.empty() :
//    							  Optional.of(deck.pop());
//    }

    // Dimensione del deck
    public int size() {
        return deck.size();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    // Rappresentazione immutabile delle carte rimaste nel deck
    public List<Card> getCards() {
        return Collections.unmodifiableList(deck);
    }
}
