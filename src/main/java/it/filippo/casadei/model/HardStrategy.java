package it.filippo.casadei.model;

import java.util.*;


/**
 * Strategia "Hard" per il comportamento CPU nel gioco della Briscola.
 *<p>
 * Implementa una logica avanzata per scegliere la carta più adatta
 * sia quando la CPU gioca per prima che per seconda.
 *</p>
 */
public class HardStrategy implements CpuStrategy {

    // TODO: potrei utilizzare getMyPoints() e getOpponentPoints() dalla memoria invece che dai giocatori
    private final Memory memory = new Memory();

    /**
     * Metodo principale di scelta della carta.
     * Aggiorna prima la memoria di gioco, poi delega al metodo specifico
     * in base a chi è il primo giocatore sul tavolo.
     *
     * @param cpu  la CPU corrente
     * @param game lo stato attuale della partita
     * @return la carta scelta dalla CPU
     */
    @Override
    public Card chooseCard(Cpu cpu, BriscolaGame game) {
        updateMemory(cpu, game);
        Table table = game.getTable();
        Player opponent = game.getOpponent(cpu);
        Hand hand = cpu.getHand();
        Suit briscolaSuit = game.getBriscola().getSuit();

        Card chosen;
        if (table.getFirstPlayer().equals(cpu)) {
            // Caso: CPU gioca per prima
            chosen = chooseAsFirstPlayer(hand, briscolaSuit);
        } else {
            // Caso: CPU gioca per secondaPlayer opponent, Card cpuCard, Card firstCard, Suit briscolaSuit
            chosen = chooseAsSecondPlayer(cpu, game);
            Card firstCard = table.getFirstCard();
            // Se con la carta scelta l'avversario vincerebbe la partita gioca per vincere
            if (canOpponentWinWith(opponent, chosen, firstCard, briscolaSuit)) {
                Optional<Card> alternative = tryToWin(cpu, opponent, firstCard, briscolaSuit);
                if (alternative.isPresent()) return alternative.get();
            }
        }

        return chosen;
    }

    /**
     * Logica di scelta quando la CPU è il primo a giocare.
     * <ol>
     *   <li>Carta più alta non briscola e non carico (Asso/3) di seme con due carichi usciti.</li>
     *   <li>Carta più alta non briscola e non carico di seme con almeno un carico uscito.</li>
     *   <li>Carta più alta non briscola e non carico.</li>
     *   <li>Carta più bassa di briscola (escludendo il 3 se l'Asso di briscola è ancora in gioco).</li>
     *   <li>Un carico (3 o Asso) con priorità ai 3 di semi senza Asso rimasto, poi Assi, poi 3 a caso.</li>
     * </ol>
     *
     * @param hand         la mano della CPU
     * @param briscolaSuit il seme di briscola
     * @return la carta scelta
     */
    private Card chooseAsFirstPlayer(Hand hand, Suit briscolaSuit) {
        List<Card> cards = hand.getCards();

        // 1) Massima priorità: alta non carico/non briscola e seme con 2 carichi usciti
        Optional<Card> step1 = getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, 2);
        if (step1.isPresent()) {
            return step1.get();
        }

        // 2) Alta non carico/non briscola e seme con 1 carico uscito
        Optional<Card> step2 = getHighestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, 1);
        if (step2.isPresent()) {
            return step2.get();
        }

        // 3) Alta non carico/non briscola in generale
        Optional<Card> step3 = cards.stream()
                .filter(c -> !isCarico(c))
                .filter(c -> !c.getSuit().equals(briscolaSuit))
                .max(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
        if (step3.isPresent()) {
            return step3.get();
        }

        // 4) Bassa di briscola, escludi il 3 se l'asso è ancora disponibile
        boolean aceStillInDeck = isBriscolaAceStillInGame(briscolaSuit);
        Optional<Card> step4 = cards.stream()
                .filter(c -> c.getSuit().equals(briscolaSuit))
                .filter(c -> !(c.getRank() == Rank.THREE && aceStillInDeck))
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
        if (step4.isPresent()) {
            return step4.get();
        }

        // TODO: DA RIVEDERE
        // 5) Gioca un carico: priorità a Asso, poi a 3 di un seme in cui è uscito già l'asso, altrimenti 3 a caso
        Optional<Card> step5 = cards.stream()
                //
                .filter(c -> c.getRank().equals(Rank.ACE))
                .findAny()
                .or(() -> cards.stream()
                        .filter(c -> memory.getCarichiAlreadyPlayedForSuit(c.getSuit()) == 1)
                        .findAny())
                .or(() -> cards.stream()
                        .findAny());
        if (step5.isPresent()) {
            return step5.get();
        } else {
            throw new IllegalStateException("Non è stata scelta una carta per la CPU: " + hand.getCards());
        }


        // 5) Gioca un carico: priorità a 3 senza Asso residuo, poi Asso, altrimenti 3 a caso
        // TODO: DA RIVEDERE. L'ho modificato per essere più leggibile. Non serve più getCaricoPriority
//        List<Card> carichi = cards.stream()
//                .filter(this::isCarico)
//                .sorted(Comparator.comparingInt(this::getCaricoPriority))
//                .toList();
//        // Ordino per priorità interna: 0 = 3 preferito, 1 = Asso, 2 = altri 3
//        return carichi.getFirst();
    }

    /**
     * Logica di scelta quando la CPU gioca per seconda.
     * <ol>
     *   <li>Supero 60 punti con questa presa → gioco quella carta.</li>
     *   <li>Ultima pescata: se briscola è carico → gioco un carico; altrimenti carta peggiore.</li>
     *   <li>Caso normale (gestione di briscola/colore e condizione punti+briscole).</li>
     *   <li>Controllo finale: se avversario può vincere e superare 60, tentativo di bloccarlo.</li>
     * </ol>
     *
     * @param cpu  la CPU corrente
     * @param game lo stato di gioco attuale
     * @return la carta scelta
     */
    private Card chooseAsSecondPlayer(Cpu cpu, BriscolaGame game) {
        Hand hand = cpu.getHand();
        Suit briscolaSuit = game.getBriscola().getSuit();
        Card briscolaCard = game.getBriscola();
        Table table = game.getTable();
        Card firstCard = table.getFirstCard();

        // 1. Se posso vincere con una carta nella mia mano (supero i 60 punti) -> tira quella carta
        for (Card c : hand.getCards()) {
            if (canCpuWinWith(cpu, c, firstCard, briscolaSuit)) {
                return c;
            }
        }

        // 2. Se manca l'ultima pescata cerca di perdere apposta per prendere la briscola se ne vale la pena
        if (game.getDeck().getCards().size() == 1) {
            // provo a giocare un carico, altrimenti la carta peggiore
            if (isCarico(briscolaCard)) {
                return hand.getCards().stream()
                        .filter(this::isCarico)
                        .findAny()
                        .orElseGet(() -> getWorstCard(hand, briscolaSuit));
            } else {
                return getWorstCard(hand, briscolaSuit);
            }
        }

        // 3. Caso normale: primo ha giocato briscola
        if (firstCard.getSuit().equals(briscolaSuit)) {
            // Se è 3, provo con Asso
            if (firstCard.getRank().equals(Rank.THREE)) {
                return hand.getCards().stream()
                        .filter(c -> c.getRank().equals(Rank.ACE) && c.getSuit().equals(briscolaSuit))
                        .findAny()
                        .orElseGet(() -> getWorstCard(hand, briscolaSuit));
            }
            // Altrimenti carta peggiore
            return getWorstCard(hand, briscolaSuit);
        }

        // Primo non ha giocato briscola: provo rispondendo a seme con una carta più alta
        Optional<Card> sameSuit = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(firstCard.getSuit()))
                .filter(c -> c.getRank().ordinal() > firstCard.getRank().ordinal())
                .max(Comparator.comparingInt(Card::getPoints));
        if (sameSuit.isPresent()) {
            return sameSuit.get();
        }

        // Se la carta avversaria è un carico → gioco briscole
        if (isCarico(firstCard)) {
            // prendo briscola alta non Asso → Asso → altrimenti carta peggiore
            Optional<Card> brNonAsso = hand.getCards().stream()
                    .filter(c -> c.getSuit().equals(briscolaSuit) && !c.getRank().equals(Rank.ACE))
                    .max(Comparator.comparingInt(Card::getPoints));
            if (brNonAsso.isPresent()) return brNonAsso.get();

            Optional<Card> brAsso = hand.getCards().stream()
                    .filter(c -> c.getSuit().equals(briscolaSuit) && c.getRank().equals(Rank.ACE))
                    .findAny();
            if (brAsso.isPresent()) return brAsso.get();

            return getWorstCard(hand, briscolaSuit);
        }

        // Se NON è carico, condizione punti+briscole
        Card worst = getWorstCard(hand, briscolaSuit);
        long brCount = hand.getCards().stream().filter(c -> c.getSuit().equals(briscolaSuit)).count();
        if (firstCard.getPoints() + worst.getPoints() > 5 && brCount >= 2) {
            // gioco briscola alta non Asso se possibile
            return hand.getCards().stream()
                    .filter(c -> c.getSuit().equals(briscolaSuit) && !c.getRank().equals(Rank.ACE))
                    .max(Comparator.comparingInt(Card::getPoints))
                    .orElse(worst);
        }

        return worst;
    }

    /**
     * Aggiorna la memoria di gioco con i punti correnti e le carte rimanenti.
     */
    private void updateMemory(Cpu cpu, BriscolaGame game) {
        Player opp = game.getOpponent(cpu);

        memory.setMyPoints(cpu.getPoints());
        memory.setOpponentPoints(opp.getPoints());
        List<Card> remaining = new ArrayList<>(game.getDeck().getCards());
        // Aggiungo alle carte ancora nel deck anche quelle nella mano dell'avversario
        remaining.addAll(opp.getHand().getCards());
        memory.setRemainingCards(remaining);
    }

    /**
     * Applica il controllo finale per bloccare la vittoria avversaria
     * e restituisce la carta alternativa se necessario.
     */
    private Optional<Card> tryToWin(Player cpu, Player opponent, Card firstCard, Suit briscolaSuit) {
        Hand hand = cpu.getHand();

        // provo con carta dello stesso seme che lo batte
        Optional<Card> card = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(firstCard.getSuit()))
                .filter(c -> c.getRank().ordinal() > firstCard.getRank().ordinal())
                .max(Comparator.comparingInt(Card::getPoints));
        if (card.isPresent()) return card;

        // provo con la briscola più alta che ho in mano
        card = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(briscolaSuit))
                .min(Comparator.comparingInt(c -> c.getRank().ordinal()));
        return card;
    }

    /**
     * Determina la carta "peggiore" in mano secondo la strategia:
     * - non carico, non briscola
     * - preferibilmente di seme con 2 carichi rimasti
     * - altrimenti seme con 1 carico rimasto
     * - altrimenti la più bassa non carico/non briscola
     * - altrimenti la carta complessivamente più bassa
     *
     * @param hand         la mano della CPU
     * @param briscolaSuit il seme di briscola
     * @return l'opzionale della carta peggiore
     */
    private Card getWorstCard(Hand hand, Suit briscolaSuit) {
        List<Card> cards = hand.getCards();
        return getLowestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, 2)
                .or(() -> getLowestNonCaricoNonBriscolaWithCarichi(cards, briscolaSuit, 1))
                .or(() -> cards.stream()
                        .filter(c -> !isCarico(c))
                        .min(Comparator.comparingInt(Card::getPoints)
                                .thenComparing(c -> c.getRank().ordinal())))
                .or(() -> cards.stream()
                        .min(Comparator.comparingInt(Card::getPoints)
                                .thenComparing(c -> c.getRank().ordinal())))
                .get();
    }

    /**
     * Filtra la carta più alta non carico/non briscola con esattamente <code>carichiUsciti</code> già usciti.
     */
    private Optional<Card> getHighestNonCaricoNonBriscolaWithCarichi(
            List<Card> cards, Suit briscolaSuit, int carichiUsciti) {
        return cards.stream()
                .filter(c -> !isCarico(c))
                .filter(c -> !c.getSuit().equals(briscolaSuit))
                .filter(c -> memory.getCarichiAlreadyPlayedForSuit(c.getSuit()) == carichiUsciti)
                .max(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    /**
     * Filtra la carta più bassa non carico/non briscola con esattamente <code>carichiUsciti</code> già usciti.
     */
    private Optional<Card> getLowestNonCaricoNonBriscolaWithCarichi(
            List<Card> cards, Suit briscolaSuit, int carichiUsciti) {
        return cards.stream()
                .filter(c -> !isCarico(c))
                .filter(c -> !c.getSuit().equals(briscolaSuit))
                .filter(c -> memory.getCarichiAlreadyPlayedForSuit(c.getSuit()) == carichiUsciti)
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    /**
     * Ritorna true se la carta è "carico" (Asso o 3).
     */
    private boolean isCarico(Card c) {
        return c.getRank() == Rank.ACE || c.getRank() == Rank.THREE;
    }

    // TODO: NON SERVE PIU
    /**
     * Priorità di scelta per i carichi: 0=3 senza Asso, 1=Asso, 2=altri 3.
     */
    private int getCaricoPriority(Card c) {
        if (c.getRank() == Rank.THREE && memory.getCarichiAlreadyPlayedForSuit(c.getSuit()) >= 1) {
            return 0;
        } else if (c.getRank() == Rank.ACE) {
            return 1;
        }
        return 2;
    }

    /**
     * Controlla se l'Asso di briscola è ancora tra le carte non giocate.
     */
    private boolean isBriscolaAceStillInGame(Suit briscolaSuit) {
        return memory.getRemainingCards().stream()
                .anyMatch(c -> c.getSuit().equals(briscolaSuit) && c.getRank() == Rank.ACE);
    }

    /**
     * Simula se la Cpu può vincere la carta <code>cpuCard</code> contro
     * la carta dell'avversario e superare 60 punti totali.
     */
    private boolean canCpuWinWith(Player cpu, Card cpuCard, Card firstCard, Suit briscolaSuit) {
        int winner = GameRules.compareCards(firstCard, cpuCard, briscolaSuit);
        int possibleCpuPoints = cpu.getPoints() + firstCard.getPoints() + cpuCard.getPoints();
        return (winner == 1) && (possibleCpuPoints > BriscolaGame.HALF_TOTAL_POINTS);
    }

    /**
     * Simula se l'avversario può vincere la carta <code>cpuCard</code> contro
     * la carta della cpu e superare 60 punti totali.
     */
    private boolean canOpponentWinWith(Player opponent, Card cpuCard, Card firstCard, Suit briscolaSuit) {
        int winner = GameRules.compareCards(firstCard, cpuCard, briscolaSuit);
        int possibleOpponentPoints = opponent.getPoints() + firstCard.getPoints() + cpuCard.getPoints();
        return (winner == 0) && (possibleOpponentPoints > BriscolaGame.HALF_TOTAL_POINTS);
    }
}
