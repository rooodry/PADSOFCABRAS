package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Profile and settings screen for the registered customer.
 */
public class PanelPerfil extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JLabel lblUsuario;
    private final JLabel lblDni;
    private final JTextField txtNuevoNombre;

    /**
     * Builds the profile panel.
     *
     * @param mainFrame main GUI controller
     */
    public PanelPerfil(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.lblUsuario = new JLabel();
        this.lblDni = new JLabel();
        this.txtNuevoNombre = new JTextField(22);

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "PERFIL"), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Refreshes the displayed customer data.
     */
    public void refrescar() {
        lblUsuario.setText("Usuario: " + mainFrame.getClienteActual().getNombre());
        lblDni.setText("Documento: " + mainFrame.getClienteActual().getDNI());
        txtNuevoNombre.setText(mainFrame.getClienteActual().getNombre());
    }

    private JPanel crearFormulario() {
        JPanel envoltura = new JPanel(new GridBagLayout());
        envoltura.setBackground(UiStyle.COLOR_FONDO);

        JPanel panel = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 28);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EmptyBorder(24, 32, 24, 32));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 14, 0);

        JLabel titulo = new JLabel("Mi cuenta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(titulo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 4, 0);
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblUsuario, gbc);

        gbc.gridy++;
        lblDni.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblDni, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 6, 0);
        JLabel subtitulo = new JLabel("Notificaciones");
        subtitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        subtitulo.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(subtitulo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 2, 0);
        panel.add(new JCheckBox("Actualizaciones de pedidos", true), gbc);
        gbc.gridy++;
        panel.add(new JCheckBox("Ofertas de intercambios", true), gbc);
        gbc.gridy++;
        panel.add(new JCheckBox("Descuentos de productos", true), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 0, 4, 8);
        panel.add(new JLabel("Nuevo nombre:"), gbc);

        gbc.gridx = 1;
        txtNuevoNombre.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        panel.add(txtNuevoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 0, 0, 0);
        JButton guardar = new UiStyle.RoundedButton("Guardar cambios", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 20);
        guardar.setPreferredSize(new Dimension(180, 36));
        guardar.setFocusPainted(false);
        guardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        guardar.addActionListener(e -> mainFrame.cambiarNombreCliente(txtNuevoNombre.getText()));
        panel.add(guardar, gbc);

        envoltura.add(panel);
        return envoltura;
    }
}
