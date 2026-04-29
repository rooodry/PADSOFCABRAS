package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegistroPanel extends JPanel {

    private Main mainFrame; // Guardamos la referencia

    // Constructor actualizado
    public RegistroPanel(Main mainFrame) {
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

        // Título: Registro:
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Registro:", SwingConstants.CENTER);
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

        // Etiqueta Documento de identidad
        gbc.gridy = 5;
        JLabel docLabel = new JLabel("Documento de identidad");
        docLabel.setForeground(Color.WHITE);
        docLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        formContainer.add(docLabel, gbc);

        // Campo de Documento de identidad
        gbc.gridy = 6;
        JTextField docField = new JTextField();
        docField.setBorder(null);
        docField.setPreferredSize(new Dimension(250, 30));
        formContainer.add(docField, gbc);

        // Contenedor de Botones de Acción
        gbc.gridy = 7;
        gbc.insets = new Insets(25, 0, 0, 0);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0)); 

        // Botón Cancelar
        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setBackground(new Color(181, 86, 68)); // Rojo
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.setBorderPainted(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setPreferredSize(new Dimension(100, 35));
        
        // --- ACCIÓN: Cancelar te devuelve al login del cliente ---
        cancelButton.addActionListener(e -> {
            mainFrame.cambiarPantalla("PANTALLA_CLIENTE");
        });

        // Botón Continuar
        JButton continueButton = new JButton("Continuar");
        continueButton.setBackground(new Color(75, 70, 65)); // Gris oscuro
        continueButton.setForeground(Color.WHITE);
        continueButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        continueButton.setBorderPainted(false);
        continueButton.setFocusPainted(false);
        continueButton.setPreferredSize(new Dimension(100, 35));
        
        // --- ACCIÓN: Simular que el registro ha sido exitoso ---
        continueButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "¡Cuenta creada exitosamente!");
            mainFrame.cambiarPantalla("PANTALLA_CLIENTE"); // Devuelve al login
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(continueButton);
        formContainer.add(buttonPanel, gbc);

        add(formContainer, BorderLayout.CENTER);
    }
}