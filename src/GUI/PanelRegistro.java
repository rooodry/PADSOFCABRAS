package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


/**
 * Panel de compatibilidad para el flujo antiguo de registro.
 *
 * <p>La pantalla real de registro se gestiona desde {@link Main}; esta clase se
 * mantiene para no romper codigo que todavia cree el panel por nombre.</p>
 */
public class PanelRegistro extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Construye un marcador visual que redirige conceptualmente al registro de
     * {@link Main}.
     *
     * @param contenedorPrincipal contenedor de tarjetas original
     * @param baraja layout de tarjetas original
     */
    public PanelRegistro(JPanel contenedorPrincipal, CardLayout baraja) {
        setLayout(new BorderLayout());
        add(new JLabel("Registro disponible desde GUI.Main", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
