package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Compatibility panel for the old {@link VentanaPrincipal} prototype.
 *
 * <p>The functional registration screen used by the application is
 * {@link RegistroPanel}. This class remains only so older prototype code keeps
 * compiling while the real application runs from {@link Main}.</p>
 */
public class PanelRegistro extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Builds a small compatibility placeholder.
     *
     * @param contenedorPrincipal original card container
     * @param baraja original card layout
     */
    public PanelRegistro(JPanel contenedorPrincipal, CardLayout baraja) {
        setLayout(new BorderLayout());
        add(new JLabel("Registro disponible desde GUI.Main", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
