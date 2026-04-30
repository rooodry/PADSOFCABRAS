package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Login screen for registered customers.
 */
public class LoginClientePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Builds the customer login form.
     *
     * @param mainFrame main GUI controller
     */
    public LoginClientePanel(Main mainFrame) {
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, ""), BorderLayout.NORTH);

        JPanel formContainer = new UiStyle.RoundedPanel(UiStyle.COLOR_CABECERA, 30);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

        JLabel titleLabel = new JLabel("Log in", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        gbc.gridy = 0;
        formContainer.add(titleLabel, gbc);

        JTextField idField = new JTextField(mainFrame.getClienteActual().getNombre());
        JPasswordField passField = new JPasswordField("1234");

        gbc.gridy = 1;
        formContainer.add(crearEtiqueta("Identificacion"), gbc);
        gbc.gridy = 2;
        formContainer.add(idField, gbc);

        gbc.gridy = 3;
        formContainer.add(crearEtiqueta("Contrasena"), gbc);
        gbc.gridy = 4;
        formContainer.add(passField, gbc);

        JButton loginButton = crearBoton("Continuar");
        loginButton.addActionListener(e -> mainFrame.iniciarSesionCliente(idField.getText()));
        gbc.gridy = 5;
        gbc.insets = new Insets(16, 0, 5, 0);
        formContainer.add(loginButton, gbc);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerPanel.setOpaque(false);
        registerPanel.add(crearEtiqueta("No tienes cuenta?"));
        JButton registerButton = crearBoton("Crear cuenta");
        registerButton.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_REGISTRO));
        registerPanel.add(registerButton);
        gbc.gridy = 6;
        formContainer.add(registerPanel, gbc);

        JButton invitado = crearBoton("Continuar sin registrarse");
        invitado.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_HOME));
        gbc.gridy = 7;
        formContainer.add(invitado, gbc);

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setOpaque(false);
        JRadioButton clienteRadio = new JRadioButton("Cliente", true);
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
        JRadioButton gestorRadio = new JRadioButton("Gestor");
        clienteRadio.setOpaque(false);
        empleadoRadio.setOpaque(false);
        gestorRadio.setOpaque(false);
        clienteRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        empleadoRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        gestorRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        radioPanel.add(clienteRadio);
        radioPanel.add(empleadoRadio);
        radioPanel.add(gestorRadio);
        gbc.gridy = 8;
        formContainer.add(radioPanel, gbc);

        JPanel envoltura = new JPanel(new GridBagLayout());
        envoltura.setBackground(UiStyle.COLOR_FONDO);
        envoltura.add(formContainer);
        add(envoltura, BorderLayout.CENTER);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel(texto);
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        return label;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(250, 34));
        return boton;
    }
}
