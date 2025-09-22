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

    // == METODI PUBBLICI ==

    /**
     * Determina quale carta giocare per la CPU in base allo stato corrente della partita e alla logica di difficoltà.
     *
     * @param cpu  il giocatore CPU che deve fare la mossa
     * @param game lo stato corrente della partita 
     * @return la carta selezionata dalla CPU da giocare
     */
    @Override
    public Card chooseCard(Cpu cpu, BriscolaGame game) {
        Table table = game.getTable();
        Hand hand = cpu.getHand();
        Suit briscolaSuit = game.getBriscola().getSuit();

        // Caso 1: Primo a giocare
        // Gioca la carta con meno valore
        if (table.getFirstPlayer().equals(cpu)) {
            return getLessValuableCard(hand, briscolaSuit);
        }

        // Caso 2: Secondo a giocare
        Card cardOnTable = table.getFirstCard();

        // Caso 2.1: Il seme non è briscola
        if (!cardOnTable.isBriscola(briscolaSuit)) {

            // Gioca la carta dello stesso seme più alta
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

            // Se la carta sul tavolo è un carico gioca la briscola più bassa
            chosen = getLowestPointBriscola(hand, briscolaSuit);
            if (chosen.isPresent()) {
                return chosen.get();
            }

            // Se non ne ho gioca la carta più bassa non di briscola
            return getLowestPointCard(hand);
        }

        // Caso 2.2: Il seme è briscola

        // Se è il 3 di briscola prendi con l'asso
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


    // == METODI HELPER ==

    /**
     * Restituisce la carta meno preziosa dalla mano, considerando il seme di briscola.
     * Il metodo cerca di selezionare la carta col minor valore che sia:
     * 1. Non di briscola
     * 2. La briscola col minor punteggio se non ci sono carte non di briscola
     * 3. In ultima analisi la carta col minor punteggio in assoluto
     *
     * @param hand         la mano di carte da cui selezionare la carta meno preziosa
     * @param briscolaSuit il seme designato come briscola per la partita
     * @return la carta meno preziosa secondo la logica definita
     * @throws RuntimeException se la mano è vuota
     */
    private Card getLessValuableCard(Hand hand, Suit briscolaSuit) {
        return getLowestPointNotBriscola(hand, briscolaSuit)
                .or(() -> getLowestPointBriscola(hand, briscolaSuit))
                .or(() -> Optional.of(getLowestPointCard(hand)))
                .get();
    }

    /**
     * Determina la carta dalla mano data con il valore di punti più basso.
     * Se più carte hanno lo stesso valore di punti, viene selezionata la carta con il rango più basso.
     * Se la mano è vuota, viene lanciata una RuntimeException.
     *
     * @param hand la mano di carte da analizzare
     * @return la carta con il valore di punti più basso, o il rango più basso in caso di parità
     * @throws RuntimeException se la mano è vuota
     */
    private Card getLowestPointCard(Hand hand) {
        return hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()))
                .orElseThrow(() -> new RuntimeException("La mano è vuota."));
    }

    /**
     * Cerca la carta con il minor punteggio dalla mano che non sia un carico (carte di alto valore)
     * e che non sia del seme di briscola.
     * Se più carte soddisfano i criteri, viene selezionata quella con il rank più basso.
     *
     * @param hand         la mano da cui selezionare la carta
     * @param briscolaSuit il seme designato come briscola per la partita
     * @return un Optional contenente la carta con il minor punteggio che non sia un carico
     * né del seme di briscola, oppure un Optional vuoto se non esiste tale carta
     */
    private Optional<Card> getLowestPointNotBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> !c.isCarico())
                .filter(c -> !c.isBriscola(briscolaSuit))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    /**
     * Cerca e restituisce dalla mano data la carta di briscola con il minor punteggio.
     * Tra le carte del seme di briscola, seleziona quella con il punteggio più basso.
     * In caso di parità di punteggio, viene scelta la carta con il rank più basso.
     *
     * @param hand         la mano di carte da analizzare
     * @param briscolaSuit il seme designato come briscola per la partita
     * @return un Optional contenente la carta di briscola con il minor punteggio,
     * oppure un Optional vuoto se non esistono carte di briscola nella mano
     */
    private Optional<Card> getLowestPointBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> !c.isCarico())
                .filter(c -> c.isBriscola(briscolaSuit))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }
}
