package it.filippo.casadei.model;

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

    /**
     * Sceglie una carta da giocare secondo la strategia "Facile".
     * Se possibile gioca la briscola con il punteggio più alto,
     * altrimenti gioca la carta con il minor numero di punti.
     *
     * @param cpu  il giocatore CPU che deve scegliere la carta
     * @param game il riferimento alla partita corrente
     * @return la carta scelta per essere giocata
     */
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

    /**
     * Cerca nella mano la briscola con il punteggio più alto.
     *
     * @param hand         la mano del giocatore
     * @param briscolaSuit il seme di briscola della partita
     * @return Optional contenente la briscola con più punti, se presente
     */
    private Optional<Card> getHighestPointBriscola(Hand hand, Suit briscolaSuit) {
        return hand.getCards().stream()
                .filter(c -> c.isBriscola(briscolaSuit))
                .max(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()));
    }

    /**
     * Cerca nella mano la carta con il punteggio più basso.
     *
     * @param hand la mano del giocatore
     * @return la carta con il minor numero di punti
     * @throws IllegalStateException se la mano è vuota
     */
    private Card getLowestPointCard(Hand hand) {
        return hand.getCards().stream()
                .min(Comparator.comparingInt(Card::getPoints)
                        .thenComparing(c -> c.getRank().ordinal()))
                .orElseThrow(() -> new IllegalStateException("La mano è vuota!"));
    }
}