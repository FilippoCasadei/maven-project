package it.filippo.casadei.model;

public class GameRules {

    // == METODI PUBBLICI STATICI ==

    /**
     * Determina quale carta vince la mano di gioco.
     *
     * @param firstCard    La carta giocata per prima.
     * @param secondCard   La carta giocata per seconda.
     * @param briscolaSuit Seme della briscola.
     * @return 0 se vince il primo giocatore, 1 se vince il secondo.
     */
    public static int compareCards(Card firstCard, Card secondCard, Suit briscolaSuit) {
        boolean firstIsBriscola = firstCard.getSuit() == briscolaSuit;
        boolean secondIsBriscola = secondCard.getSuit() == briscolaSuit;

        // Primo è briscola e secondo non è briscola -> vince primo
        if (firstIsBriscola && !secondIsBriscola) {
            return 0;
        }
        // Primo non è briscola e secondo è briscola -> vince secondo
        else if (!firstIsBriscola && secondIsBriscola) {
            return 1;
        }
        // Seme è lo stesso -> vince chi ha il valore di carta più alto
        else if (firstCard.getSuit() == secondCard.getSuit()) {
            return firstCard.getRank().ordinal() > secondCard.getRank().ordinal() ? 0 : 1;
        }
        // Seme è diverso e nessuno è briscola -> vince primo
        else {
            return 0;
        }
    }

    /**
     * Calcola il punteggio totale delle carte giocate in una mano di gioco.
     *
     * @param firstCard  La prima carta giocata
     * @param secondCard La seconda carta giocata
     * @return La somma dei punti delle due carte
     */
    public static int calculatePointsWon(Card firstCard, Card secondCard) {
        return firstCard.getRank().getCardPoints() + secondCard.getRank().getCardPoints();
    }
}