package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * Representa el componente VentanaPrincipal de la interfaz grafica.
 */
public class VentanaPrincipal {

    /**
     * Construye la ventana principal auxiliar.
     */
    public VentanaPrincipal() {
    }

    /**
     * Punto de entrada de la aplicacion.
     * @param args parametro utilizado por la operacion
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().iniciar());
    }

    /**
     * Ejecuta la operacion publica iniciar.
     */
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
