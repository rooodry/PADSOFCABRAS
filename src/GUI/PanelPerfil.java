package GUI;

<<<<<<< HEAD
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
=======
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * Pantalla de perfil y configuracion del cliente registrado.
 */
public class PanelPerfil extends JPanel {

    private static final long serialVersionUID = 1L;

<<<<<<< HEAD
    private final Main mainFrame;
    private final JLabel lblUsuario;
    private final JLabel lblDni;
    private final JTextField txtNuevoNombre;
=======
    public PanelPerfil() {
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

    /**
     * Crea el panel de perfil.
     *
     * @param mainFrame controlador principal
     */
    public PanelPerfil(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.lblUsuario = new JLabel();
        this.lblDni = new JLabel();
        this.txtNuevoNombre = new JTextField(22);

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "PERFIL"), BorderLayout.NORTH);
        add(crearFormulario(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Actualiza los datos visibles del cliente.
     */
    public void refrescar() {
        lblUsuario.setText("Usuario: " + mainFrame.getClienteActual().getNombre());
        lblDni.setText("Documento: " + mainFrame.getClienteActual().getDNI());
        txtNuevoNombre.setText(mainFrame.getClienteActual().getNombre());
    }

<<<<<<< HEAD
    private JPanel crearFormulario() {
        JPanel envoltura = new JPanel(new GridBagLayout());
        envoltura.setBackground(UiStyle.COLOR_FONDO);
=======

        //SECCION NOTIFICACIONES//
        JLabel etiquetaNotificaciones = new JLabel("Notificaciones");
>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UiStyle.COLOR_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyle.COLOR_CABECERA),
                new EmptyBorder(24, 32, 24, 32)));

<<<<<<< HEAD
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(6, 0, 14, 0);

        JLabel titulo = new JLabel("Mi cuenta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(titulo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 4, 0);
        lblUsuario.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblUsuario, gbc);

        gbc.gridy++;
        lblDni.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblDni, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(18, 0, 6, 0);
        JLabel subtitulo = new JLabel("Notificaciones");
        subtitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        subtitulo.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(subtitulo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(2, 0, 2, 0);
        panel.add(new JCheckBox("Actualizaciones de pedidos", true), gbc);
        gbc.gridy++;
        panel.add(new JCheckBox("Ofertas de intercambios", true), gbc);
        gbc.gridy++;
        panel.add(new JCheckBox("Descuentos de productos", true), gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(20, 0, 4, 8);
        panel.add(new JLabel("Nuevo nombre:"), gbc);

        gbc.gridx = 1;
        txtNuevoNombre.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        panel.add(txtNuevoNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(16, 0, 0, 0);
        JButton guardar = new JButton("Guardar cambios");
        guardar.setBackground(UiStyle.COLOR_TEXTO);
        guardar.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        guardar.setFocusPainted(false);
        guardar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        guardar.addActionListener(e -> mainFrame.cambiarNombreCliente(txtNuevoNombre.getText()));
        panel.add(guardar, gbc);

        envoltura.add(panel);
        return envoltura;
=======

        //SECION CUENTA//
        JLabel etiquetaCuenta = new JLabel("Mi cuenta");
        JButton botonCambiarNombre = new JButton("Cambiar nombre");
        JButton botonCambiarContraseña = new JButton("Cambiar contraseña");


        //SECCION GUARDAR//
        JButton botonGuardar = new JButton("Guardar cambios");
        botonGuardar.setVisible(false);

        botonCambiarNombre.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    JFrame ventana = new JFrame("Cambiar nombre");
                    
                    PanelCambiarNombre panelCambiarNombre = new PanelCambiarNombre();
                    panelCambiarNombre.setVisible(true);
                    botonGuardar.setVisible(true);

                    ventana.add(panelCambiarNombre);
                    ventana.setSize(300, 150);
                    ventana.setLocationRelativeTo(null); 
                    ventana.setVisible(true);
        
                    panelCambiarNombre.getBoton().addActionListener(ev -> ventana.dispose());
                }
            }
        );

        botonCambiarContraseña.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JFrame ventana = new JFrame("Cambiar contraseña");
                    PanelCambiarContraseña panelCambiarContraseña = new PanelCambiarContraseña();
                    panelCambiarContraseña.setVisible(true);
                    botonGuardar.setVisible(true);

                    ventana.add(panelCambiarContraseña);
                    ventana.setSize(300, 150);
                    ventana.setLocationRelativeTo(null); 
                    ventana.setVisible(true);

                    
                }
            }
        );

        botonGuardar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    
                    ventana.dispose();
                }
            }
        );

        

        ventana.add(etiquetaConfiguracion);
        ventana.add(etiquetaNotificaciones);
        ventana.add(casilla1);
        ventana.add(casilla2);
        ventana.add(casilla3);
        ventana.add(casilla4);
        ventana.add(etiquetaCuenta);
        ventana.add(botonCambiarNombre);
        ventana.add(botonCambiarContraseña);
        ventana.add(botonGuardar);


        ventana.setSize(400, 400);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);

>>>>>>> 334c8ba93cb956f4c93ad768b9567505ab7036e9
    }
}
