package it.filippo.casadei.benchmark;

import it.filippo.casadei.model.*;

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
     * livelli di difficoltà e calcola le statistiche delle prestazioni per ogni livello.
     *
     * @param args
     */
    public static void main(String[] args) {
        int games = 100000;
        int winsPlayer1 = 0;
        int winsPlayer2 = 0;
        int draws = 0;

        for (int i = 0; i < games; i++) {
            Cpu cpu1 = new Cpu("CPU1");
            Cpu cpu2 = new Cpu("CPU2");
            cpu1.setDifficulty(new MediumDifficulty());
            cpu2.setDifficulty(new HardDifficulty());

            Deck deck = new Deck();
            Table table = new Table();
            BriscolaGame game = new BriscolaGame(cpu1, cpu2, deck, table);

            // ciclo di gioco automatico
            game.setupGame();
            while (!game.isGameOver()) {
                Player first = table.getFirstPlayer();
                Player second = table.getSecondPlayer();

                Card card1 = ((Cpu)first).chooseCard(game);
                game.playCard(first, card1);

                Card card2 = ((Cpu)second).chooseCard(game);
                game.playCard(second, card2);

                game.evaluateHand();
                table.clear();

                game.drawCard(table.getFirstPlayer());
                game.drawCard(table.getSecondPlayer());
            }

            // determina vincitore
            int p1Points = cpu1.getPoints();
            int p2Points = cpu2.getPoints();

            if (p1Points > p2Points) winsPlayer1++;
            else if (p2Points > p1Points) winsPlayer2++;
            else draws++;

            game.resetGame();
        }

        System.out.println("Risultati dopo " + games + " partite:");
        System.out.println("CPU1 vittorie: " + winsPlayer1/games + "%");
        System.out.println("CPU2 vittorie: " + winsPlayer2/games + "%");
        System.out.println("Pareggi: " + draws/games + "%");
    }

}
