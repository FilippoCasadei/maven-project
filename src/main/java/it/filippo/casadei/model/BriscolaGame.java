package it.filippo.casadei.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe che rappresenta lo stato centrale di una partita di Briscola.
 */
public class BriscolaGame {

    private final Player player1;
    private final Player player2;
    private final Deck deck;
    private final Table table;
    private final List<Card> playedCards = new ArrayList<>();
    private Card briscola;

    public BriscolaGame(Player player1, Player player2, Deck deck, Table table) {
        this.player1 = player1;
        this.player2 = player2;
        this.deck = deck;
        this.table = table;
    }

    // === GETTER ===
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Deck getDeck() {
        return deck;
    }

    public Table getTable() {
        return table;
    }

    public Card getBriscola() {
        return briscola;
    }

    public List<Card> getPlayedCards() {
        return Collections.unmodifiableList(playedCards);
    }

    // === SETTER ===
    public void setBriscola(Card briscola) {
        this.briscola = briscola;
    }

    // === METODI DI SUPPORTO ===
    public void registerPlayedCard(Card card) {
        this.playedCards.add(card);
    }

    public Player getOpponent(Player player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        throw new IllegalArgumentException("Unknown player");
    }

    public boolean isCardAlreadyPlayed(Card card) {
        return playedCards.contains(card);
    }

    public boolean isCardBriscola(Card card) {
        return card.getSuit().equals(briscola.getSuit());
    }
}
