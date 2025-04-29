package org.filippo.casadei.model;

/*
    Classe rappresenta la mano di gioco durante un turno
 */
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
