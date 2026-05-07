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
import javax.swing.ScrollPaneConstants;
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
import productos.Producto;
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
 * Manager workspace for administration, stock, discounts, packs and reports.
 */
public class PanelGestor extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final String DASHBOARD = "DASHBOARD";
    private static final String EMPLEADOS = "EMPLEADOS";
    private static final String INVENTARIO = "INVENTARIO";
    private static final String DESCUENTOS = "DESCUENTOS";
    private static final String SEGUNDA_MANO = "SEGUNDA_MANO";
    private static final String PACKS = "PACKS";
    private static final String OPERATIVA = "OPERATIVA";
    private static final String ESTADISTICAS = "ESTADISTICAS";
    private static final String DETALLE_PRODUCTO = "DETALLE_PRODUCTO";

    private final Main mainFrame;
    private final JPanel contenido;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private String seccionActiva;
    private ProductoTienda productoSeleccionado;
    private boolean editandoProducto;

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

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        derecha.setOpaque(false);
        derecha.add(crearBotonIcono("\uD83D\uDD14", "Notificaciones", 26, 42));
        derecha.add(crearBotonIcono("\uD83D\uDC10", "Gestor", 28, 42));
        cabecera.add(derecha, BorderLayout.EAST);
        return cabecera;
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
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

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
        JMenuItem salir = crearItemMenu("CERRAR SESION", DASHBOARD);
        salir.addActionListener(e -> mainFrame.cerrarSesion());
        menu.add(salir);
        menu.show(origen, 0, origen.getHeight() + 6);
    }

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

    private JScrollPane crearScroll() {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        return scroll;
    }

    private void pintarDashboard() {
        contenido.add(crearTitulo("Resumen de gestion"));
        JPanel grid = new JPanel(new GridLayout(0, 4, 14, 14));
        grid.setOpaque(false);
        grid.add(crearMetrica("Catalogo", String.valueOf(mainFrame.getProductosTienda().size()), "productos"));
        grid.add(crearMetrica("Stock", String.valueOf(totalUnidadesStock()), "unidades"));
        grid.add(crearMetrica("Pedidos", String.valueOf(mainFrame.getPedidosGestion().size()), "registrados"));
        grid.add(crearMetrica("Empleados", String.valueOf(mainFrame.getEmpleados().size()), "activos"));
        contenido.add(grid);

        contenido.add(crearTitulo("Accesos rapidos"));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        acciones.setOpaque(false);
        acciones.add(crearAcceso("Nuevo empleado", EMPLEADOS));
        acciones.add(crearAcceso("Editar descuentos", DESCUENTOS));
        acciones.add(crearAcceso("Gestionar packs", PACKS));
        acciones.add(crearAcceso("Ver estadisticas", ESTADISTICAS));
        contenido.add(acciones);

        contenido.add(crearTitulo("Actividad pendiente"));
        contenido.add(crearEtiqueta("Pedidos en preparacion: " + contarPedidos(EstadoPedido.EN_PREPARACION)));
        contenido.add(crearEtiqueta("Pedidos listos para entregar: " + contarPedidos(EstadoPedido.LISTO)));
        contenido.add(crearEtiqueta("Productos pendientes de valorar: "
                + mainFrame.getProductosPendientesValoracion().size()));
        contenido.add(crearEtiqueta("Intercambios pendientes: " + contarIntercambiosPendientes()));
    }

    private JButton crearAcceso(String texto, String seccion) {
        JButton boton = crearBoton(texto, 170);
        boton.addActionListener(e -> {
            seccionActiva = seccion;
            refrescar();
        });
        return boton;
    }

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

    private void pintarEmpleados() {
        contenido.add(crearTitulo("Gestion de empleados"));
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

    private JPanel crearFilaEmpleado(Empleado empleado) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(22, 16, 22, 16));
        fila.setPreferredSize(new Dimension(0, 86));
        fila.setMinimumSize(new Dimension(0, 86));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        fila.add(crearEtiqueta("<b>" + empleado.getNombre() + "</b><br>Permisos: "
                + textoPermisos(empleado)), BorderLayout.CENTER);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton permisos = crearBoton("Permisos", 120);
        permisos.addActionListener(e -> mostrarDialogoEmpleado(empleado));
        acciones.add(permisos);
        JButton baja = crearBoton("Baja", 86);
        baja.addActionListener(e -> confirmarBajaEmpleado(empleado));
        acciones.add(baja);
        fila.add(acciones, BorderLayout.EAST);
        return fila;
    }

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

    private JPanel crearTarjetaProductoHome(ProductoTienda producto) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setOpaque(false);
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        TarjetaProducto vistaCliente = new TarjetaProducto(producto);
        vistaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirDetalleProducto(producto, false);
            }
        });
        tarjeta.add(vistaCliente, BorderLayout.CENTER);
        JButton editar = new UiStyle.RoundedButton("Editar", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        editar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        editar.setPreferredSize(new Dimension(200, 28));
        editar.addActionListener(e -> abrirDetalleProducto(producto, true));
        tarjeta.add(editar, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void abrirDetalleProducto(ProductoTienda producto, boolean editar) {
        productoSeleccionado = producto;
        editandoProducto = editar;
        seccionActiva = DETALLE_PRODUCTO;
        refrescar();
    }

    private JLabel crearImagenProducto(ProductoTienda producto, int ancho, int alto) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(ancho, alto));
        label.setMinimumSize(new Dimension(ancho, alto));
        label.setMaximumSize(new Dimension(ancho, alto));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        String ruta = producto.getImagen();
        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imagen));
        } else {
            label.setText("<html><center>SIN<br>IMAGEN</center></html>");
            label.setOpaque(true);
            label.setBackground(UiStyle.COLOR_MARRON_MEDIO);
            label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        }
        return label;
    }

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
            public void confirmar(PanelDeProducto.DatosEdicion datos) {
                mainFrame.editarProductoTienda(productoSeleccionado,
                        datos.nombre,
                        parseDouble(datos.precio, productoSeleccionado.getPrecio()),
                        datos.stock,
                        datos.descripcion,
                        datos.imagen,
                        parseCategorias(datos.categorias));
                editandoProducto = false;
                refrescar();
            }

            @Override
            public void cancelar() {
                editandoProducto = false;
                refrescar();
            }
        });
        contenido.add(detalle);
    }

    private JPanel crearBarraVolverInventario() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton volver = crearBotonFlechaVolver();
        volver.addActionListener(e -> volverAInventario());
        barra.add(volver);
        return barra;
    }

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

    private void volverAInventario() {
        editandoProducto = false;
        productoSeleccionado = null;
        seccionActiva = INVENTARIO;
        refrescar();
    }

    private JPanel crearDetalleIzquierdo(ProductoTienda producto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(300, 0));
        panel.add(crearImagenProducto(producto, 184, 250));
        panel.add(Box.createVerticalStrut(12));
        JLabel nombre = new JLabel("<html><center>" + producto.getNombre() + "</center></html>", SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 20));
        nombre.setForeground(Color.BLACK);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        nombre.setMaximumSize(new Dimension(290, 52));
        panel.add(nombre);
        JLabel precio = new JLabel(String.format("%.2f\u20ac", producto.getPrecio()).replace('.', ','));
        precio.setFont(new Font("SansSerif", Font.BOLD, 38));
        precio.setForeground(Color.BLACK);
        precio.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(precio);
        JButton editar = new UiStyle.RoundedButton("Editar", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        editar.setFont(new Font("SansSerif", Font.PLAIN, 22));
        editar.setPreferredSize(new Dimension(270, 38));
        editar.setMaximumSize(new Dimension(270, 38));
        editar.setAlignmentX(Component.CENTER_ALIGNMENT);
        editar.addActionListener(e -> editarProducto(producto));
        panel.add(editar);
        return panel;
    }

    private JPanel crearDetalleDerecho(ProductoTienda producto) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(crearTituloDetalle("Descripcion"));
        JTextArea descripcion = new JTextArea(producto.getDescripcion());
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 17));
        descripcion.setForeground(Color.BLACK);
        descripcion.setOpaque(false);
        descripcion.setEditable(false);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setBorder(null);
        panel.add(descripcion);
        panel.add(Box.createVerticalStrut(14));
        panel.add(crearTituloDetalle("Comentarios"));
        for (String[] comentario : producto.getComentarios()) {
            panel.add(crearComentarioGestor(comentario[0], comentario[1]));
            panel.add(Box.createVerticalStrut(6));
        }
        return panel;
    }

    private JLabel crearTituloDetalle(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel crearComentarioGestor(String usuario, String texto) {
        JPanel tarjeta = new UiStyle.RoundedPanel(new Color(145, 124, 101), 12);
        tarjeta.setLayout(new BorderLayout(6, 2));
        tarjeta.setBorder(new EmptyBorder(6, 10, 7, 10));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JLabel nombre = new JLabel("@" + usuario);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        nombre.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        JTextArea cuerpo = new JTextArea(texto);
        cuerpo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cuerpo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        cuerpo.setOpaque(false);
        cuerpo.setEditable(false);
        cuerpo.setLineWrap(true);
        cuerpo.setWrapStyleWord(true);
        cuerpo.setBorder(null);
        tarjeta.add(nombre, BorderLayout.NORTH);
        tarjeta.add(cuerpo, BorderLayout.CENTER);
        return tarjeta;
    }

    private void pintarDescuentos() {
        contenido.add(crearTitulo("Descuentos"));
        contenido.add(crearSubtitulo("Configura rebajas por producto o por categoria."));
        contenido.add(crearPanelDescuentoProducto());
        contenido.add(Box.createVerticalStrut(16));
        contenido.add(crearPanelDescuentoCategoria());
        contenido.add(crearTitulo("Promociones activas"));
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            if (tieneDescuento(producto)) {
                contenido.add(crearEtiqueta(producto.getNombre() + ": " + textoDescuento(producto)));
            }
        }
    }

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

    private JPanel crearPanelDescuentoCategoria() {
        JPanel panel = crearTarjeta();
        panel.add(crearEtiqueta("<b>Aplicar a categoria</b>"));
        JTextField categoria = new JTextField();
        categoria.setMaximumSize(new Dimension(360, 30));
        JComboBox<String> tipo = new JComboBox<>(new String[] {"Porcentaje", "Rebaja fija", "2x1"});
        JSpinner porcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner fija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, 1.0));
        tipo.addActionListener(e -> actualizarCamposDescuento(tipo, porcentaje, fija));
        panel.add(crearEtiqueta("Categoria o texto de categoria"));
        panel.add(categoria);
        panel.add(crearPanelSeleccionDescuento(tipo, porcentaje, fija));
        actualizarCamposDescuento(tipo, porcentaje, fija);
        JButton aplicar = crearBoton("Aplicar a categoria", 180);
        aplicar.addActionListener(e -> {
            int total = mainFrame.aplicarDescuentoCategoria(categoria.getText(),
                    tipoDescuentoSeleccionado(tipo),
                    valorDescuentoSeleccionado(tipo, porcentaje, fija));
            JOptionPane.showMessageDialog(this, "Productos actualizados: " + total);
        });
        panel.add(aplicar);
        return panel;
    }

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

    private void actualizarCamposDescuento(JComboBox<String> tipo, JSpinner porcentaje, JSpinner fija) {
        String seleccion = (String) tipo.getSelectedItem();
        porcentaje.setEnabled("Porcentaje".equals(seleccion));
        fija.setEnabled("Rebaja fija".equals(seleccion));
    }

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

    private void pintarSegundaMano() {
        contenido.add(crearTitulo("Productos de segunda mano"));
        contenido.add(crearSubtitulo("Valora los productos subidos por clientes: precio estimado y estado de conservacion."));
        List<ProductoSegundaMano> productos = mainFrame.getProductosSegundaManoGestion();
        if (productos.isEmpty()) {
            contenido.add(crearEtiqueta("No hay productos de segunda mano."));
            return;
        }
        for (ProductoSegundaMano producto : productos) {
            contenido.add(crearFilaProductoSegundaMano(producto));
            contenido.add(Box.createVerticalStrut(10));
        }
    }

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
                    + resumenPack(pack) + "<br>" + String.format("%.2f EUR", pack.getPrecio())),
                    BorderLayout.CENTER);
            JButton editar = crearBoton("Modificar", 125);
            editar.addActionListener(e -> editarPack(pack));
            fila.add(editar, BorderLayout.EAST);
            contenido.add(fila);
            contenido.add(Box.createVerticalStrut(10));
        }
    }

    private void pintarOperativa() {
        contenido.add(crearTitulo("Pedidos e intercambios"));
        contenido.add(crearSubtitulo("Supervision de pedidos preparados, entregas e intercambios."));
        contenido.add(crearPanelPlazoOfertas());
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(crearTitulo("Pedidos"));
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            contenido.add(crearFilaPedido(pedido));
            contenido.add(Box.createVerticalStrut(8));
        }
        contenido.add(crearTitulo("Intercambios"));
        for (Intercambio intercambio : mainFrame.getIntercambios()) {
            contenido.add(crearFilaIntercambio(intercambio));
            contenido.add(Box.createVerticalStrut(8));
        }
    }

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

    private void pintarEstadisticas() {
        contenido.add(crearTitulo("Estadisticas"));
        JPanel grid = new JPanel(new GridLayout(0, 4, 14, 14));
        grid.setOpaque(false);
        grid.add(crearMetrica("Ventas", String.format("%.2f", totalVentasEntregadas()), "EUR entregados"));
        grid.add(crearMetrica("Pedidos", String.valueOf(contarPedidos(EstadoPedido.ENTREGADO)), "entregados"));
        grid.add(crearMetrica("Valoraciones", String.format("%.2f", totalValoraciones()), "EUR estimados"));
        grid.add(crearMetrica("Clientes", String.valueOf(mainFrame.getClientesRegistrados().size()), "registrados"));
        contenido.add(grid);

        contenido.add(crearTitulo("Ventas por mes"));
        contenido.add(crearSubtitulo("Importe de pedidos entregados durante los ultimos 12 meses."));
        contenido.add(new GraficaVentasMensuales(ventasEntregadasPorMes()));

        contenido.add(crearTitulo("Pedidos por estado"));
        contenido.add(crearPanelPedidosPorEstado());

        contenido.add(crearTitulo("Usuarios con mas compras"));
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

    private void mostrarDialogoEmpleado(Empleado empleado) {
        JTextField nombre = new JTextField(empleado == null ? "" : empleado.getNombre());
        nombre.setEnabled(empleado == null);
        JPasswordField contrasena = new JPasswordField(empleado == null ? "" : empleado.getContrase\u00f1a());
        JCheckBox producto = new JCheckBox("Productos, stock, categorias y packs");
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
        panel.add(new JLabel("Contrasena asignada por gestor"));
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
            empleado.setContrase\u00f1a(new String(contrasena.getPassword()));
            mainFrame.configurarPermisosEmpleado(empleado, permisos);
        }
        refrescar();
    }

    private void confirmarBajaEmpleado(Empleado empleado) {
        int respuesta = JOptionPane.showConfirmDialog(this,
                "Dar de baja a " + empleado.getNombre() + "?",
                "Baja empleado", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            mainFrame.eliminarEmpleadoDesdeGestor(empleado);
            refrescar();
        }
    }

    private void cargarProductosDeFichero() {
        JFileChooser chooser = new JFileChooser(".");
        int respuesta = chooser.showOpenDialog(this);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            mainFrame.recargarCatalogoDesdeFichero(chooser.getSelectedFile().getPath());
            refrescar();
        }
    }

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
        JTextField comicIdioma = new JTextField("Espanol");
        JTextField comicFormato = new JTextField("Tapa blanda");
        JTextField comicIsbn = new JTextField();

        JSpinner juegoJugadoresMin = new JSpinner(new SpinnerNumberModel(2, 1, 99, 1));
        JSpinner juegoJugadoresMax = new JSpinner(new SpinnerNumberModel(4, 1, 99, 1));
        JSpinner juegoEdad = new JSpinner(new SpinnerNumberModel(8, 0, 99, 1));
        JComboBox<TipoJuego> tipoJuego = new JComboBox<>(TipoJuego.values());
        JSpinner juegoDuracion = new JSpinner(new SpinnerNumberModel(30, 1, 999, 5));
        JTextField juegoEditorial = new JTextField();
        JTextField juegoIdioma = new JTextField("Espanol");
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
        detalles.addTab("Comic", crearPanelComic(comicPaginas, comicAutor, comicEditorial,
                comicGenero, comicAnio, comicIdioma, comicFormato, comicIsbn));
        detalles.addTab("Juego", crearPanelJuego(juegoJugadoresMin, juegoJugadoresMax,
                juegoEdad, tipoJuego, juegoDuracion, juegoEditorial, juegoIdioma, juegoComponentes));
        detalles.addTab("Figura", crearPanelFigura(figuraAltura, figuraMarca, figuraMaterial,
                figuraEscala, figuraPersonaje, figuraFranquicia, figuraArticulada));

        JComboBox<String> promocion = new JComboBox<>(new String[] {
                "Sin promocion", "2x1", "Rebaja porcentaje", "Rebaja fija"
        });
        JSpinner rebajaPorcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner rebajaFija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 9999.0, 1.0));

        JPanel basicos = crearPanelFormulario();
        basicos.add(crearTituloFormulario("Datos basicos"));
        basicos.add(crearFilaFormulario("Nombre", nombre));
        basicos.add(crearFilaFormulario("Precio", precio));
        basicos.add(crearFilaFormulario("Stock inicial", stock));
        basicos.add(crearFilaFormulario("Valoracion inicial", valoracion));
        basicos.add(crearFilaFormulario("Categorias", categorias));
        basicos.add(crearFilaFormulario("Promocion", promocion));
        basicos.add(crearFilaFormulario("Porcentaje", rebajaPorcentaje));
        basicos.add(crearFilaFormulario("Rebaja fija", rebajaFija));
        basicos.add(crearTituloFormulario("Descripcion"));
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
                juegoJugadoresMin, juegoJugadoresMax, juegoEdad, tipoJuego, juegoDuracion,
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
                parseCategorias(categorias.getText()),
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

    private JPanel crearPanelComic(JSpinner paginas, JTextField autor, JTextField editorial,
            JComboBox<Genero> genero, JSpinner anio, JTextField idioma,
            JTextField formato, JTextField isbn) {
        JPanel panel = crearPanelFormulario();
        panel.setPreferredSize(new Dimension(330, 0));
        panel.add(crearTituloFormulario("Detalles de comic"));
        panel.add(crearFilaFormulario("Paginas", paginas));
        panel.add(crearFilaFormulario("Autor", autor));
        panel.add(crearFilaFormulario("Editorial", editorial));
        panel.add(crearFilaFormulario("Genero", genero));
        panel.add(crearFilaFormulario("Anio", anio));
        panel.add(crearFilaFormulario("Idioma", idioma));
        panel.add(crearFilaFormulario("Formato", formato));
        panel.add(crearFilaFormulario("ISBN", isbn));
        return panel;
    }

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
        panel.add(crearFilaFormulario("Edad minima", edad));
        panel.add(crearFilaFormulario("Tipo", tipoJuego));
        panel.add(crearFilaFormulario("Duracion min.", duracion));
        panel.add(crearFilaFormulario("Editorial", editorial));
        panel.add(crearFilaFormulario("Idioma", idioma));
        panel.add(crearTituloFormulario("Componentes"));
        panel.add(new JScrollPane(componentes));
        return panel;
    }

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

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        return panel;
    }

    private JLabel crearTituloFormulario(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(8, 0, 8, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

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

    private void seleccionarImagenProducto(JTextField campoImagen, JLabel preview) {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileFilter(new FileNameExtensionFilter("Imagenes", "png", "jpg", "jpeg", "gif"));
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

    private String descripcionConDetalles(String tipo, String descripcion,
            JSpinner comicPaginas, JTextField comicAutor, JTextField comicEditorial,
            JComboBox<Genero> comicGenero, JSpinner comicAnio, JTextField comicIdioma,
            JTextField comicFormato, JTextField comicIsbn,
            JSpinner juegoJugadoresMin, JSpinner juegoJugadoresMax, JSpinner juegoEdad,
            JComboBox<TipoJuego> tipoJuego, JSpinner juegoDuracion, JTextField juegoEditorial,
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
            texto.append("Paginas: ").append(comicPaginas.getValue()).append('\n');
            texto.append("Autor: ").append(textoCampo(comicAutor)).append('\n');
            texto.append("Editorial: ").append(textoCampo(comicEditorial)).append('\n');
            texto.append("Genero: ").append(comicGenero.getSelectedItem()).append('\n');
            texto.append("Anio: ").append(comicAnio.getValue()).append('\n');
            texto.append("Idioma: ").append(textoCampo(comicIdioma)).append('\n');
            texto.append("Formato: ").append(textoCampo(comicFormato)).append('\n');
            texto.append("ISBN: ").append(textoCampo(comicIsbn));
        } else if ("JUEGO".equals(tipo)) {
            texto.append("Jugadores: ").append(juegoJugadoresMin.getValue()).append("-")
                    .append(juegoJugadoresMax.getValue()).append('\n');
            texto.append("Edad minima: ").append(juegoEdad.getValue()).append('\n');
            texto.append("Tipo: ").append(tipoJuego.getSelectedItem()).append('\n');
            texto.append("Duracion: ").append(juegoDuracion.getValue()).append(" min\n");
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
            texto.append("Articulada: ").append(figuraArticulada.isSelected() ? "si" : "no");
        }
        return texto.toString();
    }

    private String textoCampo(JTextField campo) {
        return campo.getText() == null || campo.getText().isBlank() ? "No indicado" : campo.getText().trim();
    }

    private String textoArea(JTextArea campo) {
        return campo.getText() == null || campo.getText().isBlank() ? "No indicado" : campo.getText().trim();
    }

    private void editarProducto(ProductoTienda producto) {
        JTextField precio = new JTextField(String.format("%.2f", producto.getPrecio()).replace(',', '.'));
        JSpinner stock = new JSpinner(new SpinnerNumberModel(mainFrame.getStock().getNumProductos(producto), 0, 9999, 1));
        JTextArea descripcion = new JTextArea(producto.getDescripcion(), 5, 28);
        JTextField imagen = new JTextField(producto.getImagen() == null ? "" : producto.getImagen());
        JTextField categorias = new JTextField(String.join(", ", producto.getCategoriasTexto()));

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Precio"));
        panel.add(precio);
        panel.add(new JLabel("Unidades de stock"));
        panel.add(stock);
        panel.add(new JLabel("Descripcion"));
        panel.add(new JScrollPane(descripcion));
        panel.add(new JLabel("Imagen"));
        panel.add(imagen);
        panel.add(new JLabel("Categorias separadas por coma"));
        panel.add(categorias);

        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                "Editar " + producto.getNombre(), JOptionPane.OK_CANCEL_OPTION);
        if (respuesta == JOptionPane.OK_OPTION) {
            mainFrame.editarProductoTienda(producto,
                    parseDouble(precio.getText(), producto.getPrecio()),
                    ((Integer) stock.getValue()).intValue(),
                    descripcion.getText(),
                    imagen.getText(),
                    parseCategorias(categorias.getText()));
        }
    }

    private void editarPack(Pack pack) {
        JTextField nombre = new JTextField(pack == null ? "" : pack.getNombre());
        nombre.setEnabled(pack == null);
        JTextField precio = new JTextField(pack == null ? "0.00"
                : String.format("%.2f", pack.getPrecio()).replace(',', '.'));
        JPanel productos = new JPanel(new GridLayout(0, 1, 4, 4));
        List<JCheckBox> checks = new ArrayList<>();
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            JCheckBox check = new JCheckBox(producto.getNombre());
            check.putClientProperty("producto", producto);
            check.setSelected(pack != null && pack.getProductos().contains(producto));
            checks.add(check);
            productos.add(check);
        }

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel datos = new JPanel(new GridLayout(0, 1, 6, 6));
        datos.add(new JLabel("Nombre"));
        datos.add(nombre);
        datos.add(new JLabel("Precio"));
        datos.add(precio);
        panel.add(datos, BorderLayout.NORTH);
        panel.add(new JScrollPane(productos), BorderLayout.CENTER);

        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                pack == null ? "Nuevo pack" : "Modificar pack", JOptionPane.OK_CANCEL_OPTION);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        List<ProductoTienda> seleccionados = new ArrayList<>();
        for (JCheckBox check : checks) {
            if (check.isSelected()) {
                seleccionados.add((ProductoTienda) check.getClientProperty("producto"));
            }
        }
        if (pack == null) {
            mainFrame.crearPackGestion(nombre.getText(), parseDouble(precio.getText(), 0.0), seleccionados);
        } else {
            mainFrame.modificarPackGestion(pack, parseDouble(precio.getText(), pack.getPrecio()), seleccionados);
        }
    }

    private void valorarProductoSegundaMano(ProductoSegundaMano producto) {
        if (producto.getEstaValorado()) {
            JOptionPane.showMessageDialog(this,
                    "Este producto ya fue valorado y no puede modificarse.",
                    "Valoracion bloqueada", JOptionPane.WARNING_MESSAGE);
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
        panel.add(new JLabel("Estado de conservacion"));
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

    private void avanzarPedido(Pedido pedido) {
        if (pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
            mainFrame.prepararPedido(pedido);
        } else if (pedido.getEstadoPedido() == EstadoPedido.LISTO) {
            mainFrame.entregarPedido(pedido);
        }
    }

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

    private int totalUnidadesStock() {
        int total = 0;
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            total += mainFrame.getStock().getNumProductos(producto);
        }
        return total;
    }

    private int contarPedidos(EstadoPedido estado) {
        int total = 0;
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == estado) {
                total++;
            }
        }
        return total;
    }

    private int contarIntercambiosPendientes() {
        int total = 0;
        for (Intercambio intercambio : mainFrame.getIntercambios()) {
            if (intercambio.getOferta().getEstadoOferta() == EstadoOferta.PENDIENTE) {
                total++;
            }
        }
        return total;
    }

    private double totalVentasEntregadas() {
        double total = 0.0;
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                total += pedido.calcularPrecioTotal();
            }
        }
        return total;
    }

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

    private Date fechaEstadisticaPedido(Pedido pedido) {
        if (pedido.getFechaRecogida() != null) {
            return pedido.getFechaRecogida();
        }
        if (pedido.getFechaPago() != null) {
            return pedido.getFechaPago();
        }
        return pedido.getFechaRealizacion();
    }

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

    private double totalValoraciones() {
        double total = 0.0;
        for (ProductoSegundaMano producto : mainFrame.getProductosSegundaManoGestion()) {
            total += producto.getValorEstimado();
        }
        return total;
    }

    private boolean tieneDescuento(ProductoTienda producto) {
        return producto.getRebajaPorcentaje() > 0
                || producto.getRebajaFija() > 0
                || producto.isTiene2x1();
    }

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

    private String textoCategorias(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        if (!categorias.isEmpty()) {
            return String.join(", ", categorias);
        }
        return producto.getCategoria() == null ? "sin categorias" : producto.getCategoria().getNombre();
    }

    private String textoPermisos(Empleado empleado) {
        return empleado.getPermisos().isEmpty() ? "sin permisos" : empleado.getPermisos().toString();
    }

    private String resumenPedido(Pedido pedido) {
        return "<b>" + pedido.getEstadoPedido() + "</b> | " + pedido.getCliente().getNombre()
                + " | " + resumenProductosPedido(pedido);
    }

    private String textoProductoSegundaMano(ProductoSegundaMano producto) {
        String valor = producto.getEstaValorado()
                ? String.format("%.2f EUR | %s", producto.getValorEstimado(), producto.getEstadoConservacion())
                : "pendiente de valorar";
        return "<b>" + producto.getNombre() + "</b><br>Propietario: "
                + producto.getPropietario().getNombre() + " | " + valor;
    }

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

    private String resumenPack(Pack pack) {
        StringBuilder texto = new StringBuilder();
        for (Producto producto : pack.getProductos()) {
            if (texto.length() > 0) {
                texto.append(" + ");
            }
            texto.append(producto.getNombre());
        }
        return texto.length() == 0 ? "sin productos" : texto.toString();
    }

    private List<String> parseCategorias(String texto) {
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

    private JPanel crearTarjeta() {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(16, 16, 16, 16));
        tarjeta.setBackground(UiStyle.COLOR_TARJETA);
        return tarjeta;
    }

    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(12, 0, 10, 0));
        return titulo;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(0, 0, 16, 0));
        return label;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private JButton crearBoton(String texto, int ancho) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(ancho, 34));
        boton.setMaximumSize(new Dimension(ancho, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_MARRON_MEDIO, 1));
        return boton;
    }

    private static class GraficaVentasMensuales extends JPanel {

        private static final long serialVersionUID = 1L;

        private final Map<String, Double> ventas;

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

    private static class BarraProgreso extends JPanel {

        private static final long serialVersionUID = 1L;

        private final int valor;
        private final int max;
        private final Color color;

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
