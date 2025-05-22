package it.filippo.casadei.view;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.*;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ConsoleBriscolaViewImpl implements BriscolaView {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void start(BriscolaController controller) {
        System.out.println("Vuoi iniziare la partita? [Y/N]");
        String input = scanner.nextLine().trim().toUpperCase();

        while (!input.equals("Y") && !input.equals("N")) {
            System.out.println("Input non valido. Inserisci Y o N.");
            input = scanner.nextLine().trim().toUpperCase();
        }
        if (input.equals("Y")) {
            System.out.println("""
                ===========================================
                        🂡  BENVENUTO A BRISCOLA!  🂡
                ===========================================
                 Preparati a sfidare l'avversario a colpi
                    di carte e astuzia. Buona fortuna!
                ===========================================
            """);
            controller.startGame();
        } else {
            System.out.println("Hai scelto di non iniziare la partita. Uscita...");
            System.exit(0);  // chiude l'app
        }
    }

    /**
     * @param briscolaCard
     * @param human
     * @param cpu
     */
    @Override
    public void showSetup(Card briscolaCard, Player human, Player cpu) {
        System.out.println("Mescolando il mazzo...");
        System.out.println("Distribuendo le carte...");
    }

    @Override
    public void chooseCpuDifficulty(Cpu cpu) {
        System.out.println("""
            ------------------------------------------------------------
            Seleziona il livello di difficoltà della CPU:
            [1] Facile
            [2] Medio
            [3] Difficile
            ------------------------------------------------------------
            """);
        while (cpu.getStrategy() == null) {
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1":
                    System.out.println("Difficoltà scelta di " + cpu.getName() + ": Facile");
                    cpu.setStrategy(new EasyStrategy());
                    break;
                case "2":
                    System.out.println("Difficoltà scelta di " + cpu.getName() + ": Media");
                    cpu.setStrategy(new MediumStrategy());
                    break;
                case "3":
                    System.out.println("Difficoltà scelta di " + cpu.getName() + ": Difficile");
                    cpu.setStrategy(new HardStrategy());
                    break;
                default:
                    System.out.println("Input non valido. Riprova.");
            }
        }
    }

    @Override
    public void showBriscola(Card briscolaCard) {
        System.out.println("Briscola: " + briscolaCard);
    }

    @Override
    public Card requestCard(Player p) {
        System.out.printf("""
                ------------------------------------------------------------
                È il tuo turno, %s!
                Scegli una carta da giocare:
                ------------------------------------------------------------
                %n""", p.getName());
        // Mostra all'utente le carte disponibili
        List<Card> cards = p.getHand().getCards();
        for (int i = 0; i < cards.size(); i++) {
            System.out.println("[" + (i+1) + "] " + cards.get(i));
        }
        System.out.print(">>> Inserisci il numero della carta: ");
        Scanner scanner = new Scanner(System.in);

        int indexChosen = -1;

        // Continua a chiedere l'input fino a quando non è valido
        while (indexChosen < 0 || indexChosen >= p.getHand().getCards().size()) {
            try {
                // Input dell'indice della carta
                indexChosen = scanner.nextInt() - 1;

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
    }

    @Override
    public void showFinalScores(Map<Player, Integer> scores) {
        System.out.println("""
        ============================================================
                           PUNTEGGI FINALI
        ============================================================
        """);
        scores.forEach((p, pts) -> System.out.println(" - " + p.getName() + ": " + pts + " punti"));
    }

    @Override
    public void showWinner(Player winner) {
        if (winner != null) {
            System.out.println("\n👑 Il vincitore è: " + winner.getName() + "!");
        } else {
            System.out.println("\n🤝 La partita termina in pareggio!");
        }
        System.out.println("Grazie per aver giocato a Briscola!");
    }

    @Override
    public boolean askPlayAgain() {
        System.out.print("""

        Vuoi giocare un'altra partita? [Y/N]: 
        """);
        String input = scanner.nextLine().trim().toUpperCase();

        while (!input.equals("Y") && !input.equals("N")) {
            System.out.print("Input non valido. Inserisci Y oppure N: ");
            input = scanner.nextLine().trim().toUpperCase();
        }
        return input.equals("Y");
    }
}
