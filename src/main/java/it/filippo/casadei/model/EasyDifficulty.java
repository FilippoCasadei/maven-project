package it.filippo.casadei.model;

import java.util.Comparator;
import java.util.Optional;

public class EasyDifficulty implements CpuDifficulty {

    // === STILE DI GIOCO ===
    // Cpu cerca di vincere sempre ogni mano di gioco giocando una briscola.
    // Se non ha una briscola gioca in modo conservativo giocando la carta che vale meno punti.
    // Non distingue la giocata se è il primo o il secondo giocatore del turno.

    // == METODI PUBBLICI ==
    @Override
    public Card chooseCard(Cpu cpu, BriscolaGame game) {
        Hand hand = cpu.getHand();
        Suit briscolaSuit = game.getBriscola().getSuit();
        Optional<Card> chosen;

        // Se possibile scegli la briscola che vale di più, altrimenti gioca la carta con meno punti
        chosen = getHighestPointBriscola(hand, briscolaSuit);
        return chosen.orElseGet(() -> getLowestPointCard(hand));
    }

    // == METODI HELPER ==
    // restituisce la carta più bassa non di briscola
    private Optional<Card> getHighestPointBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> GameRules.isBriscola(c, briscolaSuit))
                .max(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    private Card getLowestPointCard(Hand hand) {
        return hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()))
                .orElseThrow(() -> new IllegalStateException("La mano è vuota!"));
    }
}
