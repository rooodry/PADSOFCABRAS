package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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

        JPanel contenedorCentral = new JPanel(new BorderLayout(20, 0));
        contenedorCentral.setBackground(UiStyle.COLOR_FONDO);
        contenedorCentral.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel izquierda = crearPanelIzquierda();
        contenedorCentral.add(izquierda, BorderLayout.WEST);
        
        JPanel derecha = crearPanelDerecha();
        contenedorCentral.add(derecha, BorderLayout.CENTER);
        
        add(contenedorCentral, BorderLayout.CENTER);
        add(crearResumen(), BorderLayout.SOUTH);
        refrescar();
    }

    private JPanel crearPanelIzquierda() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(UiStyle.COLOR_FONDO);
        panel.setPreferredSize(new Dimension(380, 400));
        
        JLabel vacio = new JLabel("(Métodos de pago)");
        vacio.setFont(new Font("SansSerif", Font.ITALIC, 12));
        vacio.setForeground(new Color(150, 150, 150));
        vacio.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(vacio, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel crearPanelDerecha() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiStyle.COLOR_FONDO);
        
        JScrollPane scroll = new JScrollPane(listaProductos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Actualiza la lista de productos y el total de la cesta.
     */
    public void refrescar() {
        listaProductos.removeAll();
        listaProductos.setBackground(UiStyle.COLOR_FONDO);
        listaProductos.setBorder(new EmptyBorder(12, 12, 12, 12));

        Map<ProductoTienda, Integer> productos = mainFrame.getClienteActual().getCesta().getProductos();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);

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
                double precioTotal = precioUnitarioFinal(producto) * unidadesAPagar(producto, cantidad);
                total += precioTotal;
                listaProductos.add(crearTarjetaProducto(producto, cantidad, precioTotal), gbc);
                gbc.gridy++;
            }
        }

        lblTotal.setText(String.format("Total  %.2f€", total));
        listaProductos.revalidate();
        listaProductos.repaint();
    }

    private JPanel crearTarjetaProducto(ProductoTienda producto, int cantidad, double precioTotal) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 12);
        tarjeta.setLayout(new BorderLayout(12, 0));
        tarjeta.setBorder(new EmptyBorder(10, 10, 10, 10));
        tarjeta.setPreferredSize(new Dimension(300, 100));

        // Imagen miniatura
        JLabel imagenLabel = new JLabel();
        imagenLabel.setPreferredSize(new Dimension(80, 80));
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setVerticalAlignment(SwingConstants.CENTER);
        cargarImagenMiniatura(imagenLabel, producto.getImagen());
        tarjeta.add(imagenLabel, BorderLayout.WEST);

        // Panel central: nombre, cantidad, precio
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout(0, 4));
        infoPanel.setBackground(UiStyle.COLOR_TARJETA);
        infoPanel.setOpaque(false);

        JLabel nombre = new JLabel(producto.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        infoPanel.add(nombre, BorderLayout.NORTH);

        JLabel cantidadLabel = new JLabel(cantidad + " ud.");
        cantidadLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cantidadLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(cantidadLabel, BorderLayout.CENTER);

        JLabel precioLabel = new JLabel(String.format("%.2f€", precioTotal));
        precioLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        precioLabel.setForeground(UiStyle.COLOR_MARRON_MEDIO);
        infoPanel.add(precioLabel, BorderLayout.SOUTH);

        tarjeta.add(infoPanel, BorderLayout.CENTER);

        // Botón retirar
        JButton retirar = new UiStyle.RoundedButton("✕", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 14);
        retirar.setFocusPainted(false);
        retirar.setPreferredSize(new Dimension(38, 38));
        retirar.setFont(new Font("SansSerif", Font.BOLD, 14));
        retirar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        retirar.addActionListener(e -> mainFrame.retirarProductoDeCesta(producto));
        tarjeta.add(retirar, BorderLayout.EAST);

        return tarjeta;
    }

    private void cargarImagenMiniatura(JLabel label, String rutaImagen) {
        try {
            if (rutaImagen != null && !rutaImagen.isBlank()) {
                BufferedImage imagen = ImageIO.read(new java.io.File(rutaImagen));
                if (imagen != null) {
                    int ancho = 80;
                    int alto = 80;
                    BufferedImage escalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
                    java.awt.Graphics2D g2d = escalada.createGraphics();
                    g2d.drawImage(imagen, 0, 0, ancho, alto, null);
                    g2d.dispose();
                    label.setIcon(new ImageIcon(escalada));
                    return;
                }
            }
        } catch (IOException e) {
            // Ignorar y mostrar texto por defecto
        }
        label.setText("SIN IMAGEN");
        label.setFont(new Font("SansSerif", Font.PLAIN, 10));
        label.setForeground(new Color(150, 150, 150));
    }

    private JPanel crearResumen() {
        JPanel resumen = new JPanel(new BorderLayout());
        resumen.setBackground(UiStyle.COLOR_CABECERA);
        resumen.setBorder(new EmptyBorder(16, 40, 16, 40));

        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotal.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        resumen.add(lblTotal, BorderLayout.WEST);

        JButton finalizar = new UiStyle.RoundedButton("Finalizar compra", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        finalizar.setFocusPainted(false);
        finalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        finalizar.setPreferredSize(new Dimension(170, 38));
        finalizar.addActionListener(e -> mainFrame.finalizarCompra());
        resumen.add(finalizar, BorderLayout.EAST);

        return resumen;
    }

    private double precioUnitarioFinal(ProductoTienda producto) {
        double precio = producto.getPrecio();
        if (producto.getRebajaPorcentaje() > 0) {
            precio -= precio * (producto.getRebajaPorcentaje() / 100.0);
        } else if (producto.getRebajaFija() > 0) {
            precio -= producto.getRebajaFija();
        }
        return Math.max(0, precio);
    }

    private int unidadesAPagar(ProductoTienda producto, int cantidad) {
        return producto.isTiene2x1() ? cantidad - (cantidad / 2) : cantidad;
    }
}
