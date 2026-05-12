package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Componente Swing de la interfaz grafica correspondiente a LogIn.
 */
public class LogIn {


    /** Dato interno asociado a ventana. */
    private JFrame ventana;
    /** Dato interno asociado a panelContenedor. */
    private JPanel panelContenedor;
    /** Dato interno asociado a cardLayout. */
    private CardLayout cardLayout;

    /**
     * Construye una instancia de LogIn.
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
            @Override
            /**
             * Gestiona la accion de actionPerformed.
             * @param e valor recibido por el metodo
             */
            public void actionPerformed(ActionEvent e) {

                cardLayout.show(panelContenedor, "PANTALLA_REGISTRO");
            }
        });

        return panelPrincipal;
    }


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
            @Override
            /**
             * Gestiona la accion de actionPerformed.
             * @param e valor recibido por el metodo
             */
            public void actionPerformed(ActionEvent e) {

                cardLayout.show(panelContenedor, "PANTALLA_LOGIN");
            }
        });

        return panelPrincipal;
    }


    private JLabel crearEtiquetaBlanca(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }


    /**
     * Punto de entrada de la aplicacion.
     * @param args valor recibido por el metodo
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            /**
             * Gestiona la accion de run.
             */
            public void run() {
                new LogIn();
            }
        });
    }
}
