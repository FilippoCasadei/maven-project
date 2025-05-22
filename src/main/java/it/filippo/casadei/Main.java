package it.filippo.casadei;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.*;
import it.filippo.casadei.view.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {


        /*
    SETUP GIOCATORI
    public void startGame(String difficulty) {
    Cpu cpu = new Cpu("CPU", null);

    cpu.setStrategy(DifficultyStrategyFactory.create(difficulty));

    Player human = new HumanPlayer("Giocatore");

    BriscolaGame game = new BriscolaGame(human, cpu, new Deck(), new Table());

    // poi lanci la partita
    }
         */
        Scanner scanner = new Scanner(System.in);
        BriscolaView view = null;

        while (view == null) {
            System.out.println("Vuoi giocare in Console (C) o GUI (G)?");
            String input = scanner.nextLine().trim().toUpperCase();

            switch (input) {
                case "G":
                    view = new GuiBriscolaViewImpl();
                    break;
                case "C":
                    view = new ConsoleBriscolaViewImpl();
                    break;
                default:
                    System.out.println("Input non valido. Riprova.");
            }
        }

        // Crea i giocatori (puoi personalizzare)
        Player p1 = new HumanPlayer("Giocatore 1");
        Player p2 = new Cpu("CPU", null);

        view.chooseCpuDifficulty((Cpu)p2);

        BriscolaController controller = new BriscolaController(view, p1, p2);
    }
}