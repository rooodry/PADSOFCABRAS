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
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import compras.Pedido;
import intercambios.Intercambio;
import intercambios.Oferta;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import utilidades.EstadoPedido;
import utilidades.EstadoOferta;

/**
 * Profile panel with tabbed navigation: Recommended, Orders, Exchanges, Settings.
 */
public class PanelPerfil extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String TAB_RECOMENDADOS = "Productos recomendados";
    private static final String TAB_PEDIDOS = "Historial de pedidos";
    private static final String TAB_INTERCAMBIOS = "Historial de intercambios";
    private static final String TAB_CONFIG = "Configuración";

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
        avatar.setMaximumSize(new Dimension(268, 120));
        JLabel iconoAvatar = new JLabel("👤", SwingConstants.CENTER);
        iconoAvatar.setFont(new Font("SansSerif", Font.PLAIN, 60));
        avatar.add(iconoAvatar, BorderLayout.CENTER);
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
        barra.add(Box.createVerticalStrut(8));
        barra.add(crearBotonNav(btnPedidos, TAB_PEDIDOS));
        barra.add(Box.createVerticalStrut(8));
        barra.add(crearBotonNav(btnIntercambios, TAB_INTERCAMBIOS));
        barra.add(Box.createVerticalStrut(8));
        barra.add(crearBotonNav(btnConfig, TAB_CONFIG));
        barra.add(Box.createVerticalGlue());

        return barra;
    }

    private JPanel crearBotonNav(JButton boton, String id) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(268, 40));
        panel.add(boton, BorderLayout.CENTER);
        return panel;
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
        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
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
        imagen.setText("[Imagen]");
        imagen.setHorizontalAlignment(SwingConstants.CENTER);
        imagen.setFont(new Font("SansSerif", Font.PLAIN, 10));
        tarjeta.add(imagen, BorderLayout.NORTH);

        JLabel nombre = new JLabel("<html><b>" + producto.getNombre() + "</b></html>");
        nombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(nombre, BorderLayout.CENTER);

        JLabel precio = new JLabel(String.format("%.2f€", producto.getPrecio()));
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

        List<Pedido> pedidos = mainFrame.getClienteActual().getPedidos();
        if (pedidos.isEmpty()) {
            JLabel vacio = new JLabel("Aún no tienes pedidos.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Pedido pedido : pedidos) {
                lista.add(crearTarjetaPedido(pedido));
                lista.add(Box.createVerticalStrut(12));
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

    private JPanel crearTarjetaPedido(Pedido pedido) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
        tarjeta.setLayout(new BorderLayout(12, 12));
        tarjeta.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel estado = new JLabel(pedido.getEstado().toString());
        estado.setFont(new Font("SansSerif", Font.BOLD, 16));
        estado.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(estado, BorderLayout.NORTH);

        Date fechaPedido = pedido.getFechaRecogida() != null ? pedido.getFechaRecogida() : pedido.getFechaRealizacion();
        JLabel fecha = new JLabel("Lo recogiste el " + formatoFecha.format(fechaPedido));
        fecha.setFont(new Font("SansSerif", Font.PLAIN, 12));
        fecha.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(fecha, BorderLayout.WEST);

        JLabel total = new JLabel(String.format("%.2f€", pedido.getTotal()));
        total.setFont(new Font("SansSerif", Font.BOLD, 14));
        total.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(total, BorderLayout.CENTER);

        JPanel imagenes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        imagenes.setOpaque(false);
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            JLabel imgProducto = new JLabel("📦");
            imgProducto.setFont(new Font("SansSerif", Font.PLAIN, 16));
            imagenes.add(imgProducto);
        }
        tarjeta.add(imagenes, BorderLayout.EAST);

        return tarjeta;
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

        List<Intercambio> intercambios = mainFrame.getIntercambios();
        if (intercambios.isEmpty()) {
            JLabel vacio = new JLabel("Aún no tienes intercambios realizados.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Intercambio intercambio : intercambios) {
                lista.add(crearTarjetaIntercambio(intercambio));
                lista.add(Box.createVerticalStrut(12));
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
        tarjeta.setLayout(new BorderLayout(12, 12));
        tarjeta.setBorder(new EmptyBorder(12, 12, 12, 12));

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

        JLabel prodOfrecido = new JLabel("📦");
        prodOfrecido.setFont(new Font("SansSerif", Font.PLAIN, 24));
        intercambio_visual.add(prodOfrecido);

        JLabel flecha = new JLabel("↔");
        flecha.setFont(new Font("SansSerif", Font.PLAIN, 18));
        flecha.setForeground(UiStyle.COLOR_TEXTO);
        intercambio_visual.add(flecha);

        JLabel prodDeseado = new JLabel("📦");
        prodDeseado.setFont(new Font("SansSerif", Font.PLAIN, 24));
        intercambio_visual.add(prodDeseado);

        tarjeta.add(intercambio_visual, BorderLayout.CENTER);

        return tarjeta;
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

        panel.add(Box.createVerticalGlue());

        JButton btnGuardar = new UiStyle.RoundedButton("Guardar cambios", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 18);
        btnGuardar.setPreferredSize(new Dimension(180, 40));
        btnGuardar.setMaximumSize(new Dimension(180, 40));
        btnGuardar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnGuardar.addActionListener(e -> mainFrame.cambiarNombreCliente(txtNuevoNombre.getText()));
        panel.add(btnGuardar);

        vista.add(panel, BorderLayout.NORTH);
        return vista;
    }

    private void abrirDialogoCambiarNombre() {
        String nuevoNombre = javax.swing.JOptionPane.showInputDialog(mainFrame,
                "Ingresa tu nuevo nombre:",
                mainFrame.getClienteActual().getNombre());
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            mainFrame.cambiarNombreCliente(nuevoNombre);
            refrescar();
        }
    }

    private void abrirDialogoCambiarContraseña() {
        javax.swing.JOptionPane.showMessageDialog(mainFrame,
                "Funcionalidad de cambio de contraseña no disponible en este momento.",
                "Cambiar contraseña",
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }
}
