package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import compras.Pedido;
import productos.ProductoTienda;
import utilidades.EstadoPedido;

/**
 * Basic management panel for employee and manager tasks.
 */
public class PanelGestion extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel contenido;

    /**
     * Builds the management panel.
     *
     * @param mainFrame main GUI controller
     */
    public PanelGestion(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.contenido = new JPanel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "GESTION"), BorderLayout.NORTH);
        add(crearScroll(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Refreshes stock, order and statistics widgets.
     */
    public void refrescar() {
        contenido.removeAll();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(UiStyle.COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(22, 34, 22, 34));

        contenido.add(crearTitulo("Gestion de stock"));
        JPanel stock = new JPanel(new GridLayout(0, 1, 8, 8));
        stock.setOpaque(false);
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            stock.add(crearFilaStock(producto));
        }
        contenido.add(stock);

        contenido.add(crearTitulo("Pedidos del cliente activo"));
        JPanel pedidos = new JPanel(new GridLayout(0, 1, 8, 8));
        pedidos.setOpaque(false);
        for (Pedido pedido : mainFrame.getClienteActual().getPedidos()) {
            pedidos.add(crearFilaPedido(pedido));
        }
        contenido.add(pedidos);

        contenido.add(crearTitulo("Estadisticas rapidas"));
        contenido.add(crearEtiqueta("Productos catalogo: " + mainFrame.getProductosTienda().size()
                + " | Pedidos: " + mainFrame.getClienteActual().getPedidos().size()
                + " | Intercambios: " + mainFrame.getIntercambios().size()));

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
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(12, 0, 10, 0));
        return titulo;
    }

    private JPanel crearFilaStock(ProductoTienda producto) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel label = crearEtiqueta(producto.getNombre() + " | stock: "
                + mainFrame.getStock().getNumProductos(producto)
                + " | precio: " + String.format("%.2f EUR", producto.getPrecio()));
        fila.add(label, BorderLayout.CENTER);

        JButton sumar = crearBoton("+5 stock");
        sumar.addActionListener(e -> mainFrame.sumarStockProducto(producto, 5));
        fila.add(sumar, BorderLayout.EAST);
        return fila;
    }

    private JPanel crearFilaPedido(Pedido pedido) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(10, 14, 10, 14));

        StringBuilder productos = new StringBuilder();
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            if (productos.length() > 0) {
                productos.append(", ");
            }
            productos.append(entry.getKey().getNombre()).append(" x").append(entry.getValue());
        }
        fila.add(crearEtiqueta(pedido.getEstadoPedido() + " | " + productos), BorderLayout.CENTER);

        JButton listo = crearBoton("Listo");
        listo.setEnabled(pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION);
        listo.addActionListener(e -> mainFrame.prepararPedido(pedido));
        fila.add(listo, BorderLayout.EAST);
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
        boton.setPreferredSize(new Dimension(108, 32));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }
}
