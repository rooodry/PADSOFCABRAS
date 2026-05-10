package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.Icon;
import javax.swing.border.EmptyBorder;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


import compras.Pedido;
import intercambios.Intercambio;
import intercambios.Oferta;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import utilidades.EstadoPedido;
import utilidades.EstadoOferta;


public class PanelPerfil extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String TAB_RECOMENDADOS = "Productos recomendados";
    private static final String TAB_PEDIDOS = "Historial de pedidos";
    private static final String TAB_INTERCAMBIOS = "Historial de intercambios";
    private static final String TAB_CONFIG = "Configuración";
    private static final int AVATAR_SIZE = 150;

    private final Main mainFrame;
    private final JPanel contenidoCentral;
    private final JButton btnRecomendados;
    private final JButton btnPedidos;
    private final JButton btnIntercambios;
    private final JButton btnConfig;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
    private final JTextField txtNuevoNombre;
    private final JLabel lblUsuario;
    private final JLabel lblDni;
    private final JLabel lblAvatar;
    private JButton btnGuardarCambios;
    private String nuevoNombrePendiente = null;
    private String contrasenaActualPendiente = null;
    private String nuevaContrasenaPendiente = null;
    private String nuevaFotoPendiente = null;

    private String tabActivo = TAB_RECOMENDADOS;

    /**
     * Builds the profile panel with tabbed navigation.
     *
     * @param mainFrame main GUI controller
     */
    public PanelPerfil(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.contenidoCentral = new JPanel(new BorderLayout());
        this.txtNuevoNombre = new JTextField(22);
        this.lblUsuario = new JLabel();
        this.lblDni = new JLabel();

        this.btnRecomendados = crearTabButton(TAB_RECOMENDADOS);
        this.btnPedidos = crearTabButton(TAB_PEDIDOS);
        this.btnIntercambios = crearTabButton(TAB_INTERCAMBIOS);
        this.btnConfig = crearTabButton(TAB_CONFIG);
        this.lblAvatar = new JLabel("lib/fotos/fotousuario.jpg", SwingConstants.CENTER);

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "PERFIL"), BorderLayout.NORTH);
        add(crearLayoutPerfil(), BorderLayout.CENTER);
        refrescar();
    }

    private JPanel crearLayoutPerfil() {
        JPanel layout = new JPanel(new BorderLayout(20, 0));
        layout.setBackground(UiStyle.COLOR_FONDO);
        layout.setBorder(new EmptyBorder(22, 22, 22, 22));

        layout.add(crearBarraLateral(), BorderLayout.WEST);
        layout.add(contenidoCentral, BorderLayout.CENTER);
        return layout;
    }

    private JPanel crearBarraLateral() {
        JPanel barra = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBorder(new EmptyBorder(20, 16, 20, 16));
        barra.setPreferredSize(new Dimension(300, 0));

        JPanel avatar = new JPanel(new BorderLayout());
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(268, AVATAR_SIZE));
        avatar.setMaximumSize(new Dimension(268, AVATAR_SIZE));
        lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 92));
        avatar.add(lblAvatar, BorderLayout.CENTER);
        barra.add(avatar);
        barra.add(Box.createVerticalStrut(12));

        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblUsuario.setForeground(UiStyle.COLOR_TEXTO);
        lblUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        barra.add(lblUsuario);

        lblDni.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblDni.setForeground(UiStyle.COLOR_TEXTO);
        lblDni.setAlignmentX(Component.CENTER_ALIGNMENT);
        barra.add(lblDni);
        barra.add(Box.createVerticalStrut(16));

        barra.add(crearBotonNav(btnRecomendados, TAB_RECOMENDADOS));
        barra.add(crearSeparadorNav());
        barra.add(crearBotonNav(btnPedidos, TAB_PEDIDOS));
        barra.add(crearSeparadorNav());
        barra.add(crearBotonNav(btnIntercambios, TAB_INTERCAMBIOS));
        barra.add(crearSeparadorNav());
        barra.add(crearBotonNav(btnConfig, TAB_CONFIG));
        barra.add(Box.createVerticalGlue());
        barra.add(crearBotonCerrarSesion());

        return barra;
    }

    private JButton crearBotonCerrarSesion() {
        JButton boton = new UiStyle.RoundedButton("Cerrar sesión",
                UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 16);
        boton.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        boton.setPreferredSize(new Dimension(268, 40));
        boton.setMaximumSize(new Dimension(268, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> mainFrame.cerrarSesion());
        return boton;
    }

    private JPanel crearBotonNav(JButton boton, String id) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(268, 40));
        panel.add(boton, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearSeparadorNav() {
        JPanel separador = new JPanel();
        separador.setBackground(UiStyle.COLOR_BORDE);
        separador.setMaximumSize(new Dimension(268, 2));
        separador.setPreferredSize(new Dimension(268, 2));
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);
        return separador;
    }

    private JButton crearTabButton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TARJETA, UiStyle.COLOR_MARRON_MEDIO, 16);
        boton.setForeground(UiStyle.COLOR_TEXTO);
        boton.setPreferredSize(new Dimension(268, 40));
        boton.addActionListener(e -> {
            tabActivo = texto;
            construirVista();
        });
        return boton;
    }

    /**
     * Refreshes the displayed customer data.
     */
    public void refrescar() {
        lblUsuario.setText("@" + mainFrame.getClienteActual().getNombre());
        lblDni.setText("DNI: " + mainFrame.getClienteActual().getDNI());
        txtNuevoNombre.setText(mainFrame.getClienteActual().getNombre());
        actualizarAvatar();
        construirVista();
    }

    private void construirVista() {
        contenidoCentral.removeAll();
        actualizarBotonesTabs();

        if (TAB_RECOMENDADOS.equals(tabActivo)) {
            contenidoCentral.add(crearVistaRecomendados(), BorderLayout.CENTER);
        } else if (TAB_PEDIDOS.equals(tabActivo)) {
            contenidoCentral.add(crearVistaPedidos(), BorderLayout.CENTER);
        } else if (TAB_INTERCAMBIOS.equals(tabActivo)) {
            contenidoCentral.add(crearVistaIntercambios(), BorderLayout.CENTER);
        } else if (TAB_CONFIG.equals(tabActivo)) {
            contenidoCentral.add(crearVistaConfig(), BorderLayout.CENTER);
        }

        contenidoCentral.revalidate();
        contenidoCentral.repaint();
    }

    private void actualizarBotonesTabs() {
        actualizaBoton(btnRecomendados, TAB_RECOMENDADOS.equals(tabActivo));
        actualizaBoton(btnPedidos, TAB_PEDIDOS.equals(tabActivo));
        actualizaBoton(btnIntercambios, TAB_INTERCAMBIOS.equals(tabActivo));
        actualizaBoton(btnConfig, TAB_CONFIG.equals(tabActivo));
    }

    private void actualizaBoton(JButton boton, boolean activo) {
        boton.setBackground(activo ? UiStyle.COLOR_CABECERA : UiStyle.COLOR_TARJETA);
        boton.setForeground(activo ? UiStyle.COLOR_TEXTO_CLARO : UiStyle.COLOR_TEXTO);
    }

    private JPanel crearVistaRecomendados() {
        JPanel vista = new JPanel(new BorderLayout());
        vista.setOpaque(false);

        JLabel titulo = new JLabel("Productos recomendados", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        vista.add(titulo, BorderLayout.NORTH);

        List<ProductoTienda> recomendados = mainFrame.getProductosRecomendados();
        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setOpaque(false);
        for (ProductoTienda producto : recomendados) {
            grid.add(crearTarjetaProducto(producto));
        }

        JScrollPane scroll = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        vista.add(scroll, BorderLayout.CENTER);
        return vista;
    }

    private JPanel crearTarjetaProducto(ProductoTienda producto) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
        tarjeta.setLayout(new BorderLayout(0, 12));
        tarjeta.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel imagen = new JLabel();
        imagen.setPreferredSize(new Dimension(120, 160));
        imagen.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icono = new ImageIcon(producto.getImagen());
        Image img = icono.getImage().getScaledInstance(120, 160, Image.SCALE_SMOOTH);
        imagen.setIcon(new ImageIcon(img));

        tarjeta.add(imagen, BorderLayout.NORTH);

        JLabel nombre = new JLabel("<html><b>" + producto.getNombre() + "</b></html>");
        nombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(nombre, BorderLayout.CENTER);

        JLabel precio = new JLabel(String.format("%.2f EUR", producto.getPrecio()));
        precio.setFont(new Font("SansSerif", Font.BOLD, 14));
        precio.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(precio, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel crearVistaPedidos() {
        JPanel vista = new JPanel(new BorderLayout());
        vista.setOpaque(false);

        JLabel titulo = new JLabel("Historial de pedidos", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        vista.add(titulo, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBorder(new EmptyBorder(16, 0, 0, 0));

        List<Pedido> pedidos = mainFrame.getClienteActual().getPedidos();
        if (pedidos.isEmpty()) {
            JLabel vacio = new JLabel("Todavía no tienes pedidos.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Pedido pedido : pedidos) {
                lista.add(crearTarjetaPedido(pedido));
                lista.add(Box.createVerticalStrut(18));
            }
        }

        JScrollPane scroll = new JScrollPane(lista,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        vista.add(scroll, BorderLayout.CENTER);
        return vista;
    }

    private JPanel crearTarjetaPedido(Pedido pedido) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(new EmptyBorder(16, 18, 16, 18));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel codigo = new JLabel("Pedido " + pedido.getCodigo().getCodigo());
        codigo.setFont(new Font("SansSerif", Font.BOLD, 15));
        codigo.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(codigo, gbc);

        gbc.gridy++;
        tarjeta.add(crearLineaPedido("Estado: " + pedido.getEstadoPedido()
                + "   Fecha: " + formatoFecha.format(pedido.getFechaRealizacion())), gbc);

        gbc.gridy++;
        tarjeta.add(crearLineaPedido(productosPedido(pedido)), gbc);

        gbc.gridy++;
        tarjeta.add(crearLineaPedido(String.format("Total: %.2f EUR", pedido.calcularPrecioTotal())), gbc);

        gbc.gridy++;
        JButton btnVerPedido = new UiStyle.RoundedButton(
                "Ver pedido",
                UiStyle.COLOR_MARRON_MEDIO,
                UiStyle.COLOR_CABECERA,
                16
        );
        btnVerPedido.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        btnVerPedido.setPreferredSize(new Dimension(140, 35));
        btnVerPedido.addActionListener(e -> mostrarDetallePedido(pedido));

        tarjeta.add(btnVerPedido, gbc);

        return tarjeta;
    }

    private void mostrarDetallePedido(Pedido pedido) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            ProductoTienda producto = entry.getKey();
            int cantidad = entry.getValue();

            JPanel fila = new JPanel(new BorderLayout(12, 8));
            fila.setBorder(new EmptyBorder(8, 8, 8, 8));

            JLabel imagen = new JLabel();
            imagen.setPreferredSize(new Dimension(80, 100));
            imagen.setHorizontalAlignment(SwingConstants.CENTER);

            ImageIcon icono = new ImageIcon(producto.getImagen());
            Image img = icono.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
            imagen.setIcon(new ImageIcon(img));

            fila.add(imagen, BorderLayout.WEST);

            JPanel datos = new JPanel();
            datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));

            JLabel nombre = new JLabel("Producto: " + producto.getNombre());
            JLabel cantidadLabel = new JLabel("Cantidad: " + cantidad);
            JLabel precio = new JLabel(String.format("Precio: %.2f EUR", producto.getPrecio()));

            datos.add(nombre);
            datos.add(cantidadLabel);
            datos.add(precio);

            fila.add(datos, BorderLayout.CENTER);

            panel.add(fila);
            panel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scroll = new JScrollPane(panel);
        scroll.setPreferredSize(new Dimension(420, 300));

        JOptionPane.showMessageDialog(
                mainFrame,
                scroll,
                "Detalle del pedido",
                JOptionPane.PLAIN_MESSAGE
        );
    }

    private JLabel crearLineaPedido(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private String productosPedido(Pedido pedido) {
        StringBuilder builder = new StringBuilder("Productos: ");
        boolean primero = true;
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            if (!primero) {
                builder.append(", ");
            }
            builder.append(entry.getKey().getNombre()).append(" x").append(entry.getValue());
            primero = false;
        }
        return builder.toString();
    }

    private JPanel crearVistaIntercambios() {
        JPanel vista = new JPanel(new BorderLayout());
        vista.setOpaque(false);

        JLabel titulo = new JLabel("Historial de intercambios", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        vista.add(titulo, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        List<Intercambio> intercambios = mainFrame.getIntercambiosClienteActual();
        if (intercambios.isEmpty()) {
            JLabel vacio = new JLabel("Aún no tienes intercambios.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Intercambio intercambio : intercambios) {
                lista.add(crearTarjetaIntercambio(intercambio));
                lista.add(Box.createVerticalStrut(18));
            }
        }

        JScrollPane scroll = new JScrollPane(lista,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        vista.add(scroll, BorderLayout.CENTER);
        return vista;
    }

    private JPanel crearTarjetaIntercambio(Intercambio intercambio) {
        Oferta oferta = intercambio.getOferta();
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(new EmptyBorder(14, 16, 14, 16));
        tarjeta.setPreferredSize(new Dimension(0, 170));
        tarjeta.setMinimumSize(new Dimension(0, 170));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        tarjeta.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mostrarDetalleIntercambio(intercambio);
            }
        });

        if (oferta != null) {
            Date fechaIntercambio = intercambio.getFechaAceptada() != null
                    ? intercambio.getFechaAceptada()
                    : intercambio.getFechaOferta();
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            gbc.insets = new Insets(0, 0, 0, 18);
            gbc.anchor = GridBagConstraints.CENTER;
            tarjeta.add(crearResumenProductoIntercambio(oferta.getProductoOfertado()), gbc);

            gbc.gridx = 1;
            JLabel flecha = new JLabel("<html><center>&harr;<br>Intercambio</center></html>", SwingConstants.CENTER);
            flecha.setFont(new Font("SansSerif", Font.BOLD, 14));
            flecha.setForeground(UiStyle.COLOR_TEXTO);
            tarjeta.add(flecha, gbc);

            gbc.gridx = 2;
            tarjeta.add(crearResumenProductoIntercambio(oferta.getProductoDeseado()), gbc);

            JPanel datos = new JPanel();
            datos.setOpaque(false);
            datos.setLayout(new BoxLayout(datos, BoxLayout.Y_AXIS));

            JLabel estado = new JLabel("Estado: " + estadoIntercambio(intercambio));
            estado.setFont(new Font("SansSerif", Font.BOLD, 16));
            estado.setForeground(UiStyle.COLOR_TEXTO);
            datos.add(estado);
            datos.add(Box.createVerticalStrut(8));

            JLabel fecha = new JLabel("Fecha: " + formatoFecha.format(fechaIntercambio));
            fecha.setFont(new Font("SansSerif", Font.PLAIN, 13));
            fecha.setForeground(UiStyle.COLOR_TEXTO);
            datos.add(fecha);
            datos.add(Box.createVerticalStrut(8));

            JLabel clientes = new JLabel("Clientes: " + nombreCliente(oferta.getUsuarioLanzador())
                    + " / " + nombreCliente(oferta.getUsuarioReceptor()));
            clientes.setFont(new Font("SansSerif", Font.PLAIN, 13));
            clientes.setForeground(UiStyle.COLOR_TEXTO);
            datos.add(clientes);

            gbc.gridx = 3;
            gbc.gridy = 0;
            gbc.gridheight = 2;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(0, 4, 0, 0);
            tarjeta.add(datos, gbc);
            return tarjeta;
        }

        JLabel estado = new JLabel(oferta.getEstadoOferta().toString());
        estado.setFont(new Font("SansSerif", Font.BOLD, 16));
        estado.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(estado, BorderLayout.NORTH);

        Date fechaIntercambio = intercambio.getFechaAceptada() != null ? intercambio.getFechaAceptada() : intercambio.getFechaOferta();
        JLabel fecha = new JLabel("Lo realizaste el " + formatoFecha.format(fechaIntercambio));
        fecha.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fecha.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(fecha, BorderLayout.WEST);

        JPanel intercambio_visual = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        intercambio_visual.setOpaque(false);

        JLabel prodOfrecido = new JLabel("\uD83D\uDCE6");
        prodOfrecido.setFont(new Font("SansSerif", Font.PLAIN, 24));
        intercambio_visual.add(prodOfrecido);

        JLabel flecha = new JLabel("\u2194");
        flecha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        flecha.setForeground(UiStyle.COLOR_TEXTO);
        intercambio_visual.add(flecha);

        JLabel prodDeseado = new JLabel("\uD83D\uDCE6");
        prodDeseado.setFont(new Font("SansSerif", Font.PLAIN, 24));
        intercambio_visual.add(prodDeseado);

        tarjeta.add(intercambio_visual, BorderLayout.CENTER);

        return tarjeta;
    }

    private JPanel crearResumenProductoIntercambio(ProductoSegundaMano producto) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(132, 140));
        panel.add(crearImagenSegundaMano(producto, 112, 96), BorderLayout.CENTER);

        JLabel nombre = new JLabel("<html><center>" + producto.getNombre() + "</center></html>", SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(nombre, BorderLayout.SOUTH);
        return panel;
    }

    private JLabel crearImagenSegundaMano(ProductoSegundaMano producto, int ancho, int alto) {
        JLabel imagen = new JLabel("SIN IMAGEN", SwingConstants.CENTER);
        imagen.setPreferredSize(new Dimension(ancho, alto));
        imagen.setMinimumSize(new Dimension(ancho, alto));
        imagen.setOpaque(true);
        imagen.setBackground(UiStyle.COLOR_CABECERA);
        imagen.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        imagen.setFont(new Font("SansSerif", Font.BOLD, 11));
        imagen.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 2));

        String ruta = producto.getImagen();
        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image escalada = icono.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            imagen.setText("");
            imagen.setIcon(new ImageIcon(escalada));
        }
        return imagen;
    }

    private void mostrarDetalleIntercambio(Intercambio intercambio) {
        Oferta oferta = intercambio.getOferta();
        JPanel panel = new JPanel(new BorderLayout(16, 14));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel productos = new JPanel(new GridLayout(1, 2, 16, 0));
        productos.add(crearDetalleProductoIntercambio("Producto ofrecido", oferta.getProductoOfertado()));
        productos.add(crearDetalleProductoIntercambio("Producto deseado", oferta.getProductoDeseado()));
        panel.add(productos, BorderLayout.CENTER);

        JPanel datos = new JPanel(new GridLayout(0, 1, 4, 4));
        datos.add(new JLabel("Estado: " + estadoIntercambio(intercambio)));
        datos.add(new JLabel("Cliente lanzador: " + nombreCliente(oferta.getUsuarioLanzador())));
        datos.add(new JLabel("Cliente receptor: " + nombreCliente(oferta.getUsuarioReceptor())));
        datos.add(new JLabel("Fecha de oferta: " + formatoFecha.format(intercambio.getFechaOferta())));
        datos.add(new JLabel("Fecha límite: " + formatoFecha.format(intercambio.getFechaLimite())));
        datos.add(new JLabel("Fecha aceptada: " + formatoFechaNullable(intercambio.getFechaAceptada())));
        datos.add(new JLabel("Intercambio materializado: " + (intercambio.getIntercambiado() ? "Sí" : "No")));
        panel.add(datos, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(mainFrame, panel, "Detalle del intercambio", JOptionPane.PLAIN_MESSAGE);
    }

    private JPanel crearDetalleProductoIntercambio(String titulo, ProductoSegundaMano producto) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(new EmptyBorder(8, 8, 8, 8));
        panel.add(crearImagenSegundaMano(producto, 170, 130), BorderLayout.NORTH);

        JLabel datos = new JLabel("<html><b>" + titulo + "</b><br>"
                + producto.getNombre()
                + "<br>Propietario: " + nombreCliente(producto.getPropietario())
                + "<br>Estado producto: " + producto.getEstadoProducto()
                + "<br>Conservación: " + textoDato(producto.getEstadoConservacion())
                + "<br>Valor estimado: " + String.format("%.2f EUR", producto.getValorEstimado())
                + "<br>Descripción: " + textoDato(producto.getDescripcion()) + "</html>");
        datos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        datos.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(datos, BorderLayout.CENTER);
        return panel;
    }

    private String estadoIntercambio(Intercambio intercambio) {
        if (intercambio.getIntercambiado()) {
            return "INTERCAMBIADO";
        }
        return intercambio.getOferta().getEstadoOferta().toString();
    }

    private String nombreCliente(usuarios.ClienteRegistrado cliente) {
        return cliente == null ? "Sin cliente" : "@" + cliente.getNombre();
    }

    private String formatoFechaNullable(Date fecha) {
        return fecha == null ? "Sin aceptar" : formatoFecha.format(fecha);
    }

    private String textoDato(Object dato) {
        return dato == null ? "Sin dato" : dato.toString();
    }

    private JPanel crearVistaConfig() {
        JPanel vista = new JPanel(new BorderLayout(0, 20));
        vista.setOpaque(false);

        JPanel panel = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Notificaciones");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(12));

        JCheckBox chkPedidos = new JCheckBox("Actualizaciones de pedidos", true);
        chkPedidos.setOpaque(false);
        chkPedidos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkPedidos.setForeground(UiStyle.COLOR_TEXTO);
        chkPedidos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(chkPedidos);

        JCheckBox chkIntercambios = new JCheckBox("Ofertas de intercambios", true);
        chkIntercambios.setOpaque(false);
        chkIntercambios.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkIntercambios.setForeground(UiStyle.COLOR_TEXTO);
        chkIntercambios.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(chkIntercambios);

        JCheckBox chkDescuentos = new JCheckBox("Descuentos de productos", true);
        chkDescuentos.setOpaque(false);
        chkDescuentos.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkDescuentos.setForeground(UiStyle.COLOR_TEXTO);
        chkDescuentos.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(chkDescuentos);

        JCheckBox chkNovedades = new JCheckBox("Actualizaciones de novedades", true);
        chkNovedades.setOpaque(false);
        chkNovedades.setFont(new Font("SansSerif", Font.PLAIN, 12));
        chkNovedades.setForeground(UiStyle.COLOR_TEXTO);
        chkNovedades.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(chkNovedades);

        panel.add(Box.createVerticalStrut(20));

        JLabel subtitulo = new JLabel("Mi cuenta");
        subtitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        subtitulo.setForeground(UiStyle.COLOR_TEXTO);
        subtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(subtitulo);
        panel.add(Box.createVerticalStrut(12));

        JButton btnCambiarNombre = new UiStyle.RoundedButton("Cambiar nombre", UiStyle.COLOR_TARJETA, UiStyle.COLOR_MARRON_MEDIO, 16);
        btnCambiarNombre.setPreferredSize(new Dimension(220, 36));
        btnCambiarNombre.setMaximumSize(new Dimension(220, 36));
        btnCambiarNombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCambiarNombre.addActionListener(e -> abrirDialogoCambiarNombre());
        panel.add(btnCambiarNombre);
        panel.add(Box.createVerticalStrut(8));

        JButton btnCambiarContraseña = new UiStyle.RoundedButton("Cambiar contraseña", UiStyle.COLOR_TARJETA, UiStyle.COLOR_MARRON_MEDIO, 16);
        btnCambiarContraseña.setPreferredSize(new Dimension(220, 36));
        btnCambiarContraseña.setMaximumSize(new Dimension(220, 36));
        btnCambiarContraseña.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCambiarContraseña.addActionListener(e -> abrirDialogoCambiarContraseña());
        panel.add(btnCambiarContraseña);

        JButton btnCambiarFoto = new UiStyle.RoundedButton("Cambiar foto de perfil", UiStyle.COLOR_TARJETA, UiStyle.COLOR_MARRON_MEDIO, 16);
        btnCambiarFoto.setPreferredSize(new Dimension(220, 36));
        btnCambiarFoto.setMaximumSize(new Dimension(220, 36));
        btnCambiarFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCambiarFoto.addActionListener(e -> abrirDialogoCambiarFotoPerfil());
        panel.add(btnCambiarFoto);
        panel.add(Box.createVerticalStrut(8));

        panel.add(Box.createVerticalGlue());

        btnGuardarCambios = new UiStyle.RoundedButton(
        "Guardar cambios",
        UiStyle.COLOR_MARRON_MEDIO,
        UiStyle.COLOR_CABECERA,
        18
        );

        btnGuardarCambios.setPreferredSize(new Dimension(180, 40));
        btnGuardarCambios.setMaximumSize(new Dimension(180, 40));
        btnGuardarCambios.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardarCambios.setVisible(hayCambiosPendientes());

        btnGuardarCambios.addActionListener(e -> guardarCambiosPendientes());

        panel.add(btnGuardarCambios);

        vista.add(panel, BorderLayout.NORTH);
        return vista;
    }

    private void abrirDialogoCambiarNombre() {
        String nuevoNombre = javax.swing.JOptionPane.showInputDialog(mainFrame,
                "Ingresa tu nuevo nombre:",
                mainFrame.getClienteActual().getNombre());

        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            nuevoNombrePendiente = nuevoNombre.trim();
            lblUsuario.setText("@" + nuevoNombrePendiente);
            txtNuevoNombre.setText(nuevoNombrePendiente);
            mostrarBotonGuardarSiHayCambios();
        }
    }

    private void abrirDialogoCambiarContraseña() {
        javax.swing.JPasswordField campoActual = new javax.swing.JPasswordField();
        javax.swing.JPasswordField campoNueva = new javax.swing.JPasswordField();
        javax.swing.JPasswordField campoConfirmacion = new javax.swing.JPasswordField();

        Object[] mensaje = {
                "Contraseña actual:", campoActual,
                "Nueva contraseña:", campoNueva,
                "Confirmar contraseña:", campoConfirmacion
        };

        int opcion = javax.swing.JOptionPane.showConfirmDialog(mainFrame, mensaje,
                "Cambiar contraseña",
                javax.swing.JOptionPane.OK_CANCEL_OPTION,
                javax.swing.JOptionPane.PLAIN_MESSAGE);

        if (opcion != javax.swing.JOptionPane.OK_OPTION) {
            return;
        }

        String actual = new String(campoActual.getPassword()).trim();
        String nueva = new String(campoNueva.getPassword()).trim();
        String confirmacion = new String(campoConfirmacion.getPassword()).trim();

        if (actual.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(mainFrame,
                    "Debes introducir tu contraseña actual.",
                    "Error",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!mainFrame.getClienteActual().getContraseña().equals(actual)) {
            javax.swing.JOptionPane.showMessageDialog(mainFrame,
                    "La contraseña actual no es correcta.",
                    "Error",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nueva.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(mainFrame,
                    "La contraseña no puede estar vacía.",
                    "Error",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nueva.equals(confirmacion)) {
            javax.swing.JOptionPane.showMessageDialog(mainFrame,
                    "Las contraseñas no coinciden.",
                    "Error",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        contrasenaActualPendiente = actual;
        nuevaContrasenaPendiente = nueva;

        javax.swing.JOptionPane.showMessageDialog(mainFrame,
                "Contraseña preparada para guardar.",
                "Cambiar contraseña",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);

        mostrarBotonGuardarSiHayCambios();
        }


    private boolean hayCambiosPendientes() {
        return nuevoNombrePendiente != null || nuevaContrasenaPendiente != null || nuevaFotoPendiente != null;
    }

    private void mostrarBotonGuardarSiHayCambios() {
        if (btnGuardarCambios != null) {
            btnGuardarCambios.setVisible(hayCambiosPendientes());
            btnGuardarCambios.revalidate();
            btnGuardarCambios.repaint();
        }
    }

    private void guardarCambiosPendientes() {
        if (nuevoNombrePendiente != null) {
            mainFrame.cambiarNombreCliente(nuevoNombrePendiente);
            nuevoNombrePendiente = null;
        }

        if (nuevaContrasenaPendiente != null) {
            boolean cambiada = mainFrame.cambiarContrasenaCliente(contrasenaActualPendiente, nuevaContrasenaPendiente);
            if (cambiada) {
                contrasenaActualPendiente = null;
                nuevaContrasenaPendiente = null;
            } else {
                JOptionPane.showMessageDialog(mainFrame,
                        "No se ha podido cambiar la contraseña. Vuelve a introducir tu contraseña actual.",
                        "Cambiar contraseña",
                JOptionPane.WARNING_MESSAGE);
            }
        }

        if (nuevaFotoPendiente != null) {
            mainFrame.cambiarFotoPerfilCliente(nuevaFotoPendiente);
            nuevaFotoPendiente = null;
        }

        JOptionPane.showMessageDialog(mainFrame, "Cambios guardados correctamente");

        mostrarBotonGuardarSiHayCambios();
        refrescar();
    }

    private void actualizarAvatarPreview(String rutaFoto) {

        if (rutaFoto == null || rutaFoto.isBlank()) {
            lblAvatar.setIcon(null);
            lblAvatar.setText("\uD83D\uDC64");
            lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 92));
            return;
        }

        File archivo = new File(rutaFoto);
        if (!archivo.exists()) {
            lblAvatar.setIcon(null);
            lblAvatar.setText("\uD83D\uDC64");
            lblAvatar.setFont(new Font("SansSerif", Font.PLAIN, 92));
            return;
        }


        ImageIcon original = new ImageIcon(rutaFoto);
        Icon escalado = new ImageIcon(original.getImage().getScaledInstance(AVATAR_SIZE, AVATAR_SIZE,
                java.awt.Image.SCALE_SMOOTH));
        lblAvatar.setIcon(escalado);
        lblAvatar.setText("");
    }

    private void abrirDialogoCambiarFotoPerfil() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setDialogTitle("Selecciona una foto de perfil");
        chooser.setFileFilter(new FileNameExtensionFilter("Imágenes", "jpg", "jpeg", "png", "gif", "bmp"));

        int resultado = chooser.showOpenDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File seleccionada = chooser.getSelectedFile();
        if (seleccionada == null || !seleccionada.exists()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "No se ha seleccionado una imagen válida.",
                    "Foto de perfil",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        nuevaFotoPendiente = seleccionada.getAbsolutePath();
        actualizarAvatarPreview(nuevaFotoPendiente);
        mostrarBotonGuardarSiHayCambios();
    }

    private void actualizarAvatar() {
        String rutaFoto = mainFrame.getClienteActual().getFotoPerfil();
        actualizarAvatarPreview(rutaFoto);
    }

}
