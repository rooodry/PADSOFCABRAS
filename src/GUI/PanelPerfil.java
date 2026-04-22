package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Flow;



public class PanelPerfil extends JPanel{

    PanelPerfil() {


        JFrame ventana = new JFrame("GOAT & GET");
        ventana.setLayout(new FlowLayout());

        JLabel etiquetaConfiguracion = new JLabel("Configuración");

        JLabel etiquetaNotificaciones = new JLabel("Notificaciones");

        JCheckBox casilla1 = new JCheckBox("Actualizaciones de pedidos");
        JCheckBox casilla2 = new JCheckBox("Ofertas de intercambios");
        JCheckBox casilla3 = new JCheckBox("Descuentos de productos");
        JCheckBox casilla4 = new JCheckBox("Actualizaciones de novedades");

        JLabel etiquetaCuenta = new JLabel("Mi cuenta");
        JButton botonCambiarNombre = new JButton("Cambiar nombre");

        botonCambiarNombre.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    PanelCambiarNombre panelCambiarNombre = new PanelCambiarNombre();
                    panelCambiarNombre.setVisible(true);
                }
            }
        );










    }







}
