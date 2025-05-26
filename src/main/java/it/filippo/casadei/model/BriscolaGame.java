package it.filippo.casadei.model;

import java.util.Optional;

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
    private Card briscola;
    private boolean isBriscolaDrawn = false;

    // == COSTRUTTORE ==
    public BriscolaGame(Player player1, Player player2, Deck deck, Table table) {
        this.player1 = player1;
        this.player2 = player2;
        this.deck = deck;
        this.table = table;
    }

    // == METODI PUBBLICI ==
    /**
     * // Mescola il mazzo, distribuisce inizialmente 3 carte a ciascun giocatore e sceglie la briscola
     */
    public void setupGame() {
        // Inserisce 40 carte nel mazzo
        deck.populate();
        // Mescola il mazzo
        deck.shuffle();
        // Distribuisce inizialmente 3 carte a ciascun giocatore
        table.setPlayersOrder(player1, player2);
        for (int i = 0; i < Hand.MAX_CARDS_IN_HAND; i++) {
            player1.addCardToHand(deck.draw());
            player2.addCardToHand(deck.draw());
        }
        // Pesca la briscola
        this.briscola = deck.draw();
    }

    public void playCard(Player player, Card card) {
//        System.out.println("Prima playCard: mano di "+player.getName()+": " + player.getHand());
        player.playCard(card);
        table.playCard(player, card);
//        System.out.println("Dopo playCard: mano di "+player.getName()+": " + player.getHand());
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
        table.setWinner(winner);

        // Aggiunge i punti al giocatore che ha vinto la mano di gioco
        int points = GameRules.calculatePointsWon(firstCard, secondCard);
        winner.addPoints(points);
        table.setPointsWon(points);

        // Il vincitore gioca per primo il turno successivo e il perdente per secondo
        table.setPlayersOrder(winner, getOpponent(winner));
    }

    /** Pesca una carta dal mazzo se è possibile e restituisce la carta */
    public Optional<Card> drawCard(Player player) {
        // Se il mazzo non è vuoto -> pesca una carta
        if (!deck.isEmpty()) {
            Card drawn = deck.draw();
            player.addCardToHand(drawn);
            return Optional.of(drawn);
        }
        // Se il mazzo è vuoto e la briscola non è stata ancora pescata -> pescala
        else if (!isBriscolaDrawn) {
            this.isBriscolaDrawn = true;
            player.addCardToHand(briscola);
            return Optional.of(briscola);
        }
        // Altrimenti non pescare
        return Optional.empty();
    }

    public boolean isGameOver() {
        return deck.isEmpty()
                && player1.getHand().isEmpty()
                && player2.getHand().isEmpty();
    }

    public void resetGame() {
        // Ripulisci il tavolo
        table.clearAll();

        // Svuota le mani e i punti dei giocatori
        player1.getHand().clear();
        player2.getHand().clear();
        player1.resetPoints();
        player2.resetPoints();

        // Ripristina la briscola e il suo flag
        briscola = null;
        isBriscolaDrawn = false;
    }

    public Player getOpponent(Player player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        throw new IllegalArgumentException("Giocatore sconosciuto: " + player.getName());
    }

    public Optional<Player> getWinner() {
        // Vincitore se presente è unico, altrimenti non c'è un vincitore e partita finisce in pareggio
        Optional<Player> winner;
        if (player1.getPoints() > player2.getPoints()) {
            winner = Optional.of(player1);
        } else if (player2.getPoints() > player1.getPoints()) {
            winner = Optional.of(player2);
        } else {
            winner = Optional.empty(); // pareggio
        }

        return winner;
    }

    // === GETTER E SETTER ===
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
}



