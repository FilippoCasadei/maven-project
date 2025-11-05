package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.Table;
import it.filippo.casadei.model.card.Rank;
import it.filippo.casadei.model.card.Suit;
import it.filippo.casadei.model.player.Hand;

import java.util.Comparator;
import java.util.Optional;

/**
 * Implementazione della difficoltà "Media" per la CPU nel gioco della Briscola.
 * Questa strategia di gioco è più sofisticata e cerca di giocare in modo più tattico
 * rispetto alla difficoltà facile.
 * 
 * Strategia di gioco:
 * <ol>
 *   <li>Quando è primo a giocare:
 *     <ul>
 *       <li>Gioca la carta di minor valore possibile</li>
 *     </ul>
 *   </li>
 *   <li>Quando è secondo a giocare:
 *     <ol>
 *       <li>Se la carta sul tavolo non è briscola:
 *         <ul>
 *           <li>Gioca la carta più alta dello stesso seme (se possibile)</li>
 *           <li>Se la carta sul tavolo non è un carico, gioca la carta di minor valore</li>
 *           <li>Se la carta sul tavolo è un carico, usa la briscola più bassa</li>
 *           <li>Se non ha briscole, gioca la carta di minor valore</li>
 *         </ul>
 *       </li>
 *       <li>Se la carta sul tavolo è briscola:
 *         <ul>
 *           <li>Se è il tre di briscola, prendi con l'asso se possibile</li>
 *           <li>Altrimenti gioca la carta di minor valore</li>
 *         </ul>
 *       </li>
 *     </ol>
 *   </li>
 * </ol>
 */

public class MediumDifficulty implements CpuDifficulty {

    // == METODI PUBBLICI ==

    @Override
    public Card chooseCard(GameContext context, Memory memory) {
        Table table = context.getTable();
        Hand hand = context.getCpuHand();
        Suit briscolaSuit = context.getBriscolaSuit();


        // Primo a giocare
        if (context.isCpuFirst()) {
            // Gioca la carta di minor valore
            return getLessValuableCard(hand, briscolaSuit);
        }
        // Caso 2: Secondo a giocare
        else {
            Card cardOnTable = table.getFirstCard();
            return chooseAsSecondPlayer(hand, briscolaSuit, cardOnTable);
        }
    }
    
    // == METODI HELPER ==
    
    /**
     * Determina quale carta giocare quando la CPU è il secondo giocatore a giocare.
     * 
     * @param hand la mano della CPU
     * @param briscolaSuit il seme di briscola
     * @param cardOnTable la carta giocata dal primo giocatore
     * @return la carta scelta dalla CPU da giocare
     */
    private Card chooseAsSecondPlayer(Hand hand, Suit briscolaSuit, Card cardOnTable) {
        // Il seme non è briscola
        if (!cardOnTable.isBriscola(briscolaSuit)) {

            // Gioca la carta dello stesso seme più alta (se possibile)
            Optional<Card> chosen = hand.getCards().stream()
                    .filter(card -> card.getSuit().equals(cardOnTable.getSuit()))
                    .filter(c -> c.getRank().ordinal() > cardOnTable.getRank().ordinal())
                    .max(Comparator.comparingInt(Card::getPoints));

            if (chosen.isPresent()) {
                return chosen.get();
            }

            // Se la carta sul tavolo non è un carico gioca la carta dal valore più basso
            if (!cardOnTable.isCarico()) {
                return getLessValuableCard(hand, briscolaSuit);
            }

            // Se la carta sul tavolo è un carico gioca la briscola più bassa (se possibile)
            chosen = getLowestPointBriscola(hand, briscolaSuit);
            if (chosen.isPresent()) {
                return chosen.get();
            }

            // Se non ho brisole gioca la carta più bassa non di briscola
            return getLowestPointCard(hand);
        }

        // Il seme è briscola

        // Se è il 3 di briscola prendi con l'asso (se possibile)
        if (cardOnTable.getRank().equals(Rank.THREE)) {
            Optional<Card> chosen = hand.getCards().stream()
                    .filter(c -> c.getRank().equals(Rank.ACE) && c.getSuit().equals(briscolaSuit))
                    .findAny();

            if (chosen.isPresent()) {
                return chosen.get();
            }
        }

        // Altrimenti gioca la carta con meno valore
        return getLessValuableCard(hand, briscolaSuit);
    }

    /**
     * Restituisce la carta meno preziosa dalla mano, considerando il seme di briscola.
     * Il metodo cerca di selezionare la carta col minor valore che sia:
     * 1. Non di briscola e non un carico
     * 2. Una briscola non carico
     * 3. La carta con il minor valore in assoluto
     */
    private Card getLessValuableCard(Hand hand, Suit briscolaSuit) {
        return getLowestPointNotBriscola(hand, briscolaSuit)
                .or(() -> getLowestPointBriscola(hand, briscolaSuit))
                .orElseGet(() -> getLowestPointCard(hand));
    }

    /**
     * Restituisce la carta più bassa dalla mano.
     */
    private Card getLowestPointCard(Hand hand) {
        return hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparingInt(c -> c.getRank().ordinal()))
                .orElseThrow(() -> new IllegalStateException("La mano è vuota."));
    }

    /**
     * Restituisce la carta non di briscola più bassa.
     */
    private Optional<Card> getLowestPointNotBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> !c.isCarico())
                .filter(c -> !c.isBriscola(briscolaSuit))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparingInt(c -> c.getRank().ordinal()));
    }

    /**
     * Restituisce dalla mano data la carta di briscola più bassa.
     */
    private Optional<Card> getLowestPointBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> !c.isCarico())
                .filter(c -> c.isBriscola(briscolaSuit))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparingInt(c -> c.getRank().ordinal()));
    }
}
