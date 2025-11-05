package it.filippo.casadei.model.player.cpu;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.card.Suit;
import it.filippo.casadei.model.player.Hand;

import java.util.Comparator;
import java.util.Optional;

/**
 * Implementazione della difficoltà "Facile" per la CPU nel gioco della Briscola.
 * Questa strategia di gioco è molto semplice e prevedibile, adatta per giocatori principianti.
 * <p>
 * Stile di gioco: La CPU cerca di vincere sempre ogni mano di gioco giocando una briscola.
 * Se non ha una briscola gioca in modo conservativo giocando la carta che vale meno punti.
 * Non distingue la giocata se è il primo o il secondo giocatore del turno.
 */
public class EasyDifficulty implements CpuDifficulty {

    // == METODI PUBBLICI ==

    @Override
    public Card chooseCard(GameContext context, Memory memory) {
        Hand hand = context.getCpuHand();
        Suit briscolaSuit = context.getBriscolaSuit();

        // Se possibile scegli la briscola che vale di più, altrimenti gioca la carta con meno punti
        return getHighestPointBriscola(hand, briscolaSuit)
            .orElseGet(() -> getLowestPointCard(hand));
    }

    // == METODI HELPER ==

    /**
     * Cerca nella mano la briscola con il punteggio più alto.
     */
    private Optional<Card> getHighestPointBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> c.isBriscola(briscolaSuit))
                .max(Comparator.comparingInt(Card::getPoints)
                        .thenComparingInt(c -> c.getRank().ordinal()));
    }

    /**
     * Restituisce la carta più bassa dalla mano.
     */
    private Card getLowestPointCard(Hand hand) {
        return hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparingInt(c -> c.getRank().ordinal()))
                .orElseThrow(() -> new IllegalStateException("La mano è vuota!"));
    }
}