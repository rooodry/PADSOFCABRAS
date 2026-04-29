
package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginGestorPanel extends JPanel {

    private Main mainFrame; // Guardamos la referencia del Main

    // El constructor ahora recibe el Main
    public LoginGestorPanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Cabecera común
        add(new GoatGetHeader(), BorderLayout.NORTH);

        // Contenedor del formulario marrón
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBackground(new Color(139, 115, 85)); // Marrón
        formContainer.setBorder(new EmptyBorder(30, 40, 30, 40)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0); 
        gbc.gridx = 0;

        // Título: Log in:
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Log in:", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        formContainer.add(titleLabel, gbc);

        // Etiqueta Identificación
        gbc.gridy = 1;
        JLabel idLabel = new JLabel("Identificación");
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formContainer.add(idLabel, gbc);

        // Campo de Identificación
        gbc.gridy = 2;
        JTextField idField = new JTextField();
        idField.setBorder(null); 
        idField.setPreferredSize(new Dimension(250, 30));
        formContainer.add(idField, gbc);

        // Etiqueta Contraseña
        gbc.gridy = 3;
        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setForeground(Color.WHITE);
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formContainer.add(passLabel, gbc);

        // Campo de Contraseña
        gbc.gridy = 4;
        JPasswordField passField = new JPasswordField();
        passField.setBorder(null);
        passField.setPreferredSize(new Dimension(250, 30));
        formContainer.add(passField, gbc);

        // Espaciador
        gbc.gridy = 5;
        gbc.insets = new Insets(20, 0, 5, 0); 
        formContainer.add(Box.createVerticalStrut(10), gbc);

        // Contenedor de Botones de Radio
        gbc.gridy = 6;
        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false); 
        radioPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JRadioButton clienteRadio = new JRadioButton("Cliente");
        clienteRadio.setForeground(Color.WHITE);
        clienteRadio.setOpaque(false);
        
        // --- ACCIÓN: Volver a la pantalla de Cliente ---
        clienteRadio.addActionListener(e -> {
            mainFrame.cambiarPantalla("PANTALLA_CLIENTE");
        });
        
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
        empleadoRadio.setForeground(Color.WHITE);
        empleadoRadio.setOpaque(false);
        
        JRadioButton gestorRadio = new JRadioButton("Gestor");
        gestorRadio.setForeground(Color.WHITE);
        gestorRadio.setOpaque(false);
        gestorRadio.setSelected(true); // Gestor está seleccionado en esta pantalla

        // Agrupar
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