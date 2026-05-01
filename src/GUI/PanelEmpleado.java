package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;
import productos.Stock;

/**
 * Initial employee dashboard interface with read-only stock overview.
 */
public class PanelEmpleado extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel contenido;

    public PanelEmpleado(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.contenido = new JPanel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "GESTION"), BorderLayout.NORTH);
        add(crearScroll(), BorderLayout.CENTER);
        refrescar();
    }

    public void refrescar() {
        contenido.removeAll();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(UiStyle.COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(22, 34, 22, 34));

        contenido.add(crearTitulo("Panel Empleado"));
        contenido.add(crearSubtitulo("Visualizacion de stock disponible (solo lectura)."));

        contenido.add(crearTitulo("Inventario de tienda"));
        JPanel stockPanel = new JPanel();
        stockPanel.setOpaque(false);
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        Stock stock = mainFrame.getStock();
        List<ProductoTienda> productos = mainFrame.getProductosTienda();

        if (productos.isEmpty()) {
            stockPanel.add(crearEtiqueta("No hay productos en el inventario."));
        } else {
            for (ProductoTienda producto : productos) {
                stockPanel.add(crearFilaStock(producto, stock.getNumProductos(producto)));
            }
        }
        contenido.add(stockPanel);

        contenido.add(crearTitulo("Tareas rapidas"));
        contenido.add(crearEtiqueta("Pedidos y cambios en curso: " + mainFrame.getIntercambios().size()));

        contenido.revalidate();
        contenido.repaint();
    }

    private JScrollPane crearScroll() {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(14, 0, 10, 0));
        return titulo;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(0, 0, 18, 0));
        return label;
    }

    private JPanel crearFilaStock(ProductoTienda producto, int cantidad) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel label = crearEtiqueta(producto.getNombre() + " | stock: " + cantidad
                + " | precio: " + String.format("%.2f EUR", producto.getPrecio()));
        fila.add(label, BorderLayout.CENTER);

        JButton ver = crearBoton("Ver producto");
        ver.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_HOME));
        fila.add(ver, BorderLayout.EAST);
        return fila;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(118, 32));
        return boton;
    }
}
