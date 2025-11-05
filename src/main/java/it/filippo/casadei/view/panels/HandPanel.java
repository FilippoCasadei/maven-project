package it.filippo.casadei.view.panels;

import it.filippo.casadei.model.card.Card;
import it.filippo.casadei.model.player.Hand;
import it.filippo.casadei.model.player.Player;
import it.filippo.casadei.view.BriscolaViewObserver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel che rappresenta la mano di un giocatore.
 * Versione SEMPLIFICATA che usa direttamente l'observer.
 */
public class HandPanel extends JPanel {
    private static final int CARD_WIDTH = 100;
    private static final int CARD_HEIGHT = 160;

    private final boolean isHuman;
    private final List<JLabel> cardLabels;
    private BriscolaViewObserver observer;
    private Player currentPlayer;
    private boolean selectionEnabled = false;

    public HandPanel(boolean isHuman) {
        super(new FlowLayout(FlowLayout.CENTER, 10, 10));
        setOpaque(false);
        this.isHuman = isHuman;
        this.cardLabels = new ArrayList<>();
    }

    /**
     * Imposta l'observer per notificare le selezioni di carte.
     */
    public void setObserver(BriscolaViewObserver observer) {
        this.observer = observer;
    }

    /**
     * Aggiorna la visualizzazione della mano.
     */
    public void updateHand(Hand hand, boolean showCards) {
        removeAll();
        cardLabels.clear();

        for (Card card : hand.getCards()) {
            String filename = showCards ? card.toFileName() : "back";
            JLabel cardLabel = createCardLabel(filename);

            // Salva il riferimento alla carta
            if (showCards) {
                cardLabel.putClientProperty("card", card);
            }

            // Aggiungi listener se la selezione è abilitata
            if (isHuman && showCards && selectionEnabled) {
                attachClickListener(cardLabel);
            }

            cardLabels.add(cardLabel);
            add(cardLabel);
        }

        revalidate();
        repaint();
    }

    /**
     * Abilita il click sulle carte.
     */
    public void enableCardSelection(Player player) {
        if (!isHuman) return;

        this.currentPlayer = player;
        this.selectionEnabled = true;

        // Abilita i listener su tutte le carte correnti
        for (JLabel cardLabel : cardLabels) {
            cardLabel.setEnabled(true);
            cardLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            attachClickListener(cardLabel);
        }
    }

    /**
     * Disabilita il click sulle carte.
     */
    public void disableCardSelection() {
        this.selectionEnabled = false;

        for (JLabel cardLabel : cardLabels) {
            cardLabel.setEnabled(false);
            cardLabel.setCursor(Cursor.getDefaultCursor());
            cardLabel.setBorder(null);

            // Rimuovi tutti i listener
            for (var listener : cardLabel.getMouseListeners()) {
                cardLabel.removeMouseListener(listener);
            }
        }
    }

    /**
     * Attacca il listener di click a una carta.
     */
    private void attachClickListener(JLabel cardLabel) {
        // Rimuovi listener esistenti
        for (var listener : cardLabel.getMouseListeners()) {
            cardLabel.removeMouseListener(listener);
        }

        // Aggiungi nuovo listener
        cardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!selectionEnabled || observer == null) return;

                Card clickedCard = (Card) cardLabel.getClientProperty("card");
                if (clickedCard != null) {
                    // Disabilita immediatamente per evitare doppi click
                    disableCardSelection();
                    // Notifica l'observer
                    observer.humanPlaysCard(currentPlayer, clickedCard);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectionEnabled && cardLabel.isEnabled()) {
                    // Mostra un bordo giallo sulla carta
                    cardLabel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // Rimuove il bordo sulla carta
                cardLabel.setBorder(null);
            }
        });
    }

    /**
     * Crea una JLabel per una carta.
     */
    private JLabel createCardLabel(String filename) {
        ImageIcon icon = new ImageIcon(
                getClass().getResource("/cards/" + filename + ".png")
        );
        Image scaledImage = icon.getImage().getScaledInstance(
                CARD_WIDTH, CARD_HEIGHT, Image.SCALE_SMOOTH
        );

        JLabel label = new JLabel(new ImageIcon(scaledImage));
        label.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        return label;
    }
}