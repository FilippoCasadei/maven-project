package org.filippo.casadei.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
    La classe Table rappresenta lo stato temporaneo della mano di gioco in corso
 */
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
// TODO: SOSTITUITA PER PERMETTERE MIGLIORE SCALABILITA' E GENERICITA'
/*
public class PlayingHand {
    private final Player firstPlayer;
    private final Card firstCard;
    private Player secondPlayer;
    private Card secondCard;

    // Mano di gioco quando il primo giocatore gioca una carta
    public PlayingHand(Player firstPlayer, Card firstCard) {
        this.firstPlayer = firstPlayer;
        this.firstCard = firstCard;
    }

    // Mano di gioco quando il secondo giocatore gioca una carta
    public void setSecondPlay(Player secondPlayer, Card secondCard) {
        this.secondPlayer = secondPlayer;
        this.secondCard = secondCard;
    }
}
 */

