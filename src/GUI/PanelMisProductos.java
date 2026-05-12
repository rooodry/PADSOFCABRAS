package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoProducto;


/**
 * Representa el componente PanelMisProductos de la interfaz gráfica.
 * Este panel permite al cliente visualizar y gestionar los productos de su cartera,
 * divididos en diferentes estados: subidos, valorados y publicados.
 */
public class PanelMisProductos extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Índice que representa la pestaña de productos subidos y pendientes de valorar. */
    private static final int TAB_SUBIDOS = 0;
    
    /** Índice que representa la pestaña de productos que ya han sido valorados por un administrador. */
    private static final int TAB_VALORADOS = 1;
    
    /** Índice que representa la pestaña de productos que están actualmente publicados para su intercambio. */
    private static final int TAB_PUBLICADOS = 2;

    /** Referencia al controlador principal de la ventana y de la aplicación. */
    private final Main mainFrame;
    
    /** El cliente registrado que es propietario de los productos mostrados en este panel. */
    private ClienteRegistrado cliente;
    
    /** Contenedor principal que organiza las tarjetas de los productos en formato de cuadrícula. */
    private final JPanel panelGrid;
    
    /** Arreglo que almacena los botones de navegación entre las pestañas (Subidos, Valorados, Publicados). */
    private final JButton[] botonesTab;

    /** Almacena el índice de la pestaña que se encuentra actualmente visible. Por defecto es TAB_SUBIDOS. */
    private int tabActivo = TAB_SUBIDOS;
    
    /** Escuchador para manejar el evento de subir un nuevo producto. */
    private ActionListener listenerSubir;
    
    /** Escuchador para manejar el evento de solicitar la valoración de un producto subido. */
    private ActionListener listenerPedirValoracion;
    
    /** Escuchador para manejar el evento de publicar un producto que ya ha sido valorado. */
    private ActionListener listenerPublicar;
    
    /** Escuchador para manejar el evento de eliminar un producto de la cartera del usuario. */
    private ActionListener listenerEliminar;

    /**
     * Crea el panel de cartera para el cliente indicado, integrándolo con el flujo principal.
     *
     * @param mainFrame controlador principal de la aplicación que gestiona las vistas generales.
     * @param cliente   cliente propietario de la cartera cuyos productos se van a visualizar.
     */
    public PanelMisProductos(Main mainFrame, ClienteRegistrado cliente) {
        this.mainFrame = mainFrame;
        this.cliente = cliente;
        this.panelGrid = new JPanel();
        this.botonesTab = new JButton[3];

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(crearCabeceraSimple(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
    }

    /**
     * Constructor de compatibilidad para pruebas visuales aisladas, sin depender del controlador principal.
     *
     * @param cliente cliente propietario de la cartera cuyos productos se van a visualizar.
     */
    public PanelMisProductos(ClienteRegistrado cliente) {
        this(null, cliente);
    }

    /**
     * Registra el listener que se ejecutará al pulsar el botón de subir un nuevo producto.
     *
     * @param listener escuchador que define la acción a ejecutar.
     */
    public void addListenerSubirProducto(ActionListener listener) {
        this.listenerSubir = listener;
    }

    /**
     * Registra el listener que se ejecutará para solicitar la valoración de un producto.
     *
     * @param listener escuchador que recibirá el evento con el producto correspondiente como origen (source).
     */
    public void addListenerPedirValoracion(ActionListener listener) {
        this.listenerPedirValoracion = listener;
    }

    /**
     * Registra el listener que se ejecutará para publicar un producto previamente valorado en el mercado.
     *
     * @param listener escuchador que recibirá el evento con el producto correspondiente como origen (source).
     */
    public void addListenerPublicar(ActionListener listener) {
        this.listenerPublicar = listener;
    }

    /**
     * Registra el listener que se ejecutará para confirmar y proceder con la eliminación de un producto.
     * 
     * @param listener escuchador que define la acción de eliminación a aplicar sobre un producto.
     */
    public void addListenerEliminarProducto(ActionListener listener) {
        this.listenerEliminar = listener;
    }

    /**
     * Actualiza y repinta la cuadrícula (grid) de productos, reflejando cualquier
     * cambio en los datos subyacentes o un cambio de pestaña.
     */
    public void refrescar() {
        actualizarGrid();
    }

    /**
     * Cambia el cliente activo en el panel y actualiza la vista para mostrar los productos del nuevo cliente.
     *
     * @param cliente el nuevo cliente cuyos productos deben mostrarse.
     */
    public void setCliente(ClienteRegistrado cliente) {
        this.cliente = cliente;
        refrescar();
    }

    /**
     * Construye la parte superior del panel, que puede incluir un componente de navegación general
     * o un título simple si se utiliza en modo aislado.
     * 
     * @return un {@code JPanel} configurado que actúa como cabecera.
     */
    private JPanel crearCabeceraSimple() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(UiStyle.COLOR_FONDO);
        if (mainFrame != null) {
            contenedor.add(new HomePanel.PanelNavegacionCliente(mainFrame, "MIS PRODUCTOS"), BorderLayout.NORTH);
        } else {
            JLabel titulo = new JLabel("Mis productos", SwingConstants.CENTER);
            titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
            titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            contenedor.setBackground(UiStyle.COLOR_CABECERA);
            contenedor.add(titulo, BorderLayout.CENTER);
        }
        return contenedor;
    }

    /**
     * Construye el cuerpo central del panel, agregando la barra de pestañas en la parte superior
     * y un panel desplazable (scroll) para la cuadrícula de productos.
     * 
     * @return un {@code JPanel} con el diseño del cuerpo central completo.
     */
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(UiStyle.COLOR_FONDO);
        cuerpo.add(crearBarraTabs(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelGrid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        cuerpo.add(scroll, BorderLayout.CENTER);

        actualizarGrid();
        return cuerpo;
    }

    /**
     * Crea la barra de navegación entre las categorías de los productos (Subidos, Valorados, Publicados).
     * 
     * @return un {@code JPanel} que contiene los botones de las pestañas configurados con sus eventos.
     */
    private JPanel crearBarraTabs() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 14));
        barra.setBackground(UiStyle.COLOR_FONDO);

        String[] etiquetas = {"Subidos", "Valorados", "Publicados"};
        for (int i = 0; i < etiquetas.length; i++) {
            final int indice = i;
            JButton boton = new UiStyle.RoundedButton(etiquetas[i], UiStyle.COLOR_FONDO,
                    UiStyle.COLOR_TARJETA, 22);
            boton.setPreferredSize(new Dimension(150, 34));
            boton.setFocusPainted(false);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.addActionListener(e -> {
                tabActivo = indice;
                actualizarTabs();
                actualizarGrid();
            });
            botonesTab[i] = boton;
            barra.add(boton);
        }
        actualizarTabs();
        return barra;
    }

    /**
     * Refresca el aspecto visual de los botones que forman las pestañas, aplicando estilos
     * distintos al botón que corresponde con la pestaña activa actualmente.
     */
    private void actualizarTabs() {
        for (int i = 0; i < botonesTab.length; i++) {
            JButton boton = botonesTab[i];
            boton.setForeground(i == tabActivo ? UiStyle.COLOR_TEXTO_CLARO : UiStyle.COLOR_TEXTO);
            if (boton instanceof UiStyle.RoundedButton) {
                Color normal = i == tabActivo ? UiStyle.COLOR_CABECERA : UiStyle.COLOR_FONDO;
                Color hover = i == tabActivo ? UiStyle.COLOR_MARRON_MEDIO : UiStyle.COLOR_TARJETA;
                ((UiStyle.RoundedButton) boton).setButtonColors(normal, hover);
            } else {
                boton.setBackground(i == tabActivo ? UiStyle.COLOR_CABECERA : UiStyle.COLOR_FONDO);
                boton.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_CABECERA));
            }
        }
    }

    /**
     * Vacía el contenedor de productos y lo vuelve a rellenar de manera dinámica
     * con las tarjetas de los productos que corresponden a la pestaña activa en ese momento.
     * También añade una tarjeta especial para subir nuevos productos si se está en la primera pestaña.
     */
    private void actualizarGrid() {
        panelGrid.removeAll();
        panelGrid.setBackground(UiStyle.COLOR_FONDO);
        panelGrid.setBorder(new EmptyBorder(20, 40, 20, 40));

        List<ProductoSegundaMano> filtrados = filtrarPorTab(cliente.getCartera().getProductos());
        int columnas = 3;
        int total = filtrados.size() + (tabActivo == TAB_SUBIDOS ? 1 : 0);
        int filas = Math.max(1, (int) Math.ceil(total / (double) columnas));
        panelGrid.setLayout(new GridLayout(filas, columnas, 12, 18));

        if (total == 0) {
            JLabel vacio = new JLabel("No hay productos en esta sección.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            panelGrid.add(vacio);
            for (int i = 1; i < filas * columnas; i++) {
                JPanel relleno = new JPanel();
                relleno.setBackground(UiStyle.COLOR_FONDO);
                panelGrid.add(relleno);
            }
            panelGrid.revalidate();
            panelGrid.repaint();
            return;
        }

        for (ProductoSegundaMano producto : filtrados) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            envoltura.setBackground(UiStyle.COLOR_FONDO);
            TarjetaSegundaMano tarjeta = new TarjetaSegundaMano(producto, resolverListener(producto),
                    e -> confirmarEliminacion(producto));
            tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                /**
                 * Intercepta los clics del ratón sobre la tarjeta para mostrar los detalles del producto.
                 * 
                 * @param e objeto MouseEvent que contiene los detalles de la acción del usuario.
                 */
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    mostrarDetalleProducto(producto);
                }
            });
            envoltura.add(tarjeta);
            panelGrid.add(envoltura);
        }

        if (tabActivo == TAB_SUBIDOS) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            envoltura.setBackground(UiStyle.COLOR_FONDO);
            envoltura.add(new TarjetaSubirProducto(e -> {
                if (listenerSubir != null) {
                    listenerSubir.actionPerformed(e);
                }
            }));
            panelGrid.add(envoltura);
        }

        for (int i = total; i < filas * columnas; i++) {
            JPanel vacio = new JPanel();
            vacio.setBackground(UiStyle.COLOR_FONDO);
            panelGrid.add(vacio);
        }

        panelGrid.revalidate();
        panelGrid.repaint();
    }

    /**
     * Muestra una ventana emergente de diálogo con la información detallada
     * del producto seleccionado, incluyendo su estado, descripción y valor estimado.
     * 
     * @param producto el producto de segunda mano del cual se van a extraer los detalles a mostrar.
     */
    private void mostrarDetalleProducto(ProductoSegundaMano producto) {
        String estado = producto.getEstadoConservacion() == null
                ? "Pendiente de valorar" : producto.getEstadoConservacion().toString();
        String precio = producto.getValorEstimado() > 0
                ? String.format("%.2f EUR", producto.getValorEstimado()) : "Sin tasar";
        JOptionPane.showMessageDialog(this,
                "Producto: " + producto.getNombre()
                        + "\nDescripción: " + producto.getDescripcion()
                        + "\nEstado: " + estado
                        + "\nPrecio estimado: " + precio
                        + "\nSituacion: " + producto.getEstadoProducto(),
                "Producto de segunda mano", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Filtra la lista completa de productos del usuario, devolviendo únicamente aquellos
     * que se corresponden con los requisitos de la pestaña activa en ese instante.
     * 
     * @param productos la lista original completa de productos asociados al cliente.
     * @return una sublista que contiene solo los productos aplicables a la vista actual.
     */
    private List<ProductoSegundaMano> filtrarPorTab(List<ProductoSegundaMano> productos) {
        if (tabActivo == TAB_VALORADOS) {
            return productos.stream()
                    .filter(p -> p.getEstadoProducto() == EstadoProducto.VALORADO)
                    .collect(Collectors.toList());
        }
        if (tabActivo == TAB_PUBLICADOS) {
            return productos.stream().filter(ProductoSegundaMano::getDisponibilidad).collect(Collectors.toList());
        }
        return productos;
    }

    /**
     * Determina y devuelve el listener principal adecuado para el botón de acción de
     * una tarjeta de producto, de acuerdo con el estado del producto y la pestaña actual.
     * 
     * @param producto el producto a evaluar para asociarle un comportamiento.
     * @return el {@code ActionListener} que debe dispararse (pedir valoración, publicar, etc.) o {@code null}.
     */
    private ActionListener resolverListener(ProductoSegundaMano producto) {
        if (tabActivo == TAB_PUBLICADOS) {
            return null;
        }
        if (producto.getEstadoProducto() == EstadoProducto.VALORADO) {
            return e -> {
                if (listenerPublicar != null) {
                    listenerPublicar.actionPerformed(new ActionEvent(producto, ActionEvent.ACTION_PERFORMED, "publicar"));
                }
            };
        }
        return e -> {
            if (listenerPedirValoracion != null) {
                listenerPedirValoracion.actionPerformed(
                        new ActionEvent(producto, ActionEvent.ACTION_PERFORMED, "valorar"));
            }
        };
    }

    /**
     * Solicita confirmación al usuario antes de proceder a la eliminación de un producto
     * y notifica al listener correspondiente en caso afirmativo.
     * 
     * @param producto el producto que el usuario ha solicitado eliminar.
     */
    private void confirmarEliminacion(ProductoSegundaMano producto) {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Quieres eliminar este producto de tu cartera?",
                "Eliminar producto", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION && listenerEliminar != null) {
            listenerEliminar.actionPerformed(new ActionEvent(producto, ActionEvent.ACTION_PERFORMED, "eliminar"));
        }
    }
}