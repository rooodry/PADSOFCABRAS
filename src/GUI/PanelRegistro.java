package GUI;

import java.awt.BorderLayout;
import java.awt.CardLayout;

<<<<<<< HEAD
import javax.swing.JPanel;

/**
 * Adaptador conservado por compatibilidad con prototipos antiguos.
 *
 * <p>La pantalla de registro funcional es {@link RegistroPanel}. Esta clase
 * evita romper codigo previo que esperaba un {@code PanelRegistro}.</p>
 */
public class PanelRegistro extends JPanel {
=======
public class PanelRegistro extends JPanel {
    private JTextField txtIdentificacion;
    private JPasswordField txtContrasena;
    private JComboBox<String> cbRol;
    private JButton btnLogin;
    private JButton btnCrearCuenta;
    private JButton btnContinuarSinRegistro;

    public PanelRegistro() {
        // Usamos GridBagLayout para centrar y alinear como en el PDF
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes
        gbc.fill = GridBagConstraints.HORIZONTAL;
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

    private static final long serialVersionUID = 1L;

    /**
     * Crea un panel vacio de compatibilidad para la ventana antigua.
     *
     * @param contenedorPrincipal contenedor de cartas original
     * @param baraja              layout de cartas original
     */
    public PanelRegistro(JPanel contenedorPrincipal, CardLayout baraja) {
        setLayout(new BorderLayout());
        add(new RegistroLegacyPanel(contenedorPrincipal, baraja), BorderLayout.CENTER);
    }

    private static class RegistroLegacyPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        RegistroLegacyPanel(JPanel contenedorPrincipal, CardLayout baraja) {
            setLayout(new BorderLayout());
            add(new javax.swing.JLabel("Registro disponible desde GUI.Main", javax.swing.SwingConstants.CENTER),
                    BorderLayout.CENTER);
        }
    }
}
