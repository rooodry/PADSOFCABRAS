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

import javax.swing.Box;
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
 * Representa el componente PanelEmpleado de la interfaz grafica.
 */
public class PanelEmpleado extends JPanel {

    private static final long serialVersionUID = 1L;

    /**      * Estado interno de mainFrame.      */
    private final Main mainFrame;
    /**      * Estado interno de contenido.      */
    private final JPanel contenido;
    /**      * Estado interno de seccionActiva.      */
    private String seccionActiva;
    /**      * Estado interno de productoSeleccionado.      */
    private ProductoTienda productoSeleccionado;
    /**      * Estado interno de editandoProducto.      */
    private boolean editandoProducto;
    /**      * Estado interno de seccionAnteriorProductos.      */
    private String seccionAnteriorProductos;

    /**
     * Construye una instancia de PanelEmpleado.
     * @param mainFrame parametro utilizado por la operacion
     */
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

    /**
     * Ejecuta la operacion publica refrescar.
     */
    public void refrescar() {
        contenido.removeAll();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(UiStyle.COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(22, 34, 22, 34));

        Empleado empleado = mainFrame.getEmpleadoActual();
        if (empleado == null) {
            contenido.add(crearTitulo("Panel empleado"));
            contenido.add(crearEtiqueta("Inicia sesión con un empleado creado por el gestor."));
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
        } else if ("PERFIL".equals(seccionActiva)) {
            pintarPerfilEmpleado(empleado);
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
        JButton perfil = UiStyle.crearBotonImagen(UiStyle.ICONO_PERFIL_CABRA, "", "Perfil", 42, 40, 32);
        perfil.addActionListener(e -> {
            seccionActiva = "PERFIL";
            refrescar();
        });
        derecha.add(perfil);
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
        menu.add(crearItemMenu("PERFIL", "PERFIL", true));
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
        JMenuItem salir = crearItemMenu("CERRAR SESIÓN", seccionActiva, true);
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
            /**
             * Ejecuta la operacion publica mouseClicked.
             * @param e parametro utilizado por la operacion
             */
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

    private void pintarPerfilEmpleado(Empleado empleado) {
        contenido.setBorder(new EmptyBorder(34, 74, 34, 74));
        contenido.add(crearTitulo("PERFIL DE EMPLEADO"));

        JPanel layout = new JPanel(new BorderLayout(24, 0));
        layout.setOpaque(false);
        layout.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel datos = crearTarjeta();
        datos.setPreferredSize(new Dimension(420, 0));
        datos.add(crearAvatarEmpleado(empleado));
        datos.add(Box.createVerticalStrut(14));
        datos.add(crearDatoPerfil("Usuario", empleado.getNombre()));
        datos.add(crearDatoPerfil("Contraseña", empleado.getContraseña()));
        datos.add(crearDatoPerfil("Foto de perfil", textoVacio(empleado.getFotoPerfil())));
        datos.add(crearDatoPerfil("Notificaciones", String.valueOf(empleado.getNotificaciones().size())));
        datos.add(crearDatoPerfil("Productos para valorar", String.valueOf(empleado.getProductosParaValorar().size())));
        datos.add(crearDatoPerfil("Intercambios asignados", String.valueOf(empleado.getIntercambios().size())));

        JPanel permisos = crearTarjeta();
        permisos.add(crearSubtitulo("Permisos"));
        for (TiposEmpleado permiso : TiposEmpleado.values()) {
            permisos.add(crearFilaPermiso(permiso, empleado.tienePermiso(permiso)));
            permisos.add(Box.createVerticalStrut(10));
        }
        permisos.add(Box.createVerticalGlue());

        layout.add(datos, BorderLayout.WEST);
        layout.add(permisos, BorderLayout.CENTER);
        contenido.add(layout);
    }

    private JLabel crearAvatarEmpleado(Empleado empleado) {
        JLabel avatar = new JLabel("", SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(180, 180));
        avatar.setMinimumSize(new Dimension(180, 180));
        avatar.setMaximumSize(new Dimension(180, 180));
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        avatar.setOpaque(true);
        avatar.setBackground(UiStyle.COLOR_CABECERA);
        avatar.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        avatar.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 2));
        avatar.setFont(new Font("SansSerif", Font.BOLD, 42));

        String ruta = empleado.getFotoPerfil();
        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
            avatar.setIcon(new ImageIcon(imagen));
            return avatar;
        }

        avatar.setText(inicialesEmpleado(empleado));
        return avatar;
    }

    private JPanel crearDatoPerfil(String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout(12, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fila.setBorder(new EmptyBorder(4, 0, 4, 0));

        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setPreferredSize(new Dimension(150, 28));
        fila.add(label, BorderLayout.WEST);

        JLabel dato = new JLabel(valor == null ? "" : valor);
        dato.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dato.setForeground(UiStyle.COLOR_TEXTO);
        fila.add(dato, BorderLayout.CENTER);
        return fila;
    }

    private JPanel crearFilaPermiso(TiposEmpleado permiso, boolean activo) {
        JPanel fila = new UiStyle.RoundedPanel(activo ? UiStyle.COLOR_FONDO : new Color(230, 222, 212), 14);
        fila.setLayout(new BorderLayout(12, 0));
        fila.setBorder(new EmptyBorder(12, 14, 12, 14));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));

        JLabel estado = new JLabel(activo ? "\u2713" : "\u2013", SwingConstants.CENTER);
        estado.setFont(new Font("SansSerif", Font.BOLD, 22));
        estado.setForeground(activo ? UiStyle.COLOR_TEXTO : new Color(138, 122, 106));
        estado.setPreferredSize(new Dimension(34, 34));
        fila.add(estado, BorderLayout.WEST);

        JLabel texto = new JLabel(textoPermiso(permiso));
        texto.setFont(new Font("SansSerif", Font.BOLD, 14));
        texto.setForeground(UiStyle.COLOR_TEXTO);
        fila.add(texto, BorderLayout.CENTER);
        return fila;
    }

    private String textoPermiso(TiposEmpleado permiso) {
        switch (permiso) {
            case EMPLEADOS_PRODUCTO:
                return "Gestión de productos, stock, packs y segunda mano";
            case EMPLEADOS_PEDIDO:
                return "Gestion de pedidos";
            case EMPLEADOS_INTERCAMBIO:
                return "Gestion de intercambios";
            default:
                return permiso.toString();
        }
    }

    private String inicialesEmpleado(Empleado empleado) {
        String nombre = empleado.getNombre();
        if (nombre == null || nombre.isBlank()) {
            return "EMP";
        }
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    private String textoVacio(String texto) {
        return texto == null || texto.isBlank() ? "Sin dato" : texto;
    }

    private void pintarStock() {
        contenido.add(crearTitulo("STOCK"));
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        acciones.setOpaque(false);
        JButton cargar = crearBoton("Cargar productos de fichero", 220);
        cargar.addActionListener(e -> cargarProductosDeFichero());
        acciones.add(cargar);
        JButton crear = crearBoton("Crear producto", 150);
        crear.addActionListener(e -> crearProductoEmpleado());
        acciones.add(crear);
        contenido.add(acciones);
        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setOpaque(false);
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            JPanel tarjeta = new JPanel(new BorderLayout(0, 6));
            tarjeta.setOpaque(false);
            TarjetaProducto vistaCliente = new TarjetaProducto(producto);
            vistaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                /**
                 * Ejecuta la operacion publica mouseClicked.
                 * @param e parametro utilizado por la operacion
                 */
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
        contenido.add(crearSubtitulo("Productos de clientes pendientes de valoración o ya valorados."));
        List<ProductoSegundaMano> productos = mainFrame.getProductosSegundaManoGestion();
        if (productos.isEmpty()) {
            contenido.add(crearEtiqueta("No hay productos de segunda mano."));
            return;
        }
        for (ProductoSegundaMano producto : productos) {
            contenido.add(crearFilaValoracion(producto));
            contenido.add(Box.createVerticalStrut(18));
        }
    }

    private void pintarPedidos() {
        contenido.add(crearTitulo("GEST. PEDIDOS"));
        contenido.add(crearSubtitulo("Pedidos en preparación"));
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
                contenido.add(crearFilaPedido(pedido, "Cambiar a listo"));
                contenido.add(Box.createVerticalStrut(18));
            }
        }
        contenido.add(crearSubtitulo("Listos"));
        for (Pedido pedido : mainFrame.getPedidosGestion()) {
            if (pedido.getEstadoPedido() == EstadoPedido.LISTO) {
                contenido.add(crearFilaPedido(pedido, "Cambiar a entregado"));
                contenido.add(Box.createVerticalStrut(18));
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
            contenido.add(Box.createVerticalStrut(18));
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
            fila.add(crearEtiqueta(pack.getNombre() + " | Categoría: " + categoriaPack(pack) + " | " + resumenPack(pack)
                    + " = " + String.format("%.2f EUR", pack.getPrecio())), BorderLayout.CENTER);
            JButton editar = crearBoton("Modificar", 120);
            editar.addActionListener(e -> editarPack(pack));
            fila.add(editar, BorderLayout.EAST);
            contenido.add(fila);
            contenido.add(Box.createVerticalStrut(18));
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
            /**
             * Ejecuta la operacion publica confirmar.
             * @param datos parametro utilizado por la operacion
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
             * Ejecuta la operacion publica cancelar.
             */
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

    private void crearProductoEmpleado() {
        JTextField nombre = new JTextField();
        JTextField precio = new JTextField("0.00");
        JSpinner stock = new JSpinner(new SpinnerNumberModel(1, 0, 9999, 1));
        JSpinner valoracion = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        JComboBox<String> tipo = new JComboBox<>(new String[] {"COMIC", "JUEGO", "FIGURA"});
        JTextField categorias = new JTextField();
        JTextField imagen = new JTextField();
        imagen.setEditable(false);
        JTextArea descripcion = new JTextArea(5, 28);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);

        estilizarCampo(nombre);
        estilizarCampo(precio);
        estilizarCampo(categorias);
        estilizarCampo(imagen);
        descripcion.setBorder(new EmptyBorder(8, 8, 8, 8));
        descripcion.setBackground(UiStyle.COLOR_FONDO);
        descripcion.setForeground(UiStyle.COLOR_TEXTO);

        JLabel preview = new JLabel("SIN IMAGEN", SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(210, 240));
        preview.setMinimumSize(new Dimension(210, 240));
        preview.setMaximumSize(new Dimension(210, 240));
        preview.setOpaque(true);
        preview.setBackground(UiStyle.COLOR_CABECERA);
        preview.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        preview.setFont(new Font("SansSerif", Font.BOLD, 14));
        preview.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 2));

        JButton buscarImagen = crearBoton("Buscar foto", 130);
        buscarImagen.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(".");
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String ruta = chooser.getSelectedFile().getPath();
                imagen.setText(ruta);
                ImageIcon icono = new ImageIcon(ruta);
                Image escalada = icono.getImage().getScaledInstance(210, 240, Image.SCALE_SMOOTH);
                preview.setText("");
                preview.setIcon(new ImageIcon(escalada));
            }
        });

        JPanel panel = new JPanel(new BorderLayout(18, 14));
        panel.setBackground(UiStyle.COLOR_FONDO);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));
        panel.setPreferredSize(new Dimension(740, 520));

        JLabel titulo = new JLabel("Crear producto");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JPanel campos = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        campos.setLayout(new BoxLayout(campos, BoxLayout.Y_AXIS));
        campos.setBorder(new EmptyBorder(16, 18, 16, 18));
        campos.add(crearCampoFormulario("Nombre", nombre));
        campos.add(crearCampoFormulario("Tipo de producto", tipo));
        campos.add(crearCampoFormulario("Categorías", categorias));
        campos.add(crearCampoFormulario("Precio", precio));
        campos.add(crearCampoFormulario("Stock inicial", stock));
        campos.add(crearCampoFormulario("Estrellas", valoracion));
        campos.add(crearEtiquetaFormulario("Descripción"));
        campos.add(new JScrollPane(descripcion));

        JPanel imagenPanel = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        imagenPanel.setLayout(new BoxLayout(imagenPanel, BoxLayout.Y_AXIS));
        imagenPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        imagenPanel.add(crearEtiquetaFormulario("Foto"));
        imagenPanel.add(preview);
        imagenPanel.add(Box.createVerticalStrut(10));
        JPanel filaFoto = new JPanel(new BorderLayout(8, 0));
        filaFoto.setOpaque(false);
        filaFoto.add(imagen, BorderLayout.CENTER);
        filaFoto.add(buscarImagen, BorderLayout.EAST);
        imagenPanel.add(filaFoto);

        panel.add(campos, BorderLayout.CENTER);
        panel.add(imagenPanel, BorderLayout.EAST);

        int respuesta = JOptionPane.showConfirmDialog(this, panel, "Crear producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        if (nombre.getText().trim().isBlank()) {
            JOptionPane.showMessageDialog(this, "El producto necesita nombre.", "Producto",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.crearProductoTiendaGestion((String) tipo.getSelectedItem(),
                nombre.getText(),
                parseDouble(precio.getText(), 0.0),
                ((Integer) stock.getValue()).intValue(),
                ((Integer) valoracion.getValue()).intValue(),
                descripcion.getText(),
                imagen.getText(),
                parseCategorías(categorias.getText()),
                false, 0.0, 0.0,
                120, "", "", null, 2026,
                4, 8, null,
                10.0, "", "");
        refrescar();
    }

    private JPanel crearCampoFormulario(String etiqueta, Component campo) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.setBorder(new EmptyBorder(4, 0, 8, 0));
        JLabel label = crearEtiquetaFormulario(etiqueta);
        label.setPreferredSize(new Dimension(150, 28));
        fila.add(label, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        return fila;
    }

    private JLabel crearEtiquetaFormulario(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private void estilizarCampo(JTextField campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 1),
                new EmptyBorder(6, 8, 6, 8)));
        campo.setBackground(UiStyle.COLOR_FONDO);
        campo.setForeground(UiStyle.COLOR_TEXTO);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
        JTextField categoria = new JTextField(pack == null ? "" : pack.getCategoria());
        JTextField precio = new JTextField(pack == null ? "0.00" : String.format("%.2f", pack.getPrecio()).replace(',', '.'));
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

    private void valorarProducto(ProductoSegundaMano producto) {
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
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.add(crearImagenProductoSegundaMano(producto, 220, 160), BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(0, 1, 6, 6));
        campos.add(new JLabel("Precio estimado"));
        campos.add(precio);
        campos.add(new JLabel("Estado de conservación"));
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
        Map<ProductoTienda, Integer> cantidades = new java.util.LinkedHashMap<>();
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
        return texto.toString();
    }

    private String categoriaPack(Pack pack) {
        String categoria = pack.getCategoria();
        return categoria == null || categoria.isBlank() ? "sin categoría" : categoria;
    }

    private String categoriaProducto(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        if (!categorias.isEmpty()) {
            return String.join(", ", categorias);
        }
        return producto.getCategoria() == null ? "sin categoría" : producto.getCategoria().getNombre();
    }

    private String textoCategorías(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        return categorias.isEmpty() ? "sin categorías" : String.join(", ", categorias);
    }

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
