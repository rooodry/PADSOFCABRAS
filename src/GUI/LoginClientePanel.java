package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;

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

        JPanel panelCentral = new JPanel(new GridBagLayout());
        panelCentral.setBackground(UiStyle.COLOR_FONDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.CENTER;
        gbc.insets = new Insets(40, 0, 20, 0);

        // Logo
        JLabel logo = new JLabel();
        try {
            BufferedImage logoImage = ImageIO.read(new File("lib/fotos/GOAT&GET.png"));
            Image scaledImage = logoImage.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            logo.setText("GOAT & GET");
            logo.setFont(new Font("SansSerif", Font.BOLD, 28));
        }
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        panelCentral.add(logo, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(20, 0, 40, 0);
        
        // Panel de formulario
        JPanel formContainer = new UiStyle.RoundedPanel(UiStyle.COLOR_CABECERA, 20);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(new EmptyBorder(28, 40, 28, 40));
        formContainer.setPreferredSize(new Dimension(360, 320));

        GridBagConstraints fbgc = new GridBagConstraints();
        fbgc.fill = GridBagConstraints.HORIZONTAL;
        fbgc.gridx = 0;
        fbgc.gridwidth = 2;
        fbgc.insets = new Insets(0, 0, 16, 0);

        // Título
        JLabel titleLabel = new JLabel("Log in:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        fbgc.gridy = 0;
        formContainer.add(titleLabel, fbgc);

        // Identificación
        fbgc.gridy = 1;
        fbgc.insets = new Insets(0, 0, 4, 0);
        JLabel lblIdentificacion = new JLabel("Identificación");
        lblIdentificacion.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblIdentificacion.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        formContainer.add(lblIdentificacion, fbgc);

        fbgc.gridy = 2;
        fbgc.insets = new Insets(0, 0, 14, 0);
        JTextField idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        idField.setPreferredSize(new Dimension(230, 28));
        formContainer.add(idField, fbgc);

        // Contraseña
        fbgc.gridy = 3;
        fbgc.insets = new Insets(0, 0, 4, 0);
        JLabel lblContrasena = new JLabel("Contraseña");
        lblContrasena.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblContrasena.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        formContainer.add(lblContrasena, fbgc);

        fbgc.gridy = 4;
        fbgc.insets = new Insets(0, 0, 16, 0);
        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        passField.setPreferredSize(new Dimension(230, 28));
        formContainer.add(passField, fbgc);

        // Botón Login
        fbgc.gridy = 5;
        fbgc.insets = new Insets(0, 0, 8, 0);
        fbgc.gridwidth = 2;
        JButton loginButton = crearBoton("Entrar", 110);
        loginButton.addActionListener(e -> {
            String identificacion = idField.getText();
            String contrasena = new String(passField.getPassword());
            JRadioButton selectedRadio = null;
            for (int i = 0; i < rbGroup.getButtonCount(); i++) {
                JRadioButton rb = (JRadioButton) rbGroup.getElements().nextElement();
                if (rb.isSelected()) {
                    selectedRadio = rb;
                    break;
                }
            }
            String rol = selectedRadio != null ? selectedRadio.getText() : "Cliente";
            
            if ("Empleado".equals(rol)) {
                mainFrame.iniciarSesionGestion("Empleado", identificacion, contrasena);
            } else if ("Gestor".equals(rol)) {
                mainFrame.iniciarSesionGestion("Gestor", identificacion, contrasena);
            } else {
                if (!mainFrame.iniciarSesionCliente(identificacion, contrasena)) {
                    JOptionPane.showMessageDialog(this,
                            "Identificación o contraseña incorrecta.",
                            "Login", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        formContainer.add(loginButton, fbgc);

        // Crear cuenta y continuar
        fbgc.gridy = 6;
        fbgc.insets = new Insets(12, 0, 0, 0);
        fbgc.gridwidth = 2;
        fbgc.fill = GridBagConstraints.NONE;
        fbgc.anchor = GridBagConstraints.CENTER;
        
        // Panel con pregunta y botón "Crear cuenta"
        JPanel panelCrearCuenta = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelCrearCuenta.setOpaque(false);
        
        JLabel pregunta = new JLabel("¿No tienes cuenta?");
        pregunta.setFont(new Font("SansSerif", Font.PLAIN, 10));
        pregunta.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        panelCrearCuenta.add(pregunta);
        
        JButton crearButton = crearBoton("Crear cuenta", 70);
        crearButton.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_REGISTRO));
        panelCrearCuenta.add(crearButton);
        
        formContainer.add(panelCrearCuenta, fbgc);

        // Botón continuar sin registrarse
        fbgc.gridy = 7;
        fbgc.insets = new Insets(6, 0, 0, 0);
        fbgc.fill = GridBagConstraints.HORIZONTAL;
        
        JButton continuarButton = crearBoton("Continuar sin registrarse", 200);
        continuarButton.addActionListener(e -> mainFrame.iniciarSesionInvitado());
        formContainer.add(continuarButton, fbgc);

        // Radio buttons de rol
        JRadioButton clienteRadio = new JRadioButton("Cliente", true);
        JRadioButton empleadoRadio = new JRadioButton("Empleado");
        JRadioButton gestorRadio = new JRadioButton("Gestor");
        rbGroup = new ButtonGroup();
        rbGroup.add(clienteRadio);
        rbGroup.add(empleadoRadio);
        rbGroup.add(gestorRadio);

        fbgc.gridy = 8;
        fbgc.insets = new Insets(12, 0, 0, 0);
        fbgc.gridwidth = 2;
        fbgc.fill = GridBagConstraints.NONE;
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        radioPanel.setOpaque(false);
        clienteRadio.setOpaque(false);
        empleadoRadio.setOpaque(false);
        gestorRadio.setOpaque(false);
        clienteRadio.setFont(new Font("SansSerif", Font.PLAIN, 11));
        empleadoRadio.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gestorRadio.setFont(new Font("SansSerif", Font.PLAIN, 11));
        clienteRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        empleadoRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        gestorRadio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        radioPanel.add(clienteRadio);
        radioPanel.add(empleadoRadio);
        radioPanel.add(gestorRadio);
        formContainer.add(radioPanel, fbgc);

        panelCentral.add(formContainer, gbc);
        add(panelCentral, BorderLayout.CENTER);
    }

    private ButtonGroup rbGroup;

    private JButton crearBoton(String texto, int ancho) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 14);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(ancho, 30));
        boton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        return boton;
    }

    private JButton crearBoton(String texto) {
        return crearBoton(texto, 110);
    }
}
