package it.filippo.casadei.model;

/**
 * Rappresenta il tavolo da gioco della Briscola.
 * Gestisce le carte giocate, i giocatori e il vincitore di ogni mano.
 * Mantiene lo stato del tavolo durante il gioco.
 */
public class Table {
    private Player firstPlayer;
    private Card firstCard;
    private Player secondPlayer;
    private Card secondCard;
    private Player winner;
    private int pointsWon;

    // == METODI PUBBLICI ==
    /**
     * Gioca una carta sul tavolo per il giocatore specificato.
     *
     * @param player il giocatore che sta giocando la carta
     * @param card   la carta da giocare
     */
    public void playCard(Player player, Card card) {
        if (player.equals(firstPlayer)) {
            this.firstCard = card;
        } else {
            this.secondCard = card;
        }
    }

    /**
     * Elimina i dati della mano di gioco precedente.
     * Rimuove le carte giocate, il vincitore e i punti vinti.
     */
    public void clear() {
        this.firstCard = null;
        this.secondCard = null;
        this.winner = null;
        this.pointsWon = -1;
    }

    /**
     * Elimina tutti i dati del tavolo.
     * Rimuove giocatori, carte, vincitore e punti.
     */
    public void clearAll() {
        this.firstPlayer = null;
        this.secondPlayer = null;
        this.firstCard = null;
        this.secondCard = null;
        this.winner = null;
        this.pointsWon = -1;
    }

    /**
     * Restituisce la carta giocata da un determinato giocatore.
     *
     * @param player il giocatore di cui si vuole sapere la carta giocata
     * @return la carta giocata dal giocatore
     * @throws IllegalArgumentException se il giocatore non ha giocato una carta
     */
    public Card getCardPlayedBy(Player player) {
        if (player.equals(firstPlayer)) {
            return firstCard;
        } else if (player.equals(secondPlayer)) {
            return secondCard;
        } else {
            throw new IllegalArgumentException("Il giocatore " + player + " non ha giocato una carta.");
        }
    }

    // == GETTER E SETTER ==

    /**
     * @return il primo giocatore a giocare nel turno
     */
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    /**
     * @return il secondo giocatore a giocare nel turno
     */
    public Player getSecondPlayer() {
        return secondPlayer;
    }

    /**
     * Imposta l'ordine dei giocatori per il turno corrente.
     *
     * @param firstPlayer  il primo giocatore a giocare
     * @param secondPlayer il secondo giocatore a giocare
     */
    public void setPlayersOrder(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    /**
     * @return la prima carta giocata nel turno
     */
    public Card getFirstCard() {
        return firstCard;
    }

    /**
     * @return la seconda carta giocata nel turno
     */
    public Card getSecondCard() {
        return secondCard;
    }

    /**
     * @return i punti vinti nella mano corrente
     */
    public int getPointsWon() {
        return this.pointsWon;
    }

    /**
     * Imposta i punti vinti nella mano corrente.
     *
     * @param pointsWon i punti da assegnare
     */
    public void setPointsWon(int pointsWon) {
        this.pointsWon = pointsWon;
    }

    /**
     * @return il giocatore che ha vinto la mano corrente
     */
    public Player getWinner() {
        return this.winner;
    }

    /**
     * Imposta il vincitore della mano corrente.
     *
     * @param winner il giocatore che ha vinto la mano
     */
    public void setWinner(Player winner) {
        this.winner = winner;
    }
}
