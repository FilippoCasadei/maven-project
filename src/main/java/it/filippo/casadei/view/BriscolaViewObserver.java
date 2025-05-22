package it.filippo.casadei.view;

import it.filippo.casadei.model.Card;
import it.filippo.casadei.model.Player;

/**
 * Interfaccia che definisce gli eventi inviati dalla View al Controller.
 */
public interface BriscolaViewObserver {
    /** Evento: l'utente ha scelto una carta da giocare */
    void onCardChosen(Player player, Card card);

    /** Evento: inizializza o resetta la partita */
    void onNextTurn();

    /** Evento: l'utente vuole uscire dal gioco */
    void onQuit();
}
