package it.filippo.casadei.view;

import it.filippo.casadei.controller.BriscolaController;
import it.filippo.casadei.model.Card;
import it.filippo.casadei.model.Player;
import it.filippo.casadei.model.HumanPlayer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * GUI implementation of GameView for a 1vs1 Briscola game.
 * Uses Swing for rendering players' hands, deck, briscola, and table.
 */
public class GuiBriscolaViewImpl implements BriscolaView {

    // === Fields ===
    private final JFrame frame;
    private final JPanel cpuPanel;
    private final JPanel humanPanel;
    private final JPanel tablePanel;
    private final JPanel deckPanel;
    private final JPanel briscolaPanel;
    private final AtomicReference<Card> selectedCard = new AtomicReference<>();
    private final Object lock = new Object();

    // === Constructor & Initialization ===
    public GuiBriscolaViewImpl() {
        frame = new JFrame("Briscola Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(createBackgroundPanel());
        frame.setLayout(new BorderLayout());

        cpuPanel = createHandPanel(false);
        humanPanel = createHandPanel(true);
        tablePanel = new JPanel(null); // absolute positioning
        tablePanel.setOpaque(false);
        deckPanel = createDeckPanel();
        briscolaPanel = createBriscolaPanel();

        frame.add(cpuPanel, BorderLayout.NORTH);
        frame.add(tablePanel, BorderLayout.CENTER);
        frame.add(humanPanel, BorderLayout.SOUTH);

        JPanel sidePanel = new JPanel(new BorderLayout());
        sidePanel.setOpaque(false);
        sidePanel.add(deckPanel, BorderLayout.CENTER);
        sidePanel.add(briscolaPanel, BorderLayout.SOUTH);
        frame.add(sidePanel, BorderLayout.WEST);
    }

    /**
     * Shows the frame and optionally starts the game loop.
     * @param controller the GameController driving the game
     */
    public void start(BriscolaController controller) {
        frame.setVisible(true);
        controller.startGame();

    }

    // === GameView Methods ===

    @Override
    public void showBriscola(Card briscolaCard) {
        briscolaPanel.removeAll();
        briscolaPanel.add(createCardLabel(briscolaCard.toFileName()), BorderLayout.CENTER);
        briscolaPanel.revalidate();
        briscolaPanel.repaint();
    }

    // TODO: DA RIVEDERE. Inoltre ha più senso avere un metodo refreshHand per entrambi i giocatori che fa il refresh delle
    // carte in mano a ciascun giocatore dopo che il controller gioca o pesca una carta
    @Override
    public Card requestCard(Player p) {
        // Refresh CPU hand count to match human
        int cpuCount = p.getHand().getCards().size();
        cpuPanel.removeAll();
        for (int i = 0; i < cpuCount; i++) {
            cpuPanel.add(createCardLabel("back"));
        }
        cpuPanel.revalidate();
        cpuPanel.repaint();

        // Populate human hand with clickable cards
        humanPanel.removeAll();
        for (Card c : p.getHand().getCards()) {
            JLabel lbl = createCardLabel(c.toFileName());
            lbl.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedCard.set(c);
                    playCardOnTable(c, true);
                    synchronized (lock) { lock.notify(); }
                }
            });
            humanPanel.add(lbl);
        }
        humanPanel.revalidate();
        humanPanel.repaint();

        synchronized (lock) {
            try { lock.wait(); } catch (InterruptedException ignored) {}
        }
        return selectedCard.get();
    }

    @Override
    public void showHandResult(List<Map.Entry<Player, Card>> order, Player winner, int points) {
        StringBuilder sb = new StringBuilder();
        sb.append("Carte giocate:\n");
        order.forEach(e -> sb.append(e.getKey().getName()).append(": ")
                .append(e.getValue()).append("\n"));
        sb.append(winner.getName()).append(" vince la mano e guadagna ")
                .append(points).append(" punti.");
        JOptionPane.showMessageDialog(frame, sb.toString(), "Risultato Mano", JOptionPane.INFORMATION_MESSAGE);
        clearTable();
    }

    @Override
    public void showDraw(Player p, Card drawnCard) {
        deckPanel.removeAll();
        deckPanel.add(createCardLabel("back"));
        deckPanel.revalidate();
        deckPanel.repaint();
        String msg = p.getName() + " pesca: " +
                (p instanceof HumanPlayer ? drawnCard : "******");
        JOptionPane.showMessageDialog(frame, msg, "Pesca", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    public void showFinalScores(Map<Player, Integer> scores) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Punteggi finali ---\n");
        scores.forEach((player, pts) -> sb.append(player.getName())
                .append(": ").append(pts).append("\n"));
        JOptionPane.showMessageDialog(frame, sb.toString(), "Punteggi Finali", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showWinner(Player champion) {
        String msg = "Vincitore: " + (champion != null ? champion.getName() : "Pareggio");
        JOptionPane.showMessageDialog(frame, msg, "Fine Partita", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Display a played card on the table for either player.
     */
    @Override
    public void showPlayedCard(Player p, Card card) {
        boolean isBottom = isBottomPlayer(p); // Funzione helper
        playCardOnTable(card, isBottom);
    }

    private boolean isBottomPlayer(Player p) {
        // Supponiamo che il primo giocatore nella lista sia quello "in basso"
        return p instanceof HumanPlayer; // oppure usa un flag o una mappa se i ruoli possono variare
    }

    // === UI Component Creation ===

    private JPanel createBackgroundPanel() {
        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/background.png")));
        Image backgroundImage = bgIcon.getImage();
        return new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
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
        panel.setPreferredSize(new Dimension(177, 285));
        panel.add(new JLabel("Briscola"), BorderLayout.NORTH);
        return panel;
    }

    private JLabel createCardLabel(String filename) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/cards/" + filename + ".png")));
        // use actual image dimensions
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();
        Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        ImageIcon scaled = new ImageIcon(img);
        JLabel lbl = new JLabel(scaled);
        lbl.setPreferredSize(new Dimension(w, h));
        return lbl;
    }

    // === Table Interaction ===

    // isHuman serve solo per indicare la posizione (true = sud, false = nord)
    private void playCardOnTable(Card card, boolean isHuman) {
        JLabel lbl = createCardLabel(card.toFileName());
        int x = (tablePanel.getWidth() - lbl.getPreferredSize().width) / 2;
        int y = isHuman ? tablePanel.getHeight() - lbl.getPreferredSize().height - 10 : 10;
        lbl.setBounds(x, y, lbl.getPreferredSize().width, lbl.getPreferredSize().height);
        tablePanel.add(lbl);
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    private void clearTable() {
        tablePanel.removeAll();
        tablePanel.revalidate();
        tablePanel.repaint();
    }
}
