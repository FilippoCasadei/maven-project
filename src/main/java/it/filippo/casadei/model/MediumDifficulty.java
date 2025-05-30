package it.filippo.casadei.model;

import java.util.Comparator;
import java.util.Optional;

/**
 * Implementazione della difficoltà "Media" per la CPU nel gioco della Briscola.
 * Questa strategia di gioco è più sofisticata e cerca di giocare in modo più tattico
 * rispetto alla difficoltà facile.
 * <p>
 * Stile di gioco:
 * <ul>
 *   <li>Come primo giocatore gioca le carte di minor valore possibile</li>
 *   <li>Come secondo giocatore cerca di prendere solo se ne vale la pena:
 *     <ul>
 *       <li>Prende se ha una carta più alta dello stesso seme</li>
 *       <li>Se la carta sul tavolo è un carico usa una briscola bassa</li>
 *       <li>Se la carta è il 3 di briscola gioca l'asso di briscola</li>
 *       <li>Negli altri casi, oppure se non ho la carta per prendere,
 *           gioca la carta di minor valore</li>
 *     </ul>
 *   </li>
 * </ul>
 */

public class MediumDifficulty implements CpuDifficulty {
    private static final int HIGH_POINTS = 10;

    // == METODI PUBBLICI ==
    /**
     * Metodo principale di scelta della carta.
     *
     * @param cpu  la CPU corrente
     * @param game lo stato attuale della partita
     * @return la carta scelta dalla CPU
     */
    @Override
    public Card chooseCard(Cpu cpu, BriscolaGame game) {
        Table table = game.getTable();
        Hand hand = cpu.getHand();
        Suit briscolaSuit = game.getBriscola().getSuit();
        Optional<Card> chosen;

        // Caso 1: Primo a giocare
        // Gioca la carta con meno valore
        if (table.getFirstPlayer().equals(cpu)) {
            chosen = getLessValuableCard(hand, briscolaSuit);
        }

        // Caso 2: Secondo a giocare
        else {
            Card cardOnTable = table.getFirstCard();

            // Caso 2.1: Il seme non è briscola
            if (!cardOnTable.getSuit().equals(briscolaSuit)) {

                // Gioca la carta dello stesso seme più alta
                chosen = hand.getCards().stream()
                        .filter(card -> card.getSuit().equals(cardOnTable.getSuit()))
                        .filter(c -> c.getRank().ordinal() > cardOnTable.getRank().ordinal())
                        .max(Comparator.comparingInt(Card::getPoints));

                // Se non ne ho osserva il valore della carta sul tavolo
                if (chosen.isEmpty()) {

                    // Se la carta sul tavolo non è un carico gioca la carta dal valore più basso
                    if (cardOnTable.getPoints() < HIGH_POINTS) {
                        chosen = getLessValuableCard(hand, briscolaSuit);
                    }

                    // Se la carta sul tavolo è un carico
                    else {
                        // Gioca la briscola più bassa
                        chosen = getLowestPointBriscola(hand, briscolaSuit);

                        // Se non ne ho gioca la carta più bassa
                        if (chosen.isEmpty()) {
                            chosen = getLowestPointNotBriscola(hand, briscolaSuit);
                        }
                    }
                }
            }

            // Caso 2.2: Il seme è briscola
            else {
                // Se è il 3 di briscola prendi con l'asso
                if (cardOnTable.getRank().equals(Rank.THREE)) {
                    chosen = hand.getCards().stream()
                            .filter(c -> c.getRank().equals(Rank.ACE) && c.getSuit().equals(briscolaSuit))
                            .findAny();

                    // Se non ho l'asso gioca la carta con meno valore
                    if (chosen.isEmpty()) {
                        chosen = getLessValuableCard(hand, briscolaSuit);
                    }
                }

                // Altrimenti gioca la carta con meno valore
                else {
                    chosen = getLessValuableCard(hand, briscolaSuit);
                }
            }
        }
        return chosen.orElseThrow(() -> new RuntimeException("No chosen Card"));
    }

    // == METODI HELPER ==
    private Optional<Card> getLessValuableCard(Hand hand, Suit briscolaSuit) {
        Optional<Card> card;

        card = getLowestPointNotBriscola(hand, briscolaSuit);

        if (card.isEmpty()) {
            card = getLowestPointBriscola(hand, briscolaSuit);
        }

        if (card.isEmpty()) {
            card = getLowestPointCard(hand, briscolaSuit);
        }

        return card;
    }

    private Optional<Card> getLowestPointCard(Hand hand, Suit briscolaSuit) {
        Optional<Card> card;

        card = hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints).thenComparing(c -> c.getRank().ordinal()));

        return card;
    }

    // carta + bassa non di briscola
    private Optional<Card> getLowestPointNotBriscola(Hand hand, Suit briscolaSuit) {
        Optional<Card> card;

        card = hand.getCards().stream()
                .filter(c -> !(c.getSuit().equals(briscolaSuit) && (c.getPoints() >= HIGH_POINTS)))
                .min(Comparator.comparingInt(Card::getPoints).thenComparing(c -> c.getRank().ordinal()));

        return card;
    }

    // carta + bassa di briscola
    private Optional<Card> getLowestPointBriscola(Hand hand, Suit briscolaSuit) {
        Optional<Card> card;

        card = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(briscolaSuit))
                .min(Comparator.comparingInt(Card::getPoints).thenComparing(c -> c.getRank().ordinal()));

        return card;
    }
}
