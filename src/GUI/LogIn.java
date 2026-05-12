package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Representa la ventana de inicio de sesión y registro de la aplicación.
 * Gestiona la interfaz gráfica mediante la cual los usuarios pueden autenticarse
 * o crear una cuenta nueva utilizando un diseño de tarjetas (CardLayout) para alternar las vistas.
 */
public class LogIn {

    /** Ventana principal de la interfaz de inicio de sesión. */
    private JFrame ventana;
    
    /** Panel contenedor principal que gestiona las distintas pantallas de la interfaz. */
    private JPanel panelContenedor;
    
    /** Gestor de diseño utilizado para alternar entre la pantalla de login y la de registro. */
    private CardLayout cardLayout;

    /**
     * Construye e inicializa la ventana de inicio de sesión.
     * Configura el marco, los diseños y añade los paneles de login y registro al contenedor principal.
     */
    public LogIn() {

        ventana = new JFrame("GOAT & GET");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(500, 650);
        ventana.setLocationRelativeTo(null);
        ventana.getContentPane().setBackground(Color.WHITE);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(Color.WHITE);

        JPanel pantallaLogin = crearPantallaLogin();
        JPanel pantallaRegistro = crearPantallaRegistro();

        panelContenedor.add(pantallaLogin, "PANTALLA_LOGIN");
        panelContenedor.add(pantallaRegistro, "PANTALLA_REGISTRO");

        ventana.add(panelContenedor);
        ventana.setVisible(true);
    }

    /**
     * Crea y configura el panel correspondiente a la pantalla de inicio de sesión.
     * Incluye los campos de identificación, contraseña, selección de rol y botones de acción.
     *
     * @return El panel de inicio de sesión completamente configurado.
     */
    private JPanel crearPantallaLogin() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);

        JLabel etiquetaLogo = new JLabel("🐐 GOAT & GET (LOGO) 🐐", SwingConstants.CENTER);
        etiquetaLogo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaLogo.setPreferredSize(new Dimension(500, 150));
        panelPrincipal.add(etiquetaLogo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(new Color(140, 115, 90));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = crearEtiquetaBlanca("Log in:");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));

        JTextField campoId = new JTextField();
        JPasswordField campoPass = new JPasswordField();

        JPanel panelBotonesCuenta = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonesCuenta.setOpaque(false);
        panelBotonesCuenta.add(crearEtiquetaBlanca("¿No tienes cuenta?"));
        JButton btnCrearCuenta = new JButton("Crear cuenta");
        panelBotonesCuenta.add(btnCrearCuenta);

        JButton btnSinRegistro = new JButton("Continuar sin registrarse");
        btnSinRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panelRadios = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelRadios.setOpaque(false);
        JRadioButton radioCliente = new JRadioButton("Cliente");
        JRadioButton radioEmpleado = new JRadioButton("Empleado");
        JRadioButton radioGestor = new JRadioButton("Gestor");
        radioCliente.setForeground(Color.WHITE); radioCliente.setOpaque(false);
        radioEmpleado.setForeground(Color.WHITE); radioEmpleado.setOpaque(false);
        radioGestor.setForeground(Color.WHITE); radioGestor.setOpaque(false);

        ButtonGroup grupoRoles = new ButtonGroup();
        grupoRoles.add(radioCliente); grupoRoles.add(radioEmpleado); grupoRoles.add(radioGestor);
        panelRadios.add(radioCliente); panelRadios.add(radioEmpleado); panelRadios.add(radioGestor);

        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(titulo);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 15)));

        panelFormulario.add(crearEtiquetaBlanca("Identificación"));
        panelFormulario.add(campoId);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));

        panelFormulario.add(crearEtiquetaBlanca("Contraseña"));
        panelFormulario.add(campoPass);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 15)));

        panelFormulario.add(panelBotonesCuenta);
        panelFormulario.add(btnSinRegistro);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFormulario.add(panelRadios);

        JPanel envolturaCentro = new JPanel(new GridBagLayout());
        envolturaCentro.setBackground(Color.WHITE);
        envolturaCentro.add(panelFormulario);
        panelPrincipal.add(envolturaCentro, BorderLayout.CENTER);

        btnCrearCuenta.addActionListener(new ActionListener() {
            /**
             * Cambia la vista actual a la pantalla de registro al presionar el botón.
             * 
             * @param e El evento de acción generado por el botón.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenedor, "PANTALLA_REGISTRO");
            }
        });

        return panelPrincipal;
    }

    /**
     * Crea y configura el panel correspondiente a la pantalla de registro de nuevos usuarios.
     * Incluye los campos para identificación, contraseña, documento de identidad y botones de acción.
     *
     * @return El panel de registro completamente configurado.
     */
    private JPanel crearPantallaRegistro() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);

        JLabel etiquetaLogo = new JLabel("🐐 GOAT & GET (LOGO) 🐐", SwingConstants.CENTER);
        etiquetaLogo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaLogo.setPreferredSize(new Dimension(500, 150));
        panelPrincipal.add(etiquetaLogo, BorderLayout.NORTH);

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(new Color(140, 115, 90));
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel titulo = crearEtiquetaBlanca("Registro:");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField campoId = new JTextField();
        JPasswordField campoPass = new JPasswordField();
        JTextField campoDni = new JTextField();

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnContinuar = new JButton("Continuar");
        panelBotones.add(btnCancelar);
        panelBotones.add(btnContinuar);

        panelFormulario.add(titulo);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 15)));
        panelFormulario.add(crearEtiquetaBlanca("Identificación"));
        panelFormulario.add(campoId);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFormulario.add(crearEtiquetaBlanca("Contraseña"));
        panelFormulario.add(campoPass);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 10)));
        panelFormulario.add(crearEtiquetaBlanca("Documento de identidad"));
        panelFormulario.add(campoDni);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 20)));
        panelFormulario.add(panelBotones);

        JPanel envolturaCentro = new JPanel(new GridBagLayout());
        envolturaCentro.setBackground(Color.WHITE);
        envolturaCentro.add(panelFormulario);
        panelPrincipal.add(envolturaCentro, BorderLayout.CENTER);

        btnCancelar.addActionListener(new ActionListener() {
            /**
             * Cancela el proceso de registro y vuelve a la pantalla de inicio de sesión.
             * 
             * @param e El evento de acción generado por el botón.
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(panelContenedor, "PANTALLA_LOGIN");
            }
        });

        return panelPrincipal;
    }

    /**
     * Crea una etiqueta de texto con alineación a la izquierda y color de fuente blanco.
     * Método auxiliar para mantener la consistencia visual en los formularios.
     *
     * @param texto El texto que mostrará la etiqueta.
     * @return La etiqueta (JLabel) configurada con el estilo especificado.
     */
    private JLabel crearEtiquetaBlanca(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    /**
     * Punto de entrada principal para ejecutar y visualizar la ventana de inicio de sesión
     * de forma independiente. Útil para pruebas de interfaz.
     *
     * @param args Argumentos de la línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            /**
             * Hilo de ejecución de Swing encargado de instanciar y mostrar la interfaz gráfica.
             */
            public void run() {
                new LogIn();
            }
        });
    }
}