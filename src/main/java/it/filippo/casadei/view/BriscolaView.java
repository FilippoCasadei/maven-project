package it.filippo.casadei.view;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.Card;
import it.filippo.casadei.model.Cpu;
import it.filippo.casadei.model.Player;

import java.util.Optional;


public interface BriscolaView {
    /**
     * Avvia la partita.
     *
     * @param controller il controller che gestisce la partita
     */
    void start(BriscolaController controller);

    /**
     * Mostra la configurazione iniziale con la carta di briscola e i giocatori.
     *
     * @param briscolaCard la carta di briscola estratta
     * @param human        il giocatore umano
     * @param cpu          il giocatore CPU
     */
    void showSetup(Card briscolaCard, Player human, Player cpu);

    /**
     * Permette di scegliere la difficoltà della CPU.
     *
     * @param cpu la CPU di cui impostare la difficoltà
     */
    void chooseCpuDifficulty(Cpu cpu);

    /**
     * Mostra la carta di briscola estratta per la partita.
     *
     * @param briscolaCard la carta di briscola da mostrare
     */
    void showBriscola(Card briscolaCard);

    /**
     * Chiede all'utente di scegliere una carta dalla propria mano.
     *
     * @param p il giocatore che deve scegliere la carta
     * @return la carta scelta dal giocatore
     */
    Card requestCard(Player p);

    /**
     * Mostra la carta giocata da un giocatore.
     *
     * @param p          il giocatore che ha giocato la carta
     * @param playedCard la carta giocata
     */
    void showPlayedCard(Player p, Card playedCard);

    /**
     * Mostra il risultato della mano con il vincitore e i punti ottenuti.
     *
     * @param winner    il giocatore che ha vinto la mano
     * @param pointsWon i punti ottenuti nella mano
     */
    void showHandResult(Player winner, int pointsWon);

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
     * Chiede se si vuole giocare un'altra partita.
     *
     * @return true se si vuole giocare ancora, false altrimenti
     */
    boolean askPlayAgain();

    /**
     * Nasconde la carta di briscola.
     */
    void hideBriscola();

    /**
     * Nasconde il mazzo.
     */
    void hideDeck();

    /**
     * Chiude la view.
     */
    void close();
}