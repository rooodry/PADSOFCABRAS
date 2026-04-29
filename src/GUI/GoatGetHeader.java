package GUI;

import java.awt.*;
import javax.swing.*;

public class GoatGetHeader extends JPanel {

    public GoatGetHeader() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE); // Fondo blanco para el encabezado

        // MARCADOR DE POSICIÓN PARA LA CABRA (Sustituye por tu imagen real)
        JLabel logoLabel = new JLabel("<html><center><font size='+5'>🐐📖</font><br>GOAT & GET</center></html>");
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(logoLabel);

        // Espaciador inferior
        add(Box.createRigidArea(new Dimension(0, 20)));
    }
}