package it.filippo.casadei.model;

import java.util.List;

/**
 * Rappresenta la memoria di gioco di un giocatore CPU, tenendo traccia dei punti
 * e delle carte rimanenti nel mazzo.
 */
public class Memory {
    private int myPoints;
    private int opponentPoints;
    private List<Card> remainingCards;

    // == METODI PUBBLICI ==

    /**
     * Ritorna quanti carichi (Asso o 3) di un dato seme sono già usciti.
     *
     * @param suit il seme da controllare
     * @return il numero di carichi già giocati per il seme specificato
     */
    public int getCarichiAlreadyPlayedForSuit(Suit suit) {
        // In totale per seme ci sono 2 carichi: Asso + 3
        final int TOTAL_CARICHI_FOR_SUIT = 2;
        long remainingCarichi = remainingCards.stream()
                .filter(c -> c.getSuit() == suit)
                .filter(c -> c.getRank() == Rank.ACE || c.getRank() == Rank.THREE)
                .count();
        return TOTAL_CARICHI_FOR_SUIT - (int) remainingCarichi;
    }

    // === GETTER E SETTER ===

    /**
     * Restituisce i punti totalizzati dal giocatore CPU.
     *
     * @return i punti del giocatore CPU
     */
    public int getMyPoints() {
        return this.myPoints;
    }

    /**
     * Imposta i punti totalizzati dal giocatore CPU.
     *
     * @param myPoints i punti da assegnare al giocatore CPU
     */
    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    /**
     * Restituisce i punti totalizzati dall'avversario.
     *
     * @return i punti dell'avversario
     */
    public int getOpponentPoints() {
        return this.opponentPoints;
    }

    /**
     * Imposta i punti totalizzati dall'avversario.
     *
     * @param opponentPoints i punti da assegnare all'avversario
     */
    public void setOpponentPoints(int opponentPoints) {
        this.opponentPoints = opponentPoints;
    }

    /**
     * Restituisce la lista delle carte rimanenti nel mazzo.
     *
     * @return la lista delle carte ancora non giocate
     */
    public List<Card> getRemainingCards() {
        return this.remainingCards;
    }

    /**
     * Imposta la lista delle carte rimanenti nel mazzo.
     *
     * @param remainingCards la lista delle carte ancora non giocate
     */
    public void setRemainingCards(List<Card> remainingCards) {
        this.remainingCards = remainingCards;
    }
}