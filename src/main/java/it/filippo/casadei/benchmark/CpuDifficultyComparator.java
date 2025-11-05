package it.filippo.casadei.benchmark;

import it.filippo.casadei.model.*;
import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.cpu.Cpu;
import it.filippo.casadei.model.player.cpu.GameContext;
import it.filippo.casadei.model.player.cpu.HardDifficulty;
import it.filippo.casadei.model.player.Player;

/**
 * La classe CpuDifficultyComparator funge da strumento di simulazione per confrontare le
 * prestazioni dei diversi livelli di difficoltà della CPU in un ambiente di gioco automatizzato.
 * Esegue un numero specificato di partite tra due giocatori CPU, ognuno con diverse
 * strategie di difficoltà, e raccoglie risultati statistici per determinare l'efficacia
 * di ogni configurazione.
 * <p>
 * Questa classe è particolarmente utile per testare e valutare il comportamento e l'esecuzione
 * delle strategie dei giocatori CPU in una partita di Briscola.
 * <p>
 * Le responsabilità della classe includono:
 * - Configurare e inizializzare partite con diverse difficoltà CPU.
 * - Automatizzare e gestire il ciclo di gioco, inclusa la selezione e il gioco delle carte.
 * - Confrontare e registrare i risultati delle partite tra i giocatori CPU.
 * - Restituire le statistiche di prestazione (percentuali di vittoria e di pareggio) per ogni CPU.
 * <p>
 * L'esecuzione è gestita interamente all'interno del metodo main. È possibile simulare
 * un gran numero di partite in un ciclo, rendendolo adatto per l'analisi statistica
 * delle strategie CPU.
 */
public class CpuDifficultyComparator {
    /**
     * Simula una serie di partite automatizzate tra due giocatori CPU con differenti
     * livelli di difficoltà e calcola la percentuale di partite vinte per ogni CPU e
     * quella di pareggi.
     *
     * @param args
     */
    public static void main(String[] args) {
        final int GAMES_NUM = 100000;  // numero scelto di partite da simulare
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;
        int draws = 0;

        for (int i = 0; i < GAMES_NUM; i++) {
            // Creazione 2 cpu
            Cpu cpu1 = new Cpu("CPU1", new HardDifficulty());
            Cpu cpu2 = new Cpu("CPU2", new HardDifficulty());

            // Creazione del model
            BriscolaGame game = new BriscolaGame(cpu1, cpu2);

            // ciclo di gioco automatico
            game.setupGame();
            while (!game.isGameOver()) {
                Table table = game.getTable();
                Player first = table.getFirstPlayer();
                Player second = table.getSecondPlayer();

                // Aggiorna contesto di gioco per la prima CPU a giocare
                GameContext context1 = new GameContext(
                    first.getHand(),
                    table,
                    game.getBriscola(),
                    true, 
                    game.getDeck().size() == 2 || game.getDeck().size() == 1  // ultimo turno di pesca 
                );

                // Primo giocatore gioca la carta
                Card card1 = ((Cpu)first).chooseCard(context1);
                game.playCard(first, card1);

                // Aggiorna contesto di gioco per la seconda CPU a giocare
                GameContext context2 = new GameContext(
                    second.getHand(),
                    table,
                    game.getBriscola(),
                    false, 
                    game.getDeck().size() == 2 || game.getDeck().size() == 1  // ultimo turno di pesca 
                );

                // Secondo giocatore gioca la carta
                Card card2 = ((Cpu)second).chooseCard(context2);
                game.playCard(second, card2);

                // Valuta la mano di gioco
                game.evaluateHand();
                
                // Pulisce il tavolo per la mano successiva
                table.clear();

                // I giocatori pescano una carta
                game.drawCard(table.getFirstPlayer());
                game.drawCard(table.getSecondPlayer());
            }

            // Determina vincitore
            int p1Points = cpu1.getPoints();
            int p2Points = cpu2.getPoints();

            // Aggiorna statistiche di vittoria o pareggio
            if (p1Points > p2Points) winsPlayer1++;
            else if (p2Points > p1Points) winsPlayer2++;
            else draws++;

            // Resetta il gioco per la prossima partita
            game.resetGame();
        }

        // Stampa i risultati finali
        System.out.println("Risultati dopo " + GAMES_NUM + " partite:");
        System.out.println("CPU1 vittorie: " + String.format("%.2f%%", 100.0 * winsPlayer1 / GAMES_NUM));
        System.out.println("CPU2 vittorie: " + String.format("%.2f%%", 100.0 * winsPlayer2 / GAMES_NUM));
        System.out.println("Pareggi: " + String.format("%.2f%%", 100.0 * draws / GAMES_NUM));
    }

}
