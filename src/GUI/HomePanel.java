package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import notificaciones.Notificacion;
import productos.categoria.Comic;
import productos.categoria.Figura;
import productos.categoria.Juego;
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
    private final JPanel panelRecomendados;
    private final JTextField campoBusqueda;
    private final JTextField campoPrecioMaximo;
    private final JComboBox<String> comboCategoria;
    private final JComboBox<String> comboValoracion;
    private final JComboBox<String> comboOrden;

    /**
     * Crea el panel de catalogo para el controlador indicado.
     *
     * @param mainFrame ventana principal de la aplicacion
     */
    public HomePanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.gridProductos = new JPanel();
        this.panelRecomendados = new JPanel();
        this.campoBusqueda = new JTextField();
        this.campoPrecioMaximo = new JTextField();
        this.comboCategoria = new JComboBox<>(new String[] {"Todas", "Comics", "Juegos", "Figuras"});
        this.comboValoracion = new JComboBox<>(new String[] {"Cualquiera", "1+", "2+", "3+", "4+", "5"});
        this.comboOrden = new JComboBox<>(new String[] {
                "Nombre A-Z", "Nombre Z-A", "Precio menor", "Precio mayor", "Valoracion mayor", "Valoracion menor"});

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
        List<ProductoTienda> productos = filtrarYOrdenarProductos();
        actualizarRecomendados();
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

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(UiStyle.COLOR_FONDO);

        JLabel titulo = new JLabel("Catalogo", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(14, 0, 6, 0));
        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(crearPanelFiltros(), BorderLayout.CENTER);
        cabecera.add(panelRecomendados, BorderLayout.SOUTH);
        contenido.add(cabecera, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(gridProductos,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        contenido.add(scroll, BorderLayout.CENTER);

        return contenido;
    }

    private void actualizarRecomendados() {
        panelRecomendados.removeAll();
        panelRecomendados.setBackground(UiStyle.COLOR_FONDO);
        panelRecomendados.setLayout(new BorderLayout());

        List<ProductoTienda> recomendados = mainFrame.getProductosRecomendados();
        if (recomendados.isEmpty()) {
            panelRecomendados.revalidate();
            panelRecomendados.repaint();
            return;
        }

        JLabel titulo = new JLabel("Recomendados para ti", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        panelRecomendados.add(titulo, BorderLayout.NORTH);

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 4));
        fila.setBackground(UiStyle.COLOR_FONDO);
        int maximo = Math.min(4, recomendados.size());
        for (int i = 0; i < maximo; i++) {
            ProductoTienda producto = recomendados.get(i);
            JButton boton = new UiStyle.RoundedButton(producto.getNombre(), UiStyle.COLOR_CABECERA,
                    UiStyle.COLOR_MARRON_MEDIO, 16);
            boton.setPreferredSize(new Dimension(190, 30));
            boton.setToolTipText("Abrir recomendacion");
            boton.addActionListener(e -> abrirDetalle(producto));
            fila.add(boton);
        }
        panelRecomendados.add(fila, BorderLayout.CENTER);
        panelRecomendados.revalidate();
        panelRecomendados.repaint();
    }

    private JPanel crearPanelFiltros() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        filtros.setBackground(UiStyle.COLOR_FONDO);
        filtros.setBorder(new EmptyBorder(0, 18, 8, 18));

        campoBusqueda.setPreferredSize(new Dimension(220, 30));
        campoBusqueda.setToolTipText("Buscar por nombre o descripcion");
        campoPrecioMaximo.setPreferredSize(new Dimension(82, 30));
        campoPrecioMaximo.setToolTipText("Precio maximo");

        filtros.add(crearEtiquetaFiltro("Buscar"));
        filtros.add(campoBusqueda);
        filtros.add(crearEtiquetaFiltro("Tipo"));
        filtros.add(comboCategoria);
        filtros.add(crearEtiquetaFiltro("Min."));
        filtros.add(comboValoracion);
        filtros.add(crearEtiquetaFiltro("Max. EUR"));
        filtros.add(campoPrecioMaximo);
        filtros.add(crearEtiquetaFiltro("Orden"));
        filtros.add(comboOrden);

        DocumentListener listenerTexto = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refrescar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refrescar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                refrescar();
            }
        };
        campoBusqueda.getDocument().addDocumentListener(listenerTexto);
        campoPrecioMaximo.getDocument().addDocumentListener(listenerTexto);
        comboCategoria.addActionListener(e -> refrescar());
        comboValoracion.addActionListener(e -> refrescar());
        comboOrden.addActionListener(e -> refrescar());

        return filtros;
    }

    private JLabel crearEtiquetaFiltro(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 12));
        etiqueta.setForeground(UiStyle.COLOR_TEXTO);
        return etiqueta;
    }

    private List<ProductoTienda> filtrarYOrdenarProductos() {
        List<ProductoTienda> productos = new ArrayList<>();
        String busqueda = normalizar(campoBusqueda.getText());
        String categoria = (String) comboCategoria.getSelectedItem();
        int valoracionMinima = extraerValoracionMinima();
        double precioMaximo = extraerPrecioMaximo();

        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            if (!busqueda.isBlank()
                    && !normalizar(producto.getNombre() + " " + producto.getDescripcion()).contains(busqueda)) {
                continue;
            }
            if (!coincideCategoria(producto, categoria)) {
                continue;
            }
            if (producto.getValoracion() < valoracionMinima) {
                continue;
            }
            if (precioMaximo >= 0 && producto.getPrecio() > precioMaximo) {
                continue;
            }
            productos.add(producto);
        }

        productos.sort(comparadorSeleccionado());
        return productos;
    }

    private boolean coincideCategoria(ProductoTienda producto, String categoria) {
        if ("Comics".equals(categoria)) {
            return producto.getCategoria() instanceof Comic;
        }
        if ("Juegos".equals(categoria)) {
            return producto.getCategoria() instanceof Juego;
        }
        if ("Figuras".equals(categoria)) {
            return producto.getCategoria() instanceof Figura;
        }
        return true;
    }

    private int extraerValoracionMinima() {
        String seleccion = (String) comboValoracion.getSelectedItem();
        if (seleccion == null || "Cualquiera".equals(seleccion)) {
            return 0;
        }
        return Character.getNumericValue(seleccion.charAt(0));
    }

    private double extraerPrecioMaximo() {
        String texto = campoPrecioMaximo.getText().trim().replace(',', '.');
        if (texto.isBlank()) {
            return -1;
        }
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private Comparator<ProductoTienda> comparadorSeleccionado() {
        String orden = (String) comboOrden.getSelectedItem();
        if ("Nombre Z-A".equals(orden)) {
            return Comparator.comparing(ProductoTienda::getNombre, String.CASE_INSENSITIVE_ORDER).reversed();
        }
        if ("Precio menor".equals(orden)) {
            return Comparator.comparingDouble(ProductoTienda::getPrecio);
        }
        if ("Precio mayor".equals(orden)) {
            return Comparator.comparingDouble(ProductoTienda::getPrecio).reversed();
        }
        if ("Valoracion mayor".equals(orden)) {
            return Comparator.comparingInt(ProductoTienda::getValoracion).reversed();
        }
        if ("Valoracion menor".equals(orden)) {
            return Comparator.comparingInt(ProductoTienda::getValoracion);
        }
        return Comparator.comparing(ProductoTienda::getNombre, String.CASE_INSENSITIVE_ORDER);
    }

    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase().trim();
    }

    private void abrirDetalle(ProductoTienda producto) {
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), producto.getNombre(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        PanelDeProducto detalle = new PanelDeProducto(producto, mainFrame);
        if (!mainFrame.isSesionRegistrada()) {
            detalle.configurarBotonCesta("Inicia sesion para comprar", true);
        }
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
            menu.add(crearItemMenu("PEDIDOS", "PEDIDOS", Main.PANTALLA_PEDIDOS, activo, mainFrame));
            menu.add(crearItemMenu("INTERCAMBIOS", "INTERCAMBIOS", Main.PANTALLA_INTERCAMBIOS, activo, mainFrame));
            menu.add(crearItemMenu("PACKS", "PACKS", Main.PANTALLA_PACKS, activo, mainFrame));
            menu.addSeparator();
            menu.add(crearItemMenu("PERFIL", "PERFIL", Main.PANTALLA_PERFIL, activo, mainFrame));
            menu.add(crearItemMenu("GESTION", "GESTION", Main.PANTALLA_GESTION, activo, mainFrame));

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
