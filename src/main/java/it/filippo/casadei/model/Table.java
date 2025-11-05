package it.filippo.casadei.model;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.Player;

import java.util.ArrayList;
import java.util.List;

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
        this.pointsWon = 0;
    }

    /**
     * Elimina tutti i dati del tavolo.
     * Rimuove giocatori, carte, vincitore e punti.
     */
    public void reset() {
        this.firstPlayer = null;
        this.secondPlayer = null;
        this.firstCard = null;
        this.secondCard = null;
        this.winner = null;
        this.pointsWon = 0;
    }

    // == GETTER E SETTER ==

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
    }

    public void setPlayersOrder(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public Card getFirstCard() {
        return firstCard;
    }

    public Card getSecondCard() {
        return secondCard;
    }

    public List<Card> getPlayedCards() {
        List<Card> played = new ArrayList<>();
        if (firstCard != null) {
            played.add(firstCard);
        }    
        if (secondCard != null) {
            played.add(secondCard);
        }
        return played;
    }

    public void setPointsWon(int pointsWon) {
        this.pointsWon = pointsWon;
    }

    public Player getWinner() {
        return this.winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }
}
