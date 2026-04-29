package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginClientePanel extends JPanel {

    private Main mainFrame;

    public LoginClientePanel(Main mainFrame) {
        this.mainFrame = mainFrame; 
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(new GoatGetHeader(), BorderLayout.NORTH);

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBackground(new Color(139, 115, 85));
        formContainer.setBorder(new EmptyBorder(30, 40, 30, 40)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0); 
        gbc.gridx = 0;

        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Log in:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        formContainer.add(titleLabel, gbc);

        gbc.gridy = 1;
        JLabel idLabel = new JLabel("Identificación");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formContainer.add(idLabel, gbc);

        gbc.gridy = 2;
        JTextField idField = new JTextField();
        idField.setBorder(null);
        idField.setPreferredSize(new Dimension(250, 30));
        formContainer.add(idField, gbc);

        gbc.gridy = 3;
        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formContainer.add(passLabel, gbc);

        gbc.gridy = 4;
        JPasswordField passField = new JPasswordField();
        passField.setBorder(null);
        passField.setPreferredSize(new Dimension(250, 30));
        formContainer.add(passField, gbc);

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
        formContainer.add(loginButton, gbc);

        // Panel de Registro
        gbc.gridy = 6;
        gbc.insets = new Insets(10, 0, 5, 0); 
        JPanel registerPanel = new JPanel();
        registerPanel.setOpaque(false);
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
        registerPanel.add(registerButton);
        formContainer.add(registerPanel, gbc);

        // Botón Continuar sin registrarse
        gbc.gridy = 7;
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

        // Espaciador
        gbc.gridy = 8;
        gbc.insets = new Insets(10, 0, 0, 0); 
        formContainer.add(Box.createVerticalStrut(10), gbc);

        // Contenedor de Botones de Radio
        gbc.gridy = 9;
        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);
        radioPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JRadioButton clienteRadio = new JRadioButton("Cliente");
        clienteRadio.setForeground(Color.WHITE);
        clienteRadio.setOpaque(false);
        clienteRadio.setSelected(true); 
        
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
        empleadoRadio.setForeground(Color.WHITE);
        empleadoRadio.setOpaque(false);
        // --- ACCIÓN AÑADIDA PARA EMPLEADO ---
        empleadoRadio.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_EMPLEADO"));
        
        JRadioButton gestorRadio = new JRadioButton("Gestor");
        gestorRadio.setForeground(Color.WHITE);
        gestorRadio.setOpaque(false);
        // --- ACCIÓN AÑADIDA PARA GESTOR ---
        gestorRadio.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_GESTOR"));

        ButtonGroup group = new ButtonGroup();
        group.add(clienteRadio);
        group.add(empleadoRadio);
        group.add(gestorRadio);

        radioPanel.add(clienteRadio);
        radioPanel.add(empleadoRadio);
        radioPanel.add(gestorRadio);
        formContainer.add(radioPanel, gbc);

        add(formContainer, BorderLayout.CENTER);
    }
}