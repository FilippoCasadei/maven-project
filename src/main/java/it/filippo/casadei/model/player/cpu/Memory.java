package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.card.Rank;
import it.filippo.casadei.model.card.Suit;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta la memoria di gioco di un giocatore CPU, tenendo traccia dei propri punti e di quelli
 * dell'avversario e delle carte rimanenti nel mazzo.
 */
public class Memory {
    private int myPoints;
    private int opponentPoints;
    private List<Card> remainingCards;

    private static final int TOTAL_CARICHI_FOR_SUIT = 2;  // In totale per seme ci sono 2 carichi: Asso + 3
    
    // == METODI PUBBLICI ==

    /**
     * Inizializza la memoria con i punti a zero e il mazzo di carte come carte rimanenti.
     * 
     * @param deck il mazzo di carte completo all'inizio della partita
     */
    public void initialize(List<Card> deck) {
        this.myPoints = 0;
        this.opponentPoints = 0;
        this.remainingCards = new ArrayList<>(deck);
    }

    /**
     * Ritorna quanti carichi (Asso o 3) di un dato seme sono già usciti.
     *
     * @param suit il seme da controllare
     * @return il numero di carichi già giocati per il seme specificato
     */
    public int getCarichiAlreadyPlayedForSuit(Suit suit) {
        long remainingCarichi = remainingCards.stream()
                .filter(c -> c.getSuit() == suit)
                .filter(c -> c.getRank() == Rank.ACE || c.getRank() == Rank.THREE)
                .count();
        return TOTAL_CARICHI_FOR_SUIT - (int) remainingCarichi;
    }

    /**
     * Aggiorna la memoria rimuovendo le carte già viste (carte sul tavolo e carte nella propria mano).
     *
     * @param context il contesto di gioco attuale
     */
    public void updateFromContext(GameContext context) {
        removeSeenCards(context.getTable().getPlayedCards());  // Rimuove le carte sul tavolo
        removeSeenCards(context.getCpuHand().getCards());  // Rimuove le carte nella mano della CPU
    }

    /**
     * Aggiorna i punti in memoria alla fine della mano di gioco.
     *
     * @param handPoints  i punti totalizzati nella mano
     * @param playedCards le carte giocate nel turno
     * @param cpuWon true se la CPU ha vinto la mano, false altrimenti
     */
    public void updateAfterTurn(int handPoints, List<Card> playedCards, boolean cpuWon) {
        // aggiorna i punti in memoria
        if (cpuWon) {
            setMyPoints(getMyPoints() + handPoints);
        } else {
            setOpponentPoints(getOpponentPoints() + handPoints);
        }

        // rimuove le carte giocate nel turno
        removeSeenCards(playedCards);
    }

    // == METODI PRIVATI ==

    /**
     * Rimuove le carte viste dalla lista delle carte rimanenti.
     *
     * @param seenCards la lista delle carte viste
     */
    private void removeSeenCards(List<Card> seenCards) { 
        // Caso lista vuota o nulla
        if (seenCards == null || seenCards.isEmpty()) {
            return; 
        }

        // Rimuove le carte viste dalla lista delle carte rimanenti
        for (Card card : seenCards) {
            if (card != null) {
                remainingCards.remove(card);
            }
        }
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

    public int getCpuPoints() {
        return this.myPoints;
    }

    public int getRemainingDeck() {
        return remainingCards.size()-3;
    }

    public List<Card> getRemainingBriscole(Suit briscolaSuit) {
        return remainingCards.stream().filter(c -> c.isBriscola(briscolaSuit)).toList();
    }
}