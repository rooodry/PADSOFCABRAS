package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dimension;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;

/**
 * Pantalla de cesta del cliente registrado.
 *
 * <p>Muestra los productos actualmente contenidos en la {@code Cesta} del
 * cliente y permite retirar lineas o formalizar el pedido usando los metodos
 * del modelo.</p>
 */
public class PanelCesta extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel listaProductos;
    private final JLabel lblTotal;

    /**
     * Crea la pantalla de cesta.
     *
     * @param mainFrame controlador principal de la aplicacion
     */
    public PanelCesta(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.listaProductos = new JPanel(new GridBagLayout());
        this.lblTotal = new JLabel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "CESTA"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        add(crearResumen(), BorderLayout.SOUTH);
        refrescar();
    }

    /**
     * Actualiza la lista de productos y el total de la cesta.
     */
    public void refrescar() {
        listaProductos.removeAll();
        listaProductos.setBackground(UiStyle.COLOR_FONDO);
        listaProductos.setBorder(new EmptyBorder(18, 28, 18, 28));

        Map<ProductoTienda, Integer> productos = mainFrame.getClienteActual().getCesta().getProductos();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        double total = 0.0;
        if (productos.isEmpty()) {
            JLabel vacia = new JLabel("La cesta esta vacia.", SwingConstants.CENTER);
            vacia.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacia.setForeground(UiStyle.COLOR_TEXTO);
            listaProductos.add(vacia, gbc);
        } else {
            for (Map.Entry<ProductoTienda, Integer> entrada : productos.entrySet()) {
                ProductoTienda producto = entrada.getKey();
                int cantidad = entrada.getValue();
                total += producto.getPrecio() * cantidad;
                listaProductos.add(crearFila(producto, cantidad), gbc);
                gbc.gridy++;
            }
        }

        lblTotal.setText(String.format("Total: %.2f EUR", total));
        listaProductos.revalidate();
        listaProductos.repaint();
    }

    private JScrollPane crearContenido() {
        JScrollPane scroll = new JScrollPane(listaProductos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        return scroll;
    }

    private JPanel crearResumen() {
        JPanel resumen = new JPanel(new BorderLayout());
        resumen.setBackground(UiStyle.COLOR_FONDO);
        resumen.setBorder(new EmptyBorder(12, 28, 20, 28));

        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotal.setForeground(UiStyle.COLOR_TEXTO);
        resumen.add(lblTotal, BorderLayout.WEST);

        JButton finalizar = new UiStyle.RoundedButton("Finalizar compra", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 20);
        finalizar.setBackground(UiStyle.COLOR_TEXTO);
        finalizar.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        finalizar.setFocusPainted(false);
        finalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        finalizar.setPreferredSize(new Dimension(170, 38));
        finalizar.addActionListener(e -> mainFrame.finalizarCompra());
        resumen.add(finalizar, BorderLayout.EAST);

        return resumen;
    }

    private JPanel crearFila(ProductoTienda producto, int cantidad) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 22);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(12, 14, 12, 14));

        JLabel datos = new JLabel(String.format("%s  x%d  -  %.2f EUR",
                producto.getNombre(), cantidad, producto.getPrecio() * cantidad));
        datos.setFont(new Font("SansSerif", Font.BOLD, 14));
        datos.setForeground(UiStyle.COLOR_TEXTO);
        fila.add(datos, BorderLayout.CENTER);

        JButton retirar = new UiStyle.RoundedButton("Retirar", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        retirar.setFocusPainted(false);
        retirar.setPreferredSize(new Dimension(92, 32));
        retirar.addActionListener(e -> mainFrame.retirarProductoDeCesta(producto));
        fila.add(retirar, BorderLayout.EAST);

        return fila;
    }
}
