package it.filippo.casadei.view.panels;

import javax.swing.*;
import java.awt.*;

/**
 * Panel che rappresenta il mazzo delle carte.
 */
public class DeckPanel extends JPanel {
    public DeckPanel() {
        super(new GridLayout(1,1));
        setOpaque(false);
        showBack();
    }

    /**
     * Mostra la carta "back" (dorso di una carta) che rappresenta il mazzo.
     */
    public void showBack() {
        removeAll();
        add(cardLabel("back"));
        revalidate(); repaint();
    }

    /**
     * Mostra una carta specifica.
     *
     * @param file nome del file della carta da mostrare
     * @return JLabel che rappresenta la carta mostrata
     */
    private JLabel cardLabel(String file) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/cards/" + file + ".png"));
        return new JLabel(new ImageIcon(icon.getImage().getScaledInstance(100,160,Image.SCALE_SMOOTH)));
    }
}