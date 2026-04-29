package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;




public class PanelPerfil extends JPanel{

    public PanelPerfil() {


        JFrame ventana = new JFrame("GOAT & GET");
        ventana.setLayout(new FlowLayout());

        JLabel etiquetaConfiguracion = new JLabel("Configuración");


        //SECCION NOTIFICACIONES//
        JLabel etiquetaNotificaciones = new JLabel("Notificaciones");

        JCheckBox casilla1 = new JCheckBox("Actualizaciones de pedidos");
        JCheckBox casilla2 = new JCheckBox("Ofertas de intercambios");
        JCheckBox casilla3 = new JCheckBox("Descuentos de productos");
        JCheckBox casilla4 = new JCheckBox("Actualizaciones de novedades");


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

    }
}
