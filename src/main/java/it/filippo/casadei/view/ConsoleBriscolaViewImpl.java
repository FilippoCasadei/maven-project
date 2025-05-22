package it.filippo.casadei.view;

import it.filippo.casadei.model.Card;
import it.filippo.casadei.model.Cpu;
import it.filippo.casadei.model.HumanPlayer;
import it.filippo.casadei.model.Player;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleBriscolaViewImpl implements BriscolaView {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void showBriscola(Card briscolaCard) {
        System.out.println("Briscola: " + briscolaCard);
    }

    @Override
    public Card requestCard(Player p) {
        // Mostra all'utente le carte disponibili
        System.out.println("Le tue carte "+p.getName()+": " + p.getHand().getCards());
        System.out.print("Scegli una carta da giocare (0-" + (p.getHand().getCards().size() - 1) + "): ");
        Scanner scanner = new Scanner(System.in);

        int indexChosen = -1;

        // Continua a chiedere l'input fino a quando non è valido
        while (indexChosen < 0 || indexChosen >= p.getHand().getCards().size()) {
            try {
                // Input dell'indice della carta
                indexChosen = scanner.nextInt();

                if (indexChosen < 0 || indexChosen >= p.getHand().getCards().size()) {
                    System.out.println("Indice fuori dal range. Riprova.");
                }
            } catch (InputMismatchException e) {
                // Gestisce l'errore nel caso l'utente inserisca un input non numerico
                System.out.println("Input non valido. Inserisci un numero intero.");
                scanner.nextLine(); // Pulisce il buffer dello scanner
            }
        }

        // Ritorna la carta scelta
        return p.getHand().getCards().get(indexChosen);
    }

    @Override
    public void showPlayedCard(Player p, Card card) {
        System.out.println(p.getName()+ " ha giocato " + card);
    }

    @Override
    public void showHandResult(List<Map.Entry<Player, Card>> order, Player winner, int points) {
        System.out.println(winner.getName() + " vince la mano e guadagna " + points + " punti.");
        printDividingLine();
    }

    @Override
    public void showDraw(Player p, Card drawnCard) {
        // Mostra la carta pescata dal giocatore
        if (p instanceof HumanPlayer) {
            System.out.println(p.getName() + " pesca: " + drawnCard);
        }
        // Non mostrare la carta giocata dalla CPU
        if (p instanceof Cpu) {
            System.out.println(p.getName() + " pesca: ******");
        }
        printDividingLine();
    }

    @Override
    public void showFinalScores(Map<Player, Integer> scores) {
        System.out.println("--- Punteggi finali ---");
        scores.forEach((p, pts) -> System.out.println(p.getName() + ": " + pts));
    }

    @Override
    public void showWinner(Player champion) {
        System.out.println("Vincitore: " + (champion != null ? champion.getName() : "Pareggio"));
    }

    private void printDividingLine() {
        System.out.println("############################################################");
    }
}
