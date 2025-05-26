package it.filippo.casadei.model;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Rappresenta il mazzo di carte utilizzato nel gioco della Briscola.
 * Contiene 40 carte suddivise in 4 semi e 10 valori per seme.
 */
public class Deck {
    private final Stack<Card> deck = new Stack<>();

    // == COSTRUTTORE ==

    /**
     * Crea un nuovo mazzo vuoto di carte.
     */
    public Deck() {

    }

    // == METODI PUBBLICI ==

    /**
     * Popola il mazzo con tutte le 40 carte della briscola.
     * Viene creata una carta per ogni combinazione di semi e valori possibili.
     */
    public void populate() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                this.deck.push(new Card(suit, rank));
            }
        }
    }

    /**
     * Mescola casualmente tutte le carte presenti nel mazzo.
     */
    public void shuffle() {
        Collections.shuffle(this.deck);
    }

    /**
     * Estrae e restituisce la prima carta in cima al mazzo.
     * Se il mazzo è vuoto viene restituito null.
     *
     * @return la carta pescata, o null se il mazzo è vuoto
     */
    public Card draw() {
        return deck.isEmpty() ? null :
                deck.pop();
    }

    /**
     * Restituisce il numero di carte presenti nel mazzo.
     *
     * @return numero di carte nel mazzo
     */
    public int size() {
        return deck.size();
    }

    /**
     * Verifica se il mazzo è vuoto.
     *
     * @return true se il mazzo non contiene carte, false altrimenti
     */
    public boolean isEmpty() {
        return deck.isEmpty();
    }

    // == GETTER E SETTER ==

    /**
     * Restituisce una vista non modificabile della lista di carte nel mazzo.
     *
     * @return lista immutabile delle carte nel mazzo
     */
    public List<Card> getCards() {
        return Collections.unmodifiableList(deck);
    }
}