package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import compras.Pedido;
import intercambios.Intercambio;
import productos.Pack;
import productos.Producto;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import usuarios.ClienteRegistrado;
import usuarios.Empleado;
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
    private static final String PACKS = "PACKS";
    private static final String OPERATIVA = "OPERATIVA";
    private static final String ESTADISTICAS = "ESTADISTICAS";
    private static final String DETALLE_PRODUCTO = "DETALLE_PRODUCTO";

    private final Main mainFrame;
    private final JPanel contenido;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private String seccionActiva;
    private ProductoTienda productoSeleccionado;

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

        JPanel lista = new JPanel(new GridLayout(0, 1, 10, 10));
        lista.setOpaque(false);
        for (Empleado empleado : mainFrame.getEmpleados()) {
            lista.add(crearFilaEmpleado(empleado));
        }
        contenido.add(lista);
    }

    private JPanel crearFilaEmpleado(Empleado empleado) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(12, 16, 12, 16));
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
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_CABECERA, 12);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(12, 12, 12, 12));
        tarjeta.setPreferredSize(new Dimension(168, 200));
        tarjeta.setMaximumSize(new Dimension(168, 200));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.add(crearImagenProducto(producto, 94, 126));
        tarjeta.add(Box.createVerticalStrut(6));
        JLabel nombre = new JLabel(producto.getNombre(), SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nombre.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        nombre.setPreferredSize(new Dimension(144, 22));
        nombre.setMaximumSize(new Dimension(144, 22));
        tarjeta.add(nombre);
        JButton editar = new UiStyle.RoundedButton("Editar", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        editar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        editar.setAlignmentX(Component.CENTER_ALIGNMENT);
        editar.setPreferredSize(new Dimension(146, 22));
        editar.setMaximumSize(new Dimension(146, 22));
        editar.addActionListener(e -> editarProducto(producto));
        tarjeta.add(editar);
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                productoSeleccionado = producto;
                seccionActiva = DETALLE_PRODUCTO;
                refrescar();
            }
        });
        return tarjeta;
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
        contenido.setBorder(new EmptyBorder(24, 28, 24, 28));
        JPanel cuerpo = new JPanel(new BorderLayout(28, 0));
        cuerpo.setOpaque(false);
        cuerpo.add(crearDetalleIzquierdo(productoSeleccionado), BorderLayout.WEST);
        cuerpo.add(crearDetalleDerecho(productoSeleccionado), BorderLayout.CENTER);
        contenido.add(cuerpo);
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
        JSpinner porcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner fija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, 1.0));
        JCheckBox dosPorUno = new JCheckBox("2x1");
        panel.add(productos);
        panel.add(crearLineaCampos("Porcentaje", porcentaje, "Rebaja fija", fija, dosPorUno));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        acciones.setOpaque(false);
        JButton aplicar = crearBoton("Aplicar", 110);
        aplicar.addActionListener(e -> mainFrame.aplicarDescuentoProducto(
                (ProductoTienda) productos.getSelectedItem(),
                ((Double) porcentaje.getValue()).doubleValue(),
                ((Double) fija.getValue()).doubleValue(),
                dosPorUno.isSelected()));
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
        JSpinner porcentaje = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 100.0, 1.0));
        JSpinner fija = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 999.0, 1.0));
        JCheckBox dosPorUno = new JCheckBox("2x1");
        panel.add(crearEtiqueta("Categoria o texto de categoria"));
        panel.add(categoria);
        panel.add(crearLineaCampos("Porcentaje", porcentaje, "Rebaja fija", fija, dosPorUno));
        JButton aplicar = crearBoton("Aplicar a categoria", 180);
        aplicar.addActionListener(e -> {
            int total = mainFrame.aplicarDescuentoCategoria(categoria.getText(),
                    ((Double) porcentaje.getValue()).doubleValue(),
                    ((Double) fija.getValue()).doubleValue(),
                    dosPorUno.isSelected());
            JOptionPane.showMessageDialog(this, "Productos actualizados: " + total);
        });
        panel.add(aplicar);
        return panel;
    }

    private JPanel crearLineaCampos(String etiqueta1, JSpinner spinner1, String etiqueta2,
            JSpinner spinner2, JCheckBox check) {
        JPanel linea = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        linea.setOpaque(false);
        linea.add(new JLabel(etiqueta1));
        linea.add(spinner1);
        linea.add(new JLabel(etiqueta2));
        linea.add(spinner2);
        linea.add(check);
        return linea;
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
            fila.setBorder(new EmptyBorder(12, 16, 12, 16));
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

    private JPanel crearFilaPedido(Pedido pedido) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(12, 16, 12, 16));
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
        fila.setBorder(new EmptyBorder(12, 16, 12, 16));
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
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);
        grid.add(crearMetrica("Ventas", String.format("%.2f", totalVentasEntregadas()), "EUR entregados"));
        grid.add(crearMetrica("Valoraciones", String.format("%.2f", totalValoraciones()), "EUR estimados"));
        grid.add(crearMetrica("Clientes", String.valueOf(mainFrame.getClientesRegistrados().size()), "registrados"));
        contenido.add(grid);

        contenido.add(crearTitulo("Pedidos por estado"));
        for (EstadoPedido estado : EstadoPedido.values()) {
            contenido.add(crearEtiqueta(estado + ": " + contarPedidos(estado)));
        }

        contenido.add(crearTitulo("Usuarios con mas compras"));
        List<ClienteRegistrado> clientes = new ArrayList<>(mainFrame.getClientesRegistrados());
        clientes.sort(Comparator.comparingInt((ClienteRegistrado c) -> c.getPedidos().size()).reversed());
        for (ClienteRegistrado cliente : clientes) {
            contenido.add(crearEtiqueta(cliente.getNombre() + " | DNI " + cliente.getDNI()
                    + " | compras " + cliente.getPedidos().size()
                    + " | cartera " + cliente.getCartera().getNumProductos()));
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

        List<Producto> seleccionados = new ArrayList<>();
        for (JCheckBox check : checks) {
            if (check.isSelected()) {
                seleccionados.add((Producto) check.getClientProperty("producto"));
            }
        }
        if (pack == null) {
            mainFrame.crearPackGestion(nombre.getText(), parseDouble(precio.getText(), 0.0), seleccionados);
        } else {
            mainFrame.modificarPackGestion(pack, parseDouble(precio.getText(), pack.getPrecio()), seleccionados);
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
}
