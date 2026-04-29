package GUI;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginEmpleadoPanel extends JPanel {

    private Main mainFrame;

    public LoginEmpleadoPanel(Main mainFrame) {
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

        // Título
        gbc.gridy = 0;
        JLabel titleLabel = new JLabel("Log in (Empleado):", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        formContainer.add(titleLabel, gbc);

        // Identificación
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

        // Contraseña
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
        clienteRadio.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_CLIENTE"));
        
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
        empleadoRadio.setForeground(Color.WHITE);
        empleadoRadio.setOpaque(false);
        empleadoRadio.setSelected(true); // Seleccionado por defecto
        
        JRadioButton gestorRadio = new JRadioButton("Gestor");
        gestorRadio.setForeground(Color.WHITE);
        gestorRadio.setOpaque(false);
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