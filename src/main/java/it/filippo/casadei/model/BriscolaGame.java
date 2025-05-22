package it.filippo.casadei.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe che rappresenta lo stato centrale di una partita di Briscola.
 */
public class BriscolaGame {

    public static final int TOTAL_POINTS = 120;
    public static final int HALF_TOTAL_POINTS = TOTAL_POINTS / 2;

    private final Player player1;
    private final Player player2;
    private final Deck deck;
    private final Table table;
    private final List<Card> playedCards = new ArrayList<>();  // TODO: NON SERVE
    private Card briscola;

    public BriscolaGame(Player player1, Player player2, Deck deck, Table table) {
        this.player1 = player1;
        this.player2 = player2;
        this.deck = Deck.createDeck();
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

    // TODO: NON SERVE
    public List<Card> getPlayedCards() {
        return Collections.unmodifiableList(playedCards);
    }

    // === SETTER ===
    public void setBriscola(Card briscola) {
        this.briscola = briscola;
    }

    // === METODI DI SUPPORTO ===
    // TODO: NON SERVE
    public void registerPlayedCard(Card card) {
        this.playedCards.add(card);
    }

    public Player getOpponent(Player player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        throw new IllegalArgumentException("Unknown player");
    }

    private boolean isBriscolaDrawn() {
        return this.briscola == null;
    }

    // TODO: NON SERVE
    public boolean isCardAlreadyPlayed(Card card) {
        return playedCards.contains(card);
    }

    public boolean isCardBriscola(Card card) {
        return card.getSuit().equals(briscola.getSuit());
    }

    // == METODI PER LA GESTIONE DEL FLUSSO ==

    /**
     * // Mescola il mazzo e distribuisce inizialmente 3 carte a ciascun giocatore
     */
    public void setupGame() {
        deck.shuffle();
        table.setPlayersOrder(player1, player2);
        for (int i = 0; i < Hand.MAX_CARDS_IN_HAND; i++) {
            player1.addCardToHand(deck.draw());
            player2.addCardToHand(deck.draw());
        }
        this.briscola = deck.draw();
    }

    public void playCard(Player player, Card card) {
        table.playCard(player, card);
    }

    public boolean isHandComplete() {
        return table.bothPlayed();
    }

    public void evaluateHand() {
        Player firstPlayer = table.getFirstPlayer();
        Player secondPlayer = table.getSecondPlayer();
        Suit briscolaSuit = briscola.getSuit();

        // Prende la prima e seconda carta giocata
        Card firstCard = table.getCardPlayedBy(firstPlayer);
        Card secondCard = table.getCardPlayedBy(secondPlayer);

        // Determina il vincitore della mano di gioco
        int winIdx = GameRules.compareCards(firstCard, secondCard, briscolaSuit);
        Player winner = (winIdx == 0) ? firstPlayer : secondPlayer;

        // Aggiunge i punti al giocatore che ha vinto la mano di gioco
        int points = GameRules.calculatePointsWon(firstCard, secondCard);
        winner.addPoints(points);

        // Il vincitore gioca per primo il turno successivo e il perdente per secondo
        table.setPlayersOrder(winner, getOpponent(winner));
        table.clear();
    }

    public void drawCard(Player player) {
        // Se il mazzo non è vuoto -> pesca una carta
        if (!deck.isEmpty()) {
            Card drawn = deck.draw();
            player.addCardToHand(drawn);
        }
        // Se il mazzo è vuoto e la briscola non è stata ancora pescata -> pescala
        else if (!isBriscolaDrawn()) {
            player.addCardToHand(briscola);
            this.briscola = null;
        }
    }

    public boolean isGameOver() {
        return deck.isEmpty()
            && player1.getHand().isEmpty()
            && player2.getHand().isEmpty();
    }
}



