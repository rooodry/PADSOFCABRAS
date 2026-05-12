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
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import usuarios.Empleado;
import utilidades.EstadoConservacion;
import utilidades.EstadoOferta;
import utilidades.EstadoPedido;
import utilidades.TiposEmpleado;

/**
 * Representa el componente PanelEmpleado de la interfaz grafica.
 * Gestiona la vista y las interacciones correspondientes a los empleados
 * dentro de la aplicación.
 */
public class PanelEmpleado extends JPanel {

    private static final long serialVersionUID = 1L;

    /** 
     * Referencia a la ventana principal de la aplicación. 
     */
    private final Main mainFrame;
    
    /** 
     * Contenedor principal donde se cargan dinámicamente las distintas secciones. 
     */
    private final JPanel contenido;
    
    /** 
     * Identificador de la sección actualmente visible en el panel. 
     */
    private String seccionActiva;
    
    /** 
     * Producto de la tienda que se encuentra seleccionado para ver su detalle o edición. 
     */
    private ProductoTienda productoSeleccionado;
    
    /** 
     * Bandera que indica si el producto seleccionado está en modo edición. 
     */
    private boolean editandoProducto;
    
    /** 
     * Almacena la sección en la que se encontraba el usuario antes de ver el detalle de un producto. 
     */
    private String seccionAnteriorProductos;

    /**
     * Construye una instancia de PanelEmpleado.
     * 
     * @param mainFrame Instancia principal de la aplicación que contiene el estado global y los datos.
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
     * Refresca el contenido del panel basándose en la sección activa y los permisos
     * del empleado que ha iniciado sesión. Reconstruye la interfaz gráfica pertinente.
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

    /**
     * Crea la cabecera superior de la interfaz, que incluye el menú lateral,
     * el título de la aplicación y el botón de acceso al perfil.
     * 
     * @return JPanel configurado como cabecera.
     */
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

    /**
     * Crea un botón estilizado como un icono de texto.
     * 
     * @param texto El texto o símbolo a mostrar en el botón.
     * @param tooltip El texto de ayuda al pasar el ratón por encima.
     * @param fontSize El tamaño de la fuente para el texto.
     * @param ancho El ancho preferido del botón.
     * @return JButton configurado como icono.
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
     * Muestra el menú lateral emergente para la navegación entre secciones,
     * habilitando las opciones según los permisos del empleado actual.
     * 
     * @param origen El botón que dispara la aparición del menú para posicionarlo.
     */
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

    /**
     * Crea un elemento de menú de navegación.
     * 
     * @param texto El texto a mostrar en el elemento.
     * @param seccion La constante de sección a la que navega este elemento.
     * @param habilitado Indica si el elemento está clickeable dependiendo de los permisos.
     * @return JMenuItem configurado.
     */
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

    /**
     * Pinta la sección principal (Home), mostrando un catálogo general de productos de la tienda.
     * 
     * @param empleado El empleado actual, usado para determinar si los productos pueden ser editados.
     */
    private void pintarHome(Empleado empleado) {
        contenido.setBorder(new EmptyBorder(34, 96, 34, 96));
        JPanel grid = new JPanel(new GridLayout(0, 3, 92, 44));
        grid.setOpaque(false);
        for (ProductoTienda producto : mainFrame.getProductosTienda()) {
            grid.add(crearTarjetaProducto(producto, empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO)));
        }
        contenido.add(grid);
    }

    /**
     * Crea un panel en forma de tarjeta que representa visualmente un producto de la tienda.
     * 
     * @param producto El producto a representar.
     * @param editable Indica si se debe habilitar el botón de editar.
     * @return JPanel con la representación del producto.
     */
    private JPanel crearTarjetaProducto(ProductoTienda producto, boolean editable) {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 8));
        tarjeta.setOpaque(false);
        TarjetaProducto vistaCliente = new TarjetaProducto(producto);
        vistaCliente.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            /**
             * Responde al evento de clic abriendo el detalle del producto en modo vista.
             * 
             * @param e El evento de ratón detectado.
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

    /**
     * Cambia la vista activa para mostrar los detalles del producto seleccionado.
     * 
     * @param producto El producto a mostrar.
     * @param editar Indica si la vista de detalle debe abrirse en modo edición.
     */
    private void abrirDetalleProducto(ProductoTienda producto, boolean editar) {
        productoSeleccionado = producto;
        editandoProducto = editar;
        if (!"DETALLE_PRODUCTO".equals(seccionActiva)) {
            seccionAnteriorProductos = seccionActiva;
        }
        seccionActiva = "DETALLE_PRODUCTO";
        refrescar();
    }

    /**
     * Pinta la sección de perfil del empleado, mostrando sus datos personales y sus permisos asignados.
     * 
     * @param empleado El empleado del cual se van a mostrar los datos.
     */
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

    /**
     * Construye un componente visual que muestra la foto de perfil del empleado
     * o sus iniciales en caso de no tener foto.
     * 
     * @param empleado El empleado del cual se extraerá la foto o el nombre.
     * @return JLabel configurado como avatar.
     */
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

    /**
     * Crea una fila visual para mostrar un dato específico del perfil del empleado.
     * 
     * @param etiqueta El nombre del campo (ej. "Usuario").
     * @param valor El valor del campo a mostrar.
     * @return JPanel con la etiqueta y el valor formateados.
     */
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

    /**
     * Crea una fila visual que representa el estado de un permiso (activo o inactivo).
     * 
     * @param permiso El tipo de permiso a mostrar.
     * @param activo Indica si el empleado posee este permiso.
     * @return JPanel con la representación visual del estado del permiso.
     */
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

    /**
     * Convierte el enumerado de tipo de permiso a un texto descriptivo para mostrar en la interfaz.
     * 
     * @param permiso El tipo de permiso a traducir.
     * @return String con la descripción del permiso.
     */
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

    /**
     * Obtiene las iniciales del empleado a partir de su nombre.
     * 
     * @param empleado El empleado del cual obtener las iniciales.
     * @return Las iniciales generadas en mayúsculas.
     */
    private String inicialesEmpleado(Empleado empleado) {
        String nombre = empleado.getNombre();
        if (nombre == null || nombre.isBlank()) {
            return "EMP";
        }
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    /**
     * Proporciona un texto por defecto en caso de que una cadena esté vacía o nula.
     * 
     * @param texto El texto a evaluar.
     * @return El texto original, o "Sin dato" si es nulo o está en blanco.
     */
    private String textoVacio(String texto) {
        return texto == null || texto.isBlank() ? "Sin dato" : texto;
    }

    /**
     * Pinta la sección de gestión de stock. Muestra la lista de productos y permite
     * realizar acciones globales como cargar un fichero o crear un producto nuevo.
     */
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
                 * Responde al evento de clic abriendo el detalle del producto.
                 * 
                 * @param e El evento de ratón detectado.
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

    /**
     * Crea un panel representativo de un producto de segunda mano que requiere
     * o ya posee valoración, proveyendo la acción para valorarlo.
     * 
     * @param producto El producto de segunda mano a representar en la fila.
     * @return JPanel con la fila de valoración del producto.
     */
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

    /**
     * Pinta la sección de gestión de segunda mano. Lista los productos que clientes
     * han subido y que requieren ser valorados por el empleado.
     */
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

    /**
     * Pinta la sección de gestión de pedidos. Separa los pedidos en preparación
     * y listos para recoger/entregar, y provee botones de cambio de estado.
     */
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

    /**
     * Crea un componente visual (fila) que representa un pedido y proporciona acciones
     * como ver información o cambiar su estado de procesamiento.
     * 
     * @param pedido El pedido a representar.
     * @param accion El texto para el botón que realiza el cambio de estado.
     * @return JPanel con la información y botones correspondientes al pedido.
     */
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

    /**
     * Pinta la sección de gestión de intercambios. Muestra ofertas activas o pasadas
     * de la tienda y permite marcar las que están aceptadas como realizadas.
     */
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

    /**
     * Pinta la sección de gestión de packs de productos. Lista los packs existentes
     * y provee un botón global para generar uno nuevo.
     */
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

    /**
     * Pinta la vista detallada de un producto seleccionado, inyectando un componente
     * `PanelDeProducto` y configurando sus listeners para confirmar o cancelar la edición.
     */
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
             * Recibe y aplica los cambios realizados en el formulario de edición de un producto.
             * 
             * @param datos Contiene la información ingresada por el usuario en el panel.
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
             * Cancela el proceso de edición, restaurando la vista a su estado anterior.
             */
            public void cancelar() {
                editandoProducto = false;
                refrescar();
            }
        });
        contenido.add(detalle);
    }

    /**
     * Construye una barra superior con un botón para retroceder desde el detalle
     * de un producto hacia el listado previo.
     * 
     * @return JPanel con la barra de navegación de retorno.
     */
    private JPanel crearBarraVolverProductos() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        barra.setOpaque(false);
        barra.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton volver = crearBotonFlechaVolver();
        volver.addActionListener(e -> volverAListadoProductos());
        barra.add(volver);
        return barra;
    }

    /**
     * Crea un botón en forma de flecha apuntando a la izquierda.
     * 
     * @return JButton configurado con el símbolo de la flecha.
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
     * Realiza la acción de volver al listado de productos, limpiando las variables
     * de estado del producto seleccionado y su modo de edición.
     */
    private void volverAListadoProductos() {
        editandoProducto = false;
        productoSeleccionado = null;
        seccionActiva = seccionAnteriorProductos;
        refrescar();
    }

    /**
     * Abre un diálogo de selección de archivos para permitir al usuario cargar
     * el catálogo de productos a partir de un archivo del sistema.
     */
    private void cargarProductosDeFichero() {
        JFileChooser chooser = new JFileChooser(".");
        int respuesta = chooser.showOpenDialog(this);
        if (respuesta == JFileChooser.APPROVE_OPTION) {
            mainFrame.recargarCatalogoDesdeFichero(chooser.getSelectedFile().getPath());
        }
    }

    /**
     * Abre una ventana modal con un formulario complejo para la creación
     * de un nuevo producto de tienda desde cero, recolectando sus datos y foto.
     */
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

    /**
     * Construye un componente de formulario con una etiqueta a la izquierda
     * y el control de entrada a la derecha.
     * 
     * @param etiqueta El texto a mostrar para identificar el campo.
     * @param campo El componente donde se introduce el dato (JTextField, JSpinner, etc.).
     * @return JPanel organizado para el formulario.
     */
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

    /**
     * Crea una etiqueta con el estilo visual para los formularios.
     * 
     * @param texto El texto de la etiqueta.
     * @return JLabel con la fuente y colores definidos para formularios.
     */
    private JLabel crearEtiquetaFormulario(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    /**
     * Aplica estilos (bordes, fondo, fuente) a un campo de texto genérico.
     * 
     * @param campo El JTextField al que aplicar estilos.
     */
    private void estilizarCampo(JTextField campo) {
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 1),
                new EmptyBorder(6, 8, 6, 8)));
        campo.setBackground(UiStyle.COLOR_FONDO);
        campo.setForeground(UiStyle.COLOR_TEXTO);
        campo.setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    /**
     * Muestra un cuadro de diálogo con la información detallada de un pedido específico.
     * 
     * @param pedido El pedido a inspeccionar.
     */
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

    /**
     * Abre un cuadro de diálogo para crear un pack nuevo o editar uno ya existente.
     * 
     * @param pack El pack a editar, o `null` si se desea crear un pack nuevo.
     */
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

    /**
     * Aplica un filtro de búsqueda visual a la lista de productos al crear o editar un pack.
     * 
     * @param filtro El texto de búsqueda.
     * @param productos El contenedor panel donde se mostrarán/ocultarán las filas.
     * @param cantidades Lista con todos los componentes spinner asociados a los productos.
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
     * Obtiene cuántas unidades de un mismo producto existen dentro de un pack dado.
     * 
     * @param pack El pack donde buscar. Puede ser null.
     * @param producto El producto a contar.
     * @return El número de unidades presentes.
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
     * Despliega un diálogo para que el empleado emita una valoración formal
     * a un producto de segunda mano propuesto por un cliente.
     * 
     * @param producto El producto de segunda mano que se desea valorar.
     */
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

    /**
     * Construye un componente para visualizar la imagen de un producto de segunda mano.
     * 
     * @param producto El producto a mostrar.
     * @param ancho El ancho de la imagen.
     * @param alto El alto de la imagen.
     * @return JLabel con la imagen o texto indicativo si carece de ella.
     */
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

    /**
     * Genera una cadena resumen con todos los productos incluidos en un pedido.
     * 
     * @param pedido El pedido a resumir.
     * @return Cadena con el estado y los nombres y cantidades de los productos.
     */
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

    /**
     * Genera una representación en texto para la lista de productos de segunda mano,
     * informando sobre su estado y propietario.
     * 
     * @param producto El producto a describir.
     * @return Cadena formateada para la vista.
     */
    private String textoProductoSegundaMano(ProductoSegundaMano producto) {
        String valor = producto.getEstaValorado()
                ? String.format("%.2f EUR | %s", producto.getValorEstimado(), producto.getEstadoConservacion())
                : "pendiente de valorar";
        return "<b>" + producto.getNombre() + "</b> | propietario: "
                + producto.getPropietario().getNombre() + " | " + valor;
    }

    /**
     * Crea un texto resumen agrupado que detalla los productos que componen un pack.
     * 
     * @param pack El pack que se desea resumir.
     * @return Una cadena enlazando los nombres, cantidades y categorías de los componentes del pack.
     */
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

    /**
     * Obtiene el texto de la categoría principal de un pack o un valor por defecto
     * si se encuentra vacío.
     * 
     * @param pack El pack a evaluar.
     * @return La categoría del pack o "sin categoría".
     */
    private String categoriaPack(Pack pack) {
        String categoria = pack.getCategoria();
        return categoria == null || categoria.isBlank() ? "sin categoría" : categoria;
    }

    /**
     * Obtiene el texto de las categorías de un producto unidas por comas.
     * 
     * @param producto El producto a evaluar.
     * @return Las categorías asociadas.
     */
    private String categoriaProducto(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        if (!categorias.isEmpty()) {
            return String.join(", ", categorias);
        }
        return producto.getCategoria() == null ? "sin categoría" : producto.getCategoria().getNombre();
    }

    /**
     * Parsea una cadena de texto separada por comas en una lista de Strings de categorías.
     * 
     * @param texto El texto crudo escrito por el usuario.
     * @return Lista de categorías procesada.
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
     * Intenta convertir un string a un número de coma flotante de manera segura.
     * 
     * @param texto El texto que representa un valor numérico.
     * @param defecto El valor de retorno en caso de que ocurra una excepción.
     * @return El número de tipo double interpretado o el defecto si falla.
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
     * Devuelve un panel base con esquinas redondeadas utilizado como contenedor tipo tarjeta.
     * 
     * @return JPanel configurado como tarjeta base.
     */
    private JPanel crearTarjeta() {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(14, 14, 14, 14));
        return tarjeta;
    }

    /**
     * Crea un componente de scroll que envuelve el panel principal de contenido.
     * 
     * @return JScrollPane configurado para desplazar el contenido fluidamente.
     */
    private JScrollPane crearScroll() {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    /**
     * Crea una etiqueta con los estilos visuales para representar un título de sección.
     * 
     * @param texto El texto del título.
     * @return JLabel con la fuente en negrita y márgenes de título.
     */
    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(14, 0, 10, 0));
        return titulo;
    }

    /**
     * Crea una etiqueta con los estilos visuales para representar un subtítulo de sección.
     * 
     * @param texto El texto del subtítulo.
     * @return JLabel formateado como subtítulo.
     */
    private JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(0, 0, 18, 0));
        return label;
    }

    /**
     * Crea una etiqueta de texto genérica compatible con renderizado en HTML básico.
     * 
     * @param texto El texto contenido en la etiqueta.
     * @return JLabel formateado con la fuente estándar.
     */
    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    /**
     * Construye un botón estilizado con esquinas redondeadas.
     * 
     * @param texto El texto central del botón.
     * @param ancho El ancho máximo y preferido del botón.
     * @return JButton con los estilos visuales principales de botones aplicados.
     */
    private JButton crearBoton(String texto, int ancho) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(ancho, 34));
        boton.setMaximumSize(new Dimension(ancho, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Fuerza la revalidación y el repintado del contenedor principal
     * para reflejar los últimos cambios producidos por una actualización en la interfaz.
     */
    private void actualizar() {
        contenido.revalidate();
        contenido.repaint();
    }
}