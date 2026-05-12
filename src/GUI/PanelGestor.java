package GUI;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import compras.Pedido;
import intercambios.Intercambio;
import productos.Pack;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import productos.categoria.Genero;
import productos.categoria.TipoJuego;
import usuarios.ClienteRegistrado;
import usuarios.Empleado;
import utilidades.EstadoConservacion;
import utilidades.EstadoOferta;
import utilidades.EstadoPedido;
import utilidades.TiposEmpleado;

/**
 * Representa el componente PanelGestor de la interfaz grafica.
 * Proporciona el panel de control y administración para los usuarios
 * con rol de gestor de la tienda, permitiendo gestionar empleados,
 * inventario, métricas, descuentos y pedidos.
 */
public class PanelGestor extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador para la sección principal de resumen de gestión (Dashboard).
     */
    private static final String DASHBOARD = "DASHBOARD";
    
    /**
     * Identificador para la sección de administración de empleados.
     */
    private static final String EMPLEADOS = "EMPLEADOS";
    
    /**
     * Identificador para la sección de gestión del inventario y catálogo de productos.
     */
    private static final String INVENTARIO = "INVENTARIO";
    
    /**
     * Identificador para la sección de configuración de rebajas y promociones.
     */
    private static final String DESCUENTOS = "DESCUENTOS";
    
    /**
     * Identificador para la sección de tasación y revisión de productos de segunda mano.
     */
    private static final String SEGUNDA_MANO = "SEGUNDA_MANO";
    
    /**
     * Identificador para la sección de creación y edición de packs de productos.
     */
    private static final String PACKS = "PACKS";
    
    /**
     * Identificador para la sección de supervisión de pedidos e intercambios en curso.
     */
    private static final String OPERATIVA = "OPERATIVA";
    
    /**
     * Identificador para la sección de análisis de datos y estadísticas de la tienda.
     */
    private static final String ESTADISTICAS = "ESTADISTICAS";
    
    /**
     * Identificador para la subsección de visualización o edición de un producto específico.
     */
    private static final String DETALLE_PRODUCTO = "DETALLE_PRODUCTO";

    /**
     * Referencia a la ventana y controlador principal de la aplicación.
     */
    private final Main mainFrame;
    
    /**
     * Panel contenedor dinámico donde se pinta la sección actualmente activa.
     */
    private final JPanel contenido;
    
    /** 
     * Formateador estándar para mostrar las fechas de los pedidos, ventas e intercambios de forma legible. 
     */
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    
    /**
     * Identificador en formato de cadena de la sección que se está visualizando actualmente.
     */
    private String seccionActiva;
    
    /**
     * Almacena el producto del inventario que ha sido seleccionado para ver sus detalles o ser editado.
     */
    private ProductoTienda productoSeleccionado;
    
    /**
     * Bandera que indica si el producto seleccionado está en modo de edición o solo lectura.
     */
    private boolean editandoProducto;

    /**
     * Construye una instancia de PanelGestor, inicializando los componentes
     * y estableciendo el Dashboard como pantalla por defecto.
     * 
     * @param mainFrame Instancia principal de la aplicación que proporciona el contexto y los datos.
     */
    public PanelGestor(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.contenido = new JPanel();
        this.seccionActiva = DASHBOARD;

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(crearCabecera(), BorderLayout.NORTH);
        add(crearScroll(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Limpia y vuelve a pintar el contenido del panel dinámico basándose en
     * la variable de estado `seccionActiva`.
     */
    public void refrescar() {
        contenido.removeAll();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(UiStyle.COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(24, 32, 24, 32));

        if (DASHBOARD.equals(seccionActiva)) {
            pintarDashboard();
        } else if (EMPLEADOS.equals(seccionActiva)) {
            pintarEmpleados();
        } else if (INVENTARIO.equals(seccionActiva)) {
            pintarInventario();
        } else if (DESCUENTOS.equals(seccionActiva)) {
            pintarDescuentos();
        } else if (SEGUNDA_MANO.equals(seccionActiva)) {
            pintarSegundaMano();
        } else if (PACKS.equals(seccionActiva)) {
            pintarPacks();
        } else if (OPERATIVA.equals(seccionActiva)) {
            pintarOperativa();
        } else if (ESTADISTICAS.equals(seccionActiva)) {
            pintarEstadisticas();
        } else if (DETALLE_PRODUCTO.equals(seccionActiva)) {
            pintarDetalleProducto();
        }

        contenido.revalidate();
        contenido.repaint();
    }

    /**
     * Crea la barra superior de la interfaz, que incluye el botón de menú lateral
     * y el título principal de la aplicación.
     * 
     * @return JPanel configurado como la cabecera de la ventana.
     */
    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(UiStyle.COLOR_CABECERA);
        cabecera.setPreferredSize(new Dimension(0, 50));
        cabecera.setBorder(new EmptyBorder(3, 14, 3, 12));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        JButton menu = crearBotonIcono("\u2630", "Abrir menu", 32, 48);
        menu.addActionListener(e -> mostrarMenu(menu));
        izquierda.add(menu);
        cabecera.add(izquierda, BorderLayout.WEST);

        JLabel titulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        cabecera.add(titulo, BorderLayout.CENTER);

        return cabecera;
    }

    /**
     * Crea un botón que contiene un símbolo o icono en texto.
     * 
     * @param texto El símbolo o carácter que servirá como icono del botón.
     * @param tooltip El texto explicativo que aparece al pasar el ratón.
     * @param fontSize El tamaño de la fuente para el icono.
     * @param ancho El ancho preferido del botón.
     * @return JButton con los estilos visuales de botón icono aplicados.
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
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Muestra el menú lateral desplegable que permite navegar entre las diferentes
     * secciones de gestión disponibles para el gestor.
     * 
     * @param origen El componente (botón) sobre el cual se anclará y desplegará el menú emergente.
     */
    private void mostrarMenu(JButton origen) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(UiStyle.COLOR_CABECERA);
        menu.setBorder(new EmptyBorder(8, 8, 8, 8));
        menu.add(crearItemMenu("HOME", DASHBOARD));
        menu.add(crearItemMenu("PRODUCTOS", INVENTARIO));
        menu.add(crearItemMenu("SEGUNDA MANO", SEGUNDA_MANO));
        menu.add(crearItemMenu("DESCUENTOS", DESCUENTOS));
        menu.add(crearItemMenu("PACKS", PACKS));
        menu.add(crearItemMenu("EMPLEADOS", EMPLEADOS));
        menu.add(crearItemMenu("PEDIDOS E INTERC.", OPERATIVA));
        menu.add(crearItemMenu("ESTADISTICAS", ESTADISTICAS));
        menu.addSeparator();
        JMenuItem salir = crearItemMenu("CERRAR SESIÓN", DASHBOARD);
        salir.addActionListener(e -> mainFrame.cerrarSesion());
        menu.add(salir);
        menu.show(origen, 0, origen.getHeight() + 6);
    }

    /**
     * Crea una opción individual para el menú de navegación lateral.
     * 
     * @param texto El texto que se mostrará en el menú.
     * @param seccion La constante identificadora de la sección a la que navega esta opción.
     * @return JMenuItem configurado visual y funcionalmente.
     */
    private JMenuItem crearItemMenu(String texto, String seccion) {
        JMenuItem item = new JMenuItem(texto);
        item.setOpaque(true);
        item.setBackground(seccion.equals(seccionActiva) ? UiStyle.COLOR_TEXTO : UiStyle.COLOR_CABECERA);
        item.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        item.setFont(new Font("SansSerif", Font.BOLD, 14));
        item.setBorder(new EmptyBorder(8, 16, 8, 68));
        item.addActionListener(e -> {
            seccionActiva = seccion;
            refrescar();
        });
        return item;
    }

    /**
     * Construye un componente con barras de desplazamiento para contener la sección dinámica,
     * adaptándose al tamaño de la pantalla y el contenido.
     * 
     * @return JScrollPane que engloba el contenido principal.
     */
    private JScrollPane crearScroll() {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    /**
     * Pinta la sección principal de inicio (Dashboard), mostrando tarjetas
     * con resúmenes rápidos, accesos directos y un listado de actividad pendiente.
     */
    private void pintarDashboard() {
        contenido.add(crearTitulo("Resumen de gestión"));
        JPanel grid = new JPanel(new GridLayout(0, 4, 14, 14));
        grid.setOpaque(false);
        grid.add(crearMetrica("Catálogo", String.valueOf(mainFrame.getProductosTienda().size()), "productos"));
        grid.add(crearMetrica("Stock", String.valueOf(totalUnidadesStock()), "unidades"));
        grid.add(crearMetrica("Pedidos", String.valueOf(mainFrame.getPedidosGestion().size()), "registrados"));
        grid.add(crearMetrica("Empleados", String.valueOf(mainFrame.getEmpleados().size()), "activos"));
        contenido.add(grid);

        contenido.add(crearTitulo("Accesos rápidos"));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        acciones.setOpaque(false);
        acciones.add(crearAcceso("Nuevo empleado", EMPLEADOS));
        acciones.add(crearAcceso("Editar descuentos", DESCUENTOS));
        acciones.add(crearAcceso("Gestionar packs", PACKS));
        acciones.add(crearAcceso("Ver estadísticas", ESTADISTICAS));
        contenido.add(acciones);

        contenido.add(crearTitulo("Actividad pendiente"));
        contenido.add(crearEtiqueta("Pedidos en preparación: " + contarPedidos(EstadoPedido.EN_PREPARACION)));
        contenido.add(crearEtiqueta("Pedidos listos para entregar: " + contarPedidos(EstadoPedido.LISTO)));
        contenido.add(crearEtiqueta("Productos pendientes de valorar: "
                + mainFrame.getProductosPendientesValoracion().size()));
        contenido.add(crearEtiqueta("Intercambios pendientes: " + contarIntercambiosPendientes()));
    }

    /**
     * Construye un botón estilizado diseñado específicamente como acceso directo
     * hacia otra pantalla de la interfaz gestora.
     * 
     * @param texto Etiqueta del botón.
     * @param seccion Constante de sección que se activará al pulsar.
     * @return JButton funcional que navega a la sección indicada.
     */
    private JButton crearAcceso(String texto, String seccion) {
        JButton boton = crearBoton(texto, 170);
        boton.addActionListener(e -> {
            seccionActiva = seccion;
            refrescar();
        });
        return boton;
    }

    /**
     * Crea un panel en forma de tarjeta diseñado para mostrar una estadística clave
     * o métrica rápida en el Dashboard.
     * 
     * @param titulo Título de la métrica (ej. "Stock").
     * @param valor Valor numérico representativo.
     * @param detalle Subtítulo explicativo o sufijo (ej. "unidades").
     * @return JPanel configurado visualmente como tarjeta de métrica.
     */
    private JPanel crearMetrica(String titulo, String valor, String detalle) {
        JPanel tarjeta = crearTarjeta();
        JLabel t = crearEtiqueta("<b>" + titulo + "</b>");
        JLabel v = new JLabel(valor);
        v.setFont(new Font("SansSerif", Font.BOLD, 32));
        v.setForeground(UiStyle.COLOR_TEXTO);
        JLabel d = crearEtiqueta(detalle);
        tarjeta.add(t);
        tarjeta.add(v);
        tarjeta.add(d);
        return tarjeta;
    }

    /**
     * Pinta la sección de gestión de empleados, mostrando la lista de empleados
     * creados, con botones para ver o editar sus detalles, ver sus ventas y darlos de baja.
     */
    private void pintarEmpleados() {
        contenido.add(crearTitulo("Gestión de empleados"));
        contenido.add(crearSubtitulo("El gestor da de alta empleados, asigna permisos y puede darlos de baja."));
        JButton nuevo = crearBoton("Nuevo empleado", 160);
        nuevo.addActionListener(e -> mostrarDialogoEmpleado(null));
        contenido.add(nuevo);
        contenido.add(Box.createVerticalStrut(14));

        for (Empleado empleado : mainFrame.getEmpleados()) {
            contenido.add(crearFilaEmpleado(empleado));
            contenido.add(Box.createVerticalStrut(10));
        }
    }

    /**
     * Crea un panel horizontal que representa la información de un único empleado
     * junto con los controles para gestionar dicho empleado.
     * 
     * @param empleado El empleado cuyos datos se mostrarán en la fila.
     * @return JPanel visual que representa al empleado.
     */
    private JPanel crearFilaEmpleado(Empleado empleado) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(22, 16, 22, 16));
        fila.setPreferredSize(new Dimension(0, 104));
        fila.setMinimumSize(new Dimension(0, 104));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 104));
        fila.add(crearEtiqueta("<b>" + empleado.getNombre() + "</b><br>Permisos: "
                + textoPermisos(empleado) + "<br>"
                + resumenVentasEmpleado(empleado)), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton permisos = crearBoton("Editar", 90);
        permisos.addActionListener(e -> mostrarDialogoEmpleado(empleado));
        acciones.add(permisos);
        JButton ventas = crearBoton("Ventas", 90);
        ventas.addActionListener(e -> mostrarVentasEmpleado(empleado));
        acciones.add(ventas);
        JButton baja = crearBoton("Baja", 86);
        baja.addActionListener(e -> confirmarBajaEmpleado(empleado));
        acciones.add(baja);
        fila.add(acciones, BorderLayout.EAST);
        return fila;
    }

    /**
     * Pinta la sección del inventario, organizando los productos de la tienda
     * en una cuadrícula y proporcionando utilidades de creación manual o carga por CSV.
     */
    private void pintarInventario() {
        contenido.setBorder(new EmptyBorder(12, 60, 24, 60));
        JLabel titulo = new JLabel("HOME", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(10));

        JPanel herramientas = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        herramientas.setOpaque(false);
        JButton nuevo = crearBoton("Nuevo producto", 150);
        nuevo.addActionListener(e -> mostrarDialogoNuevoProducto());
        herramientas.add(nuevo);
        JButton cargar = crearBoton("Cargar CSV", 118);
        cargar.addActionListener(e -> cargarProductosDeFichero());
        herramientas.add(cargar);
        contenido.add(herramientas);
        contenido.add(Box.createVerticalStrut(14));

        JPanel grid = new JPanel(new GridLayout(0, 3, 54, 24));
        grid.setOpaque(false);
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            envoltura.setOpaque(false);
            envoltura.add(crearTarjetaProductoHome(producto));
            grid.add(envoltura);
        }
        contenido.add(grid);
    }

    /**
     * Crea un panel en forma de tarjeta vertical para visualizar un producto del catálogo,
     * añadiendo opciones exclusivas del gestor para editarlo o eliminarlo permanentemente.
     * 
     * @param producto El producto a renderizar visualmente.
     * @return JPanel con la representación visual y botones de control del producto.
     */
    private JPanel crearTarjetaProductoHome(ProductoTienda producto) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setOpaque(false);
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        TarjetaProducto vistaCliente = new TarjetaProducto(producto);
        vistaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            /**
             * Responde al evento de clic abriendo el detalle completo del producto en modo lectura.
             * 
             * @param e El evento de ratón interceptado.
             */
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirDetalleProducto(producto, false);
            }
        });
        tarjeta.add(vistaCliente, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new GridLayout(1, 2, 8, 0));
        acciones.setOpaque(false);

        JButton editar = new UiStyle.RoundedButton("Editar", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        editar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        editar.setPreferredSize(new Dimension(96, 28));
        editar.addActionListener(e -> abrirDetalleProducto(producto, true));
        acciones.add(editar);

        JButton eliminar = new UiStyle.RoundedButton("Eliminar", new Color(120, 55, 45),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        eliminar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eliminar.setPreferredSize(new Dimension(96, 28));
        eliminar.addActionListener(e -> confirmarEliminacionProducto(producto));
        acciones.add(eliminar);

        tarjeta.add(acciones, BorderLayout.SOUTH);
        return tarjeta;
    }

    /**
     * Muestra un cuadro de confirmación antes de eliminar permanentemente un producto.
     * Si el gestor acepta, realiza el borrado de sistema y refresca la interfaz.
     * 
     * @param producto El producto objetivo para eliminar.
     */
    private void confirmarEliminacionProducto(ProductoTienda producto) {
        if (producto == null) {
            return;
        }
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Vas a eliminar " + producto.getNombre()
                        + " del catálogo, del stock y de los packs que lo incluyan.\n"
                        + "El historial de pedidos se conservará.\n\n¿Seguro que quieres eliminarlo?",
                "Eliminar producto",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (respuesta == JOptionPane.YES_OPTION) {
            mainFrame.eliminarProductoTiendaGestion(producto);
            productoSeleccionado = null;
            editandoProducto = false;
            seccionActiva = INVENTARIO;
            refrescar();
        }
    }

    /**
     * Cambia la sección activa a la pantalla de detalle para el producto indicado.
     * 
     * @param producto El producto a examinar.
     * @param editar Si es verdadero, el detalle se abrirá en estado editable de formulario.
     */
    private void abrirDetalleProducto(ProductoTienda producto, boolean editar) {
        productoSeleccionado = producto;
        editandoProducto = editar;
        seccionActiva = DETALLE_PRODUCTO;
        refrescar();
    }

    /**
     * Pinta la sección de vista en detalle de un producto específico, integrando el
     * componente `PanelDeProducto` y sus callbacks para confirmar cambios.
     */
    private void pintarDetalleProducto() {
        if (productoSeleccionado == null) {
            seccionActiva = INVENTARIO;
            pintarInventario();
            return;
        }
        contenido.setBorder(new EmptyBorder(8, 14, 24, 34));
        contenido.add(crearBarraVolverInventario());
        PanelDeProducto detalle = new PanelDeProducto(productoSeleccionado, mainFrame, editandoProducto);
        detalle.configurarBotonCesta("Modificar", true);
        detalle.addListenerCesta(e -> {
            editandoProducto = true;
            refrescar();
        });
        detalle.setAlignmentX(Component.CENTER_ALIGNMENT);
        detalle.setListenerEdicion(new PanelDeProducto.ListenerEdicion() {
            @Override
            /**
             * Aplica los cambios ingresados por el gestor a los datos del producto seleccionado.
             * 
             * @param datos Objeto con la información estructurada que introdujo el gestor.
             */
            public void confirmar(PanelDeProducto.DatosEdicion datos) {
                mainFrame.editarProductoTienda(productoSeleccionado,
                        datos.nombre,
                        parseDouble(datos.precio, productoSeleccionado.getPrecio()),
                        datos.stock,
                        datos.descripcion,
                        datos.imagen,
                        parseCategorías(datos.categorias));
                editandoProducto = false;
                refrescar();
            }

            @Override
            /**
             * Cancela el modo edición del producto retornando a su visualización normal.
             */
            public void cancelar() {
                editandoProducto = false;
                refrescar();
            }
        });
        contenido.add(detalle);
    }

    /**
     * Crea un panel superior muy simple que provee la acción de volver
     * a la vista general de catálogo.
     * 
     * @return JPanel con el botón hacia atrás.
     */
    private JPanel crearBarraVolverInventario() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton volver = crearBotonFlechaVolver();
        volver.addActionListener(e -> volverAInventario());
        barra.add(volver);
        return barra;
    }

    /**
     * Construye un botón visualmente minimalista en forma de flecha apuntando a la izquierda.
     * 
     * @return JButton configurado como flecha de retorno.
     */
    private JButton crearBotonFlechaVolver() {
        JButton boton = new JButton("\u2190");
        boton.setFont(new Font("SansSerif", Font.BOLD, 30));
        boton.setForeground(UiStyle.COLOR_TEXTO);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setToolTipText("Volver a productos");
        boton.setPreferredSize(new Dimension(48, 40));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Regresa la vista principal al inventario, limpiando la selección activa
     * de cualquier producto específico.
     */
    private void volverAInventario() {
        editandoProducto = false;
        productoSeleccionado = null;
        seccionActiva = INVENTARIO;
        refrescar();
    }

    /**
     * Pinta la sección de gestión de descuentos, posibilitando aplicar o remover
     * porcentajes, ofertas de precio fijo o "2x1" sobre productos y categorías enteras.
     */
    private void pintarDescuentos() {
        contenido.add(crearTitulo("Descuentos"));
        contenido.add(crearSubtitulo("Configura rebajas por producto o por categoría."));
        contenido.add(crearPanelDescuentoProducto());
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(crearPanelDescuentoCategoria());
        contenido.add(crearTitulo("Promociónes activas"));
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            if (tieneDescuento(producto)) {
                contenido.add(crearEtiqueta(producto.getNombre() + ": " + textoDescuento(producto)));
            }
        }
    }

    /**
     * Crea un formulario encapsulado para asignar un tipo de descuento a un solo
     * producto de manera específica.
     * 
     * @return JPanel estructurado como una tarjeta que envuelve el formulario.
     */
    private JPanel crearPanelDescuentoProducto() {
        JPanel panel = crearTarjeta();
        panel.add(crearEtiqueta("<b>Aplicar a producto</b>"));
        JComboBox<ProductoTienda> productos = new JComboBox<>(
                mainFrame.getProductosTienda().toArray(new ProductoTienda[0]));
        productos.setRenderer((list, value, index, selected, focus) ->
                new JLabel(value == null ? "" : value.getNombre()));
        JComboBox<String> tipo = new JComboBox<>(new String[] {"Porcentaje", "Rebaja fija", "2x1"});
        JSpinner porcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner fija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, 1.0));
        tipo.addActionListener(e -> actualizarCamposDescuento(tipo, porcentaje, fija));
        productos.addActionListener(e -> seleccionarTipoDescuentoActual(
                (ProductoTienda) productos.getSelectedItem(), tipo, porcentaje, fija));
        panel.add(productos);
        panel.add(crearPanelSeleccionDescuento(tipo, porcentaje, fija));
        seleccionarTipoDescuentoActual((ProductoTienda) productos.getSelectedItem(), tipo, porcentaje, fija);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        JButton aplicar = crearBoton("Aplicar", 110);
        aplicar.addActionListener(e -> mainFrame.aplicarDescuentoProducto(
                (ProductoTienda) productos.getSelectedItem(),
                tipoDescuentoSeleccionado(tipo),
                valorDescuentoSeleccionado(tipo, porcentaje, fija)));
        acciones.add(aplicar);
        JButton limpiar = crearBoton("Quitar", 100);
        limpiar.addActionListener(e -> mainFrame.limpiarDescuentoProducto(
                (ProductoTienda) productos.getSelectedItem()));
        acciones.add(limpiar);
        panel.add(acciones);
        return panel;
    }

    /**
     * Crea un formulario encapsulado para aplicar de manera masiva un descuento
     * a todos los productos que concuerden con un texto de categoría.
     * 
     * @return JPanel estructurado para la edición del descuento masivo.
     */
    private JPanel crearPanelDescuentoCategoria() {
        JPanel panel = crearTarjeta();
        panel.add(crearEtiqueta("<b>Aplicar a categoría</b>"));
        JTextField categoria = new JTextField();
        categoria.setMaximumSize(new Dimension(360, 30));
        JComboBox<String> tipo = new JComboBox<>(new String[] {"Porcentaje", "Rebaja fija", "2x1"});
        JSpinner porcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner fija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, 1.0));
        tipo.addActionListener(e -> actualizarCamposDescuento(tipo, porcentaje, fija));
        panel.add(crearEtiqueta("Categoría o texto de categoría"));
        panel.add(categoria);
        panel.add(crearPanelSeleccionDescuento(tipo, porcentaje, fija));
        actualizarCamposDescuento(tipo, porcentaje, fija);
        JButton aplicar = crearBoton("Aplicar a categoría", 180);
        aplicar.addActionListener(e -> {
            int total = mainFrame.aplicarDescuentoCategoria(categoria.getText(),
                    tipoDescuentoSeleccionado(tipo),
                    valorDescuentoSeleccionado(tipo, porcentaje, fija));
            JOptionPane.showMessageDialog(this, "Productos actualizados: " + total);
        });
        panel.add(aplicar);
        return panel;
    }

    /**
     * Genera la fila de la interfaz gráfica donde conviven el combo del tipo
     * de promoción y sus campos numéricos pertinentes (porcentaje o valor fijo).
     * 
     * @param tipo Selector del tipo de promoción.
     * @param porcentaje Control numérico para promociones porcentuales.
     * @param fija Control numérico para promociones de valor absoluto.
     * @return JPanel configurado horizontalmente.
     */
    private JPanel crearPanelSeleccionDescuento(JComboBox<String> tipo, JSpinner porcentaje, JSpinner fija) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linea.setOpaque(false);
        linea.add(new JLabel("Tipo"));
        linea.add(tipo);
        linea.add(new JLabel("Porcentaje"));
        linea.add(porcentaje);
        linea.add(new JLabel("Rebaja fija"));
        linea.add(fija);
        return linea;
    }

    /**
     * Habilita o deshabilita la escritura en los campos numéricos de descuento
     * dependiendo de qué opción se haya elegido en el comboBox principal.
     * 
     * @param tipo Selector en el que se basa la decisión ("Porcentaje", "Rebaja fija", etc.).
     * @param porcentaje El componente que representa un porcentaje.
     * @param fija El componente que representa una rebaja fija en divisa.
     */
    private void actualizarCamposDescuento(JComboBox<String> tipo, JSpinner porcentaje, JSpinner fija) {
        String seleccion = (String) tipo.getSelectedItem();
        porcentaje.setEnabled("Porcentaje".equals(seleccion));
        fija.setEnabled("Rebaja fija".equals(seleccion));
    }

    /**
     * Sincroniza los controles del formulario de descuentos con el estado
     * real actual de un producto, mostrando el descuento que actualmente posee.
     * 
     * @param producto Producto objetivo cuyas promociones se examinarán.
     * @param tipo El comboBox que se debe ajustar al tipo de oferta existente.
     * @param porcentaje Control que alojará el porcentaje (si aplica).
     * @param fija Control que alojará la cantidad fija (si aplica).
     */
    private void seleccionarTipoDescuentoActual(ProductoTienda producto,
            JComboBox<String> tipo, JSpinner porcentaje, JSpinner fija) {
        String activo = mainFrame.getTipoDescuentoProducto(producto);
        if ("PORCENTAJE".equals(activo)) {
            tipo.setSelectedItem("Porcentaje");
            porcentaje.setValue(producto.getRebajaPorcentaje());
        } else if ("FIJO".equals(activo)) {
            tipo.setSelectedItem("Rebaja fija");
            fija.setValue(producto.getRebajaFija());
        } else if ("DOS_POR_UNO".equals(activo)) {
            tipo.setSelectedItem("2x1");
        } else {
            tipo.setSelectedItem("Porcentaje");
            porcentaje.setValue(0.0);
            fija.setValue(0.0);
        }
        actualizarCamposDescuento(tipo, porcentaje, fija);
    }

    /**
     * Mapea un String visible en la interfaz para el usuario, a la constante de sistema
     * representativa del tipo de descuento (por ejemplo, "Rebaja fija" -> "FIJO").
     * 
     * @param tipo El JComboBox de donde extraer el valor.
     * @return Constante string reconocida por el backend como tipo de descuento.
     */
    private String tipoDescuentoSeleccionado(JComboBox<String> tipo) {
        String seleccion = (String) tipo.getSelectedItem();
        if ("Rebaja fija".equals(seleccion)) {
            return "FIJO";
        }
        if ("2x1".equals(seleccion)) {
            return "DOS_POR_UNO";
        }
        return "PORCENTAJE";
    }

    /**
     * Extrae de los JSpinners el valor real numérico que se debe aplicar
     * según el tipo de oferta marcada en el combo principal.
     * 
     * @param tipo Selector de tipo para saber qué spinner inspeccionar.
     * @param porcentaje Control JSpinner correspondiente a porcentajes.
     * @param fija Control JSpinner correspondiente a valores absolutos fijos.
     * @return El número extraído. 0.0 si es "2x1".
     */
    private double valorDescuentoSeleccionado(JComboBox<String> tipo, JSpinner porcentaje, JSpinner fija) {
        String seleccion = (String) tipo.getSelectedItem();
        if ("Rebaja fija".equals(seleccion)) {
            return ((Double) fija.getValue()).doubleValue();
        }
        if ("2x1".equals(seleccion)) {
            return 0.0;
        }
        return ((Double) porcentaje.getValue()).doubleValue();
    }

    /**
     * Pinta la vista donde el gestor puede ver los productos subidos por usuarios
     * y proporcionar una valoración monetaria inicial, así como constatar el estado.
     */
    private void pintarSegundaMano() {
        contenido.add(crearTitulo("Productos de segunda mano"));
        contenido.add(crearSubtitulo("Valora los productos subidos por clientes: precio estimado y estado de conservación."));
        List<ProductoSegundaMano> productos = mainFrame.getProductosSegundaManoGestion();
        if (productos.isEmpty()) {
            contenido.add(crearEtiqueta("No hay productos de segunda mano."));
            return;
        }
        for (ProductoSegundaMano producto : productos) {
            contenido.add(crearFilaProductoSegundaMano(producto));
            contenido.add(Box.createVerticalStrut(18));
        }
    }

    /**
     * Compone la fila que muestra un producto de segunda mano al gestor,
     * habilitando un botón para valorarlo en caso de que aún no lo esté.
     * 
     * @param producto Producto a mostrar en la fila.
     * @return Componente de interfaz de la fila.
     */
    private JPanel crearFilaProductoSegundaMano(ProductoSegundaMano producto) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(22, 16, 22, 16));
        fila.setPreferredSize(new Dimension(0, 86));
        fila.setMinimumSize(new Dimension(0, 86));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        fila.add(crearEtiqueta(textoProductoSegundaMano(producto)), BorderLayout.CENTER);
        JButton valorar = crearBoton(producto.getEstaValorado() ? "Valorado" : "Valorar", 125);
        valorar.setEnabled(!producto.getEstaValorado());
        valorar.addActionListener(e -> valorarProductoSegundaMano(producto));
        fila.add(valorar, BorderLayout.EAST);
        return fila;
    }

    /**
     * Pinta la pantalla de administración de packs de productos. Lista los
     * agrupamientos de la tienda creados por el gestor.
     */
    private void pintarPacks() {
        contenido.add(crearTitulo("Packs de productos"));
        contenido.add(crearSubtitulo("Crea y modifica paquetes configurados por el gestor."));
        JButton nuevo = crearBoton("Nuevo pack", 140);
        nuevo.addActionListener(e -> editarPack(null));
        contenido.add(nuevo);
        contenido.add(Box.createVerticalStrut(14));
        for (Pack pack : mainFrame.getPacks()) {
            JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
            fila.setLayout(new BorderLayout(12, 0));
            fila.setBorder(new EmptyBorder(22, 16, 22, 16));
            fila.setPreferredSize(new Dimension(0, 86));
            fila.setMinimumSize(new Dimension(0, 86));
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
            fila.add(crearEtiqueta("<b>" + pack.getNombre() + "</b><br>"
                    + "Categoría: " + categoriaPack(pack) + "<br>"
                    + resumenPack(pack) + "<br>" + String.format("%.2f EUR", pack.getPrecio())),
                    BorderLayout.CENTER);
            JButton editar = crearBoton("Modificar", 125);
            editar.addActionListener(e -> editarPack(pack));
            fila.add(editar, BorderLayout.EAST);
            contenido.add(fila);
            contenido.add(Box.createVerticalStrut(18));
        }
    }

    /**
     * Pinta la sección general operativa del gestor. Permite ver todos los
     * pedidos, cambiar el tiempo límite que tienen las ofertas de intercambio
     * y modificar los estados del flujo de la venta.
     */
    private void pintarOperativa() {
        contenido.add(crearTitulo("Pedidos e intercambios"));
        contenido.add(crearSubtitulo("Supervision de pedidos preparados, entregas e intercambios."));
        contenido.add(crearPanelPlazoOfertas());
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(crearTitulo("Pedidos"));
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            contenido.add(crearFilaPedido(pedido));
            contenido.add(Box.createVerticalStrut(18));
        }
        contenido.add(crearTitulo("Intercambios"));
        for (Intercambio intercambio : mainFrame.getIntercambios()) {
            contenido.add(crearFilaIntercambio(intercambio));
            contenido.add(Box.createVerticalStrut(18));
        }
    }

    /**
     * Elabora el panel responsable de gestionar la variable global "PlazoOfertasHoras",
     * que define el tiempo límite antes de que un intercambio expire sin acción.
     * 
     * @return JPanel con el formulario para configurar el plazo.
     */
    private JPanel crearPanelPlazoOfertas() {
        JPanel panel = crearTarjeta();
        panel.add(crearEtiqueta("<b>Plazo de ofertas</b>"));
        panel.add(crearSubtitulo("Tiempo maximo para aceptar una oferta antes de que caduque."));
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        linea.setOpaque(false);
        JSpinner horas = new JSpinner(new SpinnerNumberModel(mainFrame.getPlazoOfertasHoras(), 1, 720, 1));
        linea.add(new JLabel("Horas"));
        linea.add(horas);
        JButton guardar = crearBoton("Guardar plazo", 140);
        guardar.addActionListener(e -> {
            mainFrame.setPlazoOfertasHoras(((Integer) horas.getValue()).intValue());
            JOptionPane.showMessageDialog(this, "Plazo actualizado para nuevas ofertas.");
        });
        linea.add(guardar);
        panel.add(linea);
        return panel;
    }

    /**
     * Crea un elemento de lista en la vista de gestor para interactuar con un pedido
     * particular, ver su estado y avanzarlo si procede.
     * 
     * @param pedido El pedido a reflejar gráficamente.
     * @return JPanel correspondiente al pedido.
     */
    private JPanel crearFilaPedido(Pedido pedido) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(22, 16, 22, 16));
        fila.setPreferredSize(new Dimension(0, 86));
        fila.setMinimumSize(new Dimension(0, 86));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        fila.add(crearEtiqueta(resumenPedido(pedido)), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton info = crearBoton("Info", 80);
        info.addActionListener(e -> mostrarInfoPedido(pedido));
        acciones.add(info);
        JButton avanzar = crearBoton("Avanzar", 105);
        avanzar.setEnabled(pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION
                || pedido.getEstadoPedido() == EstadoPedido.LISTO);
        avanzar.addActionListener(e -> avanzarPedido(pedido));
        acciones.add(avanzar);
        fila.add(acciones, BorderLayout.EAST);
        return fila;
    }

    /**
     * Crea un componente en forma de barra para reflejar un proceso de intercambio,
     * permitiendo validarlo como completado (Realizado) si fue aceptado por los clientes.
     * 
     * @param intercambio El intercambio a mostrar en la interfaz.
     * @return JPanel correspondiente al intercambio de productos.
     */
    private JPanel crearFilaIntercambio(Intercambio intercambio) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(22, 16, 22, 16));
        fila.setPreferredSize(new Dimension(0, 86));
        fila.setMinimumSize(new Dimension(0, 86));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        String texto = intercambio.getOferta().getProductoOfertado().getNombre()
                + " por " + intercambio.getOferta().getProductoDeseado().getNombre()
                + " | " + intercambio.getOferta().getEstadoOferta();
        fila.add(crearEtiqueta(texto), BorderLayout.CENTER);
        JButton realizado = crearBoton("Realizado", 120);
        realizado.setEnabled(!intercambio.getIntercambiado()
                && intercambio.getOferta().getEstadoOferta() == EstadoOferta.ACEPTADA);
        realizado.addActionListener(e -> mainFrame.marcarIntercambioRealizado(intercambio));
        fila.add(realizado, BorderLayout.EAST);
        return fila;
    }

    /**
     * Pinta el menú de análisis y estadísticas. Carga componentes gráficos
     * elaborados como barras de rendimiento y resúmenes monetarios para medir la salud de la tienda.
     */
    private void pintarEstadisticas() {
        contenido.add(crearTitulo("Estadísticas"));
        JPanel grid = new JPanel(new GridLayout(0, 4, 14, 14));
        grid.setOpaque(false);
        grid.add(crearMetrica("Ventas", String.format("%.2f", totalVentasEntregadas()), "EUR entregados"));
        grid.add(crearMetrica("Pedidos", String.valueOf(contarPedidos(EstadoPedido.ENTREGADO)), "entregados"));
        grid.add(crearMetrica("Valoraciones", String.format("%.2f", totalValoraciones()), "EUR estimados"));
        grid.add(crearMetrica("Clientes", String.valueOf(mainFrame.getClientesRegistrados().size()), "registrados"));
        contenido.add(grid);

        contenido.add(crearTitulo("Ventas por mes"));
        contenido.add(crearSubtitulo("Importe de pedidos entregados durante los últimos 12 meses."));
        contenido.add(new GraficaVentasMensuales(ventasEntregadasPorMes()));

        contenido.add(crearTitulo("Pedidos por estado"));
        contenido.add(crearPanelPedidosPorEstado());

        contenido.add(crearTitulo("Usuarios con más compras"));
        List<ClienteRegistrado> clientes = new ArrayList<>(mainFrame.getClientesRegistrados());
        clientes.sort(Comparator.comparingInt((ClienteRegistrado c) -> c.getPedidos().size()).reversed());
        int maxCompras = 1;
        for (ClienteRegistrado cliente : clientes) {
            maxCompras = Math.max(maxCompras, cliente.getPedidos().size());
        }
        for (ClienteRegistrado cliente : clientes) {
            contenido.add(crearBarraCliente(cliente, maxCompras));
            contenido.add(Box.createVerticalStrut(8));
        }
    }

    /**
     * Muestra una ventana de diálogo (Modal) que contiene un formulario para crear
     * un nuevo empleado en el sistema o alterar los permisos y contraseña de uno ya existente.
     * 
     * @param empleado Referencia al empleado a editar, si es `null` se interpreta como "nuevo empleado".
     */
    private void mostrarDialogoEmpleado(Empleado empleado) {
        JTextField nombre = new JTextField(empleado == null ? "" : empleado.getNombre());
        nombre.setEnabled(empleado == null);
        JPasswordField contrasena = new JPasswordField(empleado == null ? "" : empleado.getContrase\u00f1a());
        JCheckBox producto = new JCheckBox("Productos, stock, categorías y packs");
        JCheckBox pedido = new JCheckBox("Pedidos");
        JCheckBox intercambio = new JCheckBox("Intercambios y valoraciones");
        if (empleado != null) {
            producto.setSelected(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO));
            pedido.setSelected(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PEDIDO));
            intercambio.setSelected(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO));
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Nombre"));
        panel.add(nombre);
        panel.add(new JLabel(empleado == null ? "Contraseña asignada por gestor" : "Nueva clave de inicio de sesión"));
        panel.add(contrasena);
        panel.add(producto);
        panel.add(pedido);
        panel.add(intercambio);

        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                empleado == null ? "Nuevo empleado" : "Empleado",
                JOptionPane.OK_CANCEL_OPTION);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        Set<TiposEmpleado> permisos = new HashSet<>();
        if (producto.isSelected()) {
            permisos.add(TiposEmpleado.EMPLEADOS_PRODUCTO);
        }
        if (pedido.isSelected()) {
            permisos.add(TiposEmpleado.EMPLEADOS_PEDIDO);
        }
        if (intercambio.isSelected()) {
            permisos.add(TiposEmpleado.EMPLEADOS_INTERCAMBIO);
        }

        if (empleado == null) {
            mainFrame.crearEmpleadoDesdeGestor(nombre.getText(), new String(contrasena.getPassword()), permisos);
        } else {
            String nuevaClave = new String(contrasena.getPassword()).trim();
            if (nuevaClave.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "La clave de inicio de sesión no puede estar vacía.",
                        "Empleado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            empleado.setContrase\u00f1a(nuevaClave);
            mainFrame.configurarPermisosEmpleado(empleado, permisos);
        }
        refrescar();
    }

    /**
     * Construye una cadena breve con el resumen sobre las ventas vinculadas
     * o procesadas por un empleado concreto.
     * 
     * @param empleado Empleado a consultar.
     * @return Cadena que muestra el conteo de ventas y el total económico ingresado.
     */
    private String resumenVentasEmpleado(Empleado empleado) {
        List<Pedido> ventas = mainFrame.getVentasEmpleado(empleado);
        return "Ventas realizadas: " + ventas.size()
                + " | " + String.format("%.2f EUR", mainFrame.getTotalVentasEmpleado(empleado));
    }

    /**
     * Dispara un cuadro de diálogo detallado que desglosa en formato texto
     * todos los pedidos específicos que han sido manejados o asignados al empleado indicado.
     * 
     * @param empleado Empleado para analizar sus registros de venta.
     */
    private void mostrarVentasEmpleado(Empleado empleado) {
        List<Pedido> ventas = mainFrame.getVentasEmpleado(empleado);
        JTextArea detalle = new JTextArea(12, 46);
        detalle.setEditable(false);
        detalle.setLineWrap(true);
        detalle.setWrapStyleWord(true);
        if (ventas.isEmpty()) {
            detalle.setText("Este empleado todavía no tiene ventas entregadas registradas.");
        } else {
            StringBuilder texto = new StringBuilder();
            for (Pedido pedido : ventas) {
                texto.append("Pedido ").append(pedido.getCodigo().getCodigo())
                        .append(" | Cliente: ").append(pedido.getCliente().getNombre())
                        .append(" | Fecha: ").append(formatoFecha.format(fechaEstadisticaPedido(pedido)))
                        .append(" | Total: ").append(String.format("%.2f EUR", pedido.calcularPrecioTotal()))
                        .append('\n')
                        .append(resumenProductosPedido(pedido))
                        .append("\n\n");
            }
            detalle.setText(texto.toString());
            detalle.setCaretPosition(0);
        }
        JOptionPane.showMessageDialog(this, new JScrollPane(detalle),
                "Ventas de " + empleado.getNombre(), JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Advierte al usuario gestor e inicia el proceso para la eliminación permanente
     * de un perfil de empleado del sistema de la tienda.
     * 
     * @param empleado Instancia del empleado a eliminar.
     */
    private void confirmarBajaEmpleado(Empleado empleado) {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Dar de baja a " + empleado.getNombre() + "?",
                "Baja empleado", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            mainFrame.eliminarEmpleadoDesdeGestor(empleado);
            refrescar();
        }
    }

    /**
     * Despliega un explorador de archivos nativo que permite elegir un fichero .csv
     * para re-popular el inventario de la aplicación en bloque.
     */
    private void cargarProductosDeFichero() {
        JFileChooser chooser = new JFileChooser(".");
        int respuesta = chooser.showOpenDialog(this);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            mainFrame.recargarCatalogoDesdeFichero(chooser.getSelectedFile().getPath());
            refrescar();
        }
    }

    /**
     * Construye un extenso formulario compuesto de pestañas para crear manualmente un producto
     * categorizado como "Cómic", "Juego de Mesa" o "Figura", integrando toda su información en el sistema.
     */
    private void mostrarDialogoNuevoProducto() {
        JTextField nombre = new JTextField();
        JTextField precio = new JTextField("0.00");
        JSpinner stock = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        JSpinner valoracion = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        JTextField imagen = new JTextField();
        imagen.setEditable(false);
        JTextField categorias = new JTextField();
        JTextArea descripcion = new JTextArea(7, 28);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);

        JLabel preview = new JLabel("Sin imagen", SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(180, 190));
        preview.setOpaque(true);
        preview.setBackground(UiStyle.COLOR_TARJETA);
        preview.setForeground(UiStyle.COLOR_TEXTO);
        preview.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 1));
        JButton buscarImagen = crearBoton("Buscar imagen", 140);
        buscarImagen.addActionListener(e -> seleccionarImagenProducto(imagen, preview));

        JSpinner comicPaginas = new JSpinner(new SpinnerNumberModel(120, 1, 3000, 1));
        JTextField comicAutor = new JTextField();
        JTextField comicEditorial = new JTextField();
        JComboBox<Genero> comicGenero = new JComboBox<>(Genero.values());
        JSpinner comicAnio = new JSpinner(new SpinnerNumberModel(2026, 1900, 2100, 1));
        JTextField comicIdioma = new JTextField("Español");
        JTextField comicFormato = new JTextField("Tapa blanda");
        JTextField comicIsbn = new JTextField();

        JSpinner juegoJugadoresMin = new JSpinner(new SpinnerNumberModel(2, 1, 99, 1));
        JSpinner juegoJugadoresMax = new JSpinner(new SpinnerNumberModel(4, 1, 99, 1));
        JSpinner juegoEdad = new JSpinner(new SpinnerNumberModel(8, 0, 99, 1));
        JComboBox<TipoJuego> tipoJuego = new JComboBox<>(TipoJuego.values());
        JSpinner juegoDuración = new JSpinner(new SpinnerNumberModel(30, 1, 999, 5));
        JTextField juegoEditorial = new JTextField();
        JTextField juegoIdioma = new JTextField("Español");
        JTextArea juegoComponentes = new JTextArea(3, 20);
        juegoComponentes.setLineWrap(true);
        juegoComponentes.setWrapStyleWord(true);

        JSpinner figuraAltura = new JSpinner(new SpinnerNumberModel(10.0, 0.0, 999.0, 0.5));
        JTextField figuraMarca = new JTextField();
        JTextField figuraMaterial = new JTextField();
        JTextField figuraEscala = new JTextField("1/10");
        JTextField figuraPersonaje = new JTextField();
        JTextField figuraFranquicia = new JTextField();
        JCheckBox figuraArticulada = new JCheckBox("Figura articulada");

        JTabbedPane detalles = new JTabbedPane();
        detalles.addTab("Cómic", crearPanelComic(comicPaginas, comicAutor, comicEditorial,
                comicGenero, comicAnio, comicIdioma, comicFormato, comicIsbn));
        detalles.addTab("Juego", crearPanelJuego(juegoJugadoresMin, juegoJugadoresMax,
                juegoEdad, tipoJuego, juegoDuración, juegoEditorial, juegoIdioma, juegoComponentes));
        detalles.addTab("Figura", crearPanelFigura(figuraAltura, figuraMarca, figuraMaterial,
                figuraEscala, figuraPersonaje, figuraFranquicia, figuraArticulada));

        JComboBox<String> promocion = new JComboBox<>(new String[] {
                "Sin promoción", "2x1", "Rebaja porcentaje", "Rebaja fija"
        });
        JSpinner rebajaPorcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner rebajaFija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 9999.0, 1.0));

        JPanel basicos = crearPanelFormulario();
        basicos.add(crearTituloFormulario("Datos básicos"));
        basicos.add(crearFilaFormulario("Nombre", nombre));
        basicos.add(crearFilaFormulario("Precio", precio));
        basicos.add(crearFilaFormulario("Stock inicial", stock));
        basicos.add(crearFilaFormulario("Valoración inicial", valoracion));
        basicos.add(crearFilaFormulario("Categorías", categorias));
        basicos.add(crearFilaFormulario("Promoción", promocion));
        basicos.add(crearFilaFormulario("Porcentaje", rebajaPorcentaje));
        basicos.add(crearFilaFormulario("Rebaja fija", rebajaFija));
        basicos.add(crearTituloFormulario("Descripción"));
        basicos.add(new JScrollPane(descripcion));

        JPanel imagenPanel = crearPanelFormulario();
        imagenPanel.add(crearTituloFormulario("Imagen"));
        imagenPanel.add(preview);
        imagenPanel.add(Box.createVerticalStrut(8));
        imagenPanel.add(buscarImagen);
        imagenPanel.add(Box.createVerticalStrut(8));
        imagenPanel.add(crearFilaFormulario("Archivo", imagen));

        JPanel izquierda = new JPanel(new BorderLayout(0, 12));
        izquierda.setOpaque(false);
        izquierda.add(basicos, BorderLayout.CENTER);
        izquierda.add(imagenPanel, BorderLayout.EAST);

        JPanel panel = new JPanel(new BorderLayout(18, 0));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.setPreferredSize(new Dimension(860, 560));
        panel.add(izquierda, BorderLayout.CENTER);
        panel.add(detalles, BorderLayout.EAST);

        int respuesta = JOptionPane.showConfirmDialog(this, panel, "Nuevo producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        if (nombre.getText().trim().isBlank()) {
            JOptionPane.showMessageDialog(this, "El producto necesita nombre.",
                    "Producto", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String promo = (String) promocion.getSelectedItem();
        String tipoSeleccionado = detalles.getTitleAt(detalles.getSelectedIndex()).toUpperCase();
        String descripcionCompleta = descripcionConDetalles(tipoSeleccionado, descripcion.getText(),
                comicPaginas, comicAutor, comicEditorial, comicGenero, comicAnio, comicIdioma, comicFormato, comicIsbn,
                juegoJugadoresMin, juegoJugadoresMax, juegoEdad, tipoJuego, juegoDuración,
                juegoEditorial, juegoIdioma, juegoComponentes,
                figuraAltura, figuraMarca, figuraMaterial, figuraEscala, figuraPersonaje,
                figuraFranquicia, figuraArticulada);
        mainFrame.crearProductoTiendaGestion(tipoSeleccionado,
                nombre.getText(),
                parseDouble(precio.getText(), 0.0),
                ((Integer) stock.getValue()).intValue(),
                ((Integer) valoracion.getValue()).intValue(),
                descripcionCompleta,
                imagen.getText(),
                parseCategorías(categorias.getText()),
                "2x1".equals(promo),
                "Rebaja porcentaje".equals(promo) ? ((Double) rebajaPorcentaje.getValue()).doubleValue() : 0.0,
                "Rebaja fija".equals(promo) ? ((Double) rebajaFija.getValue()).doubleValue() : 0.0,
                ((Integer) comicPaginas.getValue()).intValue(),
                comicAutor.getText(),
                comicEditorial.getText(),
                (Genero) comicGenero.getSelectedItem(),
                ((Integer) comicAnio.getValue()).intValue(),
                ((Integer) juegoJugadoresMax.getValue()).intValue(),
                ((Integer) juegoEdad.getValue()).intValue(),
                (TipoJuego) tipoJuego.getSelectedItem(),
                ((Double) figuraAltura.getValue()).doubleValue(),
                figuraMarca.getText(),
                figuraMaterial.getText());
        refrescar();
    }

    /**
     * Construye un subformulario específico para rellenar campos característicos de cómics.
     * 
     * @param paginas Campo de páginas del cómic.
     * @param autor Campo de autor del cómic.
     * @param editorial Campo de la editorial publicadora.
     * @param genero Combobox que aloja el género de la historia.
     * @param anio Año de lanzamiento.
     * @param idioma Idioma de edición.
     * @param formato Formato de publicación (tapa blanda, dura, etc.).
     * @param isbn Identificador numérico ISBN.
     * @return JPanel organizado para su introducción en las pestañas (Tabs).
     */
    private JPanel crearPanelComic(JSpinner paginas, JTextField autor, JTextField editorial,
            JComboBox<Genero> genero, JSpinner anio, JTextField idioma,
            JTextField formato, JTextField isbn) {
        JPanel panel = crearPanelFormulario();
        panel.setPreferredSize(new Dimension(330, 0));
        panel.add(crearTituloFormulario("Detalles de cómic"));
        panel.add(crearFilaFormulario("Páginas", paginas));
        panel.add(crearFilaFormulario("Autor", autor));
        panel.add(crearFilaFormulario("Editorial", editorial));
        panel.add(crearFilaFormulario("Género", genero));
        panel.add(crearFilaFormulario("Año", anio));
        panel.add(crearFilaFormulario("Idioma", idioma));
        panel.add(crearFilaFormulario("Formato", formato));
        panel.add(crearFilaFormulario("ISBN", isbn));
        return panel;
    }

    /**
     * Construye un subformulario específico para rellenar campos característicos de juegos de mesa.
     * 
     * @param jugadoresMin Mínimo de jugadores simultáneos.
     * @param jugadoresMax Máximo de jugadores.
     * @param edad Edad sugerida para el juego.
     * @param tipoJuego Tipo de mecánicas o categoría general del juego.
     * @param duracion Tiempo aproximado por partida en minutos.
     * @param editorial Empresa distribuidora.
     * @param idioma Lenguaje principal en el que se encuentra.
     * @param componentes Descripción de los elementos que incluye la caja.
     * @return JPanel organizado para las pestañas de creación.
     */
    private JPanel crearPanelJuego(JSpinner jugadoresMin, JSpinner jugadoresMax,
            JSpinner edad, JComboBox<TipoJuego> tipoJuego, JSpinner duracion,
            JTextField editorial, JTextField idioma, JTextArea componentes) {
        JPanel panel = crearPanelFormulario();
        panel.setPreferredSize(new Dimension(330, 0));
        componentes.setLineWrap(true);
        componentes.setWrapStyleWord(true);
        panel.add(crearTituloFormulario("Detalles de juego"));
        panel.add(crearFilaFormulario("Jugadores min.", jugadoresMin));
        panel.add(crearFilaFormulario("Jugadores max.", jugadoresMax));
        panel.add(crearFilaFormulario("Edad mínima", edad));
        panel.add(crearFilaFormulario("Tipo", tipoJuego));
        panel.add(crearFilaFormulario("Duración min.", duracion));
        panel.add(crearFilaFormulario("Editorial", editorial));
        panel.add(crearFilaFormulario("Idioma", idioma));
        panel.add(crearTituloFormulario("Componentes"));
        panel.add(new JScrollPane(componentes));
        return panel;
    }

    /**
     * Construye un subformulario específico para rellenar campos característicos de figuras de coleccionismo.
     * 
     * @param altura Control del alto del objeto en centímetros.
     * @param marca Fabricante original de la figura.
     * @param material Tipo de plástico, resina, etc.
     * @param escala Proporción respecto al sujeto original (1/6, 1/12, etc.).
     * @param personaje Nombre del personaje emulado.
     * @param franquicia Universo o licencia al que pertenece el personaje.
     * @param articulada Check para validar si dispone de uniones móviles.
     * @return JPanel organizado adecuadamente.
     */
    private JPanel crearPanelFigura(JSpinner altura, JTextField marca, JTextField material,
            JTextField escala, JTextField personaje, JTextField franquicia, JCheckBox articulada) {
        JPanel panel = crearPanelFormulario();
        panel.setPreferredSize(new Dimension(330, 0));
        panel.add(crearTituloFormulario("Detalles de figura"));
        panel.add(crearFilaFormulario("Altura cm", altura));
        panel.add(crearFilaFormulario("Marca", marca));
        panel.add(crearFilaFormulario("Material", material));
        panel.add(crearFilaFormulario("Escala", escala));
        panel.add(crearFilaFormulario("Personaje", personaje));
        panel.add(crearFilaFormulario("Franquicia", franquicia));
        panel.add(articulada);
        return panel;
    }

    /**
     * Devuelve una plantilla de JPanel básica, pre-configurada verticalmente, para alojar un formulario.
     * 
     * @return Panel listo para hospedar entradas de texto y controles.
     */
    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    /**
     * Crea un título tipográfico orientado para dividir los grupos de componentes
     * dentro de una ventana de inserción de datos grande.
     * 
     * @param texto La cadena visible del título divisorio.
     * @return JLabel formateado en negrita y alineado a la izquierda.
     */
    private JLabel crearTituloFormulario(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(8, 0, 8, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Acomoda de manera uniforme un componente de formulario con su nombre o etiqueta asociada.
     * 
     * @param etiqueta El título descriptivo del campo para los usuarios.
     * @param campo El componente interactivo (input, spinner, combo).
     * @return JPanel envuelto que dispone la etiqueta a la izquierda y el input a la derecha.
     */
    private JPanel crearFilaFormulario(String etiqueta, Component campo) {
        JPanel fila = new JPanel(new BorderLayout(8, 0));
        fila.setBorder(new EmptyBorder(4, 0, 4, 0));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel label = new JLabel(etiqueta);
        label.setPreferredSize(new Dimension(104, 26));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fila.add(label, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        return fila;
    }

    /**
     * Abre un JFileChooser filtrado sólo para extensiones de imagen habituales.
     * Si el usuario selecciona un fichero válido, actualiza el String de la ruta
     * y repinta el objeto visual de 'preview' con su contenido escalado.
     * 
     * @param campoImagen El JTextField donde se inyectará la ruta del fichero.
     * @param preview Etiqueta visual donde volcar miniatura del archivo cargado.
     */
    private void seleccionarImagenProducto(JTextField campoImagen, JLabel preview) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "png", "jpg", "jpeg", "gif"));
        int respuesta = chooser.showOpenDialog(this);
        if (respuesta != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String ruta = chooser.getSelectedFile().getPath();
        campoImagen.setText(ruta);
        ImageIcon icono = new ImageIcon(ruta);
        Image escalada = icono.getImage().getScaledInstance(180, 190, Image.SCALE_SMOOTH);
        preview.setText("");
        preview.setIcon(new ImageIcon(escalada));
    }

    /**
     * Extrae y compila las cadenas de múltiples componentes de texto en un
     * párrafo enorme amalgamado. Utilizado para convertir en string las métricas detalladas del producto.
     * 
     * @param tipo El tipo base sobre el que discriminar qué tab está activo (CÓMIC, JUEGO, FIGURA).
     * @param descripcion La descripción base del objeto.
     * @param comicPaginas Campo referenciado de cómic.
     * @param comicAutor Campo referenciado de cómic.
     * @param comicEditorial Campo referenciado de cómic.
     * @param comicGenero Campo referenciado de cómic.
     * @param comicAnio Campo referenciado de cómic.
     * @param comicIdioma Campo referenciado de cómic.
     * @param comicFormato Campo referenciado de cómic.
     * @param comicIsbn Campo referenciado de cómic.
     * @param juegoJugadoresMin Campo referenciado de juego.
     * @param juegoJugadoresMax Campo referenciado de juego.
     * @param juegoEdad Campo referenciado de juego.
     * @param tipoJuego Campo referenciado de juego.
     * @param juegoDuración Campo referenciado de juego.
     * @param juegoEditorial Campo referenciado de juego.
     * @param juegoIdioma Campo referenciado de juego.
     * @param juegoComponentes Campo referenciado de juego.
     * @param figuraAltura Campo referenciado de figura.
     * @param figuraMarca Campo referenciado de figura.
     * @param figuraMaterial Campo referenciado de figura.
     * @param figuraEscala Campo referenciado de figura.
     * @param figuraPersonaje Campo referenciado de figura.
     * @param figuraFranquicia Campo referenciado de figura.
     * @param figuraArticulada Campo referenciado de figura.
     * @return Una representación en texto extendido que funde la descripción con atributos concretos.
     */
    private String descripcionConDetalles(String tipo, String descripcion,
            JSpinner comicPaginas, JTextField comicAutor, JTextField comicEditorial,
            JComboBox<Genero> comicGenero, JSpinner comicAnio, JTextField comicIdioma,
            JTextField comicFormato, JTextField comicIsbn,
            JSpinner juegoJugadoresMin, JSpinner juegoJugadoresMax, JSpinner juegoEdad,
            JComboBox<TipoJuego> tipoJuego, JSpinner juegoDuración, JTextField juegoEditorial,
            JTextField juegoIdioma, JTextArea juegoComponentes,
            JSpinner figuraAltura, JTextField figuraMarca, JTextField figuraMaterial,
            JTextField figuraEscala, JTextField figuraPersonaje, JTextField figuraFranquicia,
            JCheckBox figuraArticulada) {
        StringBuilder texto = new StringBuilder(descripcion == null ? "" : descripcion.trim());
        if (texto.length() > 0) {
            texto.append("\n\n");
        }
        texto.append("Detalles del producto:\n");
        if ("COMIC".equals(tipo)) {
            texto.append("Páginas: ").append(comicPaginas.getValue()).append('\n');
            texto.append("Autor: ").append(textoCampo(comicAutor)).append('\n');
            texto.append("Editorial: ").append(textoCampo(comicEditorial)).append('\n');
            texto.append("Género: ").append(comicGenero.getSelectedItem()).append('\n');
            texto.append("Año: ").append(comicAnio.getValue()).append('\n');
            texto.append("Idioma: ").append(textoCampo(comicIdioma)).append('\n');
            texto.append("Formato: ").append(textoCampo(comicFormato)).append('\n');
            texto.append("ISBN: ").append(textoCampo(comicIsbn));
        } else if ("JUEGO".equals(tipo)) {
            texto.append("Jugadores: ").append(juegoJugadoresMin.getValue()).append("-")
                    .append(juegoJugadoresMax.getValue()).append('\n');
            texto.append("Edad mínima: ").append(juegoEdad.getValue()).append('\n');
            texto.append("Tipo: ").append(tipoJuego.getSelectedItem()).append('\n');
            texto.append("Duración: ").append(juegoDuración.getValue()).append(" min\n");
            texto.append("Editorial: ").append(textoCampo(juegoEditorial)).append('\n');
            texto.append("Idioma: ").append(textoCampo(juegoIdioma)).append('\n');
            texto.append("Componentes: ").append(textoArea(juegoComponentes));
        } else {
            texto.append("Altura: ").append(figuraAltura.getValue()).append(" cm\n");
            texto.append("Marca: ").append(textoCampo(figuraMarca)).append('\n');
            texto.append("Material: ").append(textoCampo(figuraMaterial)).append('\n');
            texto.append("Escala: ").append(textoCampo(figuraEscala)).append('\n');
            texto.append("Personaje: ").append(textoCampo(figuraPersonaje)).append('\n');
            texto.append("Franquicia: ").append(textoCampo(figuraFranquicia)).append('\n');
            texto.append("Articulada: ").append(figuraArticulada.isSelected() ? "sí" : "no");
        }
        return texto.toString();
    }

    /**
     * Recupera y formatea con seguridad el texto introducido en una caja genérica de texto.
     * 
     * @param campo Control visual del que recuperar la cadena.
     * @return El contenido limpio, o "No indicado" si no contenía datos viables.
     */
    private String textoCampo(JTextField campo) {
        return campo.getText() == null || campo.getText().isBlank() ? "No indicado" : campo.getText().trim();
    }

    /**
     * Recupera y formatea con seguridad el texto introducido en un área multilínea.
     * 
     * @param campo Control visual del que recuperar el texto ampliado.
     * @return El contenido textual limpio, o "No indicado" si carece de longitud o texto real.
     */
    private String textoArea(JTextArea campo) {
        return campo.getText() == null || campo.getText().isBlank() ? "No indicado" : campo.getText().trim();
    }

    /**
     * Modal principal encargado de la gestión de packs. Permite la introducción
     * de su nombre general, la categoría, el costo unitario del paquete
     * y modificar qué productos o en qué cantidad los contiene.
     * 
     * @param pack El objeto `Pack` que se modificará, si es nulo se considera un proceso de creación.
     */
    private void editarPack(Pack pack) {
        JTextField nombre = new JTextField(pack == null ? "" : pack.getNombre());
        nombre.setEnabled(pack == null);
        JTextField categoria = new JTextField(pack == null ? "" : pack.getCategoria());
        JTextField precio = new JTextField(pack == null ? "0.00"
                : String.format("%.2f", pack.getPrecio()).replace(',', '.'));
        JTextField buscar = new JTextField();
        JPanel productos = new JPanel(new GridLayout(0, 1, 4, 4));
        List<JSpinner> cantidades = new ArrayList<>();
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            JSpinner cantidad = new JSpinner(new SpinnerNumberModel(cantidadProductoPack(pack, producto), 0, 99, 1));
            cantidad.putClientProperty("producto", producto);
            JPanel filaProducto = new JPanel(new BorderLayout(8, 0));
            filaProducto.add(new JLabel(producto.getNombre() + " | " + categoriaProducto(producto)), BorderLayout.CENTER);
            filaProducto.add(cantidad, BorderLayout.EAST);
            cantidad.putClientProperty("fila", filaProducto);
            cantidades.add(cantidad);
            productos.add(filaProducto);
        }
        buscar.addActionListener(e -> filtrarCantidadesProducto(buscar.getText(), productos, cantidades));
        JButton aplicarBusqueda = crearBoton("Buscar", 90);
        aplicarBusqueda.addActionListener(e -> filtrarCantidadesProducto(buscar.getText(), productos, cantidades));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel datos = new JPanel(new GridLayout(0, 1, 6, 6));
        datos.add(new JLabel("Nombre"));
        datos.add(nombre);
        datos.add(new JLabel("Categoria del pack"));
        datos.add(categoria);
        datos.add(new JLabel("Precio"));
        datos.add(precio);
        datos.add(new JLabel("Buscar producto"));
        JPanel filaBuscar = new JPanel(new BorderLayout(8, 0));
        filaBuscar.add(buscar, BorderLayout.CENTER);
        filaBuscar.add(aplicarBusqueda, BorderLayout.EAST);
        datos.add(filaBuscar);
        panel.add(datos, BorderLayout.NORTH);
        panel.add(new JScrollPane(productos), BorderLayout.CENTER);

        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                pack == null ? "Nuevo pack" : "Modificar pack", JOptionPane.OK_CANCEL_OPTION);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        List<ProductoTienda> seleccionados = new ArrayList<>();
        for (JSpinner cantidad : cantidades) {
            ProductoTienda producto = (ProductoTienda) cantidad.getClientProperty("producto");
            int unidades = ((Integer) cantidad.getValue()).intValue();
            for (int i = 0; i < unidades; i++) {
                seleccionados.add(producto);
            }
        }
        if (pack == null) {
            mainFrame.crearPackGestion(nombre.getText(), categoria.getText(), parseDouble(precio.getText(), 0.0), seleccionados);
        } else {
            mainFrame.modificarPackGestion(pack, categoria.getText(), parseDouble(precio.getText(), pack.getPrecio()), seleccionados);
        }
    }

    /**
     * Control de interfaz que actúa como un buscador, recorriendo el array paralelo
     * de los elementos gráficos de un paquete ocultando/mostrando aquellos que coincidan con su texto.
     * 
     * @param filtro El string de búsqueda contra el cual comparar.
     * @param productos El componente general que contiene las filas.
     * @param cantidades La lista enlazada a spinners que contienen el metadato del producto de forma individual.
     */
    private void filtrarCantidadesProducto(String filtro, JPanel productos, List<JSpinner> cantidades) {
        String normalizado = filtro == null ? "" : filtro.trim().toLowerCase();
        productos.removeAll();
        for (JSpinner cantidad : cantidades) {
            ProductoTienda producto = (ProductoTienda) cantidad.getClientProperty("producto");
            String texto = producto.getNombre() + " " + categoriaProducto(producto);
            if (normalizado.isBlank() || texto.toLowerCase().contains(normalizado)) {
                productos.add((JPanel) cantidad.getClientProperty("fila"));
            }
        }
        productos.revalidate();
        productos.repaint();
    }

    /**
     * Extrae cuántas veces está referenciado un único producto individualmente dentro
     * del array agrupador de un pack en concreto.
     * 
     * @param pack Objeto paquete que engloba a los demás (Si nulo retorna 0 directamente).
     * @param producto Referencia del producto original sobre el que se hace la búsqueda.
     * @return Conteo absoluto de ocurrencias.
     */
    private int cantidadProductoPack(Pack pack, ProductoTienda producto) {
        if (pack == null) {
            return 0;
        }
        int cantidad = 0;
        for (ProductoTienda incluido : pack.getProductos()) {
            if (incluido == producto) {
                cantidad++;
            }
        }
        return cantidad;
    }

    /**
     * Muestra un cuadro de diálogo confirmando la evaluación y valor estimado
     * de un bien insertado al mercado por parte de un consumidor/vendedor externo.
     * 
     * @param producto Componente a puntuar en estado y fijar precio monetario.
     */
    private void valorarProductoSegundaMano(ProductoSegundaMano producto) {
        if (producto.getEstaValorado()) {
            JOptionPane.showMessageDialog(this,
                    "Este producto ya fue valorado y no puede modificarse.",
                    "Valoración bloqueada", JOptionPane.WARNING_MESSAGE);
            return;
        }
        double precioInicial = producto.getEstaValorado() ? producto.getValorEstimado() : 10.0;
        JSpinner precio = new JSpinner(new SpinnerNumberModel(precioInicial, 0.0, 9999.0, 1.0));
        JComboBox<EstadoConservacion> conservacion = new JComboBox<>(EstadoConservacion.values());
        if (producto.getEstadoConservacion() != null) {
            conservacion.setSelectedItem(producto.getEstadoConservacion());
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Precio estimado"));
        panel.add(precio);
        panel.add(new JLabel("Estado de conservación"));
        panel.add(conservacion);

        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                "Valorar " + producto.getNombre(),
                JOptionPane.OK_CANCEL_OPTION);
        if (respuesta == JOptionPane.OK_OPTION) {
            mainFrame.valorarProductoSegundaMano(producto,
                    ((Double) precio.getValue()).doubleValue(),
                    (EstadoConservacion) conservacion.getSelectedItem());
        }
    }

    /**
     * Control del flujo principal logístico. Transiciona el estado temporal de
     * un pedido al siguiente si se dan las condiciones viables en el sistema.
     * 
     * @param pedido La compra física sujeta de este traslado o avance operativo.
     */
    private void avanzarPedido(Pedido pedido) {
        if (pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
            mainFrame.prepararPedido(pedido);
        } else if (pedido.getEstadoPedido() == EstadoPedido.LISTO) {
            mainFrame.entregarPedido(pedido);
        }
    }

    /**
     * Lanza una ventana modal informativa volcando íntegramente los campos
     * del resumen textual vinculados a un identificador de un pedido.
     * 
     * @param pedido Objeto de compra completada, pagada, o en espera de entrega.
     */
    private void mostrarInfoPedido(Pedido pedido) {
        JOptionPane.showMessageDialog(this,
                "Cliente: " + pedido.getCliente().getNombre()
                        + "\nDNI: " + pedido.getCliente().getDNI()
                        + "\nCodigo: " + pedido.getCodigo().getCodigo()
                        + "\nEstado: " + pedido.getEstadoPedido()
                        + "\nFecha: " + formatoFecha.format(pedido.getFechaRealizacion())
                        + "\nProductos: " + resumenProductosPedido(pedido)
                        + "\nTotal: " + String.format("%.2f EUR", pedido.calcularPrecioTotal()),
                "Pedido", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Calcula la cantidad física total de artículos albergados en los inventarios.
     * Iterativo y lento con grandes catálogos.
     * 
     * @return El entero que refleja todas las unidades dispuestas.
     */
    private int totalUnidadesStock() {
        int total = 0;
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            total += mainFrame.getStock().getNumProductos(producto);
        }
        return total;
    }

    /**
     * Busca qué volumen de pedidos en el estado operativo solicitado existe
     * actualmente en el arreglo gestionado de pedidos.
     * 
     * @param estado Estado objetivo que actúa de discriminador en la búsqueda.
     * @return Conteo total válido devuelto en tipo entero.
     */
    private int contarPedidos(EstadoPedido estado) {
        int total = 0;
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == estado) {
                total++;
            }
        }
        return total;
    }

    /**
     * Realiza un filtrado entre todas las propuestas originadas de forma mutua entre clientes,
     * determinando aquellas sobre las cuales aún existe posibilidad de decisión.
     * 
     * @return Representación en valor entero de los intercambios activos.
     */
    private int contarIntercambiosPendientes() {
        int total = 0;
        for (Intercambio intercambio : mainFrame.getIntercambios()) {
            if (intercambio.getOferta().getEstadoOferta() == EstadoOferta.PENDIENTE) {
                total++;
            }
        }
        return total;
    }

    /**
     * Sume la contribución neta total en divisas de todo aquel pedido
     * validado por el sistema como oficialmente entregado a las manos del cliente.
     * 
     * @return Volumen económico de la cuenta con doble precisión decimal.
     */
    private double totalVentasEntregadas() {
        double total = 0.0;
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                total += pedido.calcularPrecioTotal();
            }
        }
        return total;
    }

    /**
     * Realiza un histórico retroactivo agrupando el influjo neto mensual derivado
     * por las compras resueltas favorablemente durante los últimos once meses más el corriente.
     * 
     * @return Mapa ligando el nombre corto mes-año frente al montante recabado.
     */
    private Map<String, Double> ventasEntregadasPorMes() {
        Map<String, Double> ventas = new LinkedHashMap<>();
        SimpleDateFormat formatoMes = new SimpleDateFormat("MMM yy");
        Calendar inicio = Calendar.getInstance();
        inicio.set(Calendar.DAY_OF_MONTH, 1);
        inicio.add(Calendar.MONTH, -11);

        for (int i = 0; i < 12; i++) {
            ventas.put(formatoMes.format(inicio.getTime()), 0.0);
            inicio.add(Calendar.MONTH, 1);
        }

        Calendar limite = Calendar.getInstance();
        limite.set(Calendar.DAY_OF_MONTH, 1);
        limite.add(Calendar.MONTH, -11);

        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                Date fecha = fechaEstadisticaPedido(pedido);
                if (fecha != null && !fecha.before(limite.getTime())) {
                    String mes = formatoMes.format(fecha);
                    if (ventas.containsKey(mes)) {
                        ventas.put(mes, ventas.get(mes) + pedido.calcularPrecioTotal());
                    }
                }
            }
        }
        return ventas;
    }

    /**
     * Resuelve qué punto temporal exacto tomar a la hora de ubicar
     * el pedido estadísticamente según su propia evolución orgánica.
     * 
     * @param pedido Muestra u objeto de compra inspeccionado.
     * @return Timestamp del cobro, envío o inicio originario.
     */
    private Date fechaEstadisticaPedido(Pedido pedido) {
        if (pedido.getFechaRecogida() != null) {
            return pedido.getFechaRecogida();
        }
        if (pedido.getFechaPago() != null) {
            return pedido.getFechaPago();
        }
        return pedido.getFechaRealizacion();
    }

    /**
     * Construye un conglomerado visual que contiene las barras de progreso estáticas
     * correspondientes de todos los estados válidos programables de un pedido en la tienda.
     * 
     * @return Panel listo para incorporarse bajo el área de analíticas.
     */
    private JPanel crearPanelPedidosPorEstado() {
        JPanel panel = crearTarjeta();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        int max = 1;
        for (EstadoPedido estado : EstadoPedido.values()) {
            max = Math.max(max, contarPedidos(estado));
        }
        for (EstadoPedido estado : EstadoPedido.values()) {
            panel.add(crearBarraEstado(estado, contarPedidos(estado), max));
            panel.add(Box.createVerticalStrut(8));
        }
        return panel;
    }

    /**
     * Forma de forma individual una de las barras representativas para
     * ilustrar volumen numérico correlacionado con el estado del pedido, dotando de nombre y color.
     * 
     * @param estado Etiqueta a situar a su extremo inicial.
     * @param valor Magnitud neta.
     * @param max Tope superior limitante y que se equiparará con el ancho de banda del 100%.
     * @return Hilera gráfica final con el ratio pintado con el widget interno de la clase "BarraProgreso".
     */
    private JPanel crearBarraEstado(EstadoPedido estado, int valor, int max) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        JLabel nombre = crearEtiqueta("<b>" + estado + "</b>");
        nombre.setPreferredSize(new Dimension(160, 28));
        fila.add(nombre, BorderLayout.WEST);
        fila.add(new BarraProgreso(valor, max, colorEstadoPedido(estado)), BorderLayout.CENTER);

        JLabel cantidad = crearEtiqueta(String.valueOf(valor));
        cantidad.setHorizontalAlignment(SwingConstants.RIGHT);
        cantidad.setPreferredSize(new Dimension(38, 28));
        fila.add(cantidad, BorderLayout.EAST);
        return fila;
    }

    /**
     * Construye la barra de progreso particular vinculada al número de
     * adquisiciones totales resueltas por un individuo del modelo.
     * 
     * @param cliente Instancia del cliente en la cual basar la métrica de éxito.
     * @param maxCompras Ratio superpuesto por el cliente con mayor volumen para trazar el límite 100%.
     * @return JPanel del cliente individual asimilado en la clasificación líder.
     */
    private JPanel crearBarraCliente(ClienteRegistrado cliente, int maxCompras) {
        JPanel fila = crearTarjeta();
        fila.setLayout(new BorderLayout(12, 0));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel datos = crearEtiqueta("<b>" + cliente.getNombre() + "</b><br>DNI " + cliente.getDNI()
                + " | cartera " + cliente.getCartera().getNumProductos());
        datos.setPreferredSize(new Dimension(260, 42));
        fila.add(datos, BorderLayout.WEST);
        fila.add(new BarraProgreso(cliente.getPedidos().size(), maxCompras, UiStyle.COLOR_MARRON_MEDIO),
                BorderLayout.CENTER);

        JLabel compras = crearEtiqueta("<b>" + cliente.getPedidos().size() + "</b> compras");
        compras.setHorizontalAlignment(SwingConstants.RIGHT);
        compras.setPreferredSize(new Dimension(110, 42));
        fila.add(compras, BorderLayout.EAST);
        return fila;
    }

    /**
     * Recupera un color estandarizado pre-mapeado en función de en
     * qué ciclo se encuentra varada la transacción analizada.
     * 
     * @param estado Estado lógico a inquirir.
     * @return Un objeto de la clase Color con componentes RGB pre-ajustadas.
     */
    private Color colorEstadoPedido(EstadoPedido estado) {
        switch (estado) {
        case EN_CARRITO:
            return new Color(116, 130, 145);
        case EN_PREPARACION:
            return new Color(188, 136, 55);
        case LISTO:
            return new Color(68, 132, 166);
        case ENTREGADO:
            return new Color(75, 145, 95);
        case CANCELADO:
            return new Color(170, 82, 72);
        default:
            return UiStyle.COLOR_MARRON_MEDIO;
        }
    }

    /**
     * Acumula de manera unitaria la suma íntegra de valoraciones estipuladas
     * de lo que el sistema contabiliza en objetos de segunda mano pendientes.
     * 
     * @return Sumatorio devuelto como punto flotante de 64 bits.
     */
    private double totalValoraciones() {
        double total = 0.0;
        for (ProductoSegundaMano producto : mainFrame.getProductosSegundaManoGestion()) {
            total += producto.getValorEstimado();
        }
        return total;
    }

    /**
     * Comprueba flag y/o magnitudes absolutas si algún atributo está
     * forzando al producto a presentar una tarificación no original.
     * 
     * @param producto Ente bajo escrutinio.
     * @return Boolean indicativo de la existencia del descuento.
     */
    private boolean tieneDescuento(ProductoTienda producto) {
        return producto.getRebajaPorcentaje() > 0
                || producto.getRebajaFija() > 0
                || producto.isTiene2x1();
    }

    /**
     * Crea un glosario resumido con todos aquellos aspectos que
     * reduzcan explícitamente el montante a abonar para una unidad del objeto.
     * 
     * @param producto Componente a formatear.
     * @return Texto resultante delimitado por comas.
     */
    private String textoDescuento(ProductoTienda producto) {
        List<String> partes = new ArrayList<>();
        if (producto.getRebajaPorcentaje() > 0) {
            partes.add(String.format("%.0f%%", producto.getRebajaPorcentaje()));
        }
        if (producto.getRebajaFija() > 0) {
            partes.add(String.format("%.2f EUR", producto.getRebajaFija()));
        }
        if (producto.isTiene2x1()) {
            partes.add("2x1");
        }
        return partes.isEmpty() ? "sin descuento" : String.join(", ", partes);
    }

    /**
     * Facilita el volcado rápido en cadena alfabética de la estructura
     * lógica categorizada referenciada.
     * 
     * @param producto Producto a extraer su categorización.
     * @return String de agrupamiento con las clasificaciones adyacentes si hay más de una.
     */
    private String textoCategorías(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        if (!categorias.isEmpty()) {
            return String.join(", ", categorias);
        }
        return producto.getCategoria() == null ? "sin categorías" : producto.getCategoria().getNombre();
    }

    /**
     * Devuelve una lectura en lenguaje explícito del set o enumeración de
     * autoridades delegadas hacia el empleado que accede a estos directorios.
     * 
     * @param empleado Instancia de rol subordinado a gestor.
     * @return Conversión alfabética de los flags permitidos.
     */
    private String textoPermisos(Empleado empleado) {
        return empleado.getPermisos().isEmpty() ? "sin permisos" : empleado.getPermisos().toString();
    }

    /**
     * Formateador compacto enfocado para una visualización muy reducida de la identidad de venta.
     * 
     * @param pedido Cesta o canasto a sintetizar.
     * @return Línea de texto breve de HTML interpretativo.
     */
    private String resumenPedido(Pedido pedido) {
        return "<b>" + pedido.getEstadoPedido() + "</b> | " + pedido.getCliente().getNombre()
                + " | " + resumenProductosPedido(pedido);
    }

    /**
     * Formatea un texto explicativo del estatus económico y del ofertante
     * de algo sometido como bien de reventa de un individuo para con el sistema o tienda física.
     * 
     * @param producto Cúmulo de datos referenciado a segunda mano.
     * @return Cadena que incluye el propietario y una mención de tasación o espera de esta.
     */
    private String textoProductoSegundaMano(ProductoSegundaMano producto) {
        String valor = producto.getEstaValorado()
                ? String.format("%.2f EUR | %s", producto.getValorEstimado(), producto.getEstadoConservacion())
                : "pendiente de valorar";
        return "<b>" + producto.getNombre() + "</b><br>Propietario: "
                + producto.getPropietario().getNombre() + " | " + valor;
    }

    /**
     * Ejecuta una recolección pormenorizada del listado adjunto del HashMap y recuenta
     * iterativamente su cantidad anexándola al nominativo de este en una String corrida.
     * 
     * @param pedido Identificador de venta del que descifrar su carrito.
     * @return Frase gramatical indicando los elementos insertados.
     */
    private String resumenProductosPedido(Pedido pedido) {
        StringBuilder productos = new StringBuilder();
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            if (productos.length() > 0) {
                productos.append(", ");
            }
            productos.append(entry.getKey().getNombre()).append(" x").append(entry.getValue());
        }
        return productos.toString();
    }

    /**
     * Acopla la información desordenada dentro del agregado pre-armado
     * mediante su contabilización y sumatoria local a nivel de vista del gestor.
     * 
     * @param pack Envolvente a computar.
     * @return Frase de la lista que lo engloba uniendo sus designaciones y categorías.
     */
    private String resumenPack(Pack pack) {
        StringBuilder texto = new StringBuilder();
        Map<ProductoTienda, Integer> cantidades = new LinkedHashMap<>();
        for (ProductoTienda producto : pack.getProductos()) {
            cantidades.merge(producto, 1, Integer::sum);
        }
        for (Map.Entry<ProductoTienda, Integer> entry : cantidades.entrySet()) {
            if (texto.length() > 0) {
                texto.append(" + ");
            }
            texto.append(entry.getKey().getNombre());
            if (entry.getValue() > 1) {
                texto.append(" x").append(entry.getValue());
            }
            texto.append(" [").append(categoriaProducto(entry.getKey())).append("]");
        }
        return texto.length() == 0 ? "sin productos" : texto.toString();
    }

    /**
     * Obtiene el texto representativo o nombre de la categoría asignada directamente
     * hacia un lote predispuesto.
     * 
     * @param pack Paquete al cual referenciar su clasificación matriz.
     * @return El nombre recuperado, o "sin categoría" si por fallos de diseño carece de este.
     */
    private String categoriaPack(Pack pack) {
        String categoria = pack.getCategoria();
        return categoria == null || categoria.isBlank() ? "sin categoría" : categoria;
    }

    /**
     * Puente auxiliar para invocar el texto de jerarquías sobre un bien general.
     * 
     * @param producto Elemento de consulta.
     * @return String de agrupamiento de la entidad.
     */
    private String categoriaProducto(ProductoTienda producto) {
        return textoCategorías(producto);
    }

    /**
     * Divide a partir de comas simples una palabra extendida transformándola de
     * forma automatizada al entorno genérico coleccionable de Java (List).
     * 
     * @param texto Input crudo del gestor introducido en un JTextField.
     * @return Un vector enlazado listado como ArrayList conteniendo todas ellas limpias de espacios blancos.
     */
    private List<String> parseCategorías(String texto) {
        List<String> categorias = new ArrayList<>();
        if (texto == null || texto.isBlank()) {
            return categorias;
        }
        for (String categoria : texto.split(",")) {
            if (!categoria.trim().isBlank()) {
                categorias.add(categoria.trim());
            }
        }
        return categorias;
    }

    /**
     * Interpreta de manera blindada el cambio desde texto libre a decimal doble,
     * reemplazando convenciones europeas en coma para evitar saltos de línea (crashes).
     * 
     * @param texto Fuente originaria o input del usuario que requiere el traspaso.
     * @param defecto Número comodín por el que decantarse de saltar el "NumberFormatException".
     * @return El doble válido.
     */
    private double parseDouble(String texto, double defecto) {
        if (texto == null || texto.isBlank()) {
            return defecto;
        }
        try {
            return Double.parseDouble(texto.replace(',', '.'));
        } catch (NumberFormatException e) {
            return defecto;
        }
    }

    /**
     * Devuelve una lámina vacía provista de bordes redondeados orientada hacia
     * organizar bloques independientes dentro de un gran contenedor como las estadísticas.
     * 
     * @return Módulo de tarjeta JPanel.
     */
    private JPanel crearTarjeta() {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 16, 16, 16));
        tarjeta.setBackground(UiStyle.COLOR_TARJETA);
        return tarjeta;
    }

    /**
     * Fabrica dinámicamente un JLabel en función paramétrica portando formato
     * grueso como rotulación primaria en cabezas de bloque.
     * 
     * @param texto Inscripción base requerida.
     * @return JLabel formalizado y dotado de grandes márgenes.
     */
    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(12, 0, 10, 0));
        return titulo;
    }

    /**
     * Fabrica un componente auxiliar como un subtítulo para dotar
     * de explicación en contextos donde la cabecera queda insuficiente.
     * 
     * @param texto Explicación requerida para el componente de vista contiguo.
     * @return Título formatado de grado 2 ó menor.
     */
    private JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(0, 0, 16, 0));
        return label;
    }

    /**
     * Agiliza la composición de strings de interfaz envueltos por la etiqueta `<html>`,
     * soportando saltos o tipografías especiales.
     * 
     * @param texto Información raw.
     * @return Equivalente JLabel instanciado con colores estandarizados.
     */
    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    /**
     * Diseña y provee de estructura los botones más recurrentes de interacciones de control
     * acotados hacia las dimensiones facilitadas por los márgenes de ventana en gestor.
     * 
     * @param texto Enunciado interior del control interaccionable.
     * @param ancho Escala predefinida como preferente, evitando que se colapse la rejilla.
     * @return Botón plenamente customizado asilando las clases `UiStyle`.
     */
    private JButton crearBoton(String texto, int ancho) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(ancho, 34));
        boton.setMaximumSize(new Dimension(ancho, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_MARRON_MEDIO, 1));
        return boton;
    }

    /**
     * Clase interna encargada de proveer de representación gráfica personalizada 
     * en 2D de las ventas mensuales históricas computadas a través de barras de dibujo.
     */
    private static class GraficaVentasMensuales extends JPanel {

        private static final long serialVersionUID = 1L;

        /** 
         * Referencia constante al mapa que asocia el mes (en texto) frente
         * a su respectivo sumatorio en ventas entregadas durante el ciclo. 
         */
        private final Map<String, Double> ventas;

        /**
         * Constructor de la gráfica dinámica interna de ventas.
         * 
         * @param ventas Origen de datos para proyectar las barras numéricas proporcionalmente.
         */
        GraficaVentasMensuales(Map<String, Double> ventas) {
            this.ventas = ventas;
            setOpaque(false);
            setPreferredSize(new Dimension(0, 300));
            setMinimumSize(new Dimension(0, 300));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
            setBorder(new EmptyBorder(18, 18, 18, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int x = 20;
            int y = 10;
            int w = getWidth() - 40;
            int h = getHeight() - 20;
            g2.setColor(UiStyle.COLOR_TARJETA);
            g2.fillRoundRect(x, y, w, h, 18, 18);

            int left = x + 56;
            int right = x + w - 18;
            int top = y + 36;
            int bottom = y + h - 48;
            double max = 1.0;
            for (double valor : ventas.values()) {
                max = Math.max(max, valor);
            }

            g2.setFont(new Font("SansSerif", Font.BOLD, 15));
            g2.setColor(UiStyle.COLOR_TEXTO);
            g2.drawString("Ventas mensuales", x + 18, y + 26);

            g2.setStroke(new BasicStroke(1f));
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            FontMetrics metrics = g2.getFontMetrics();
            for (int i = 0; i <= 4; i++) {
                int gy = bottom - (int) ((bottom - top) * (i / 4.0));
                g2.setColor(new Color(185, 165, 140));
                g2.drawLine(left, gy, right, gy);
                g2.setColor(UiStyle.COLOR_TEXTO);
                String etiqueta = String.format("%.0f", max * i / 4.0);
                g2.drawString(etiqueta, left - metrics.stringWidth(etiqueta) - 8, gy + 4);
            }

            int meses = ventas.size();
            int espacio = Math.max(1, right - left);
            int anchoBarra = Math.max(12, Math.min(42, espacio / (meses * 2)));
            int indice = 0;
            for (Map.Entry<String, Double> entry : ventas.entrySet()) {
                int centro = left + (int) ((indice + 0.5) * espacio / meses);
                int barraAltura = (int) ((bottom - top) * (entry.getValue() / max));
                int bx = centro - anchoBarra / 2;
                int by = bottom - barraAltura;

                g2.setColor(UiStyle.COLOR_MARRON_MEDIO);
                g2.fillRoundRect(bx, by, anchoBarra, barraAltura, 10, 10);
                g2.setColor(UiStyle.COLOR_TEXTO);
                String mes = entry.getKey();
                g2.drawString(mes, centro - metrics.stringWidth(mes) / 2, bottom + 22);
                indice++;
            }
            g2.dispose();
        }
    }

    /**
     * Componente UI personalizado en un dibujo en 2D dedicado 
     * a mostrar una barra gráfica horizontal ilustrando el relleno en proporción a un máximo.
     */
    private static class BarraProgreso extends JPanel {

        private static final long serialVersionUID = 1L;

        /** 
         * Magnitud real lograda y completada de la barra que el usuario tiene sobre sí. 
         */
        private final int valor;
        
        /** 
         * Tope artificial al 100% que representará gráficamente el fin del renderizado de la barra. 
         */
        private final int max;
        
        /** 
         * Color corporativo o dependiente del estado que teñirá el tramo completado de dicha barra. 
         */
        private final Color color;

        /**
         * Inicializa una barra de progreso genérica para su pintado en vista de estadísticas.
         * 
         * @param valor Unidades netas cubiertas o consolidadas.
         * @param max Límite base o mejor resultado pre-existente.
         * @param color Tono de pintado.
         */
        BarraProgreso(int valor, int max, Color color) {
            this.valor = valor;
            this.max = Math.max(1, max);
            this.color = color;
            setOpaque(false);
            setPreferredSize(new Dimension(120, 28));
            setMinimumSize(new Dimension(80, 28));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int h = Math.min(16, getHeight() - 8);
            int y = (getHeight() - h) / 2;
            g2.setColor(new Color(232, 222, 210));
            g2.fillRoundRect(0, y, getWidth(), h, h, h);
            int ancho = (int) (getWidth() * (valor / (double) max));
            if (valor > 0) {
                g2.setColor(color);
                g2.fillRoundRect(0, y, Math.max(h, ancho), h, h, h);
            }
            g2.dispose();
        }
    }
}