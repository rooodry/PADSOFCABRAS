package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoProducto;

/**
 * Panel principal que muestra los productos de segunda mano de un
 * {@link ClienteRegistrado}, organizado en tres pestañas:
 *
 * <ul>
 *   <li><b>Subidos</b>: todos los productos de la cartera del usuario.
 *       Muestra el botón "Pedir valoración" en cada tarjeta y la tarjeta
 *       especial para subir un nuevo producto.</li>
 *   <li><b>Valorados</b>: productos con {@link EstadoProducto#VALORADO}.
 *       Muestra el botón "Publicar" en cada tarjeta.</li>
 *   <li><b>Publicados</b>: productos con disponibilidad activa
 *       ({@code getDisponibilidad() == true}).
 *       Las tarjetas no muestran botón de acción.</li>
 * </ul>
 *
 * <p>La pestaña activa se resalta con un fondo oscuro; las inactivas
 * tienen fondo transparente con borde.</p>
 *
 * <p>Las acciones de negocio (pedir valoración, publicar, subir producto)
 * se delegan hacia el controlador mediante {@link ActionListener}s que se
 * registran a través de la API pública del panel.</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * ClienteRegistrado cliente = ...;
 * PanelMisProductos panel = new PanelMisProductos(cliente);
 *
 * panel.addListenerSubirProducto(e -> abrirDialogoSubida());
 * panel.addListenerPedirValoracion(e -> pedirValoracion(productoSeleccionado));
 * panel.addListenerPublicar(e -> publicarProducto(productoSeleccionado));
 * }</pre>
 *
 * @see TarjetaSegundaMano
 * @see TarjetaSubirProducto
 * @see ClienteRegistrado
 */
public class PanelMisProductos extends JPanel {

    // ------------------------------------------------------------------ //
    //  Constantes de diseño                                               //
    // ------------------------------------------------------------------ //

    /** Color de fondo general (beige arena). */
    private static final Color COLOR_FONDO      = new Color(0xC4, 0xA8, 0x82);

    /** Color de cabecera y tab activo. */
    private static final Color COLOR_OSCURO     = new Color(0x5C, 0x40, 0x20);

    /** Color del texto sobre fondos oscuros. */
    private static final Color COLOR_TEXTO_CLARO = new Color(0xE8, 0xD5, 0xB0);

    /** Color del texto sobre el fondo claro. */
    private static final Color COLOR_TEXTO      = new Color(0x2B, 0x1F, 0x0E);

    /** Color del borde de los tabs inactivos. */
    private static final Color COLOR_BORDE_TAB  = new Color(0x8B, 0x6A, 0x3E);

    // ------------------------------------------------------------------ //
    //  Pestañas disponibles                                              //
    // ------------------------------------------------------------------ //

    /** Identificador de la pestaña "Subidos". */
    private static final int TAB_SUBIDOS    = 0;

    /** Identificador de la pestaña "Valorados". */
    private static final int TAB_VALORADOS  = 1;

    /** Identificador de la pestaña "Publicados". */
    private static final int TAB_PUBLICADOS = 2;

    // ------------------------------------------------------------------ //
    //  Estado interno                                                    //
    // ------------------------------------------------------------------ //

    /** Cliente cuya cartera se muestra. */
    private final ClienteRegistrado cliente;

    /** Índice de la pestaña actualmente seleccionada. */
    private int tabActivo = TAB_SUBIDOS;

    /** Panel central que contiene la cuadrícula de productos. */
    private JPanel panelGrid;

    /** Botones de las tres pestañas, para poder actualizar su estilo. */
    private JButton[] botonesTab;

    // ------------------------------------------------------------------ //
    //  Listeners de negocio registrados desde el controlador             //
    // ------------------------------------------------------------------ //

    /** Listener para el botón "Subir producto" de la tarjeta especial. */
    private ActionListener listenerSubir;

    /** Listener para el botón "Pedir valoración" de cada tarjeta. */
    private ActionListener listenerPedirValoracion;

    /** Listener para el botón "Publicar" de cada tarjeta. */
    private ActionListener listenerPublicar;

    // ------------------------------------------------------------------ //
    //  Constructor                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Crea el panel de productos del usuario.
     *
     * @param cliente cliente registrado cuya cartera se va a mostrar;
     *                no puede ser {@code null}
     * @throws IllegalArgumentException si {@code cliente} es {@code null}
     */
    public PanelMisProductos(ClienteRegistrado cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser null.");
        }
        this.cliente = cliente;
        construirUI();
    }

    // ------------------------------------------------------------------ //
    //  API pública — registro de listeners                               //
    // ------------------------------------------------------------------ //

    /**
     * Registra el listener que se invoca cuando el usuario pulsa
     * "Subir producto" en la tarjeta especial de subida.
     *
     * @param l listener a registrar
     */
    public void addListenerSubirProducto(ActionListener l) {
        this.listenerSubir = l;
    }

    /**
     * Registra el listener que se invoca cuando el usuario pulsa
     * "Pedir valoración" en una tarjeta de la pestaña "Subidos".
     *
     * <p>El {@code ActionEvent} transporta el producto afectado en su
     * {@code source}, de modo que el controlador pueda identificarlo.</p>
     *
     * @param l listener a registrar
     */
    public void addListenerPedirValoracion(ActionListener l) {
        this.listenerPedirValoracion = l;
    }

    /**
     * Registra el listener que se invoca cuando el usuario pulsa
     * "Publicar" en una tarjeta de la pestaña "Valorados".
     *
     * @param l listener a registrar
     */
    public void addListenerPublicar(ActionListener l) {
        this.listenerPublicar = l;
    }

    /**
     * Fuerza la recarga de la cuadrícula de productos.
     * Debe llamarse desde el controlador tras cualquier cambio en la
     * cartera del cliente (nueva subida, valoración, publicación).
     */
    public void refrescar() {
        actualizarGrid();
    }

    // ------------------------------------------------------------------ //
    //  Construcción de la interfaz                                       //
    // ------------------------------------------------------------------ //

    /**
     * Ensambla los subpaneles principales: cabecera, barra de tabs y cuadrícula.
     */
    private void construirUI() {
        setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(750, 500));

        add(crearCabecera(),   BorderLayout.NORTH);
        add(crearCuerpo(),     BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ //
    //  Cabecera                                                          //
    // ------------------------------------------------------------------ //

    /**
     * Crea la barra superior con el logotipo y los iconos de acción.
     *
     * @return panel de cabecera configurado
     */
    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(new Color(0x2B, 0x1F, 0x0E));
        cab.setPreferredSize(new Dimension(750, 48));
        cab.setBorder(new EmptyBorder(0, 10, 0, 10));

        cab.add(crearBotonIcono("≡", 22), BorderLayout.WEST);

        JLabel titulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 20));
        titulo.setForeground(COLOR_TEXTO_CLARO);
        cab.add(titulo, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        derecha.setBackground(new Color(0x2B, 0x1F, 0x0E));
        derecha.add(crearBotonIcono("🔔", 16));
        derecha.add(crearBotonIcono("👤", 16));
        cab.add(derecha, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea un botón transparente para los iconos de la cabecera.
     *
     * @param icono    texto o símbolo del icono
     * @param fontSize tamaño de fuente
     * @return botón configurado
     */
    private JButton crearBotonIcono(String icono, int fontSize) {
        JButton btn = new JButton(icono);
        btn.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        btn.setForeground(COLOR_TEXTO_CLARO);
        btn.setBackground(new Color(0x2B, 0x1F, 0x0E));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ------------------------------------------------------------------ //
    //  Cuerpo: barra de tabs + cuadrícula                                //
    // ------------------------------------------------------------------ //

    /**
     * Crea el cuerpo principal con la barra de tabs encima y la cuadrícula debajo.
     *
     * @return panel con el contenido principal
     */
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(COLOR_FONDO);

        cuerpo.add(crearBarraTabs(), BorderLayout.NORTH);

        panelGrid = new JPanel();
        panelGrid.setBackground(COLOR_FONDO);
        actualizarGrid();

        JScrollPane scroll = new JScrollPane(panelGrid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO);

        cuerpo.add(scroll, BorderLayout.CENTER);
        return cuerpo;
    }

    // ------------------------------------------------------------------ //
    //  Barra de pestañas                                                 //
    // ------------------------------------------------------------------ //

    /**
     * Crea la barra de tres pestañas (Subidos / Valorados / Publicados).
     * Al pulsar cada tab se actualiza la cuadrícula sin reconstruir todo el panel.
     *
     * @return panel con los tres botones de pestaña
     */
    private JPanel crearBarraTabs() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        barra.setBackground(COLOR_FONDO);

        String[] etiquetas = {"Subidos", "Valorados", "Publicados"};
        botonesTab = new JButton[etiquetas.length];

        for (int i = 0; i < etiquetas.length; i++) {
            final int idx = i;
            JButton btn = new JButton(etiquetas[i]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            btn.setFocusPainted(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.setPreferredSize(new Dimension(130, 32));
            aplicarEstiloTab(btn, i == tabActivo);

            btn.addActionListener(e -> {
                tabActivo = idx;
                actualizarEstiloTabs();
                actualizarGrid();
            });

            botonesTab[i] = btn;
            barra.add(btn);
        }
        return barra;
    }

    /**
     * Aplica el estilo visual a un botón de pestaña según si está activo o no.
     *
     * @param btn    botón al que aplicar el estilo
     * @param activo {@code true} si es la pestaña seleccionada
     */
    private void aplicarEstiloTab(JButton btn, boolean activo) {
        if (activo) {
            btn.setBackground(COLOR_OSCURO);
            btn.setForeground(COLOR_TEXTO_CLARO);
            btn.setOpaque(true);
            btn.setBorderPainted(false);
        } else {
            btn.setBackground(COLOR_FONDO);
            btn.setForeground(COLOR_TEXTO);
            btn.setOpaque(true);
            btn.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_TAB, 1, true));
        }
    }

    /**
     * Recorre todos los botones de tab y actualiza su estilo según el tab activo.
     */
    private void actualizarEstiloTabs() {
        for (int i = 0; i < botonesTab.length; i++) {
            aplicarEstiloTab(botonesTab[i], i == tabActivo);
        }
    }

    // ------------------------------------------------------------------ //
    //  Cuadrícula de productos                                           //
    // ------------------------------------------------------------------ //

    /**
     * Reconstruye el contenido de la cuadrícula según la pestaña activa.
     * Filtra la lista de productos de la cartera del cliente y genera
     * las tarjetas correspondientes.
     */
    private void actualizarGrid() {
        panelGrid.removeAll();

        List<ProductoSegundaMano> todos = cliente.getCartera().getProductos();
        List<ProductoSegundaMano> filtrados = filtrarPorTab(todos, tabActivo);

        int columnas = 3;
        int filas    = Math.max(1, (int) Math.ceil(
                (filtrados.size() + (tabActivo == TAB_SUBIDOS ? 1 : 0)) / (double) columnas));

        panelGrid.setLayout(new GridLayout(filas, columnas, 12, 12));
        panelGrid.setBorder(new EmptyBorder(14, 16, 14, 16));

        for (ProductoSegundaMano p : filtrados) {
            ActionListener listener = resolverListener(p);
            panelGrid.add(new TarjetaSegundaMano(p, listener));
        }

        // En la pestaña "Subidos" se añade la tarjeta especial de subida
        if (tabActivo == TAB_SUBIDOS) {
            panelGrid.add(new TarjetaSubirProducto(
                    e -> { if (listenerSubir != null) listenerSubir.actionPerformed(e); }));
        }

        // Rellena celdas vacías para mantener el layout uniforme
        int total     = filtrados.size() + (tabActivo == TAB_SUBIDOS ? 1 : 0);
        int restantes = filas * columnas - total;
        for (int i = 0; i < restantes; i++) {
            JPanel vacio = new JPanel();
            vacio.setBackground(COLOR_FONDO);
            panelGrid.add(vacio);
        }

        panelGrid.revalidate();
        panelGrid.repaint();
    }

    /**
     * Filtra la lista completa de productos según la pestaña activa:
     * <ul>
     *   <li>Subidos: todos los productos.</li>
     *   <li>Valorados: solo los que tienen {@link EstadoProducto#VALORADO}.</li>
     *   <li>Publicados: solo los que tienen disponibilidad activa.</li>
     * </ul>
     *
     * @param todos lista completa de productos de la cartera
     * @param tab   índice de la pestaña activa
     * @return lista filtrada
     */
    private List<ProductoSegundaMano> filtrarPorTab(
            List<ProductoSegundaMano> todos, int tab) {
        switch (tab) {
            case TAB_VALORADOS:
                return todos.stream()
                        .filter(p -> p.getEstadoProducto() == EstadoProducto.VALORADO)
                        .collect(Collectors.toList());
            case TAB_PUBLICADOS:
                return todos.stream()
                        .filter(ProductoSegundaMano::getDisponibilidad)
                        .collect(Collectors.toList());
            default: // TAB_SUBIDOS
                return todos;
        }
    }

    /**
     * Determina qué listener de negocio debe asociarse al botón de una tarjeta
     * en función del estado del producto y la pestaña activa.
     *
     * <p>En la pestaña "Publicados" no se muestra botón, por lo que devuelve
     * {@code null}.</p>
     *
     * @param p producto al que pertenece la tarjeta
     * @return listener apropiado, o {@code null} si no procede botón
     */
    private ActionListener resolverListener(ProductoSegundaMano p) {
        if (tabActivo == TAB_PUBLICADOS) {
            return null;
        }
        if (p.getEstadoProducto() == EstadoProducto.VALORADO) {
            return e -> { if (listenerPublicar != null) listenerPublicar.actionPerformed(e); };
        }
        return e -> { if (listenerPedirValoracion != null) listenerPedirValoracion.actionPerformed(e); };
    }
}