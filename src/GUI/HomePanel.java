package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import notificaciones.Notificacion;
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
            setPreferredSize(new Dimension(0, 46));
            setBorder(new EmptyBorder(3, 14, 3, 12));

            JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            izquierda.setOpaque(false);
            JButton menu = crearBotonIcono("\u2630", "Abrir menu", 32, 48);
            menu.addActionListener(e -> mostrarMenu(menu, mainFrame, activo));
            izquierda.add(menu);
            add(izquierda, BorderLayout.WEST);

            JLabel marca = new JLabel("GOAT & GET", SwingConstants.CENTER);
            marca.setFont(new Font("SansSerif", Font.BOLD, 26));
            marca.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            add(marca, BorderLayout.CENTER);

            JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
            derecha.setOpaque(false);
            JButton campana = crearBotonIcono("\uD83D\uDD14" + contarNoLeidas(mainFrame), "Notificaciones", 25, 42);
            campana.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_NOTIFICACIONES));
            JButton perfil = crearBotonIcono("\uD83D\uDC10", "Perfil", 27, 42);
            perfil.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_PERFIL));
            derecha.add(campana);
            derecha.add(perfil);
            add(derecha, BorderLayout.EAST);
        }

        private JButton crearBotonIcono(String texto, String tooltip, int fontSize, int ancho) {
            JButton boton = new JButton(texto);
            boton.setFont(new Font("Dialog", Font.BOLD, fontSize));
            boton.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            boton.setBackground(UiStyle.COLOR_CABECERA);
            boton.setBorderPainted(false);
            boton.setContentAreaFilled(false);
            boton.setFocusPainted(false);
            boton.setOpaque(false);
            boton.setToolTipText(tooltip);
            boton.setPreferredSize(new Dimension(ancho, 40));
            boton.setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            return boton;
        }

        private String contarNoLeidas(Main mainFrame) {
            int contador = 0;
            for (Notificacion notificacion : mainFrame.getClienteActual().getNotificaciones()) {
                if (!notificacion.getLeida() && !notificacion.getBorrada()) {
                    contador++;
                }
            }
            return contador > 0 ? String.valueOf(contador) : "";
        }

        private void mostrarMenu(JButton origen, Main mainFrame, String activo) {
            JPopupMenu menu = new JPopupMenu();
            menu.setBackground(UiStyle.COLOR_CABECERA);
            menu.setBorder(new EmptyBorder(8, 8, 8, 8));

            menu.add(crearItemMenu("HOME", "HOME", Main.PANTALLA_HOME, activo, mainFrame));
            menu.add(crearItemMenu("CARTERA", "MIS PRODUCTOS", Main.PANTALLA_MIS_PRODUCTOS, activo, mainFrame));
            menu.add(crearItemMenu("CESTA", "CESTA", Main.PANTALLA_CESTA, activo, mainFrame));
            menu.add(crearItemMenu("INTERCAMBIOS", "INTERCAMBIOS", Main.PANTALLA_INTERCAMBIOS, activo, mainFrame));
            menu.add(crearItemMenu("PACKS", "PACKS", Main.PANTALLA_PACKS, activo, mainFrame));
            menu.addSeparator();
            menu.add(crearItemMenu("PERFIL", "PERFIL", Main.PANTALLA_PERFIL, activo, mainFrame));

            menu.show(origen, 0, origen.getHeight() + 6);
        }

        private JMenuItem crearItemMenu(String texto, String claveActiva, String pantalla, String activo, Main mainFrame) {
            JMenuItem item = new JMenuItem(texto);
            item.setOpaque(true);
            item.setBackground(claveActiva.equals(activo) ? UiStyle.COLOR_TEXTO : UiStyle.COLOR_CABECERA);
            item.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            item.setFont(new Font("SansSerif", Font.BOLD, 14));
            item.setBorder(new EmptyBorder(8, 16, 8, 46));
            item.addActionListener(e -> mainFrame.cambiarPantalla(pantalla));
            return item;
        }

        @SuppressWarnings("unused")
        private JPanel crearBotoneraAntigua(Main mainFrame, String activo) {
            JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
            centro.setOpaque(false);
            return centro;
        }
    }
}
