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

//TODO: C'E PROBLEMA SE CLICCO MOLTE VOLTE SULLE CARTE. GESTIRE MEGLIO QUANDO LE CARTE POSSONO ESSERE CLICCATE E IL THREAD SLEEP
/**
 * Implementazione GUI di BriscolaView per una partita di Briscola 1vs1.
 * Utilizza Swing per visualizzare le mani dei giocatori, il mazzo, la briscola e il tavolo.
 */
public class GuiBriscolaViewImpl implements BriscolaView {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 160;
    private static final boolean showCpuCards = false; // utile per il testing delle scelte della cpu

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
     * Mostra la GUI e avvia il ciclo completo della sessione nel controller.
     */
    @Override
    public void start(BriscolaController controller) {
        frame.setVisible(true);
        controller.startGame();
    }

    /**
     * Chiamato dal controller dopo model.setupGame() per mostrare il mazzo, la briscola e le mani iniziali.
     */
    @Override
    public void showSetup(Card briscolaCard, Player human, Player cpu) {
        // Rendi visibili i panel
        cpuPanel.setVisible(true);
        humanPanel.setVisible(true);
        tablePanel.setVisible(true);
        sidePanel.setVisible(true);
        briscolaPanel.setVisible(true);
        deckPanel.setVisible(true);

        // Popola la view
        refreshHand(human);
        refreshHand(cpu);
        showBriscola(briscolaCard);
    }

    /**
     * Chiede all'utente di scegliere la difficoltà della CPU tramite una finestra di dialogo.
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

    /**
     * Mostra la carta di briscola nella GUI aggiornando il pannel corrispondente.
     *
     * @param briscolaCard la carta rappresentante la briscola da mostrare
     */
    @Override
    public void showBriscola(Card briscolaCard) {
        briscolaPanel.removeAll();
        briscolaPanel.add(createCardLabel(briscolaCard.toFileName()));
        briscolaPanel.revalidate();
        briscolaPanel.repaint();
    }

    /**
     * Richiede la selezione di una carta dal giocatore specificato.
     * Questo metodo aggiorna la visualizzazione della mano del giocatore e attende
     * l'input dell'utente per selezionare una carta. La carta selezionata viene poi restituita.
     *
     * @param p il giocatore di cui viene richiesta la carta
     * @return la carta selezionata dal giocatore
     */
    @Override
    public Card requestCard(Player p) {
        // Fai il refresh della mano del giocatore prima
        refreshHand(p);
        // Aspetta per la selezione dell'utente
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ignored) {
            }
        }
        return selectedCard.get();
    }

    /**
     * Mostra la carta giocata da un giocatore e aggiorna la visualizzazione della mano del giocatore.
     * Per un giocatore non umano, simula una pausa per rappresentare il tempo di "pensiero".
     *
     * @param p    il giocatore che ha giocato la carta
     * @param card la carta giocata dal giocatore
     */
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
    // TODO: usare javax.Time invece di sleep
    // boolean isHuman = p instanceof HumanPlayer;
    //    if (!isHuman) {
    //        new javax.swing.Timer(1000, e -> {
    //            playCardOnTable(card, false);
    //            refreshHand(p);
    //        }).setRepeats(false).start();
    //    } else {
    //        playCardOnTable(card, true);
    //        refreshHand(p);
    //    }

    /**
     * Mostra il risultato della mano indicando il vincitore e i punti guadagnati,
     * e pulisce il tavolo per preparare il prossimo turno.
     *
     * @param winner    il giocatore che ha vinto la mano
     * @param pointsWon i punti guadagnati dal vincitore in questa mano
     */
    @Override
    public void showHandResult(Player winner, int pointsWon) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        clearTable();
    }

    /**
     * Aggiorna la GUI per mostrare una carta pescata. In particolare, aggiorna il pannello
     * del mazzo per mostrare il dorso delle carte rimanenti e aggiorna la visualizzazione
     * della mano del giocatore che ha pescato la carta.
     *
     * @param p         il giocatore che ha pescato la carta
     * @param drawnCard la carta che è stata pescata dal giocatore
     */
    @Override
    public void showDraw(Player p, Card drawnCard) {
        deckPanel.removeAll();
        deckPanel.add(createCardLabel("back"));
        deckPanel.revalidate();
        deckPanel.repaint();
        refreshHand(p);
    }

    /**
     * Mostra informazioni sull'ultimo turno in cui vengono pescate le carte.
     * Permette all'utente di conoscere qual è l'ultimo turno prima di pescare la briscola.
     */
    @Override
    public void showLastDrawingTurn() {
        String msg = "Chi perde la prossima mano di gioco pescherà la briscola";
        JOptionPane.showMessageDialog(frame, msg, "Ultimo turno di pesca", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostra i punteggi finali dei giocatori in una finestra di dialogo.
     *
     * @param player1  il primo giocatore di cui mostrare il punteggio finale
     * @param player2  il secondo giocatore di cui mostrare il punteggio finale
     * @param p1points i punti finali ottenuti dal primo giocatore
     * @param p2points i punti finali ottenuti dal secondo giocatore
     */
    @Override
    public void showFinalScores(Player player1, Player player2, int p1points, int p2points) {
        String sb = "--- Punteggi finali ---\n" +
                player1.getName() + ": " + p1points + "\n" +
                player2.getName() + ": " + p2points + "\n";
        JOptionPane.showMessageDialog(frame, sb, "Punteggi Finali", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Mostra un messaggio con il vincitore della partita.
     * Se non c'è un vincitore, indica che la partita è terminata in pareggio.
     *
     * @param winner un oggetto Optional contenente il giocatore vincitore,
     *               vuoto in caso di pareggio
     */
    @Override
    public void showWinner(Optional<Player> winner) {
        String msg = "Vincitore: " + (winner.isPresent() ? winner.get().getName() : "Pareggio");
        JOptionPane.showMessageDialog(frame, msg, "Fine Partita", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Chiede all'utente se desidera giocare un'altra partita tramite una finestra di dialogo.
     *
     * @return true se l'utente sceglie di giocare un'altra partita, false altrimenti
     */
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
     * Nasconde la carta di briscola.
     */
    @Override
    public void hideBriscola() {
        briscolaPanel.setVisible(false);
    }

    /**
     * Nasconde il mazzo.
     */
    @Override
    public void hideDeck() {
        deckPanel.setVisible(false);
    }

    /**
     * Chiude l'applicazione.
     */
    @Override
    public void close() {
        System.exit(0);
    }

    // == METODI HELPER ==

    /**
     * Aggiorna il pannello della mano per il giocatore specificato con dimensioni fisse delle carte.
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
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        selectedCard.set(c);
                        playCardOnTable(c, true);
                        synchronized (lock) {
                            lock.notify();
                        }
                    }
                });
                panel.add(lbl);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Mostra una carta giocata sul tavolo per ogni giocatore.
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
     * Pulisce il tavolo dalle carte giocate.
     */
    private void clearTable() {
        tablePanel.removeAll();
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    // == JPANEL ==

    /**
     * Crea un JPanel con un'immagine di sfondo personalizzata. Il pannello viene renderizzato
     * con l'immagine specificata che riempie i suoi limiti.
     *
     * @return un JPanel con un'immagine di sfondo personalizzata dipinta sulle sue dimensioni
     */
    private JPanel createBackgroundPanel() {
        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/background.png")));
        Image bg = bgIcon.getImage();
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
    }

    /**
     * Crea un pannello per visualizzare le carte dei giocatori nella GUI.
     * Il layout del pannello è centrato con spaziatura uniforme tra gli elementi e ha uno sfondo trasparente.
     *
     * @param isHuman flag che indica se il pannello appartiene al giocatore umano (true) o alla CPU (false)
     * @return un JPanel configurato per visualizzare la mano del giocatore
     */
    private JPanel createHandPanel(boolean isHuman) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Crea un pannello per rappresentare il mazzo di carte nella GUI. 
     * Il pannello ha un layout con una griglia singola, uno sfondo trasparente 
     * e include un'etichetta che rappresenta il dorso delle carte.
     *
     * @return un JPanel configurato per visualizzare il mazzo di carte
     */
    private JPanel createDeckPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 1));
        panel.setOpaque(false);
        panel.add(createCardLabel("back"));
        return panel;
    }

    /**
     * Crea e restituisce un JPanel configurato per visualizzare la briscola (carta dominante) nel gioco.
     * Il pannello utilizza un BorderLayout, ha uno sfondo trasparente e una dimensione preferita
     * corrispondente alle dimensioni predefinite delle carte. Include anche una JLabel in alto con il testo "Briscola".
     *
     * @return un JPanel configurato per visualizzare la briscola nel gioco
     */
    private JPanel createBriscolaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        panel.add(new JLabel("Briscola"), BorderLayout.NORTH);
        return panel;
    }

    // == JLABEL ==

    /**
     * Crea una JLabel con dimensioni fisse per le carte.
     *
     * @param filename il nome del file dell'immagine della carta senza estensione
     * @return una JLabel contenente l'immagine ridimensionata della carta
     */
    private JLabel createCardLabel(String filename) {
        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/cards/" + filename + ".png")));
        Image img = icon.getImage().getScaledInstance(CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH);
        JLabel lbl = new JLabel(new ImageIcon(img));
        lbl.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));
        return lbl;
    }
}

//TODO: private void enableCardClicking(Player p) {
//    if (!(p instanceof HumanPlayer)) return;
//
//    for (Component c : humanPanel.getComponents()) {
//        if (c instanceof JLabel cardLabel) {
//            for (MouseListener ml : cardLabel.getMouseListeners()) {
//                cardLabel.removeMouseListener(ml); // Rimuovi eventuali vecchi listener
//            }
//            cardLabel.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    String name = cardLabel.getName(); // es: "bastoni_1"
//                    Card clicked = p.getHand().stream()
//                            .filter(card -> card.toFileName().equals(name))
//                            .findFirst().orElse(null);
//                    if (clicked != null && selectedCard.get() == null) {
//                        selectedCard.set(clicked);
//                        synchronized (lock) {
//                            lock.notifyAll();
//                        }
//                    }
//                }
//            });
//        }
//    }
//}
// @Override
//public Card requestCard(Player p) {
//    selectedCard.set(null);
//    refreshHand(p); // Mostra la mano aggiornata
//    enableCardClicking(p); // Abilita il click SOLO ORA
//    synchronized (lock) {
//        try {
//            lock.wait();
//        } catch (InterruptedException ignored) {
//        }
//    }
//    return selectedCard.get();
//}