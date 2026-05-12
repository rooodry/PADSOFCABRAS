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
 * Panel principal del catalogo de cliente, con recomendaciones y filtros de busqueda.
 * Muestra el catálogo de productos disponibles, una sección de recomendaciones
 * y un panel de filtros para facilitar la búsqueda al usuario.
 */
public class HomePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Referencia a la ventana principal de la aplicación. */
    private final Main mainFrame;

    /** Panel que contiene la cuadrícula donde se muestran los productos. */
    private final JPanel gridProductos;

    /** Panel dedicado a mostrar los productos recomendados para el usuario. */
    private final JPanel panelRecomendados;

    /** Campo de texto utilizado para buscar productos por nombre o descripción. */
    private final JTextField campoBusqueda;

    /** Campo de texto utilizado para establecer un filtro de precio máximo. */
    private final JTextField campoPrecioMaximo;

    /** Desplegable para filtrar los productos por su categoría (Cómics, Juegos, Figuras). */
    private final JComboBox<String> comboCategoria;

    /** Desplegable para filtrar los productos según su valoración mínima. */
    private final JComboBox<String> comboValoracion;

    /** Desplegable para establecer el criterio de ordenación del catálogo mostrado. */
    private final JComboBox<String> comboOrden;

    /**
     * Construye el panel de catálogo para el controlador principal indicado.
     * Inicializa los componentes, establece el layout y aplica los filtros por defecto.
     *
     * @param mainFrame Ventana principal de la aplicación que gestiona el estado y la navegación.
     */
    public HomePanel(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.gridProductos = new JPanel();
        this.panelRecomendados = new JPanel();
        this.campoBusqueda = new JTextField();
        this.campoPrecioMaximo = new JTextField();
        this.comboCategoria = new JComboBox<>(new String[] {"Todas", "Cómics", "Juegos", "Figuras"});
        this.comboValoracion = new JComboBox<>(new String[] {"Cualquiera", "1+", "2+", "3+", "4+", "5"});
        this.comboOrden = new JComboBox<>(new String[] {
                "Nombre A-Z", "Nombre Z-A", "Precio menor", "Precio mayor", "Valoración mayor", "Valoración menor"});

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new PanelNavegacionCliente(mainFrame, "HOME"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Reconstruye la cuadrícula de la interfaz con los datos actuales de productos y stock.
     * Aplica los filtros de búsqueda, categoría, valoración y precio máximo,
     * y ordena los resultados según la selección del usuario antes de repintar.
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
            TarjetaProducto tarjeta = new TarjetaProducto(producto, mainFrame.getStock().getNumProductos(producto));
            tarjeta.setToolTipText("Ver detalle");
            tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
                /**
                 * Evento ejecutado al hacer clic sobre la tarjeta de un producto.
                 * Abre la ventana de detalles del producto correspondiente.
                 *
                 * @param e El evento de ratón generado.
                 */
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
    }

    /**
     * Crea y estructura el panel principal de contenido, incluyendo la cabecera,
     * los filtros, las recomendaciones y el área de scroll para el catálogo.
     *
     * @return El panel contenedor estructurado con todos sus elementos visuales.
     */
    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(UiStyle.COLOR_FONDO);

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(UiStyle.COLOR_FONDO);

        JLabel titulo = new JLabel("Catálogo", SwingConstants.CENTER);
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

    /**
     * Actualiza el panel de productos recomendados obteniéndolos del controlador principal.
     * Si no hay recomendaciones, el panel se oculta o se muestra vacío.
     */
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

    /**
     * Crea el panel que agrupa todos los filtros de búsqueda y ordenación.
     * Asocia además los listeners pertinentes para que la vista se actualice automáticamente.
     *
     * @return El panel configurado con los componentes de filtrado.
     */
    private JPanel crearPanelFiltros() {
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        filtros.setBackground(UiStyle.COLOR_FONDO);
        filtros.setBorder(new EmptyBorder(0, 18, 8, 18));

        campoBusqueda.setPreferredSize(new Dimension(220, 30));
        campoBusqueda.setToolTipText("Buscar por nombre o descripción");
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
            /**
             * Se ejecuta al insertar texto en un campo monitoreado.
             * @param e El evento de documento.
             */
            @Override
            public void insertUpdate(DocumentEvent e) {
                refrescar();
            }

            /**
             * Se ejecuta al eliminar texto de un campo monitoreado.
             * @param e El evento de documento.
             */
            @Override
            public void removeUpdate(DocumentEvent e) {
                refrescar();
            }

            /**
             * Se ejecuta al cambiar los atributos del texto en un campo monitoreado.
             * @param e El evento de documento.
             */
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

    /**
     * Crea una etiqueta (JLabel) con el estilo visual estándar para los filtros.
     *
     * @param texto El texto que mostrará la etiqueta.
     * @return La etiqueta configurada.
     */
    private JLabel crearEtiquetaFiltro(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 12));
        etiqueta.setForeground(UiStyle.COLOR_TEXTO);
        return etiqueta;
    }

    /**
     * Aplica los filtros actuales (texto, categoría, valoración, precio) a la lista global
     * de productos de la tienda y ordena el resultado final.
     *
     * @return Una lista de productos que cumplen todos los criterios de filtrado seleccionados.
     */
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

    /**
     * Comprueba si un producto pertenece a la categoría especificada.
     *
     * @param producto  El producto a evaluar.
     * @param categoria El nombre de la categoría (ej. "Cómics", "Juegos", "Figuras").
     * @return {@code true} si el producto pertenece a la categoría, o si la categoría es "Todas"; {@code false} en caso contrario.
     */
    private boolean coincideCategoria(ProductoTienda producto, String categoria) {
        if ("Cómics".equals(categoria)) {
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

    /**
     * Extrae el valor numérico de la valoración mínima seleccionada en el combo de valoraciones.
     *
     * @return Un entero representando la valoración mínima (0 si es "Cualquiera").
     */
    private int extraerValoracionMinima() {
        String seleccion = (String) comboValoracion.getSelectedItem();
        if (seleccion == null || "Cualquiera".equals(seleccion)) {
            return 0;
        }
        return Character.getNumericValue(seleccion.charAt(0));
    }

    /**
     * Extrae y formatea el valor numérico del precio máximo introducido por el usuario.
     *
     * @return El valor del precio máximo como {@code double}, o -1 si el campo está vacío o es inválido.
     */
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

    /**
     * Determina el comparador adecuado para ordenar los productos según la opción seleccionada.
     *
     * @return Un objeto {@link Comparator} configurado para la ordenación elegida.
     */
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
        if ("Valoración mayor".equals(orden)) {
            return Comparator.comparingInt(ProductoTienda::getValoracion).reversed();
        }
        if ("Valoración menor".equals(orden)) {
            return Comparator.comparingInt(ProductoTienda::getValoracion);
        }
        return Comparator.comparing(ProductoTienda::getNombre, String.CASE_INSENSITIVE_ORDER);
    }

    /**
     * Normaliza una cadena de texto eliminando tildes y marcas diacríticas,
     * convirtiéndola a minúsculas para facilitar búsquedas insensibles a mayúsculas o acentos.
     *
     * @param texto El texto a normalizar.
     * @return La cadena de texto normalizada y en minúsculas.
     */
    private String normalizar(String texto) {
        if (texto == null) {
            return "";
        }
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase().trim();
    }

    /**
     * Abre un cuadro de diálogo modal que muestra los detalles completos de un producto seleccionado.
     * Configura además el comportamiento del botón de compra dependiendo de si hay stock y sesión iniciada.
     *
     * @param producto El producto cuyos detalles se desean visualizar.
     */
    private void abrirDetalle(ProductoTienda producto) {
        JDialog dialogo = new JDialog(SwingUtilities.getWindowAncestor(this), producto.getNombre(),
                java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        PanelDeProducto detalle = new PanelDeProducto(producto, mainFrame);
        if (!mainFrame.isSesionRegistrada()) {
            detalle.configurarBotonCesta("Inicia sesión para comprar", true);
        } else if (mainFrame.getStock().getNumProductos(producto) <= 0) {
            detalle.configurarBotonCesta("Sin stock", false);
        }
        detalle.addListenerCesta(e -> {
            mainFrame.anadirCantidadProductoACesta(producto, detalle.getCantidadCestaSeleccionada());
            dialogo.dispose();
        });

        dialogo.add(detalle);
        dialogo.pack();
        dialogo.setResizable(true);
        dialogo.setSize(Math.max(dialogo.getWidth(), 860), Math.max(dialogo.getHeight(), 560));
        dialogo.setLocationRelativeTo(this);
        dialogo.setVisible(true);
    }


    /**
     * Componente que representa la barra de navegación superior (Navbar)
     * mostrada en las vistas de cliente.
     */
    static class PanelNavegacionCliente extends JPanel {

        private static final long serialVersionUID = 1L;

        /**
         * Crea el panel de navegación para el cliente.
         *
         * @param mainFrame Ventana principal de la aplicación.
         * @param activo    Identificador de la pantalla actual activa para resaltarla en el menú.
         */
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
            JButton campana = UiStyle.crearBotonImagen(UiStyle.ICONO_NOTIFICACIONES, contarNoLeidas(mainFrame),
                    "Notificaciones", 52, 40, 30);
            campana.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_NOTIFICACIONES));
            JButton perfil = UiStyle.crearBotonImagen(UiStyle.ICONO_PERFIL_CABRA, "", "Perfil", 42, 40, 32);
            perfil.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_PERFIL));
            derecha.add(campana);
            derecha.add(perfil);
            add(derecha, BorderLayout.EAST);
        }

        /**
         * Método auxiliar para crear botones que muestran un carácter/icono de texto.
         *
         * @param texto    El carácter Unicode a mostrar.
         * @param tooltip  El texto descriptivo para el tooltip.
         * @param fontSize El tamaño de fuente del icono.
         * @param ancho    La anchura preferida del botón.
         * @return El botón icono creado.
         */
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

        /**
         * Cuenta el número de notificaciones no leídas y no borradas del cliente actual.
         *
         * @param mainFrame Ventana principal para acceder a los datos del cliente.
         * @return Un String con la cantidad de notificaciones, o una cadena vacía si no hay ninguna.
         */
        private String contarNoLeidas(Main mainFrame) {
            int contador = 0;
            for (Notificacion notificacion : mainFrame.getClienteActual().getNotificaciones()) {
                if (!notificacion.getLeida() && !notificacion.getBorrada()) {
                    contador++;
                }
            }
            return contador > 0 ? String.valueOf(contador) : "";
        }

        /**
         * Despliega un menú emergente (JPopupMenu) de navegación.
         *
         * @param origen    El componente botón desde donde se despliega el menú.
         * @param mainFrame La ventana principal encargada del cambio de pantallas.
         * @param activo    El identificador de la pantalla actual.
         */
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
            if (mainFrame.isSesionEmpleado() || mainFrame.isSesionGestor()) {
                menu.add(crearItemMenu("GESTION", "GESTION", Main.PANTALLA_GESTION, activo, mainFrame));
            }
            menu.addSeparator();
            menu.add(crearItemCerrarSesion(mainFrame));

            menu.show(origen, 0, origen.getHeight() + 6);
        }

        /**
         * Crea un elemento de menú de navegación para una vista específica.
         *
         * @param texto       Texto a mostrar en la opción del menú.
         * @param claveActiva Identificador que marca si este ítem es la pantalla activa.
         * @param pantalla    Constante de la pantalla destino (definida en Main).
         * @param activo      Identificador de la pantalla en la que se encuentra el usuario.
         * @param mainFrame   Ventana principal de la aplicación.
         * @return El {@link JMenuItem} configurado.
         */
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

        /**
         * Crea el elemento de menú encargado de cerrar la sesión actual del usuario.
         *
         * @param mainFrame Ventana principal de la aplicación.
         * @return El {@link JMenuItem} configurado para cerrar sesión.
         */
        private JMenuItem crearItemCerrarSesion(Main mainFrame) {
            JMenuItem item = new JMenuItem("CERRAR SESIÓN");
            item.setOpaque(true);
            item.setBackground(UiStyle.COLOR_CABECERA);
            item.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            item.setFont(new Font("SansSerif", Font.BOLD, 14));
            item.setBorder(new EmptyBorder(8, 16, 8, 46));
            item.addActionListener(e -> mainFrame.cerrarSesion());
            return item;
        }

        /**
         * Método obsoleto para crear una botonera antigua (no usado actualmente).
         *
         * @param mainFrame Ventana principal.
         * @param activo    Identificador de la pantalla actual.
         * @return Un panel con la estructura de botones obsoleta.
         */
        @SuppressWarnings("unused")
        private JPanel crearBotoneraAntigua(Main mainFrame, String activo) {
            JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
            centro.setOpaque(false);
            return centro;
        }
    }
}