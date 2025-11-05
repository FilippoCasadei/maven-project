package it.filippo.casadei.view.panels;

import it.filippo.casadei.model.card.Card;

import javax.swing.*;
import java.awt.*;

/**
 * Panel che rappresenta il tavolo di gioco.
 */
public class TablePanel extends JPanel {
    private static final int CARD_W = 100, CARD_H = 160;
    private JLabel humanCardLabel, cpuCardLabel;

    /**
     * Crea un nuovo TablePanel vuoto.
     */
    public TablePanel() {
        super(null);
        setOpaque(false);
    }

    /**
     * Mostra la carta sul tavolo.
     *
     * @param card carta da mostrare
     * @param isHuman vero se giocatore è umano
     */
    public void playCard(Card card, boolean isHuman) {
        JLabel lbl = createCardLabel(card);
        int x = (getWidth() - CARD_W) / 2;
        int y = isHuman ? getHeight() - CARD_H - 10 : 10;
        lbl.setBounds(x, y, CARD_W, CARD_H);
        if (isHuman) {
            if (humanCardLabel != null) remove(humanCardLabel);
            humanCardLabel = lbl;
        } else {
            if (cpuCardLabel != null) remove(cpuCardLabel);
            cpuCardLabel = lbl;
        }
        add(lbl);
        revalidate(); repaint();
    }

    /**
     * Rimuove le carte dal tavolo.
     */
    public void clear() {
        removeAll();
        humanCardLabel = cpuCardLabel = null;
        revalidate();
        repaint();
    }

    /**
     * Crea un nuovo JLabel per la carta specificata.
     * @param c carta da mostrare
     * @return
     */
    private JLabel createCardLabel(Card c) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/cards/" + c.toFileName() + ".png"));
        Image img = icon.getImage().getScaledInstance(CARD_W, CARD_H, Image.SCALE_SMOOTH);
        JLabel lbl = new JLabel(new ImageIcon(img));
        lbl.setPreferredSize(new Dimension(CARD_W, CARD_H));
        return lbl;
    }
}
