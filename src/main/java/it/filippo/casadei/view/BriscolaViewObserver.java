package it.filippo.casadei.view;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.cpu.CpuDifficulty;
import it.filippo.casadei.model.player.Player;


public interface BriscolaViewObserver {

    /*
     * Gestisce la mossa del giocatore umano dopo che una carta è stata scelta.
     */
    void humanPlaysCard(Player humanPlayer, Card card);

    /*
     * Gestisce la creazione del model dopo che la difficoltà della CPU è stata scelta.
     */
    void createModel(CpuDifficulty difficulty);  

    /*
     * Gestisce la richiesta di riavvio del gioco dopo la fine della partita.
     */
    void restartGame(boolean playAgain);
}
