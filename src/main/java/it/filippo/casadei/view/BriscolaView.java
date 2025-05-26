package it.filippo.casadei.view;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.Card;
import it.filippo.casadei.model.Cpu;
import it.filippo.casadei.model.Player;

import java.util.Optional;


public interface BriscolaView {
    /** Fa partire la partita. */
    void start(BriscolaController controller);

    void showSetup(Card briscolaCard, Player human, Player cpu);

    /** Sceglie la difficoltà della CPU. */
    void chooseCpuDifficulty(Cpu cpu);

    /** Mostra la carta di briscola estratta all'inizio. */
    void showBriscola(Card briscolaCard);

    /** Richiede all'utente di scegliere una carta dalla sua mano. */
    Card requestCard(Player p);

    /** Mostra la carta giocata da un giocatore **/
    void showPlayedCard(Player p, Card playedCard);

    /** Mostra il risultato della mano: carte, vincitore e punti guadagnati. */
    void showHandResult(Player winner, int pointsWon);

    /** Mostra quale carta è stata pescata da un giocatore. */
    void showDraw(Player p, Card drawnCard);

    /** Mostra i punteggi finali di tutti i giocatori. */
    void showFinalScores(Player player1, Player player2, int player1Points, int player2Points);

    /** Mostra il vincitore della partita. */
    void showWinner(Optional<Player> winner);

    boolean askPlayAgain();

    void hideBriscola();

    void hideDeck();

    void close();
}
