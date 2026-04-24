package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import productos.ProductoTienda;

/**
 * Panel que muestra el catálogo de productos de la tienda en forma de cuadrícula.
 *
 * <p>Cada producto se representa como una tarjeta ({@link TarjetaProducto}) que
 * muestra su nombre, precio y valoración. Al hacer clic en una tarjeta se abre
 * un {@link JDialog} con el {@link PanelDeProducto} completo.</p>
 *
 * <p>El panel usa un {@link GridLayout} de tres columnas y se envuelve en un
 * {@link JScrollPane} para soportar catálogos grandes.</p>
 *
 * @see PanelDeProducto
 * @see TarjetaProducto
 */
public class PanelProductos extends JPanel {

    /** Color de fondo principal (beige arena). */
    private static final Color COLOR_FONDO  = new Color(0xC4, 0xA8, 0x82);

    /** Color de cabecera (marrón oscuro). */
    private static final Color COLOR_OSCURO = new Color(0x2B, 0x1F, 0x0E);

    /** Color del texto sobre fondo oscuro. */
    private static final Color COLOR_CLARO  = new Color(0xE8, 0xD5, 0xB0);

    /**
     * Crea el panel de catálogo con el título y la lista de productos indicados.
     *
     * @param titulo    título que se muestra en la cabecera del catálogo
     * @param productos lista de productos de tienda a mostrar; puede estar vacía
     */
    public PanelProductos(String titulo, List<ProductoTienda> productos) {
        setLayout(new BorderLayout());
        setBackground(COLOR_FONDO);
        setPreferredSize(new Dimension(700, 500));

        add(crearCabecera(titulo), BorderLayout.NORTH);
        add(crearGridProductos(productos), BorderLayout.CENTER);
    }

    /**
     * Crea la barra superior con el logotipo "GOAT &amp; GET" y el título del
     * catálogo visible como subtítulo.
     *
     * @param titulo título del catálogo
     * @return panel de cabecera configurado
     */
    private JPanel crearCabecera(String titulo) {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(COLOR_OSCURO);
        cab.setPreferredSize(new Dimension(700, 48));
        cab.setBorder(new EmptyBorder(0, 10, 0, 10));

        JButton btnMenu = crearBotonIcono("≡", 22);
        cab.add(btnMenu, BorderLayout.WEST);

        JLabel lblTitulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Serif", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_CLARO);
        cab.add(lblTitulo, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        derecha.setBackground(COLOR_OSCURO);
        derecha.add(crearBotonIcono("🔔", 16));
        derecha.add(crearBotonIcono("👤", 16));
        cab.add(derecha, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea un botón icónico sin borde para la cabecera.
     *
     * @param icono    texto o carácter del icono
     * @param fontSize tamaño de fuente en puntos
     * @return botón configurado
     */
    private JButton crearBotonIcono(String icono, int fontSize) {
        JButton btn = new JButton(icono);
        btn.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        btn.setForeground(COLOR_CLARO);
        btn.setBackground(COLOR_OSCURO);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ------------------------------------------------------------------ //
    //  Cuadrícula de productos                                           //
    // ------------------------------------------------------------------ //

    /**
     * Crea el área de cuadrícula con una {@link TarjetaProducto} por cada
     * elemento de la lista, organizado en tres columnas, envuelto en scroll.
     *
     * @param productos lista de productos a mostrar
     * @return scroll pane con la cuadrícula
     */
    private JScrollPane crearGridProductos(List<ProductoTienda> productos) {
        int columnas = 3;
        int filas    = (int) Math.ceil(productos.size() / (double) columnas);
        filas        = Math.max(filas, 1);

        JPanel grid = new JPanel(new GridLayout(filas, columnas, 12, 12));
        grid.setBackground(COLOR_FONDO);
        grid.setBorder(new EmptyBorder(16, 16, 16, 16));

        for (ProductoTienda p : productos) {
            TarjetaProducto tarjeta = new TarjetaProducto(p);
            tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    abrirDetalle(p);
                }
            });
            grid.add(tarjeta);
        }

        // Rellena las celdas sobrantes con paneles vacíos
        int restantes = filas * columnas - productos.size();
        for (int i = 0; i < restantes; i++) {
            JPanel vacio = new JPanel();
            vacio.setBackground(COLOR_FONDO);
            grid.add(vacio);
        }

        JScrollPane scroll = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO);
        return scroll;
    }

    /**
     * Abre un {@link JDialog} modal con el {@link PanelDeProducto} del
     * producto indicado. El botón "Añadir a la cesta" muestra un mensaje
     * de confirmación.
     *
     * @param producto producto cuyo detalle se va a mostrar
     */
    private void abrirDetalle(ProductoTienda producto) {
        JDialog dialogo = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                producto.getNombre(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);

        PanelDeProducto detalle = new PanelDeProducto(producto);

        // Acción del botón "Añadir a la cesta"
        detalle.addListenerCesta(e ->
                JOptionPane.showMessageDialog(
                        dialogo,
                        "\"" + producto.getNombre() + "\" añadido a la cesta.",
                        "Cesta",
                        JOptionPane.INFORMATION_MESSAGE));

        dialogo.add(detalle);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }
}
