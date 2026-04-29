package GUI;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
    private JPanel panelContenedor;
    private CardLayout cardLayout;

    public Main() {
        setTitle("GOAT & GET - App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);

        // 1. Inicializamos el CardLayout (la baraja) y el contenedor principal
        cardLayout = new CardLayout();
        panelContenedor = new JPanel(cardLayout);

        // 2. Añadimos todas tus pantallas a la "baraja" y les ponemos una "Etiqueta" secreta
        // IMPORTANTE: Le pasamos 'this' (esta ventana Main) para que los paneles puedan comunicarse con ella
        panelContenedor.add(new LoginClientePanel(this), "PANTALLA_CLIENTE");
        panelContenedor.add(new LoginGestorPanel(this), "PANTALLA_GESTOR");
        panelContenedor.add(new RegistroPanel(this), "PANTALLA_REGISTRO");
        // Cuando crees la de empleado, la añades así: panelContenedor.add(new LoginEmpleadoPanel(this), "PANTALLA_EMPLEADO");

        // Añadimos el contenedor a la ventana
        add(panelContenedor);
    }

    // 3. Este es el método mágico que las pantallas llamarán para cambiar la vista
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