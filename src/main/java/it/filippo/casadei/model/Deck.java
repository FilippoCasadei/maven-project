package it.filippo.casadei.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private final Stack<Card> deck;

    // TODO: MEGLIO UTILIZZARE UN COSTRUTTORE NORMALE E UN METODO PER POPOLARE IL DECK?
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
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    // Pescare una carta
    public Card draw() {
        return deck.isEmpty() ? null :
                                deck.pop();
    }

    // Dimensione del deck
    public int size() {
        return deck.size();
    }

    public boolean isEmpty() {
        return deck.isEmpty();
    }

    // == GETTEER ==
    public List<Card> getAllCards() {
        List<Card> allCards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                allCards.add(new Card(suit, rank));
            }
        }
        return allCards;
    }

    public List<Card> getCards() {
        return Collections.unmodifiableList(deck);
    }
}
