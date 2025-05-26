package it.filippo.casadei.app;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.*;
import it.filippo.casadei.view.*;

import java.util.Scanner;

/**
 * Punto di ingresso dell'applicazione Briscola.
 * Questa classe contiene il metodo {@code main}, che avvia il programma e gestisce la scelta
 * tra l'interfaccia grafica (GUI) e quella da console. Dopo aver selezionato la modalità,
 * vengono creati i due giocatori (uno umano, uno CPU), viene richiesta la difficoltà della CPU,
 * e viene avviato il controller del gioco.
 */
public class Main {

    /**
     * Metodo principale di avvio dell'applicazione.
     * Richiede all'utente di scegliere tra modalità console o GUI, istanzia i giocatori,
     * richiede il livello di difficoltà della CPU e avvia il controller della partita.
     *
     * @param args
     */
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

        // Crea i giocatori
        Player p1 = new HumanPlayer("Giocatore 1");
        Player p2 = new Cpu("CPU");

        // Imposta la difficoltà della CPU tramite la view
        view.chooseCpuDifficulty((Cpu)p2);

        // Avvia il controller del gioco
        new BriscolaController(view, p1, p2);
    }
}
