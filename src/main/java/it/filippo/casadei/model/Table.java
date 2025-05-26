package it.filippo.casadei.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Table {
    private Player firstPlayer;
    private Card firstCard;
    private Player secondPlayer;
    private Card secondCard;
    private Player winner;
    private int pointsWon;

    // == METODI PUBBLICI ==
    // NOTA: I Player sono sempre presenti e mai nulli, mentre le carte sono null finchè non vengono giocate
    public void playCard(Player player, Card card) {
        if (player.equals(firstPlayer)) {
            this.firstCard = card;
        }
        else if (player.equals(secondPlayer)) {
            this.secondCard = card;
        }
        else {
            throw new IllegalStateException("Sono già state giocate due carte.");
        }
    }

    // Elimina i dati della mano di gioco precedente
    public void clear() {
        this.firstCard = null;
        this.secondCard = null;
        this.winner = null;
        this.pointsWon = -1;
    }

    // Elimina tutti i dati del tavolo
    public void clearAll() {
        this.firstPlayer = null;
        this.secondPlayer = null;
        this.firstCard = null;
        this.secondCard = null;
        this.winner = null;
        this.pointsWon = -1;
    }

    public Card getCardPlayedBy(Player player) {
        if (player.equals(firstPlayer)) {
            return firstCard;
        }
        else if (player.equals(secondPlayer)) {
            return secondCard;
        }
        else {
            throw new IllegalArgumentException("Il giocatore " + player + " non ha giocato una carta.");
        }
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

    public int getPointsWon() {
        return this.pointsWon;
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

