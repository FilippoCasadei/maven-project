package it.filippo.casadei.model;

import java.util.Comparator;
import java.util.Optional;

public class EasyStrategy implements CpuStrategy{

    // === STILE DI GIOCO ===
    // Cpu cerca di vincere sempre ogni mano di gioco giocando una briscola.
    // Se non ha una briscola gioca in modo conservativo giocando la carta che vale meno punti.
    // Non distingue la giocata se è il primo o il secondo giocatore del turno.

    @Override
    public Card chooseCard(Cpu cpu, BriscolaGame game) {
        Hand hand = cpu.getHand();
        Suit briscolaSuit = game.getBriscola().getSuit();
        Optional<Card> chosen;

        chosen = getHighestPointBriscola(hand, briscolaSuit);

        if (chosen.isEmpty()) {
            chosen = getLowestPointCard(hand, briscolaSuit);
        }

        return chosen.orElseThrow(() -> new RuntimeException("No chosen Card"));
    }

    // restituisce la carta più bassa non di briscola
    private Optional<Card> getHighestPointBriscola(Hand hand, Suit briscolaSuit) {
        Optional<Card> card;

        card = hand.getCards().stream()
                .filter(c -> c.getSuit().equals(briscolaSuit))
                .max(Comparator.comparingInt(Card::getPoints).thenComparing(c -> c.getRank().ordinal()));

        return card;
    }

    private Optional<Card> getLowestPointCard(Hand hand, Suit briscolaSuit) {
        Optional<Card> card;

        card = hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints).thenComparing(c -> c.getRank().ordinal()));

        return card;
    }
}
