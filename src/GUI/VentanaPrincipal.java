package GUI;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal {

    public static void main(String[] args) {
        // Arrancamos el programa
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().iniciar());
    }

    public void iniciar() {
        JFrame ventana = new JFrame("GOAT & GET");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(500, 650);
        ventana.setLocationRelativeTo(null); 

        // Creamos la "baraja" y el panel que la contiene
        CardLayout baraja = new CardLayout();
        JPanel contenedorPrincipal = new JPanel(baraja);

        // INSTANCIAMOS TUS DOS CLASES (Les pasamos el contenedor y la baraja para que puedan cambiar de pantalla)
        LoginPanel pantallaLogin = new LoginPanel(contenedorPrincipal, baraja);
        PanelRegistro pantallaRegistro = new PanelRegistro(contenedorPrincipal, baraja);

        // Añadimos las pantallas a la baraja con su nombre en clave
        contenedorPrincipal.add(pantallaLogin, "LOGIN");
        contenedorPrincipal.add(pantallaRegistro, "REGISTRO");

        ventana.add(contenedorPrincipal);
        ventana.setVisible(true);
    }
}
