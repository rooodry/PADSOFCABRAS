package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import productos.ProductoTienda;

/**
 * Tarjeta visual que representa un {@link ProductoTienda} dentro de la
 * cuadrícula del catálogo ({@link PanelProductos}).
 *
 * <p>Muestra la imagen del producto (o un placeholder), su nombre, la
 * valoración en estrellas y el precio. El cursor cambia a manita para
 * indicar que es clicable.</p>
 *
 * <p>El tamaño preferido es de 190 × 240 píxeles para encajar bien en
 * una cuadrícula de tres columnas en una ventana de 700 px de ancho.</p>
 */
public class TarjetaProducto extends JPanel {

    /** Color de fondo de la tarjeta (ligeramente más claro que el fondo general). */
    private static final Color COLOR_TARJETA       = new Color(0xD4, 0xB8, 0x8A);

    /** Color del texto principal. */
    private static final Color COLOR_TEXTO         = new Color(0x2B, 0x1F, 0x0E);

    /** Color de las estrellas rellenas. */
    private static final Color COLOR_ESTRELLA      = new Color(0xC4, 0x84, 0x1A);

    /** Color de las estrellas vacías. */
    private static final Color COLOR_ESTRELLA_VACIA = new Color(0xA0, 0x84, 0x5A);

    /** Número máximo de estrellas. */
    private static final int MAX_ESTRELLAS = 5;

    // ------------------------------------------------------------------ //
    //  Constructor                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Crea la tarjeta visual para el producto indicado.
     *
     * @param producto producto de tienda que se va a representar
     */
    public TarjetaProducto(ProductoTienda producto) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR_TARJETA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xA0, 0x84, 0x5A), 1),
                new EmptyBorder(10, 10, 10, 10)));
        setPreferredSize(new Dimension(190, 240));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(crearImagen(producto.getImagen()));
        add(Box.createVerticalStrut(8));
        add(crearNombre(producto.getNombre()));
        add(Box.createVerticalStrut(4));
        add(crearEstrellas(producto.getValoracion()));
        add(Box.createVerticalStrut(4));
        add(crearPrecio(producto.getPrecio()));
    }

    // ------------------------------------------------------------------ //
    //  Componentes internos                                              //
    // ------------------------------------------------------------------ //

    /**
     * Crea la imagen de la tarjeta (100 × 120 px). Si la ruta es inválida
     * o {@code null}, muestra un placeholder de color.
     *
     * @param ruta ruta o URL de la imagen
     * @return etiqueta con la imagen o el placeholder
     */
    private JLabel crearImagen(String ruta) {
        JLabel lbl = new JLabel();
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setPreferredSize(new Dimension(100, 120));
        lbl.setMinimumSize(new Dimension(100, 120));
        lbl.setMaximumSize(new Dimension(100, 120));
        lbl.setBorder(BorderFactory.createLineBorder(new Color(0x6B, 0x4E, 0x2A), 1));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        if (ruta != null && !ruta.isBlank()) {
            try {
                ImageIcon ico = new ImageIcon(ruta);
                lbl.setIcon(new ImageIcon(ico.getImage()
                        .getScaledInstance(100, 120, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
                configurarPlaceholder(lbl);
            }
        } else {
            configurarPlaceholder(lbl);
        }
        return lbl;
    }

    /**
     * Aplica el aspecto de placeholder cuando no hay imagen disponible.
     *
     * @param lbl etiqueta a configurar
     */
    private void configurarPlaceholder(JLabel lbl) {
        lbl.setText("<html><center>Sin<br>imagen</center></html>");
        lbl.setBackground(new Color(0x88, 0x66, 0x44));
        lbl.setForeground(new Color(0xE8, 0xD5, 0xB0));
        lbl.setOpaque(true);
    }

    /**
     * Crea la etiqueta con el nombre del producto truncado si es demasiado largo.
     *
     * @param nombre nombre del producto
     * @return etiqueta con el nombre
     */
    private JLabel crearNombre(String nombre) {
        // Truncar a 30 caracteres para evitar desbordamiento en tarjetas pequeñas
        String texto = nombre.length() > 30 ? nombre.substring(0, 27) + "…" : nombre;
        JLabel lbl = new JLabel(
                "<html><div style='text-align:center'>" + texto + "</div></html>",
                SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea un panel horizontal con {@code n} estrellas rellenas y el resto vacías.
     *
     * @param n número de estrellas rellenas (0–5)
     * @return panel con las estrellas
     */
    private JPanel crearEstrellas(int n) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 0));
        panel.setBackground(COLOR_TARJETA);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (int i = 0; i < MAX_ESTRELLAS; i++) {
            JLabel s = new JLabel("★");
            s.setFont(new Font("Dialog", Font.PLAIN, 16));
            s.setForeground(i < n ? COLOR_ESTRELLA : COLOR_ESTRELLA_VACIA);
            panel.add(s);
        }
        return panel;
    }

    /**
     * Crea la etiqueta con el precio formateado en euros.
     *
     * @param precio precio del producto
     * @return etiqueta con el precio
     */
    private JLabel crearPrecio(double precio) {
        JLabel lbl = new JLabel(String.format("%.2f€", precio), SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 16));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
}
