package it.filippo.casadei.model;

import java.util.List;

public class Memory {
    private int myPoints;
    private int opponentPoints;
    private List<Card> remainingCards;

    // == METODI PUBBLICI ==
    /**
     * Ritorna quanti carichi (Asso o 3) di un dato seme sono già usciti.
     * @param suit il seme da controllare
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
    public int getMyPoints() {
        return this.myPoints;
    }

    public void setMyPoints(int myPoints) {
        this.myPoints = myPoints;
    }

    public int getOpponentPoints() {
        return this.opponentPoints;
    }

    public void setOpponentPoints(int opponentPoints) {
        this.opponentPoints = opponentPoints;
    }

    public List<Card> getRemainingCards() {
        return this.remainingCards;
    }

    public void setRemainingCards(List<Card> remainingCards) {
        this.remainingCards = remainingCards;
    }
}
