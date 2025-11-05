package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.BriscolaGame;
import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.GameRules;
import it.filippo.casadei.model.Table;
import it.filippo.casadei.model.card.Rank;
import it.filippo.casadei.model.card.Suit;
import it.filippo.casadei.model.player.Hand;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementazione della difficoltà "Hard" per la CPU nel gioco della Briscola.
 * Questa strategia adotta una logica di gioco avanzata che tiene conto delle carte viste
 * e valuta le mosse in modo strategico per massimizzare i punti e ridurre i rischi.
 *
 * Caratteristiche principali:
 * <ul>
 *   <li>Memorizza tutte le carte giocate, in particolare i carichi (Assi e Tre)</li>
 *   <li>Gestisce in modo intelligente l'uso delle briscole e dei carichi</li>
 *   <li>Adatta il comportamento in base alle carte già viste e ai punti possibili</li>
 * </ul>
 *
 * Strategia di gioco:
 * <ol>
 *   <li>Quando è primo a giocare:
 *     <ul>
 *       <li>Gioca carte alte nei semi "sicuri" dove sono già usciti i carichi</li>
 *       <li>Conserva le briscole per catturare carte di valore elevato</li>
 *       <li>Usa i carichi strategicamente in base alle carte rimaste</li>
 *       <li>Evita di giocare il Tre di briscola se l'Asso è ancora in gioco</li>
 *     </ul>
 *   </li>
 *   <li>Quando è secondo a giocare:
 *     <ul>
 *       <li>Cerca di vincere immediatamente se può superare 60 punti</li>
 *       <li>Prova a vincere la mano con una carta dello stesso seme</li>
 *       <li>Usa briscole solo per catturare carte di valore</li>
 *       <li>Nell'ultima pescata può scegliere di perdere per ottenere la briscola</li>
 *       <li>In situazioni critiche tenta di bloccare la vittoria dell’avversario</li>
 *     </ul>
 *   </li>
 * </ol>
 */

public class HardDifficulty implements CpuDifficulty {

    @Override
    public Card chooseCard(GameContext context, Memory memory) {
        Hand hand = context.getCpuHand();
        Table table = context.getTable();
        Suit briscolaSuit = context.getBriscolaSuit();

        // Aggiorna la memoria all’inizio della decisione
        memory.updateFromContext(context);

        // Primo a giocare
        if (context.isCpuFirst()) {
            return chooseAsFirstPlayer(hand, briscolaSuit, memory, context);
        }
        // Secondo a giocare
        else {
            Card opponentCard = table.getFirstCard();
            Card chosen = chooseAsSecondPlayer(hand, opponentCard, briscolaSuit, memory, context);

            // Controllo finale: se l'avversario può vincere e superare 60 punti, tenta di prendere
            if (wouldCpuLose(chosen, opponentCard, briscolaSuit, memory.getOpponentPoints())) {
                Optional<Card> newChosen = chooseToNotLose(hand, opponentCard, briscolaSuit);
                if (newChosen.isPresent()) {
                    return newChosen.get();
                }
                // se non esiste alcuna carta vincente, tieni la scelta fatta in precedenza
        }

        return chosen;
        }
    }

    // == METODI HELPER ==

    /**
     * Logica di scelta quando la CPU è il primo a giocare.
     *
     * @param hand         la mano della CPU
     * @param briscolaSuit il seme di briscola
     * @param memory       la memoria dell'avversario e delle carte giocate dall'avversario prima di questo turno
     * @param context      il contesto della partita, contiene anche le carte giocate dall'avversario e la mano dell'avversario
     * @return la carta scelta
     */
    private Card chooseAsFirstPlayer(Hand hand, Suit briscolaSuit, Memory memory, GameContext context) {
        List<Card> cards = hand.getCards();

        // Caso: Ultimo turno di pesca
        if (context.isLastDraw()) {
            Card briscolaCard = context.getBriscolaCard();
            // se la briscola è un carico cerco di prenderla in ogni modo perdendo la mano (cerco di giocare tanti punti)   
            if (briscolaCard.isCarico()) {
                return getCardToLose(hand, briscolaSuit, memory);
            }
        }

        // Caso normale
        // 1. Alta non carico/non briscola di seme con 2 carichi usciti
        Optional<Card> highNonCaricoTwoOut = getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 2);
        if (highNonCaricoTwoOut.isPresent()) {
            return highNonCaricoTwoOut.get();
        }

        // 2. Alta non carico/non briscola di seme con 1 carico uscito
        Optional<Card> highNonCaricoOneOut = getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 1);
        if (highNonCaricoOneOut.isPresent()) {
            return highNonCaricoOneOut.get();
        }

        // 3. Alta non carico/non briscola qualsiasi
        Optional<Card> highNonCarico = getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 0);
        if (highNonCarico.isPresent()) {
            return highNonCarico.get();
        }

        // 4. Briscola più bassa, evitando il 3 se l'Asso è ancora disponibile
        boolean aceStillInDeck = isBriscolaAceStillInGame(briscolaSuit, memory);
        Optional<Card> lowestBriscola = cards.stream()
                .filter(c -> c.isBriscola(briscolaSuit))
                .filter(c -> !(c.getRank() == Rank.THREE && aceStillInDeck))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
        if (lowestBriscola.isPresent()) {
            return lowestBriscola.get();
        }

        // 5. Carico non briscola
        Optional<Card> caricoCard = chooseCaricoCard(cards, memory, briscolaSuit);
        if (caricoCard.isPresent()) {
            return caricoCard.get();
        }

        // 6. Altrimenti gioca la carta dal valore più basso possibile (dovrebbe essere sempre il Tre di briscola)
        return cards.stream()
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()))
                .get();
    }

    /**
     * Logica di scelta quando la CPU gioca per seconda.
     *
     * @param hand         la mano della CPU
     * @param firstCard    la carta giocata dall'avversario
     * @param briscolaSuit il seme di briscola
     * @param memory       la memoria dell'avversario e delle carte giocate dall'avversario prima di questo turno
     * @param context      il contesto della partita, contiene anche le carte giocate dall'avversario e la mano dell'avversario
     * @return la carta scelta
     */
    private Card chooseAsSecondPlayer(Hand hand, Card firstCard, Suit briscolaSuit, Memory memory, GameContext context) {
        int cpuPoints = memory.getMyPoints();
        List<Card> cards = hand.getCards();

        // Caso: Possibilità di vittoria
        // Se può vincere e superare 60 punti con una delle carte nella mano, giocala
        for (Card c : cards) {
            if (canCpuWinWith(cpuPoints, c, firstCard, briscolaSuit))
                return c;
        }

        // Caso: Ultimo turno di pesca
        if (context.isLastDraw()) {
            Card briscolaCard = context.getBriscolaCard();
            // se la briscola è un carico cerco di prenderla in ogni modo perdendo la mano (anche se avversario gioca un carico)   
            if (briscolaCard.isCarico()) {
                return getWorstCard(hand, briscolaSuit, memory);
            }
        }

        // Caso normale
        // 1. Primo ha giocato briscola, se è il tre prova a prenderlo con asso altrimenti gioca la peggiore
        if (firstCard.isBriscola(briscolaSuit)) {
            if (firstCard.getRank() == Rank.THREE) {
                return cards.stream()
                        .filter(c -> c.isBriscola(briscolaSuit) && c.getRank() == Rank.ACE)
                        .findAny()
                        .orElseGet(() -> getWorstCard(hand, briscolaSuit, memory));
            }
            return getWorstCard(hand, briscolaSuit, memory);
        }

        // 2. Primo non ha giocato briscola, prova a prendere con carta di seme più alta
        Optional<Card> sameSuit = cards.stream()
                .filter(c -> c.getSuit() == firstCard.getSuit())
                .filter(c -> c.getRank().ordinal() > firstCard.getRank().ordinal())
                .max(Comparator.comparingInt(Card::getPoints));
        if (sameSuit.isPresent()) return sameSuit.get();

        // 3. Primo ha giocato un carico, prova a prenderlo con la briscola che vale più punti
        //    ma non l'asso. Se di briscola ho solo l'asso lo gioco. Se non ho briscola gioco la peggiore
        if (firstCard.isCarico()) {
            Optional<Card> briscola = cards.stream()
                    .filter(c -> c.isBriscola(briscolaSuit) && c.getRank() != Rank.ACE)
                    .max(Comparator.comparingInt(Card::getPoints));
            if (briscola.isPresent()) {
                return briscola.get();
            }

            return cards.stream()
                    .filter(c -> c.isBriscola(briscolaSuit) && c.getRank() == Rank.ACE)
                    .findAny()
                    .orElseGet(() -> getWorstCard(hand, briscolaSuit, memory));
        }

        // 4. Se non posso prendere e devo giocare la carta peggiore controlla se conviene
        //    giocare una briscola per non lasciare punti all'avversario
        Card worst = getWorstCard(hand, briscolaSuit, memory);
        long briscolaCount = cards.stream().filter(c -> c.isBriscola(briscolaSuit)).count();

        // se concedo più di 5 punti e ho almeno 2 briscole, gioco la briscola più alta (non asso)
        if (firstCard.getPoints()+worst.getPoints() > 5 && briscolaCount >= 2) {
            return cards.stream()
                    .filter(c -> c.isBriscola(briscolaSuit) && !c.getRank().equals(Rank.ACE))
                    .max(Comparator.comparingInt(Card::getPoints))
                    .orElse(worst);
        }
        return worst;
    }


    /**
     * Determina la carta "peggiore" in mano secondo la strategia:
     * 1. non carico, non briscola
     * 2. preferibilmente di seme con 2 carichi rimasti
     * 3. altrimenti seme con 1 carico rimasto
     * 4. altrimenti la più bassa non carico/non briscola
     * 5. altrimenti la carta complessivamente più bassa
     *
     * @param hand         la mano della CPU
     * @param briscolaSuit il seme di briscola
     * @return l'opzionale della carta peggiore
     */
    private Card getWorstCard(Hand hand, Suit briscolaSuit, Memory memory) {
        List<Card> cards = hand.getCards();
        return getLowestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 2)
                .or(() -> getLowestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 1))
                // non carico/non briscola più bassa
                .or(() -> cards.stream()
                        .filter(c -> !c.isCarico())
                        .min(Comparator.comparingInt(Card::getPoints)
                                .thenComparing(c -> c.getRank().ordinal())))
                // carta complessivamente più bassa
                .or(() -> cards.stream()
                        .min(Comparator.comparingInt(Card::getPoints)
                                .thenComparing(c -> c.getRank().ordinal())))
                .get();
    }

    /**
     * Filtra la carta più alta non carico/non briscola che ha un certo numero di carichi già usciti.
     */
    private Optional<Card> getHighestNonCaricoNonBriscolaWithCarichi(
            List<Card> cards, Suit briscolaSuit, Memory memory, int carichiUsciti) {
        return cards.stream()
                .filter(c -> !c.isCarico() && !c.isBriscola(briscolaSuit))
                .filter(c -> memory.getCarichiAlreadyPlayedForSuit(c.getSuit()) == carichiUsciti)
                .max(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    /**
     * Filtra la carta più bassa non carico/non briscola che ha un certo numero di carichi già usciti.
     */
    private Optional<Card> getLowestNonCaricoNonBriscolaWithCarichi(
            List<Card> cards, Suit briscolaSuit, Memory memory, int carichiUsciti) {
        return cards.stream()
                .filter(c -> !c.isCarico() && !c.isBriscola(briscolaSuit))
                .filter(c -> memory.getCarichiAlreadyPlayedForSuit(c.getSuit()) == carichiUsciti)
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    /**
     * Controlla se l'Asso di briscola è ancora tra le carte non giocate.
     */
    private boolean isBriscolaAceStillInGame(Suit briscolaSuit, Memory memory) {
        return memory.getRemainingCards().stream()
                .anyMatch(c -> c.isBriscola(briscolaSuit) && c.getRank() == Rank.ACE);
    }

    /**
     * Sceglie un carico (3 o Asso) da giocare secondo la priorità:
     * 1. Tre di semi senza Asso rimasto
     * 2. Asso
     * 3. Tre
     */
    private Optional<Card> chooseCaricoCard(List<Card> cards, Memory memory, Suit briscolaSuit) {
        // 1) Tre sicuri (Asso dello stesso seme non è più in gioco)
        Optional<Card> threeWithNoAce = cards.stream()
                .filter(Card::isCarico)
                .filter(c -> !c.isBriscola(briscolaSuit))
                .filter(c -> c.getRank() == Rank.THREE)
                .filter(c -> memory.getRemainingCards().stream()
                        .noneMatch(rc -> rc.getSuit() == c.getSuit() && rc.getRank() == Rank.ACE))
                .findAny();
        if (threeWithNoAce.isPresent()) {
            return threeWithNoAce;
        }

        // 2) Asso
        Optional<Card> ace = cards.stream()
                .filter(Card::isCarico)
                .filter(c -> !c.isBriscola(briscolaSuit))
                .filter(c -> c.getRank() == Rank.ACE)
                .findAny();
        if (ace.isPresent()) {
            return ace;
        }

        // 3) Tre
        Optional<Card> threeAny = cards.stream()
                .filter(Card::isCarico)
                .filter(c -> !c.isBriscola(briscolaSuit))
                .filter(c -> c.getRank() == Rank.THREE)
                .findAny();
        if (threeAny.isPresent()) {
            return threeAny;
        }

        // Se non ho carichi non di briscola in mano ritorna un optional vuoto
        return Optional.empty();
    }

    /**
     * Simula se la Cpu può vincere la mano di gioco e superare
     * 60 punti totali.
     */
    private boolean canCpuWinWith(int cpuPoints, Card cpuCard, Card firstCard, Suit briscolaSuit) {
        int winner = GameRules.compareCards(firstCard, cpuCard, briscolaSuit);
        int possibleCpuPoints = cpuPoints + firstCard.getPoints() + cpuCard.getPoints();
        return (winner == 1) && (possibleCpuPoints > BriscolaGame.HALF_TOTAL_POINTS);
    }

    /**
     * Simula se l'avversario può vincere la mano di gioco
     * e superare 60 punti totali.
     */
    private boolean wouldCpuLose(Card cpuCard, Card firstCard, Suit briscolaSuit, int opponentPoints) {
        int winner = GameRules.compareCards(firstCard, cpuCard, briscolaSuit);
        int possibleOpponentPoints = opponentPoints + firstCard.getPoints() + cpuCard.getPoints();
        return (winner == 0) && (possibleOpponentPoints > BriscolaGame.HALF_TOTAL_POINTS);
    }

    /**
     * Sceglie la carta in modo da cercare in ogni modo di vincere la mano di gioco.
     */
    private Optional<Card> chooseToNotLose(Hand hand, Card firstCard, Suit briscolaSuit) {
        // provo con carta dello stesso seme che lo batte
        Optional<Card> card = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(firstCard.getSuit()))
                .filter(c -> c.getRank().ordinal() > firstCard.getRank().ordinal())
                .max(Comparator.comparingInt(Card::getPoints));
        if (card.isPresent()) {
            return card;
        }

        // provo con la briscola più alta che ho in mano
        card = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(briscolaSuit))
                .max(Comparator.comparingInt(c -> c.getRank().ordinal()));

        // ritorno l'eventuale carta trovata (oppure optional vuoto)
        return card;
    }

    /**
     * Sceglie la carta da giocare come primo giocatore in modo da cercare
     * di perdere la mano di gioco.
     */
    private Card getCardToLose(Hand hand, Suit briscolaSuit, Memory memory) {
        List<Card> cards = hand.getCards();

        // 1. Gioca un carico per cercarde di fare prendere l'avversario
        if (chooseCaricoCard(cards, memory, briscolaSuit).isPresent()) {
            return chooseCaricoCard(cards, memory, briscolaSuit).get();
        }
        
        // 2. Se non ho un carico gioca una carta non di briscola che vale più punti possibile
        if (getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 2).isPresent()) {
            return getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 2).get();
        }
        if (getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 1).isPresent()) {
            return getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 1).get(); 
        }
        if (getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 0).isPresent()) {
            return getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, memory, 0).get();
        }

        // 3. Altrimenti gioca la briscola più bassa possibile
        return cards.stream()
                .filter(c -> c.isBriscola(briscolaSuit))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()))
                .get();
    }
}
