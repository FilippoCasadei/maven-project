package it.filippo.casadei.view;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GUI implementation of BriscolaView for a 1vs1 Briscola game.
 * Uses Swing for rendering players' hands, deck, briscola, and table.
 */
public class GuiBriscolaViewImpl implements BriscolaView {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 160;
    private static final boolean showCpuCards = true; // utile per il testing delle scelte della cpu

    private final JFrame frame;
    private final JPanel cpuPanel;
    private final JPanel humanPanel;
    private final JPanel tablePanel;
    private final JPanel deckPanel;
    private final JPanel briscolaPanel;
    private final JPanel sidePanel;
    private final AtomicReference<Card> selectedCard = new AtomicReference<>();
    private final Object lock = new Object();

    // == COSTRUTTORE ==
    public GuiBriscolaViewImpl() {
        frame = new JFrame("Briscola Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(createBackgroundPanel());
        frame.setLayout(new BorderLayout());

        // Inizialmente nascondi i panel (visibile solo il background)
        cpuPanel = createHandPanel(false);
        humanPanel = createHandPanel(true);
        cpuPanel.setVisible(false);
        humanPanel.setVisible(false);

        tablePanel = new JPanel(null); // absolute positioning for played cards
        tablePanel.setOpaque(false);
        tablePanel.setVisible(false);

        deckPanel = createDeckPanel();
        briscolaPanel = createBriscolaPanel();

        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setOpaque(false);
        sidePanel.add(deckPanel, BorderLayout.CENTER);
        sidePanel.add(briscolaPanel, BorderLayout.SOUTH);
        sidePanel.setVisible(false);
        briscolaPanel.setVisible(false);
        briscolaPanel.setVisible(false);

        frame.add(cpuPanel, BorderLayout.NORTH);
        frame.add(tablePanel, BorderLayout.CENTER);
        frame.add(humanPanel, BorderLayout.SOUTH);
        frame.add(sidePanel, BorderLayout.WEST);
    }

    // == METODI PUBBLICI ==
    /**
     * Shows the GUI and triggers the full session loop in the controller.
     */
    @Override
    public void start(BriscolaController controller) {
        frame.setVisible(true);
        controller.startGame();
    }

    /**
     * Called by controller after model.setupGame() to display deck, briscola, and initial hands.
     */
    @Override
    public void showSetup(Card briscolaCard, Player human, Player cpu) {
        // Reveal panels
        cpuPanel.setVisible(true);
        humanPanel.setVisible(true);
        tablePanel.setVisible(true);
        sidePanel.setVisible(true);
        briscolaPanel.setVisible(true);
        deckPanel.setVisible(true);

        // Populate view
        refreshHand(human);
        refreshHand(cpu);
        showBriscola(briscolaCard);
    }

    /**
     * Asks the user to choose CPU difficulty via dialog.
     */
    @Override
    public void chooseCpuDifficulty(Cpu cpu) {
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
        switch (choice) {
            case 0 -> cpu.setDifficulty(new EasyDifficulty());
            case 1 -> cpu.setDifficulty(new MediumDifficulty());
            case 2 -> cpu.setDifficulty(new HardDifficulty());
        }
    }

    @Override
    public void showBriscola(Card briscolaCard) {
        briscolaPanel.removeAll();
        briscolaPanel.add(createCardLabel(briscolaCard.toFileName()));
        briscolaPanel.revalidate();
        briscolaPanel.repaint();
    }

    @Override
    public Card requestCard(Player p) {
        // Refresh hands before request
        refreshHand(p);
        // Wait for user selection
        synchronized (lock) {
            try { lock.wait(); } catch (InterruptedException ignored) {}
        }
        return selectedCard.get();
    }

    @Override
    public void showPlayedCard(Player p, Card card) {
        boolean isHuman = p instanceof HumanPlayer;
        // La cpu aspetta del tempo prima di giocare una carta ("pensando")
        if (!isHuman) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        playCardOnTable(card, isHuman);
        refreshHand(p);
    }

    @Override
    public void showHandResult(Player winner, int pointsWon) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        String msg = winner.getName() + " vince la mano e guadagna " + pointsWon + " punti.";
//        JOptionPane.showMessageDialog(frame, msg, "Risultato Mano", JOptionPane.INFORMATION_MESSAGE);
        clearTable();
    }

    @Override
    public void showDraw(Player p, Card drawnCard) {
        deckPanel.removeAll();
        deckPanel.add(createCardLabel("back"));
        deckPanel.revalidate();
        deckPanel.repaint();
//        String msg = p.getName() + " pesca: " + (p instanceof HumanPlayer ? drawnCard : "******");
//        JOptionPane.showMessageDialog(frame, msg, "Pesca", JOptionPane.PLAIN_MESSAGE);
        refreshHand(p);
    }

    @Override
    public void showFinalScores(Player player1, Player player2, int p1points, int p2points) {
        String sb = "--- Punteggi finali ---\n" +
                player1.getName() + ": " + p1points + "\n" +
                player2.getName() + ": " + p2points + "\n";
        JOptionPane.showMessageDialog(frame, sb, "Punteggi Finali", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWinner(Optional<Player> winner) {
        String msg = "Vincitore: " + (winner.isPresent() ? winner.get().getName() : "Pareggio");
        JOptionPane.showMessageDialog(frame, msg, "Fine Partita", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public boolean askPlayAgain() {
        int result = JOptionPane.showConfirmDialog(
                frame,
                "Vuoi giocare un'altra partita?",
                "Rigioca?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    /**
     *
     */
    @Override
    public void hideBriscola() {
        briscolaPanel.setVisible(false);
    }

    /**
     *
     */
    @Override
    public void hideDeck() {
        deckPanel.setVisible(false);
    }

    /**
     *
     */
    @Override
    public void close() {
        System.exit(0);
    }

    // == METODI HELPER ==
    /**
     * Refreshes the hand panel for the given player with fixed card size.
     */
    private void refreshHand(Player p) {
        JPanel panel = (p instanceof HumanPlayer) ? humanPanel : cpuPanel;
        panel.removeAll();
        List<Card> cards = p.getHand().getCards();
        if (p instanceof Cpu) {
            for (Card c : cards) {
                String filename = showCpuCards ? c.toFileName() : "back";  // utile per il testing delle scelte della cpu
                panel.add(createCardLabel(filename));
            }
        } else {
            // Show actual cards for human
            for (Card c : cards) {
                JLabel lbl = createCardLabel(c.toFileName());
                lbl.addMouseListener(new MouseAdapter() {
                    @Override public void mouseClicked(MouseEvent e) {
                        selectedCard.set(c);
                        playCardOnTable(c, true);
                        synchronized (lock) { lock.notify(); }
                    }
                });
                panel.add(lbl);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Display a played card on the table for either player.
     */
    private void playCardOnTable(Card card, boolean isHuman) {
        JLabel lbl = createCardLabel(card.toFileName());
        int x = (tablePanel.getWidth() - CARD_WIDTH) / 2;
        int y = isHuman ? tablePanel.getHeight() - CARD_HEIGHT - 10 : 10;
        lbl.setBounds(x, y, CARD_WIDTH, CARD_HEIGHT);
        tablePanel.add(lbl);
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    /**
     * Clears the table of played cards
     */
    private void clearTable() {
        tablePanel.removeAll();
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    // == JPANEL ==
    private JPanel createBackgroundPanel() {
        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/background.png")));
        Image bg = bgIcon.getImage();
        return new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    private JPanel createHandPanel(boolean isHuman) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createDeckPanel() {
        JPanel panel = new JPanel(new GridLayout(1,1));
        panel.setOpaque(false);
        panel.add(createCardLabel("back"));
        return panel;
    }

    private JPanel createBriscolaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        panel.add(new JLabel("Briscola"), BorderLayout.NORTH);
        return panel;
    }

    // == JLABEL ==
    /**
     * Creates a JLabel with fixed card dimensions.
     */
    private JLabel createCardLabel(String filename) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/cards/" + filename + ".png")));
        Image img = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        JLabel lbl = new JLabel(new ImageIcon(img));
        lbl.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        return lbl;
    }
}
