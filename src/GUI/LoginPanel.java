package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Representa el panel de inicio de sesión de la interfaz gráfica.
 * Proporciona la estructura visual para que un usuario introduzca su identificación
 * y contraseña, además de permitir la navegación hacia la pantalla de registro
 * mediante un gestor de diseño de tarjetas.
 */
public class LoginPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Construye e inicializa el panel de inicio de sesión.
     * Configura el diseño visual, los campos de entrada de credenciales y el botón
     * encargado de transitar a la vista de creación de cuenta.
     * 
     * @param contenedorPrincipal El panel contenedor padre que aloja las distintas vistas.
     * @param baraja              El gestor de diseño ({@link CardLayout}) utilizado para alternar entre las pantallas.
     */
    public LoginPanel(JPanel contenedorPrincipal, CardLayout baraja) {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        JLabel etiquetaLogo = new JLabel("🐐 GOAT & GET 🐐", SwingConstants.CENTER);
        etiquetaLogo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaLogo.setPreferredSize(new Dimension(500, 150));
        this.add(etiquetaLogo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(new Color(140, 115, 90));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = crearEtiquetaBlanca("Log in:");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField campoId = new JTextField();
        JPasswordField campoPass = new JPasswordField();

        JPanel panelBotonesCuenta = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonesCuenta.setOpaque(false);
        panelBotonesCuenta.add(crearEtiquetaBlanca("¿No tienes cuenta?"));
        JButton btnCrearCuenta = new JButton("Crear cuenta");
        panelBotonesCuenta.add(btnCrearCuenta);

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

        btnCrearCuenta.addActionListener(new ActionListener() {
            /**
             * Cambia la vista actual a la pantalla de registro al presionar el botón de crear cuenta.
             * 
             * @param e El evento de acción generado por el botón.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                baraja.show(contenedorPrincipal, "REGISTRO");
            }
        });
    }

    /**
     * Crea una etiqueta de texto con alineación a la izquierda y color de fuente blanco.
     * Método auxiliar diseñado para mantener la consistencia visual dentro de los paneles
     * con fondo oscuro.
     *
     * @param texto El texto que mostrará la etiqueta.
     * @return La etiqueta ({@link JLabel}) configurada con el estilo especificado.
     */
    private JLabel crearEtiquetaBlanca(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }
}