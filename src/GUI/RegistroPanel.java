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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Formulario de registro de clientes.
 */
public class RegistroPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Crea el panel de registro conectado con la ventana principal.
     *
     * @param mainFrame controlador principal
     */
    public RegistroPanel(Main mainFrame) {
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new GoatGetHeader(), BorderLayout.NORTH);

        JPanel formContainer = new UiStyle.RoundedPanel(UiStyle.COLOR_CABECERA, 30);
        formContainer.setLayout(new GridBagLayout());
        formContainer.setBorder(new EmptyBorder(30, 40, 30, 40));

        JTextField idField = crearCampo();
        JPasswordField passField = new JPasswordField();
        passField.setPreferredSize(new Dimension(260, 32));
        JTextField docField = crearCampo();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        JLabel titleLabel = new JLabel("Registro", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        gbc.gridy = 0;
        formContainer.add(titleLabel, gbc);

        gbc.gridy = 1;
        formContainer.add(crearEtiqueta("Identificacion"), gbc);
        gbc.gridy = 2;
        formContainer.add(idField, gbc);

        gbc.gridy = 3;
        formContainer.add(crearEtiqueta("Contrasena"), gbc);
        gbc.gridy = 4;
        formContainer.add(passField, gbc);

        gbc.gridy = 5;
        formContainer.add(crearEtiqueta("Documento de identidad"), gbc);
        gbc.gridy = 6;
        formContainer.add(docField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setOpaque(false);
        JButton cancelButton = crearBoton("Cancelar");
        cancelButton.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_CLIENTE));
        JButton continueButton = crearBoton("Continuar");
        continueButton.addActionListener(e -> registrar(mainFrame, idField, passField, docField));
        buttonPanel.add(cancelButton);
        buttonPanel.add(continueButton);

        gbc.gridy = 7;
        gbc.insets = new Insets(22, 0, 0, 0);
        formContainer.add(buttonPanel, gbc);

        JPanel envoltura = new JPanel(new GridBagLayout());
        envoltura.setBackground(UiStyle.COLOR_FONDO);
        envoltura.add(formContainer);
        add(envoltura, BorderLayout.CENTER);
    }

    private void registrar(Main mainFrame, JTextField idField, JPasswordField passField, JTextField docField) {
        String nombre = idField.getText().trim();
        String contrasena = new String(passField.getPassword()).trim();
        String dni = docField.getText().trim();

        if (nombre.isEmpty() || contrasena.isEmpty() || dni.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Completa todos los campos.", "Registro",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.registrarCliente(nombre, contrasena, dni);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        return etiqueta;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setPreferredSize(new Dimension(260, 32));
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(112, 34));
        boton.setFocusPainted(false);
        return boton;
    }
}
