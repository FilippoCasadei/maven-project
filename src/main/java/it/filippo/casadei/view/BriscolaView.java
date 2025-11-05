package it.filippo.casadei.view;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.Player;

import java.util.Optional;


public interface BriscolaView {

    // ==  METODI DI INTERAZIONE E NOTIFICA ALL'OBSERVER ==

    /*
     * Imposta l'observer per notificare gli eventi di interazione.
     */
    void setObserver(BriscolaViewObserver observer);

    /**
     * Chiede all'utente di scegliere la difficoltà della CPU e notifica l'observer.
     */
    void chooseCpuDifficulty();

    /**
     * Chiede all'utente se vuole giocare di nuovo e notifica l'observer della scelta.
     */
    void askPlayAgain();

    /**
     * Abilita la selezione delle carte per il giocatore specificato e notifica l'observer.
     *
     * @param p il giocatore che può selezionare le carte
     */
    void enableCardSelection(Player p);

    // == METODI DI AGGIORNAMENTO ==
    
    /**
     * Avvia la visualizzazione della GUI.
     */
    void start();

    /**
     * Mostra la configurazione iniziale con la carta di briscola e i giocatori.
     *
     * @param briscolaCard la carta di briscola estratta
     * @param human        il giocatore umano
     * @param cpu          il giocatore CPU
     */
    void showSetup(Card briscolaCard, Player human, Player cpu);

    /**
     * Mostra la carta giocata da un giocatore.
     *
     * @param p          il giocatore che ha giocato la carta
     * @param playedCard la carta giocata
     */
    void showPlayedCard(Player p, Card playedCard);

    void clearTable();

    /**
     * Mostra la carta pescata da un giocatore.
     *
     * @param p         il giocatore che ha pescato
     * @param drawnCard la carta pescata
     */
    void showDraw(Player p, Card drawnCard);

    /**
     * Mostra informazioni sull'ultimo turno in cui vengono pescate le carte.
     * Permette all'utente di conoscere qual è l'ultimo turno prima di pescare la briscola.
     */
    void showLastDrawingTurn();

    /**
     * Mostra i punteggi finali dei giocatori.
     *
     * @param player1       il primo giocatore
     * @param player2       il secondo giocatore
     * @param player1Points i punti del primo giocatore
     * @param player2Points i punti del secondo giocatore
     */
    void showFinalScores(Player player1, Player player2, int player1Points, int player2Points);

    /**
     * Mostra il vincitore della partita.
     *
     * @param winner il giocatore vincitore, vuoto in caso di pareggio
     */
    void showWinner(Optional<Player> winner);

    

    /**
     * Nasconde la carta di briscola.
     */
    void hideBriscola();

    /**
     * Nasconde il mazzo.
     */
    void hideDeck();

    /**
     * Disabilita la selezione delle carte per l'utente.
     */
    void disableCardSelection();

    
}