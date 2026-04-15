import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


import productos.*;
import GUI.*;



public class Main2 {

    private static List<ProductoTienda> productos;

    private Main2() {}

    public static void main(String[] args) {
        
        cargarProductos();
        inicializarGUI();



    }

    private static void cargarProductos() {
        productos = new ArrayList<ProductoTienda>();
        productos.add(new ProductoTienda("Producto1", "67", null));
        productos.add(new ProductoTienda("Producto2", "67", null));
        productos.add(new ProductoTienda("Producto3", "67", null));
    }

    private static void inicializarGUI() {
        JFrame ventana = new JFrame("PADSOF CABRAS");
        Container contenedor = ventana.getContentPane();
        contenedor.setLayout(new FlowLayout());

        JPanel panel = new PanelProductos("Productos", productos);

        contenedor.add(panel);

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setLocationRelativeTo(null);
    }
    
}
