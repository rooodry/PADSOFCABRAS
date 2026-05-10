package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

import productos.ProductoTienda;


/**
 * Representa el componente PanelProductos de la interfaz grafica.
 */
public class PanelProductos extends JPanel {


    private static final Color COLOR_FONDO  = new Color(0xC4, 0xA8, 0x82);


    private static final Color COLOR_OSCURO = new Color(0x2B, 0x1F, 0x0E);


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
        derecha.add(UiStyle.crearBotonImagen(UiStyle.ICONO_NOTIFICACIONES, "", "Notificaciones", 32, 32, 24));
        derecha.add(UiStyle.crearBotonImagen(UiStyle.ICONO_PERFIL_CABRA, "", "Perfil", 32, 32, 25));
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

        JPanel grid = new JPanel(new GridLayout(filas, columnas, 18, 18));
        grid.setBackground(COLOR_FONDO);
        grid.setBorder(new EmptyBorder(16, 16, 16, 16));

        for (ProductoTienda p : productos) {
            TarjetaProducto tarjeta = new TarjetaProducto(p);
            tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                /**
                 * Ejecuta la operacion publica mouseClicked.
                 * @param e parametro utilizado por la operacion
                 */
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    abrirDetalle(p);
                }
            });
            grid.add(tarjeta);
        }


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
