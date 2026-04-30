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
 * Pantalla de acceso para clientes registrados.
 */
public class LoginClientePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Crea el formulario de login de cliente.
     *
     * @param mainFrame controlador principal
     */
    public LoginClientePanel(Main mainFrame) {
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new GoatGetHeader(), BorderLayout.NORTH);

        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setBackground(UiStyle.COLOR_CABECERA);
        formContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;

<<<<<<< HEAD
        JLabel titleLabel = new JLabel("Log in", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(UiStyle.COLOR_TEXTO_CLARO);
=======
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9
        gbc.gridy = 0;
        formContainer.add(titleLabel, gbc);

<<<<<<< HEAD
        JTextField idField = new JTextField(mainFrame.getClienteActual().getNombre());
        JPasswordField passField = new JPasswordField("1234");
=======
        gbc.gridy = 1;
        JLabel idLabel = new JLabel("Identificación");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formContainer.add(idLabel, gbc);
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

        gbc.gridy = 1;
        formContainer.add(crearEtiqueta("Identificacion"), gbc);
        gbc.gridy = 2;
        formContainer.add(idField, gbc);

        gbc.gridy = 3;
        formContainer.add(crearEtiqueta("Contrasena"), gbc);
        gbc.gridy = 4;
        formContainer.add(passField, gbc);

<<<<<<< HEAD
        JButton loginButton = crearBoton("Continuar");
        loginButton.addActionListener(e -> mainFrame.iniciarSesionCliente(idField.getText()));
        gbc.gridy = 5;
        gbc.insets = new Insets(16, 0, 5, 0);
=======
        // BOTÓN: Continuar (Hacer Login con validación)
        gbc.gridy = 5;
        gbc.insets = new Insets(15, 0, 5, 0); 
        JButton loginButton = new JButton("Continuar");
        loginButton.setBackground(new Color(75, 70, 65));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(250, 35));
        
        // --- LÓGICA DE VALIDACIÓN DE CONTRASEÑA ---
        loginButton.addActionListener(e -> {
            String usuario = idField.getText().trim();
            String contrasena = new String(passField.getPassword());

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, rellene todos los campos.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            } else if (!usuario.equals("usuario")) { 
                // Si el usuario no existe en la "base de datos"
                JOptionPane.showMessageDialog(this, "Identificación incorrecta. Tienes que registrarte primero.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!contrasena.equals("1234")) { 
                // Si el usuario existe pero la contraseña está mal
                JOptionPane.showMessageDialog(this, "Contraseña incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Todo correcto, entra a la app
                idField.setText(""); // Limpiar campos
                passField.setText("");
                mainFrame.cambiarPantalla("PANTALLA_HOME");
            }
        });
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9
        formContainer.add(loginButton, gbc);

        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerPanel.setOpaque(false);
<<<<<<< HEAD
        registerPanel.add(crearEtiqueta("No tienes cuenta?"));
        JButton registerButton = crearBoton("Crear cuenta");
        registerButton.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_REGISTRO));
=======
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));

        JLabel registerText = new JLabel("¿No tienes cuenta?");
        registerText.setForeground(Color.WHITE);
        registerText.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton registerButton = new JButton("Crear cuenta");
        registerButton.setBackground(new Color(75, 70, 65));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setPreferredSize(new Dimension(110, 30));
        
        registerButton.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_REGISTRO"));

        registerPanel.add(registerText);
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9
        registerPanel.add(registerButton);
        gbc.gridy = 6;
        formContainer.add(registerPanel, gbc);

        JButton invitado = crearBoton("Continuar sin registrarse");
        invitado.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_HOME));
        gbc.gridy = 7;
<<<<<<< HEAD
        formContainer.add(invitado, gbc);
=======
        JButton continueWithoutButton = new JButton("Continuar sin registrarse");
        continueWithoutButton.setBackground(new Color(75, 70, 65));
        continueWithoutButton.setForeground(Color.WHITE);
        continueWithoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        continueWithoutButton.setBorderPainted(false);
        continueWithoutButton.setFocusPainted(false);
        continueWithoutButton.setPreferredSize(new Dimension(250, 40));
        
        continueWithoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Entrando como invitado...");
            mainFrame.cambiarPantalla("PANTALLA_HOME");
        });
        formContainer.add(continueWithoutButton, gbc);
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        radioPanel.setOpaque(false);
        JRadioButton clienteRadio = new JRadioButton("Cliente", true);
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
<<<<<<< HEAD
=======
        empleadoRadio.setForeground(Color.WHITE);
        empleadoRadio.setOpaque(false);
        // --- ACCIÓN AÑADIDA PARA EMPLEADO ---
        empleadoRadio.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_EMPLEADO"));
        
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9
        JRadioButton gestorRadio = new JRadioButton("Gestor");
        clienteRadio.setOpaque(false);
        empleadoRadio.setOpaque(false);
        gestorRadio.setOpaque(false);
<<<<<<< HEAD
        clienteRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        empleadoRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        gestorRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
=======
        // --- ACCIÓN AÑADIDA PARA GESTOR ---
        gestorRadio.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_GESTOR"));

        ButtonGroup group = new ButtonGroup();
        group.add(clienteRadio);
        group.add(empleadoRadio);
        group.add(gestorRadio);

>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9
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
        JButton boton = new JButton(texto);
        boton.setBackground(UiStyle.COLOR_TEXTO);
        boton.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(250, 34));
        return boton;
    }
}
