package GUI;
import java.awt.*;
import javax.swing.*;

public class Main extends JFrame {
    private JPanel panelContenedor;
    private CardLayout cardLayout;

    public Main() {
        setTitle("GOAT & GET - App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Hacemos la ventana más grande y panorámica para que la Home se vea bien
        setSize(900, 600); 
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // Añadimos todas las pantallas a la "baraja"
        panelContenedor.add(new LoginClientePanel(this), "PANTALLA_CLIENTE");
        panelContenedor.add(new LoginGestorPanel(this), "PANTALLA_GESTOR");
        panelContenedor.add(new RegistroPanel(this), "PANTALLA_REGISTRO");
        
        // Añadimos la nueva pantalla HOME
        panelContenedor.add(new HomePanel(this), "PANTALLA_HOME");

        add(panelContenedor);
    }

    public void cambiarPantalla(String nombrePantalla) {
        cardLayout.show(panelContenedor, nombrePantalla);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main ventana = new Main();
            ventana.setVisible(true);
        });
    }
}