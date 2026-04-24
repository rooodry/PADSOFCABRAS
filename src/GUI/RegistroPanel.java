package GUI;

import javax.swing.*;
import java.awt.*;

public class RegistroPanel extends JPanel {
    private JTextField txtIdentificacion;
    private JPasswordField txtContrasena;
    private JTextField txtDocumento;
    private JButton btnCancelar;
    private JButton btnContinuar;

    public RegistroPanel() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título de la tienda
        JLabel lblTitulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Registro:", SwingConstants.CENTER);
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

        // Campo Documento de identidad (DNI)
        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Documento de identidad:"), gbc);
        
        txtDocumento = new JTextField(15);
        gbc.gridx = 1; gbc.gridy = 4;
        add(txtDocumento, gbc);

        // Panel para botones (Cancelar / Continuar)
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        btnCancelar = new JButton("Cancelar");
        btnContinuar = new JButton("Continuar");
        panelBotones.add(btnCancelar);
        panelBotones.add(btnContinuar);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        add(panelBotones, gbc);
    }

    // Getters para poder añadir los ActionListeners desde el Controlador
    public JTextField getTxtIdentificacion() { return txtIdentificacion; }
    public JPasswordField getTxtContrasena() { return txtContrasena; }
    public JTextField getTxtDocumento() { return txtDocumento; }
    public JButton getBtnCancelar() { return btnCancelar; }
    public JButton getBtnContinuar() { return btnContinuar; }
}