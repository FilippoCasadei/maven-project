package it.filippo.casadei;

import it.filippo.casadei.model.*;

public class CpuDifficultyComparator {
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

            game.setupGame();

            // ciclo di gioco automatico
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
        System.out.println("CPU1 wins: " + winsPlayer1/games + "%");
        System.out.println("CPU2 wins: " + winsPlayer2/games + "%");
        System.out.println("Pareggi: " + draws/games + "%");
    }

}
