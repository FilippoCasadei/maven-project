package it.filippo.casadei;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.*;
import it.filippo.casadei.view.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
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
        Player p2 = new Cpu("CPU");

        view.chooseCpuDifficulty((Cpu)p2);

        BriscolaController controller = new BriscolaController(view, p1, p2);
    }
}