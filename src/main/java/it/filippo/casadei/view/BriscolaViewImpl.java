package it.filippo.casadei.view;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.*;
import it.filippo.casadei.model.player.cpu.CpuDifficulty;
import it.filippo.casadei.model.player.cpu.EasyDifficulty;
import it.filippo.casadei.model.player.cpu.HardDifficulty;
import it.filippo.casadei.model.player.cpu.MediumDifficulty;
import it.filippo.casadei.view.panels.*;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Classe View dell'architettura MVC.
 * 
 * Implementazione GUI di BriscolaView.
 */
public class BriscolaViewImpl implements BriscolaView {
    private static final boolean SHOW_CPU_CARDS = false;

    private BriscolaViewObserver observer;

    private final JFrame frame;
    private final HandPanel cpuHandPanel;
    private final HandPanel humanHandPanel;
    private final TablePanel tablePanel;
    private final DeckPanel deckPanel;
    private final BriscolaPanel briscolaPanel;
    private final JPanel sidePanel;

    public BriscolaViewImpl() {
        frame = new JFrame("Briscola Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);

        BackgroundPanel background = new BackgroundPanel("/background.png");
        frame.setContentPane(background);
        frame.setLayout(new BorderLayout());

        cpuHandPanel = new HandPanel(false);
        humanHandPanel = new HandPanel(true);
        tablePanel = new TablePanel();
        deckPanel = new DeckPanel();
        briscolaPanel = new BriscolaPanel();

        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setOpaque(false);
        sidePanel.add(deckPanel, BorderLayout.CENTER);
        sidePanel.add(briscolaPanel, BorderLayout.SOUTH);

        frame.add(cpuHandPanel, BorderLayout.NORTH);
        frame.add(tablePanel, BorderLayout.CENTER);
        frame.add(humanHandPanel, BorderLayout.SOUTH);
        frame.add(sidePanel, BorderLayout.WEST);

    }

    // ==  METODI DI INTERAZIONE E NOTIFICA ALL'OBSERVER ==

    @Override
    public void setObserver(BriscolaViewObserver observer) {
        this.observer = observer;
        // passa l'observer anche al pannello della mano umana per gestire la selezione delle carte
        humanHandPanel.setObserver(observer);
    }

    @Override
    public void chooseCpuDifficulty() {
        frame.setVisible(true);
        String[] options = {"Facile", "Medio", "Difficile"};
        int choice = JOptionPane.showOptionDialog(
                frame,
                "Seleziona il livello di difficoltà della CPU:",
                "Difficoltà CPU",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        CpuDifficulty difficulty;
        switch (choice) {
            case 0:
                difficulty = new EasyDifficulty();
                break;
            case 1:
                difficulty = new MediumDifficulty();
                break;
            case 2:
                difficulty = new HardDifficulty();
                break;
            default:
                difficulty = new EasyDifficulty();
                break;
        };

        observer.createModel(difficulty);
    }

    @Override
    public void askPlayAgain() {
        int result = JOptionPane.showConfirmDialog(frame,
                "Vuoi giocare un'altra partita?",
                "Rigioca?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        boolean playAgain = (result == JOptionPane.YES_OPTION);
        // Notifica l'observer della scelta dell'utente
        observer.restartGame(playAgain);
    }

    @Override
    public void enableCardSelection(Player p) {
        if (!(p instanceof Human)) return;
        humanHandPanel.enableCardSelection(p);
    }

    // == METODI DI AGGIORNAMENTO ==

    @Override
    public void start() {
        frame.setVisible(true);
    }

    @Override
    public void showSetup(Card briscolaCard, Player human, Player cpu) {
        setAllPanelsVisible(true);
        cpuHandPanel.updateHand(cpu.getHand(), SHOW_CPU_CARDS);
        humanHandPanel.updateHand(human.getHand(), true);
        briscolaPanel.show(briscolaCard);
    }

    @Override
    public void disableCardSelection() {
        humanHandPanel.disableCardSelection();
    }

    @Override
    public void showPlayedCard(Player p, Card card) {
        boolean isHuman = p instanceof Human;

        // Mostra la carta sul tavolo
        tablePanel.playCard(card, isHuman);

        if (!isHuman) {
            cpuHandPanel.updateHand(p.getHand(), SHOW_CPU_CARDS);
        } else {
            humanHandPanel.updateHand(p.getHand(), true);
        }
    }

    @Override
    public void clearTable() {
        tablePanel.clear();
    }

    @Override
    public void showDraw(Player p, Card drawnCard) {
        deckPanel.showBack();

        if (p instanceof Human) {
            humanHandPanel.updateHand(p.getHand(), true);
        } else {
            cpuHandPanel.updateHand(p.getHand(), SHOW_CPU_CARDS);
        }
    }

    @Override
    public void showLastDrawingTurn() {
        JOptionPane.showMessageDialog(frame,
                "Chi perde la prossima mano di gioco pescherà la briscola",
                "Ultimo turno di pesca",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showFinalScores(Player player1, Player player2, int p1points, int p2points) {
        String message = String.format(
                "--- Punteggi finali ---\n%s: %d\n%s: %d",
                player1.getName(), p1points,
                player2.getName(), p2points
        );
        JOptionPane.showMessageDialog(frame, message,
                "Punteggi Finali", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWinner(Optional<Player> winner) {
        String msg = winner.isPresent()
                ? "Vincitore: " + winner.get().getName()
                : "Pareggio!";
        JOptionPane.showMessageDialog(frame, msg,
                "Fine Partita", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void hideBriscola() {
        briscolaPanel.setVisible(false);
    }

    @Override
    public void hideDeck() {
        deckPanel.setVisible(false);
    }

    private void setAllPanelsVisible(boolean visible) {
        cpuHandPanel.setVisible(visible);
        humanHandPanel.setVisible(visible);
        tablePanel.setVisible(visible);
        sidePanel.setVisible(visible);
    }

}