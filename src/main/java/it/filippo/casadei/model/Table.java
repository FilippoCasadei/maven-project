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

    // Elimina le carte giocate del turno precedente
    public void clear() {
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

    public boolean bothPlayed() {
        return firstCard != null && secondCard != null;
    }

    // TODO: Non penso serva
    public List<Map.Entry<Player, Card>> getPlayOrder() {
        List<Map.Entry<Player, Card>> order = new ArrayList<>(2);
        if (firstPlayer != null) order.add(Map.entry(firstPlayer, firstCard));
        if (secondPlayer != null) order.add(Map.entry(secondPlayer, secondCard));
        return order;
    }

    // == GETTER ==
    public Player getFirstPlayer() {
        return firstPlayer;
    }

    public Player getSecondPlayer() {
        return secondPlayer;
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

    public Player getWinner() {
        return this.winner;
    }

    // == SETTER ==
    public void setPlayersOrder(Player firstPlayer, Player secondPlayer) {
        this.firstPlayer = firstPlayer;
        this.secondPlayer = secondPlayer;
    }

    public void setPointsWon(int pointsWon) {
        this.pointsWon = pointsWon;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }
}

// TODO: MIGLIORE SCALABILITA' E GENERICITA', MA PIU COMPLESSA
/*
public class Table {
    private final Map<Player, Card> playedCards = new LinkedHashMap<>();

    public void playCard(Player player, Card card) {
        playedCards.put(player, card);
    }

    public Map<Player, Card> getPlayedCards() {
        return playedCards;
    }

    public void clear() {
        playedCards.clear();
    }

    public Card getCardPlayedBy(Player player) {
        return playedCards.get(player);
    }

    public List<Map.Entry<Player, Card>> getPlayOrder() {
        return new ArrayList<>(playedCards.entrySet());
    }
}
*/

