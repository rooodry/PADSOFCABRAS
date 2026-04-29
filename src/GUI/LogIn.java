package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LogIn {

    // Variables globales para controlar las "cartas" (pantallas)
    private JFrame ventana;
    private JPanel panelContenedor;
    private CardLayout cardLayout;

    public LogIn() {
        // 1. Configurar la ventana principal
        ventana = new JFrame("GOAT & GET");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(500, 650);
        ventana.setLocationRelativeTo(null); // Centrar en pantalla
        ventana.getContentPane().setBackground(Color.WHITE); // Fondo blanco general

        // 2. Configurar el CardLayout (La baraja de cartas)
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);
        panelContenedor.setBackground(Color.WHITE);

        // 3. Crear nuestras dos pantallas (cartas)
        JPanel pantallaLogin = crearPantallaLogin();
        JPanel pantallaRegistro = crearPantallaRegistro();

        // 4. Añadir las pantallas a la baraja con un "Nombre Clave"
        panelContenedor.add(pantallaLogin, "PANTALLA_LOGIN");
        panelContenedor.add(pantallaRegistro, "PANTALLA_REGISTRO");

        // Añadir la baraja a la ventana y mostrar
        ventana.add(panelContenedor);
        ventana.setVisible(true);
    }

    private JPanel crearPantallaLogin() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);

        // --- Zona Superior: LOGO ---
        // Aquí puedes cambiar el texto por tu imagen real (te explico abajo cómo)
        JLabel etiquetaLogo = new JLabel("🐐 GOAT & GET (LOGO) 🐐", SwingConstants.CENTER);
        etiquetaLogo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaLogo.setPreferredSize(new Dimension(500, 150));
        panelPrincipal.add(etiquetaLogo, BorderLayout.NORTH);

        // --- Zona Central: FORMULARIO MARRÓN ---
        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new BoxLayout(panelFormulario, BoxLayout.Y_AXIS));
        panelFormulario.setBackground(new Color(140, 115, 90)); // Color marrón aproximado
        panelFormulario.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40)); // Márgenes internos

        // Componentes
        JLabel titulo = crearEtiquetaBlanca("Log in:");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JTextField campoId = new JTextField();
        JPasswordField campoPass = new JPasswordField();

        // Botones extra
        JPanel panelBotonesCuenta = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBotonesCuenta.setOpaque(false); // Transparente para que se vea el marrón
        panelBotonesCuenta.add(crearEtiquetaBlanca("¿No tienes cuenta?"));
        JButton btnCrearCuenta = new JButton("Crear cuenta");
        panelBotonesCuenta.add(btnCrearCuenta);

        JButton btnSinRegistro = new JButton("Continuar sin registrarse");
        btnSinRegistro.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Radio Buttons
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

        // Añadir todo al formulario apilado
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelFormulario.add(titulo);
        panelFormulario.add(Box.createRigidArea(new Dimension(0, 15))); // Espaciador
        
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

        // Envolver el formulario para que no ocupe toda la pantalla, sino que quede en el centro
        JPanel envolturaCentro = new JPanel(new GridBagLayout());
        envolturaCentro.setBackground(Color.WHITE);
        envolturaCentro.add(panelFormulario);
        panelPrincipal.add(envolturaCentro, BorderLayout.CENTER);

        // --- ACCIÓN DEL BOTÓN: CAMBIAR PANTALLA ---
        btnCrearCuenta.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ¡LA MAGIA OCURRE AQUÍ! Le decimos al layout que muestre la otra carta
                cardLayout.show(panelContenedor, "PANTALLA_REGISTRO");
            }
        });

        return panelPrincipal;
    }

    // ==========================================================
    // MÉTODO PARA CREAR LA PANTALLA DE REGISTRO
    // ==========================================================
    private JPanel crearPantallaRegistro() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(Color.WHITE);

        // --- Zona Superior: LOGO ---
        JLabel etiquetaLogo = new JLabel("🐐 GOAT & GET (LOGO) 🐐", SwingConstants.CENTER);
        etiquetaLogo.setFont(new Font("Arial", Font.BOLD, 24));
        etiquetaLogo.setPreferredSize(new Dimension(500, 150));
        panelPrincipal.add(etiquetaLogo, BorderLayout.NORTH);

        // --- Zona Central: FORMULARIO MARRÓN ---
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

        // Botones Cancelar / Continuar
        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnContinuar = new JButton("Continuar");
        panelBotones.add(btnCancelar);
        panelBotones.add(btnContinuar);

        // Añadir elementos al formulario
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

        // --- ACCIÓN DEL BOTÓN: VOLVER AL LOGIN ---
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Volvemos a mostrar la carta del Login
                cardLayout.show(panelContenedor, "PANTALLA_LOGIN");
            }
        });

        return panelPrincipal;
    }

    // Método auxiliar para no repetir código creando texto blanco
    private JLabel crearEtiquetaBlanca(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(Color.WHITE);
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return etiqueta;
    }

    // Motor de arranque
    public static void main(String[] args) {
        // Esto asegura que la interfaz gráfica se ejecute correctamente en su propio hilo
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LogIn();
            }
        });
    }
}