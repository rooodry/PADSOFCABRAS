package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;

/**
 * Pantalla principal del cliente registrado con catalogo de productos de tienda.
 *
 * <p>Obtiene siempre los productos desde {@link Main}, por lo que las acciones
 * de detalle y cesta operan sobre el modelo real de la aplicacion.</p>
 */
public class HomePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel gridProductos;

    /**
     * Crea el panel de catalogo para el controlador indicado.
     *
     * @param mainFrame ventana principal de la aplicacion
     */
    public HomePanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.gridProductos = new JPanel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new PanelNavegacionCliente(mainFrame, "HOME"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Reconstruye la cuadricula con los datos actuales de producto y stock.
     */
    public void refrescar() {
        List<ProductoTienda> productos = mainFrame.getProductosTienda();
        gridProductos.removeAll();
        gridProductos.setBackground(UiStyle.COLOR_FONDO);
        gridProductos.setBorder(new EmptyBorder(18, 24, 24, 24));

        int columnas = 3;
        int filas = Math.max(1, (int) Math.ceil(productos.size() / (double) columnas));
        gridProductos.setLayout(new GridLayout(filas, columnas, 18, 18));

        for (ProductoTienda producto : productos) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            envoltura.setBackground(UiStyle.COLOR_FONDO);
            TarjetaProducto tarjeta = new TarjetaProducto(producto);
            tarjeta.setToolTipText("Ver detalle");
            tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    abrirDetalle(producto);
                }
            });
            envoltura.add(tarjeta);
            gridProductos.add(envoltura);
        }

        int restantes = filas * columnas - productos.size();
        for (int i = 0; i < restantes; i++) {
            JPanel vacio = new JPanel();
            vacio.setBackground(UiStyle.COLOR_FONDO);
            gridProductos.add(vacio);
        }

        gridProductos.revalidate();
        gridProductos.repaint();
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(UiStyle.COLOR_FONDO);

        JLabel titulo = new JLabel("Catalogo", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(14, 0, 6, 0));
        contenido.add(titulo, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridProductos,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contenido.add(scroll, BorderLayout.CENTER);

        return contenido;
    }

    private void abrirDetalle(ProductoTienda producto) {
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), producto.getNombre(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        PanelDeProducto detalle = new PanelDeProducto(producto);
        detalle.addListenerCesta(e -> {
            mainFrame.anadirProductoACesta(producto);
            dialogo.dispose();
        });

        dialogo.add(detalle);
        dialogo.pack();
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }

    /**
     * Barra superior de navegacion comun para las pantallas de cliente.
     */
    static class PanelNavegacionCliente extends JPanel {

        private static final long serialVersionUID = 1L;

        PanelNavegacionCliente(Main mainFrame, String activo) {
            setLayout(new BorderLayout());
            setBackground(UiStyle.COLOR_CABECERA);
            setPreferredSize(new Dimension(0, 76));
            setBorder(new EmptyBorder(8, 16, 8, 16));

            JLabel marca = new JLabel("GOAT & GET");
            marca.setFont(new Font("SansSerif", Font.BOLD, 24));
            marca.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            add(marca, BorderLayout.WEST);

            JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
            centro.setOpaque(false);
            centro.add(crearBoton(mainFrame, "HOME", Main.PANTALLA_HOME, activo));
            centro.add(crearBoton(mainFrame, "CESTA", Main.PANTALLA_CESTA, activo));
            centro.add(crearBoton(mainFrame, "MIS PRODUCTOS", Main.PANTALLA_MIS_PRODUCTOS, activo));
            centro.add(crearBoton(mainFrame, "PERFIL", Main.PANTALLA_PERFIL, activo));
            add(centro, BorderLayout.CENTER);

            JLabel usuario = new JLabel(mainFrame.getClienteActual().getNombre());
            usuario.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            usuario.setFont(new Font("SansSerif", Font.BOLD, 14));
            add(usuario, BorderLayout.EAST);
        }

        private JButton crearBoton(Main mainFrame, String texto, String pantalla, String activo) {
            JButton boton = new JButton(texto);
            boton.setFocusPainted(false);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            boton.setBackground(texto.equals(activo) ? UiStyle.COLOR_TEXTO : UiStyle.COLOR_CABECERA);
            boton.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            boton.addActionListener(e -> mainFrame.cambiarPantalla(pantalla));
            return boton;
        }
    }
}
