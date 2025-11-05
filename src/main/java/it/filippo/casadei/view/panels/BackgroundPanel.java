package it.filippo.casadei.view.panels;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * Panel dello sfondo principale dell'applicazione.
 */
public class BackgroundPanel extends JPanel {
    private final Image image;

    public BackgroundPanel(String resourcePath) {
        setLayout(new BorderLayout());
        this.image = new ImageIcon(Objects.requireNonNull(
                getClass().getResource(resourcePath))).getImage();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }
}