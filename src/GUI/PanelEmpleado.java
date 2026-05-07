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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
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
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import compras.Pedido;
import intercambios.Intercambio;
import productos.Pack;
import productos.Producto;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import usuarios.Empleado;
import utilidades.EstadoConservacion;
import utilidades.EstadoOferta;
import utilidades.EstadoPedido;
import utilidades.TiposEmpleado;

/**
 * Employee workspace. Visible actions depend on permissions assigned by the manager.
 */
public class PanelEmpleado extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel contenido;
    private String seccionActiva;
    private ProductoTienda productoSeleccionado;
    private boolean editandoProducto;
    private String seccionAnteriorProductos;

    public PanelEmpleado(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.contenido = new JPanel();
        this.seccionActiva = "HOME";
        this.seccionAnteriorProductos = "HOME";

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
        contenido.setBorder(new EmptyBorder(22, 34, 22, 34));

        Empleado empleado = mainFrame.getEmpleadoActual();
        if (empleado == null) {
            contenido.add(crearTitulo("Panel empleado"));
            contenido.add(crearEtiqueta("Inicia sesion con un empleado creado por el gestor."));
            actualizar();
            return;
        }

        if ("DETALLE_PRODUCTO".equals(seccionActiva) && productoSeleccionado != null
                && empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)) {
            pintarDetalleProducto();
        } else if ("STOCK".equals(seccionActiva) && empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)) {
            pintarStock();
        } else if ("PEDIDOS".equals(seccionActiva) && empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PEDIDO)) {
            pintarPedidos();
        } else if ("INTERCAMBIOS".equals(seccionActiva)
                && empleado.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)) {
            pintarIntercambios();
        } else if ("SEGUNDA_MANO".equals(seccionActiva) && empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)) {
            pintarSegundaMano();
        } else if ("PACKS".equals(seccionActiva) && empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)) {
            pintarPacks();
        } else {
            pintarHome(empleado);
        }

        actualizar();
    }

    private JPanel crearCabecera() {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(UiStyle.COLOR_CABECERA);
        cabecera.setPreferredSize(new Dimension(0, 50));
        cabecera.setBorder(new EmptyBorder(3, 14, 3, 12));

        JPanel izquierda = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izquierda.setOpaque(false);
        JButton menu = crearBotonIcono("\u2630", "Abrir menu", 32, 48);
        menu.addActionListener(e -> mostrarMenuLateral(menu));
        izquierda.add(menu);
        cabecera.add(izquierda, BorderLayout.WEST);

        JLabel titulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 34));
        titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        cabecera.add(titulo, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        derecha.setOpaque(false);
        derecha.add(UiStyle.crearBotonImagen(UiStyle.ICONO_NOTIFICACIONES, "", "Notificaciones", 42, 40, 30));
        derecha.add(UiStyle.crearBotonImagen(UiStyle.ICONO_PERFIL_CABRA, "", "Empleado", 42, 40, 32));
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

    private void mostrarMenuLateral(JButton origen) {
        Empleado empleado = mainFrame.getEmpleadoActual();
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(UiStyle.COLOR_CABECERA);
        menu.setBorder(new EmptyBorder(8, 8, 8, 8));

        menu.add(crearItemMenu("HOME", "HOME", true));
        menu.add(crearItemMenu("STOCK", "STOCK",
                empleado == null || empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)));
        menu.add(crearItemMenu("SEGUNDA MANO", "SEGUNDA_MANO",
                empleado == null || empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)));
        menu.add(crearItemMenu("GEST. PEDIDOS", "PEDIDOS",
                empleado == null || empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PEDIDO)));
        menu.add(crearItemMenu("PACKS", "PACKS",
                empleado == null || empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)));
        menu.add(crearItemMenu("GEST. INTERCAMBIOS", "INTERCAMBIOS",
                empleado == null || empleado.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO)));
        menu.addSeparator();
        JMenuItem salir = crearItemMenu("CERRAR SESION", seccionActiva, true);
        salir.addActionListener(e -> mainFrame.cerrarSesion());
        menu.add(salir);

        menu.show(origen, 0, origen.getHeight() + 6);
    }

    private JMenuItem crearItemMenu(String texto, String seccion, boolean habilitado) {
        JMenuItem item = new JMenuItem(texto);
        item.setOpaque(true);
        item.setBackground(seccion.equals(seccionActiva) ? UiStyle.COLOR_TEXTO : UiStyle.COLOR_CABECERA);
        item.setForeground(habilitado ? UiStyle.COLOR_TEXTO_CLARO : new Color(215, 205, 192));
        item.setFont(new Font("SansSerif", Font.BOLD, 14));
        item.setBorder(new EmptyBorder(8, 16, 8, 68));
        item.setEnabled(habilitado);
        item.addActionListener(e -> {
            seccionActiva = seccion;
            refrescar();
        });
        return item;
    }

    private void pintarHome(Empleado empleado) {
        contenido.setBorder(new EmptyBorder(34, 96, 34, 96));
        JPanel grid = new JPanel(new GridLayout(0, 3, 92, 44));
        grid.setOpaque(false);
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            grid.add(crearTarjetaProducto(producto, empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)));
        }
        contenido.add(grid);
    }

    private JPanel crearTarjetaProducto(ProductoTienda producto, boolean editable) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setOpaque(false);
        TarjetaProducto vistaCliente = new TarjetaProducto(producto);
        vistaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                abrirDetalleProducto(producto, false);
            }
        });
        tarjeta.add(vistaCliente, BorderLayout.CENTER);
        JButton editar = crearBoton("Editar", 120);
        editar.setEnabled(editable);
        editar.addActionListener(e -> abrirDetalleProducto(producto, true));
        tarjeta.add(editar, BorderLayout.SOUTH);
        return tarjeta;
    }

    private void abrirDetalleProducto(ProductoTienda producto, boolean editar) {
        productoSeleccionado = producto;
        editandoProducto = editar;
        if (!"DETALLE_PRODUCTO".equals(seccionActiva)) {
            seccionAnteriorProductos = seccionActiva;
        }
        seccionActiva = "DETALLE_PRODUCTO";
        refrescar();
    }

    private JLabel crearImagenProducto(ProductoTienda producto) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(126, 154));
        label.setMinimumSize(new Dimension(126, 154));
        label.setMaximumSize(new Dimension(126, 154));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

        String ruta = producto.getImagen();
        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(126, 154, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imagen));
        } else {
            label.setText("<html><center>SIN<br>IMAGEN</center></html>");
            label.setOpaque(true);
            label.setBackground(UiStyle.COLOR_MARRON_MEDIO);
            label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        }
        return label;
    }

    private JLabel crearNombreProducto(String nombre) {
        String texto = nombre.length() > 26 ? nombre.substring(0, 23) + "..." : nombre;
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setPreferredSize(new Dimension(190, 38));
        label.setMinimumSize(new Dimension(190, 38));
        label.setMaximumSize(new Dimension(190, 38));
        return label;
    }

    private void pintarStock() {
        contenido.add(crearTitulo("STOCK"));
        JButton cargar = crearBoton("Cargar productos de fichero", 220);
        cargar.addActionListener(e -> cargarProductosDeFichero());
        contenido.add(cargar);
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            JPanel tarjeta = new JPanel(new BorderLayout(0, 6));
            tarjeta.setOpaque(false);
            TarjetaProducto vistaCliente = new TarjetaProducto(producto);
            vistaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    abrirDetalleProducto(producto, false);
                }
            });
            tarjeta.add(vistaCliente, BorderLayout.CENTER);
            tarjeta.add(crearEtiqueta(String.format("%d unidades en stock",
                    mainFrame.getStock().getNumProductos(producto))), BorderLayout.NORTH);
            JButton editar = crearBoton("Editar producto", 150);
            editar.addActionListener(e -> abrirDetalleProducto(producto, true));
            tarjeta.add(editar, BorderLayout.SOUTH);
            grid.add(tarjeta);
        }
        contenido.add(grid);
    }

    private JPanel crearFilaValoracion(ProductoSegundaMano producto) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(22, 14, 22, 14));
        fila.setPreferredSize(new Dimension(0, 86));
        fila.setMinimumSize(new Dimension(0, 86));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        fila.add(crearEtiqueta(textoProductoSegundaMano(producto)), BorderLayout.CENTER);
        JButton valorar = crearBoton(producto.getEstaValorado() ? "Valorado" : "Valorar", 120);
        valorar.setEnabled(!producto.getEstaValorado());
        valorar.addActionListener(e -> valorarProducto(producto));
        fila.add(valorar, BorderLayout.EAST);
        return fila;
    }

    private void pintarSegundaMano() {
        contenido.add(crearTitulo("SEGUNDA MANO"));
        contenido.add(crearSubtitulo("Productos de clientes pendientes de valoracion o ya valorados."));
        List<ProductoSegundaMano> productos = mainFrame.getProductosSegundaManoGestion();
        if (productos.isEmpty()) {
            contenido.add(crearEtiqueta("No hay productos de segunda mano."));
            return;
        }
        for (ProductoSegundaMano producto : productos) {
            contenido.add(crearFilaValoracion(producto));
        }
    }

    private void pintarPedidos() {
        contenido.add(crearTitulo("GEST. PEDIDOS"));
        contenido.add(crearSubtitulo("Pedidos en preparacion"));
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
                contenido.add(crearFilaPedido(pedido, "Cambiar a listo"));
            }
        }
        contenido.add(crearSubtitulo("Listos"));
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.LISTO) {
                contenido.add(crearFilaPedido(pedido, "Cambiar a entregado"));
            }
        }
    }

    private JPanel crearFilaPedido(Pedido pedido, String accion) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(22, 14, 22, 14));
        fila.setPreferredSize(new Dimension(0, 86));
        fila.setMinimumSize(new Dimension(0, 86));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        fila.add(crearEtiqueta(resumenPedido(pedido)), BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        acciones.setOpaque(false);
        JButton info = crearBoton("Info", 80);
        info.addActionListener(e -> mostrarInfoPedido(pedido));
        acciones.add(info);
        JButton boton = crearBoton(accion, 160);
        boton.addActionListener(e -> {
            if (pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
                mainFrame.prepararPedido(pedido);
            } else if (pedido.getEstadoPedido() == EstadoPedido.LISTO) {
                mainFrame.entregarPedido(pedido);
            }
        });
        acciones.add(boton);
        fila.add(acciones, BorderLayout.EAST);
        return fila;
    }

    private void pintarIntercambios() {
        contenido.add(crearTitulo("INTERCAMBIOS"));
        contenido.add(crearSubtitulo("Todas las ofertas de la tienda. Las aceptadas pueden marcarse como intercambiadas."));
        for (Intercambio intercambio : mainFrame.getIntercambios()) {
            JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
            fila.setLayout(new BorderLayout(10, 0));
            fila.setBorder(new EmptyBorder(22, 14, 22, 14));
            fila.setPreferredSize(new Dimension(0, 86));
            fila.setMinimumSize(new Dimension(0, 86));
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
            String texto = intercambio.getOferta().getProductoOfertado().getNombre()
                    + " por " + intercambio.getOferta().getProductoDeseado().getNombre()
                    + " | " + intercambio.getOferta().getEstadoOferta();
            fila.add(crearEtiqueta(texto), BorderLayout.CENTER);
            JButton marcar = crearBoton("Marcar realizado", 160);
            marcar.setEnabled(!intercambio.getIntercambiado()
                    && intercambio.getOferta().getEstadoOferta() == EstadoOferta.ACEPTADA);
            marcar.addActionListener(e -> mainFrame.marcarIntercambioRealizado(intercambio));
            fila.add(marcar, BorderLayout.EAST);
            contenido.add(fila);
        }
    }

    private void pintarPacks() {
        contenido.add(crearTitulo("PACKS"));
        JButton nuevo = crearBoton("Nuevo pack", 140);
        nuevo.addActionListener(e -> editarPack(null));
        contenido.add(nuevo);
        for (Pack pack : mainFrame.getPacks()) {
            JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
            fila.setLayout(new BorderLayout(10, 0));
            fila.setBorder(new EmptyBorder(22, 14, 22, 14));
            fila.setPreferredSize(new Dimension(0, 86));
            fila.setMinimumSize(new Dimension(0, 86));
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
            fila.add(crearEtiqueta(pack.getNombre() + " | " + resumenPack(pack)
                    + " = " + String.format("%.2f EUR", pack.getPrecio())), BorderLayout.CENTER);
            JButton editar = crearBoton("Modificar", 120);
            editar.addActionListener(e -> editarPack(pack));
            fila.add(editar, BorderLayout.EAST);
            contenido.add(fila);
        }
    }

    private void editarProducto(ProductoTienda producto) {
        abrirDetalleProducto(producto, true);
    }

    private void pintarDetalleProducto() {
        contenido.setBorder(new EmptyBorder(8, 14, 24, 34));
        contenido.add(crearBarraVolverProductos());
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

    private JPanel crearBarraVolverProductos() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton volver = crearBotonFlechaVolver();
        volver.addActionListener(e -> volverAListadoProductos());
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

    private void volverAListadoProductos() {
        editandoProducto = false;
        productoSeleccionado = null;
        seccionActiva = seccionAnteriorProductos;
        refrescar();
    }

    private void cargarProductosDeFichero() {
        JFileChooser chooser = new JFileChooser(".");
        int respuesta = chooser.showOpenDialog(this);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            mainFrame.recargarCatalogoDesdeFichero(chooser.getSelectedFile().getPath());
        }
    }

    private void mostrarInfoPedido(Pedido pedido) {
        JOptionPane.showMessageDialog(this,
                "Cliente: " + pedido.getCliente().getNombre()
                        + "\nDNI: " + pedido.getCliente().getDNI()
                        + "\nCodigo: " + pedido.getCodigo().getCodigo()
                        + "\nEstado: " + pedido.getEstadoPedido()
                        + "\nProductos: " + resumenPedido(pedido)
                        + "\nTotal: " + String.format("%.2f EUR", pedido.calcularPrecioTotal()),
                "Pedido", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarPack(Pack pack) {
        JTextField nombre = new JTextField(pack == null ? "" : pack.getNombre());
        nombre.setEnabled(pack == null);
        JTextField precio = new JTextField(pack == null ? "0.00" : String.format("%.2f", pack.getPrecio()).replace(',', '.'));
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

    private void valorarProducto(ProductoSegundaMano producto) {
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
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.add(crearImagenProductoSegundaMano(producto, 220, 160), BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 6));
        campos.add(new JLabel("Precio estimado"));
        campos.add(precio);
        campos.add(new JLabel("Estado de conservacion"));
        campos.add(conservacion);
        panel.add(campos, BorderLayout.CENTER);
        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                "Valorar " + producto.getNombre(),
                JOptionPane.OK_CANCEL_OPTION);
        if (respuesta == JOptionPane.OK_OPTION) {
            mainFrame.valorarProductoSegundaMano(producto,
                    ((Double) precio.getValue()).doubleValue(),
                    (EstadoConservacion) conservacion.getSelectedItem());
        }
    }

    private JLabel crearImagenProductoSegundaMano(ProductoSegundaMano producto, int ancho, int alto) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(ancho, alto));
        label.setMinimumSize(new Dimension(ancho, alto));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(UiStyle.COLOR_CABECERA);
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 2));

        String ruta = producto.getImagen();
        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imagen));
            return label;
        }

        label.setText("SIN IMAGEN");
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        return label;
    }

    private String resumenPedido(Pedido pedido) {
        StringBuilder productos = new StringBuilder();
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            if (productos.length() > 0) {
                productos.append(", ");
            }
            productos.append(entry.getKey().getNombre()).append(" x").append(entry.getValue());
        }
        return pedido.getEstadoPedido() + " | " + productos;
    }

    private String textoProductoSegundaMano(ProductoSegundaMano producto) {
        String valor = producto.getEstaValorado()
                ? String.format("%.2f EUR | %s", producto.getValorEstimado(), producto.getEstadoConservacion())
                : "pendiente de valorar";
        return "<b>" + producto.getNombre() + "</b> | propietario: "
                + producto.getPropietario().getNombre() + " | " + valor;
    }

    private String resumenPack(Pack pack) {
        StringBuilder texto = new StringBuilder();
        for (Producto producto : pack.getProductos()) {
            if (texto.length() > 0) {
                texto.append(" + ");
            }
            texto.append(producto.getNombre());
        }
        return texto.toString();
    }

    private String textoCategorias(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        return categorias.isEmpty() ? "sin categorias" : String.join(", ", categorias);
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
        tarjeta.setBorder(new EmptyBorder(14, 14, 14, 14));
        return tarjeta;
    }

    private JScrollPane crearScroll() {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(14, 0, 10, 0));
        return titulo;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(0, 0, 18, 0));
        return label;
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private JButton crearBoton(String texto, int ancho) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(ancho, 34));
        boton.setMaximumSize(new Dimension(ancho, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void actualizar() {
        contenido.revalidate();
        contenido.repaint();
    }
}
