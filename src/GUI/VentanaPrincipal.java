package GUI;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().iniciar());
    }

    public void iniciar() {
        JFrame ventana = new JFrame("GOAT & GET");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(500, 650);
        ventana.setLocationRelativeTo(null);


        CardLayout baraja = new CardLayout();
        JPanel contenedorPrincipal = new JPanel(baraja);


        LoginPanel pantallaLogin = new LoginPanel(contenedorPrincipal, baraja);
        PanelRegistro pantallaRegistro = new PanelRegistro(contenedorPrincipal, baraja);


        contenedorPrincipal.add(pantallaLogin, "LOGIN");
        contenedorPrincipal.add(pantallaRegistro, "REGISTRO");

        ventana.add(contenedorPrincipal);
        ventana.setVisible(true);
    }
}
