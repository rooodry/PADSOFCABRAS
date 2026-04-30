package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Simple branded header used by the login and registration screens.
 */
public class GoatGetHeader extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Builds the GOAT & GET header with the same tone as the app toolbar.
     */
    public GoatGetHeader() {
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_CABECERA);
        setPreferredSize(new Dimension(0, 92));
        setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel logoLabel = new JLabel("GOAT & GET", SwingConstants.CENTER);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 30));
        logoLabel.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        add(logoLabel, BorderLayout.CENTER);
    }
}
