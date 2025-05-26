package it.filippo.casadei.model;

import java.util.Optional;

/**
 * Gestisce lo stato e la logica di una partita a Briscola.
 * Tiene traccia dei giocatori, del mazzo, del tavolo e della briscola.
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

    /**
     * Crea una nuova istanza del gioco della Briscola.
     *
     * @param player1 il primo giocatore che partecipa al gioco
     * @param player2 il secondo giocatore che partecipa al gioco
     * @param deck    il mazzo di carte da utilizzare nel gioco
     * @param table   il tavolo per gestire le carte giocate e le interazioni durante il gioco
     */
    public BriscolaGame(Player player1, Player player2, Deck deck, Table table) {
        this.player1 = player1;
        this.player2 = player2;
        this.deck = deck;
        this.table = table;
    }

    // == METODI PUBBLICI ==
    /**
     * Inizializza il gioco: popola e mescola il mazzo, distribuisce le carte
     * e pesca la briscola.
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

    /**
     * Gioca una carta, rimuovendola dalla mano del giocatore e posizionandola sul tavolo.
     *
     * @param player giocatore che gioca la carta
     * @param card carta da giocare
     */
    public void playCard(Player player, Card card) {
        player.playCard(card);
        table.playCard(player, card);
    }

    /**
     * Valuta le due carte sul tavolo, determina il vincitore della mano
     * e assegna i punti.
     */
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

    /**
     * Permette al giocatore di pescare una carta dal mazzo o la briscola, se ancora disponibile.
     *
     * @param player giocatore che pesca
     * @return carta pescata, se presente
     */
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

    /**
     * Verifica se la partita è conclusa.
     *
     * @return true se il mazzo è vuoto e le mani sono vuote
     */
    public boolean isGameOver() {
        return deck.isEmpty()
                && player1.getHand().isEmpty()
                && player2.getHand().isEmpty();
    }

    /**
     * Resetta lo stato del gioco per una nuova partita.
     */
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

    /**
     * Restituisce l'avversario del giocatore specificato.
     *
     * @param player giocatore di riferimento
     * @return giocatore avversario
     */
    public Player getOpponent(Player player) {
        if (player.equals(player1)) return player2;
        if (player.equals(player2)) return player1;
        throw new IllegalArgumentException("Giocatore sconosciuto: " + player.getName());
    }

    /**
     * Determina il vincitore della partita in base ai punti dei due giocatori.
     * Se i punti di un giocatore superano quelli dell'altro, quel giocatore viene dichiarato vincitore.
     * Se i punti sono uguali, non viene dichiarato alcun vincitore e la partita finisce in pareggio.
     *
     * @return un {@code Optional} contenente il {@code Player} vincente se c'è un vincitore,
     * oppure un {@code Optional} vuoto se la partita è finita in pareggio.
     */
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



