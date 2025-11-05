package it.filippo.casadei.view.panels;

import it.filippo.casadei.model.card.Card;

import javax.swing.*;
import java.awt.*;

/**
 * Panel che mostra la carta di briscola.
 */
public class BriscolaPanel extends JPanel {
    public BriscolaPanel() {
        super(new BorderLayout());
        setOpaque(false);
        setPreferredSize(new Dimension(100,160));
        add(new JLabel("Briscola"), BorderLayout.NORTH);
    }

    /**
     * Mostra la carta di briscola.
     *
     * @param briscola carta di briscola da mostrare
     */
    public void show(Card briscola) {
        removeAll();
        add(new JLabel(new ImageIcon(new ImageIcon(
                getClass().getResource("/cards/" + briscola.toFileName() + ".png"))
                .getImage().getScaledInstance(100,160,Image.SCALE_SMOOTH))), BorderLayout.CENTER);
        revalidate(); repaint();
    }
}