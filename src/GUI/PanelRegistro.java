package GUI;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {
    private JTextField txtIdentificacion;
    private JPasswordField txtContrasena;
    private JComboBox<String> cbRol;
    private JButton btnLogin;
    private JButton btnCrearCuenta;
    private JButton btnContinuarSinRegistro;

    public LoginPanel() {
        // Usamos GridBagLayout para centrar y alinear como en el PDF
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Márgenes
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título de la tienda
        JLabel lblTitulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Log in:", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridy = 1;
        add(lblSubtitulo, gbc);

        // Campo Identificación
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Identificación:"), gbc);
        
        txtIdentificacion = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 2;
        add(txtIdentificacion, gbc);

        // Campo Contraseña
        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Contraseña:"), gbc);
        
        txtContrasena = new JPasswordField(15);
        gbc.gridx = 1; gbc.gridy = 3;
        add(txtContrasena, gbc);

        // Selector de Roles
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Rol:"), gbc);
        
        String[] roles = {"Cliente", "Empleado", "Gestor"};
        cbRol = new JComboBox<>(roles);
        gbc.gridx = 1; gbc.gridy = 4;
        add(cbRol, gbc);

        // Botón de Login principal
        btnLogin = new JButton("Entrar");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(btnLogin, gbc);

        // Opciones secundarias (Crear cuenta)
        JPanel panelRegistro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelRegistro.add(new JLabel("¿No tienes cuenta?"));
        btnCrearCuenta = new JButton("Crear cuenta");
        panelRegistro.add(btnCrearCuenta);
        
        gbc.gridy = 6;
        add(panelRegistro, gbc);

        // Continuar sin registrarse
        btnContinuarSinRegistro = new JButton("Continuar sin registrarse");
        gbc.gridy = 7;
        add(btnContinuarSinRegistro, gbc);
    }

    // Getters para poder añadir los ActionListeners desde el Controlador
    public JTextField getTxtIdentificacion() { return txtIdentificacion; }
    public JPasswordField getTxtContrasena() { return txtContrasena; }
    public JComboBox<String> getCbRol() { return cbRol; }
    public JButton getBtnLogin() { return btnLogin; }
    public JButton getBtnCrearCuenta() { return btnCrearCuenta; }
    public JButton getBtnContinuarSinRegistro() { return btnContinuarSinRegistro; }
}