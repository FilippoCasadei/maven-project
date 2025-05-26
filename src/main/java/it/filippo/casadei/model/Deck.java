package it.filippo.casadei.model;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private final Stack<Card> deck = new Stack<>();

    // == COSTRUTTORE ==
    public Deck() {

    }

    // == METODI PUBBLICI ==
    // Popola il mazzo in modo ordinato con le 40 carte della briscola
    public void populate() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                this.deck.push(new Card(suit, rank));
            }
        }
    }

    // Mescola il mazzo
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    // Pesca una carta
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

    // == GETTER E SETTER ==
    public List<Card> getCards() {
        return Collections.unmodifiableList(deck);
    }
}
