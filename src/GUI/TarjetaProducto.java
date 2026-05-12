package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;


/**
 * Componente Swing de la interfaz grafica correspondiente a TarjetaProducto.
 */
public class TarjetaProducto extends JPanel {

    private static final long serialVersionUID = 1L;
    /** Dato interno asociado a MAX_ESTRELLAS. */
    private static final int MAX_ESTRELLAS = 5;

    /**
     * Crea una tarjeta visual para un producto de tienda.
     *
     * @param producto producto representado por la tarjeta
     */
    public TarjetaProducto(ProductoTienda producto) {
        this(producto, null);
    }

    /**
     * Crea una tarjeta visual para un producto mostrando su stock disponible.
     *
     * @param producto producto representado por la tarjeta
     * @param stockDisponible unidades disponibles del producto
     */
    public TarjetaProducto(ProductoTienda producto, int stockDisponible) {
        this(producto, Integer.valueOf(stockDisponible));
    }

    /**
     * Construye la tarjeta indicando opcionalmente el stock visible.
     *
     * @param producto producto representado por la tarjeta
     * @param stockDisponible unidades disponibles, o null si no se muestran
     */
    private TarjetaProducto(ProductoTienda producto, Integer stockDisponible) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 14, 14, 14));
        int alto = stockDisponible == null ? 282 : 304;
        setPreferredSize(new Dimension(200, alto));
        setMaximumSize(new Dimension(200, alto));
        setMinimumSize(new Dimension(200, alto));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(crearImagen(producto.getImagen()));
        add(Box.createVerticalStrut(10));
        add(crearNombre(producto.getNombre()));
        add(Box.createVerticalStrut(4));
        add(crearEstrellas(producto.getValoracion()));
        add(Box.createVerticalStrut(6));
        if (stockDisponible != null) {
            add(crearStock(stockDisponible));
            add(Box.createVerticalStrut(4));
        }
        add(crearPrecio(producto));
        if (tienePromocion(producto)) {
            add(Box.createVerticalStrut(3));
            add(crearPromocion(producto));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiStyle.COLOR_CABECERA);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
        g2.dispose();
        super.paintComponent(g);
    }

    private JLabel crearImagen(String ruta) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(126, 144));
        label.setMinimumSize(new Dimension(126, 144));
        label.setMaximumSize(new Dimension(126, 144));
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(126, 144, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imagen));
        } else {
            label.setText("<html><center>SIN<br>IMAGEN</center></html>");
            label.setBackground(UiStyle.COLOR_MARRON_MEDIO);
            label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            label.setOpaque(true);
        }
        return label;
    }

    private JLabel crearNombre(String nombre) {
        String texto = nombre.length() > 34 ? nombre.substring(0, 31) + "..." : nombre;
        JLabel label = new JLabel("<html><center>" + texto + "</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel crearEstrellas(int valoracion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (int i = 0; i < MAX_ESTRELLAS; i++) {
            JLabel estrella = new JLabel("*");
            estrella.setFont(new Font("Dialog", Font.BOLD, 18));
            estrella.setForeground(i < valoracion ? new Color(248, 231, 175) : new Color(214, 198, 180));
            panel.add(estrella);
        }
        return panel;
    }

    /**
     * Crea la etiqueta del precio base del producto.
     *
     * @param producto producto cuyo precio se muestra
     * @return etiqueta con el precio base
     */
    private JLabel crearPrecio(ProductoTienda producto) {
        JLabel label = new JLabel(String.format("%.2f EUR", producto.getPrecio()), SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea la etiqueta de stock disponible.
     *
     * @param stockDisponible unidades disponibles
     * @return etiqueta con el stock
     */
    private JLabel crearStock(int stockDisponible) {
        String texto = stockDisponible == 1 ? "Stock: 1 unidad" : "Stock: " + stockDisponible + " unidades";
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(255, 245, 220));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Comprueba si el producto tiene alguna promoción activa.
     *
     * @param producto producto consultado
     * @return true si hay una promoción activa
     */
    private boolean tienePromocion(ProductoTienda producto) {
        return producto.getRebajaPorcentaje() > 0 || producto.getRebajaFija() > 0 || producto.isTiene2x1();
    }

    /**
     * Calcula el precio unitario que se muestra al cliente.
     *
     * @param producto producto consultado
     * @return precio final unitario
     */
    private double precioFinal(ProductoTienda producto) {
        double precio = producto.getPrecio();
        if (producto.getRebajaPorcentaje() > 0) {
            precio -= precio * (producto.getRebajaPorcentaje() / 100.0);
        } else if (producto.getRebajaFija() > 0) {
            precio -= producto.getRebajaFija();
        }
        return Math.max(0, precio);
    }

    /**
     * Crea la etiqueta que resume la promoción activa.
     *
     * @param producto producto con promoción
     * @return etiqueta de promoción
     */
    private JLabel crearPromocion(ProductoTienda producto) {
        String texto = descripcionPromocion(producto);
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(new Color(255, 245, 220));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Devuelve el texto que muestra el descuento y el precio final.
     *
     * @param producto producto con promoción
     * @return descripción de la promoción
     */
    private String descripcionPromocion(ProductoTienda producto) {
        double precioFinal = precioFinal(producto);
        if (producto.isTiene2x1()) {
            return String.format("2x1: %.2f EUR", precioFinal);
        }
        if (producto.getRebajaPorcentaje() > 0) {
            return String.format("-%.0f%%: %.2f EUR", producto.getRebajaPorcentaje(), precioFinal);
        }
        return String.format("-%.2f EUR: %.2f EUR", producto.getRebajaFija(), precioFinal);
    }
}
