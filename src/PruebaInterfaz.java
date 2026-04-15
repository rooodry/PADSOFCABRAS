import javax.swing.*;
import java.awt.*;

public class PruebaInterfaz {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> crearVentana());
    }

    static void crearVentana() {
        JFrame ventana = new JFrame("Alta de Producto");
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(400, 300);
        ventana.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel labelNombre = new JLabel("Nombre:");
        JTextField campoNombre = new JTextField();
        JLabel labelPrecio = new JLabel("Precio:");
        JTextField campoPrecio = new JTextField();

        JButton boton = new JButton("Dar de alta");

        panel.add(labelNombre);
        panel.add(campoNombre);
        panel.add(labelPrecio);
        panel.add(campoPrecio);
        panel.add(new JLabel());
        panel.add(boton);

        boton.addActionListener(e -> {
            JOptionPane.showMessageDialog(ventana, "Producto: " + campoNombre.getText());
        });

        ventana.add(panel);
        ventana.setVisible(true);
    }
}
