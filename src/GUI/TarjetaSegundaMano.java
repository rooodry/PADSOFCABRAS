package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

import productos.ProductoSegundaMano;
import utilidades.EstadoProducto;

/**
 * Tarjeta visual que representa un {@link ProductoSegundaMano} dentro del
 * panel de productos del usuario ({@link PanelMisProductos}).
 *
 * <p>La tarjeta muestra la imagen del producto, su nombre, la valoración
 * en estrellas (dada por el empleado) y un botón cuya etiqueta varía según
 * el estado del producto:</p>
 * <ul>
 *   <li>{@link EstadoProducto#PENDIENTE_DE_VALORAR} → "Pedir valoración"</li>
 *   <li>{@link EstadoProducto#VALORADO} → "Publicar"</li>
 *   <li>Disponible (publicado) → sin botón de acción</li>
 * </ul>
 *
 * <p>La acción del botón se delega hacia fuera mediante un {@link ActionListener}
 * que se pasa en el constructor, siguiendo el patrón MVC.</p>
 *
 * @see PanelMisProductos
 * @see ProductoSegundaMano
 */
public class TarjetaSegundaMano extends JPanel {

    // ------------------------------------------------------------------ //
    //  Constantes de diseño                                               //
    // ------------------------------------------------------------------ //

    /** Color de fondo de la tarjeta. */
    private static final Color COLOR_TARJETA        = new Color(0xC4, 0xA8, 0x82);

    /** Color de borde de la tarjeta cuando está seleccionada / hover. */
    private static final Color COLOR_BORDE          = new Color(0x8B, 0x6A, 0x3E);

    /** Color del texto principal. */
    private static final Color COLOR_TEXTO          = new Color(0x2B, 0x1F, 0x0E);

    /** Color del texto sobre fondos oscuros. */
    private static final Color COLOR_TEXTO_CLARO    = new Color(0xE8, 0xD5, 0xB0);

    /** Color de fondo del botón de acción. */
    private static final Color COLOR_BOTON          = new Color(0x5C, 0x40, 0x20);

    /** Color dorado para estrellas rellenas. */
    private static final Color COLOR_ESTRELLA       = new Color(0xC4, 0x84, 0x1A);

    /** Color apagado para estrellas vacías. */
    private static final Color COLOR_ESTRELLA_VACIA = new Color(0x8B, 0x6A, 0x3E);

    /** Número máximo de estrellas. */
    private static final int MAX_ESTRELLAS = 5;

    /** Ancho fijo de la tarjeta en píxeles. */
    private static final int ANCHO = 185;

    /** Alto fijo de la tarjeta en píxeles. */
    private static final int ALTO  = 245;

    // ------------------------------------------------------------------ //
    //  Constructor                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Crea la tarjeta visual para el producto de segunda mano dado.
     *
     * @param producto producto de segunda mano que se representa
     * @param listener acción a ejecutar cuando se pulsa el botón principal
     *                 (puede ser {@code null} si el producto ya está publicado)
     */
    public TarjetaSegundaMano(ProductoSegundaMano producto, ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR_TARJETA);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE, 1, true),
                new EmptyBorder(10, 10, 10, 10)));
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setMaximumSize(new Dimension(ANCHO, ALTO));

        add(crearImagen(producto.getImagen()));
        add(Box.createVerticalStrut(7));
        add(crearNombre(producto.getNombre()));
        add(Box.createVerticalStrut(5));
        add(crearEstrellas(producto.getValoracionEmpleado()));

        // El botón sólo aparece si hay un listener (no en la pestaña "Publicados")
        if (listener != null) {
            add(Box.createVerticalStrut(6));
            add(crearBoton(etiquetaBoton(producto.getEstadoProducto()), listener));
        }

    }

    // ------------------------------------------------------------------ //
    //  Métodos internos                                                  //
    // ------------------------------------------------------------------ //

    /**
     * Determina la etiqueta del botón según el estado del producto.
     *
     * @param estado estado actual del producto
     * @return cadena con la etiqueta del botón
     */
    private String etiquetaBoton(EstadoProducto estado) {
        if (estado == EstadoProducto.VALORADO) {
            return "Publicar";
        }
        return "Pedir valoración";
    }

    /**
     * Crea la imagen de la tarjeta (110 × 130 px). Si la ruta es inválida
     * o {@code null}, muestra un placeholder de color.
     *
     * @param ruta ruta o URL de la imagen
     * @return etiqueta con la imagen o el placeholder
     */
    private JLabel crearImagen(String ruta) {
        JLabel lbl = new JLabel();
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        Dimension d = new Dimension(110, 130);
        lbl.setPreferredSize(d);
        lbl.setMinimumSize(d);
        lbl.setMaximumSize(d);
        lbl.setBorder(BorderFactory.createLineBorder(COLOR_BORDE, 1));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        if (ruta != null && !ruta.isBlank()) {
            try {
                ImageIcon ico = new ImageIcon(ruta);
                lbl.setIcon(new ImageIcon(
                        ico.getImage().getScaledInstance(110, 130, Image.SCALE_SMOOTH)));
            } catch (Exception e) {
                aplicarPlaceholder(lbl);
            }
        } else {
            aplicarPlaceholder(lbl);
        }
        return lbl;
    }

    /**
     * Aplica aspecto de placeholder a la etiqueta cuando no hay imagen.
     *
     * @param lbl etiqueta a configurar
     */
    private void aplicarPlaceholder(JLabel lbl) {
        lbl.setText("<html><center>Sin<br>imagen</center></html>");
        lbl.setBackground(new Color(0x88, 0x66, 0x44));
        lbl.setForeground(COLOR_TEXTO_CLARO);
        lbl.setOpaque(true);
    }

    /**
     * Crea la etiqueta con el nombre del producto, truncado si es largo.
     *
     * @param nombre nombre del producto
     * @return etiqueta centrada con el nombre
     */
    private JLabel crearNombre(String nombre) {
        String texto = nombre.length() > 22 ? nombre.substring(0, 19) + "…" : nombre;
        JLabel lbl = new JLabel(
                "<html><div style='text-align:center'>" + texto + "</div></html>",
                SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Genera un panel horizontal con {@code n} estrellas rellenas y el resto vacías.
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
            s.setFont(new Font("Dialog", Font.PLAIN, 18));
            s.setForeground(i < n ? COLOR_ESTRELLA : COLOR_ESTRELLA_VACIA);
            panel.add(s);
        }
        return panel;
    }

    /**
     * Crea el botón de acción principal de la tarjeta.
     *
     * @param etiqueta texto que muestra el botón
     * @param listener acción a ejecutar al pulsarlo
     * @return botón configurado y alineado al centro
     */
    private JButton crearBoton(String etiqueta, ActionListener listener) {
        JButton btn = new JButton(etiqueta);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBackground(COLOR_BOTON);
        btn.setForeground(COLOR_TEXTO_CLARO);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        int ancho = ANCHO - 30;
        btn.setMaximumSize(new Dimension(ancho, 28));
        btn.setPreferredSize(new Dimension(ancho, 28));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x3A, 0x28, 0x10));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_BOTON);
            }
        });

        btn.addActionListener(listener);
        return btn;
    }
}