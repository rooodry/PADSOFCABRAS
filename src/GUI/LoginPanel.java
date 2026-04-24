package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // CONSTRUCTOR: Recibe el contenedor y la baraja de la ventana principal
    public LoginPanel(JPanel contenedorPrincipal, CardLayout baraja) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        // --- ZONA LOGO ---
        JLabel etiquetaLogo = new JLabel("🐐 GOAT & GET 🐐", SwingConstants.CENTER);
        etiquetaLogo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaLogo.setPreferredSize(new Dimension(500, 150));
        this.add(etiquetaLogo, BorderLayout.NORTH);

        // --- ZONA FORMULARIO ---
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(new Color(140, 115, 90)); 
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); 

        JLabel titulo = crearEtiquetaBlanca("Log in:");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField campoId = new JTextField();
        JPasswordField campoPass = new JPasswordField();

        // Botón que nos llevará al registro
        JPanel panelBotonesCuenta = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonesCuenta.setOpaque(false);
        panelBotonesCuenta.add(crearEtiquetaBlanca("¿No tienes cuenta?"));
        JButton btnCrearCuenta = new JButton("Crear cuenta");
        panelBotonesCuenta.add(btnCrearCuenta);

        // Añadimos todo al formulario
        panelFormulario.add(titulo);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 15)));
        panelFormulario.add(crearEtiquetaBlanca("Identificación:"));
        panelFormulario.add(campoId);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFormulario.add(crearEtiquetaBlanca("Contraseña:"));
        panelFormulario.add(campoPass);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 15)));
        panelFormulario.add(panelBotonesCuenta);

        JPanel envolturaCentro = new JPanel(new GridBagLayout());
        envolturaCentro.setBackground(Color.WHITE);
        envolturaCentro.add(panelFormulario);
        this.add(envolturaCentro, BorderLayout.CENTER);

        // --- ACCIÓN: CAMBIAR A REGISTRO ---
        btnCrearCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Usamos la baraja que nos pasaron para cambiar a la carta "REGISTRO"
                baraja.show(contenedorPrincipal, "REGISTRO");
            }
        });
    }

    private JLabel crearEtiquetaBlanca(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }
}
