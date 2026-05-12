package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.EmptyBorder;

import intercambios.Intercambio;
import intercambios.Oferta;
import productos.ProductoSegundaMano;
import utilidades.EstadoOferta;
import utilidades.EstadoProducto;


/**
 * Componente Swing de la interfaz grafica correspondiente a PanelIntercambios.
 */
public class PanelIntercambios extends JPanel {

    private static final long serialVersionUID = 1L;
    /** Dato interno asociado a TAB_LANZAR. */
    private static final String TAB_LANZAR = "LANZAR OFERTA";
    /** Dato interno asociado a TAB_OFERTAS. */
    private static final String TAB_OFERTAS = "OFERTAS";

    /** Dato interno asociado a mainFrame. */
    private final Main mainFrame;
    /** Dato interno asociado a panelContenido. */
    private final JPanel panelContenido;
    /** Dato interno asociado a btnLanzarOferta. */
    private final JButton btnLanzarOferta;
    /** Dato interno asociado a btnOfertas. */
    private final JButton btnOfertas;
    /** Dato interno asociado a txtBuscar. */
    private final JTextField txtBuscar;
    /** Formatea las fechas mostradas en los intercambios. */
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    /** Dato interno asociado a tabActivo. */
    private String tabActivo = TAB_LANZAR;
    /** Dato interno asociado a terminoBusqueda. */
    private String terminoBusqueda = "";
    /** Dato interno asociado a mercado. */
    private List<ProductoSegundaMano> mercado;
    /** Dato interno asociado a productoSeleccionado. */
    private ProductoSegundaMano productoSeleccionado;
    /** Dato interno asociado a eligiendoProductoPropio. */
    private boolean eligiendoProductoPropio = false;

    /**
     * Construye una instancia de PanelIntercambios.
     * @param mainFrame valor recibido por el metodo
     */
    public PanelIntercambios(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.panelContenido = new JPanel(new BorderLayout());
        this.btnLanzarOferta = crearTabButton(TAB_LANZAR);
        this.btnOfertas = crearTabButton(TAB_OFERTAS);
        this.txtBuscar = new JTextField(18);
        this.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            /**
             * Gestiona la accion de insertUpdate.
             * @param e valor recibido por el metodo
             */
            public void insertUpdate(DocumentEvent e) {
                actualizarBusqueda();
            }

            @Override
            /**
             * Gestiona la accion de removeUpdate.
             * @param e valor recibido por el metodo
             */
            public void removeUpdate(DocumentEvent e) {
                actualizarBusqueda();
            }

            @Override
            /**
             * Gestiona la accion de changedUpdate.
             * @param e valor recibido por el metodo
             */
            public void changedUpdate(DocumentEvent e) {
                actualizarBusqueda();
            }
        });

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "INTERCAMBIOS"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Gestiona la accion de refrescar.
     */
    public void refrescar() {
        mainFrame.actualizarIntercambiosCaducados();
        actualizarFiltro();
        construirVista();
    }

    private void actualizarBusqueda() {
        terminoBusqueda = txtBuscar.getText().trim();
        SwingUtilities.invokeLater(() -> {
            actualizarFiltro();
            construirVista();
            if (TAB_LANZAR.equals(tabActivo) && productoSeleccionado == null) {
                txtBuscar.requestFocusInWindow();
                txtBuscar.setCaretPosition(txtBuscar.getText().length());
            }
        });
    }

    private void actualizarFiltro() {
        mercado = new ArrayList<>(mainFrame.getProductosSegundaManoDisponibles());
        mercado.removeIf(p -> mainFrame.getClienteActual().getCartera().getProductos().contains(p));
        String termino = terminoBusqueda.toLowerCase();
        if (!termino.isBlank()) {
            mercado.removeIf(p -> !(p.getNombre().toLowerCase().contains(termino)
                    || p.getDescripcion().toLowerCase().contains(termino)
                    || p.getEstadoProducto().toString().toLowerCase().contains(termino)));
        }
        if (productoSeleccionado != null && !mercado.contains(productoSeleccionado)) {
            productoSeleccionado = null;
        }
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new BorderLayout());
        contenido.setBackground(UiStyle.COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(22, 20, 22, 20));

        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        barra.setOpaque(false);
        barra.add(btnLanzarOferta);
        barra.add(btnOfertas);
        contenido.add(barra, BorderLayout.NORTH);
        contenido.add(panelContenido, BorderLayout.CENTER);
        return contenido;
    }

    private JButton crearTabButton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TARJETA, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setForeground(UiStyle.COLOR_TEXTO);
        boton.setPreferredSize(new Dimension(180, 40));
        boton.addActionListener(e -> {
            tabActivo = texto;
            if (TAB_LANZAR.equals(texto)) {
                productoSeleccionado = null;
                eligiendoProductoPropio = false;
            }
            construirVista();
        });
        return boton;
    }

    private void construirVista() {
        panelContenido.removeAll();
        actualizarBotonesTabs();
        if (TAB_LANZAR.equals(tabActivo)) {
            panelContenido.add(crearVistaLanzar(), BorderLayout.CENTER);
        } else {
            panelContenido.add(crearVistaOfertas(), BorderLayout.CENTER);
        }
        panelContenido.revalidate();
        panelContenido.repaint();
    }

    private void actualizarBotonesTabs() {
        actualizaBoton(btnLanzarOferta, TAB_LANZAR.equals(tabActivo));
        actualizaBoton(btnOfertas, TAB_OFERTAS.equals(tabActivo));
    }

    private void actualizaBoton(JButton boton, boolean activo) {
        boton.setBackground(activo ? UiStyle.COLOR_CABECERA : UiStyle.COLOR_TARJETA);
        boton.setForeground(activo ? UiStyle.COLOR_TEXTO_CLARO : UiStyle.COLOR_TEXTO);
    }

    private JPanel crearVistaLanzar() {
        if (productoSeleccionado == null) {
            return crearPanelCatalogo();
        }
        if (eligiendoProductoPropio) {
            return crearPanelSeleccionPropio();
        }
        return crearPanelDetalle(productoSeleccionado);
    }

    private JPanel crearPanelCatalogo() {
        JPanel panel = new JPanel(new BorderLayout(0, 18));
        panel.setOpaque(false);

        JPanel cabecera = crearCabeceraCatalogo("Productos disponibles");
        panel.add(cabecera, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setOpaque(false);
        if (mercado.isEmpty()) {
            JPanel aviso = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
            aviso.setOpaque(true);
            aviso.setBorder(new EmptyBorder(24, 24, 24, 24));
            aviso.setLayout(new BorderLayout());
            JLabel mensaje = new JLabel("No hay productos que coincidan con la búsqueda.", SwingConstants.CENTER);
            mensaje.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mensaje.setForeground(UiStyle.COLOR_TEXTO);
            aviso.add(mensaje, BorderLayout.CENTER);
            grid.add(aviso);
        } else {
            for (ProductoSegundaMano producto : mercado) {
                grid.add(crearTarjetaCatalogo(producto));
            }
        }

        JScrollPane scroll = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearCabeceraCatalogo(String tituloTexto) {
        JPanel cabecera = new JPanel(new BorderLayout(16, 16));
        cabecera.setOpaque(false);

        JLabel titulo = new JLabel(tituloTexto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        cabecera.add(titulo, BorderLayout.NORTH);

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buscador.setOpaque(false);
        JLabel lblBuscar = new JLabel("Buscar producto:");
        lblBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lblBuscar.setForeground(UiStyle.COLOR_TEXTO);
        buscador.add(lblBuscar);

        txtBuscar.setPreferredSize(new Dimension(240, 32));
        buscador.add(txtBuscar);
        cabecera.add(buscador, BorderLayout.SOUTH);
        return cabecera;
    }

    private JPanel crearTarjetaCatalogo(ProductoSegundaMano producto) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        tarjeta.setLayout(new BorderLayout(0, 14));
        tarjeta.setBorder(new EmptyBorder(14, 14, 14, 14));

        JLabel imagen = crearMiniatura(producto.getImagen(), 180, 160);
        tarjeta.add(imagen, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel("<html><b>" + producto.getNombre() + "</b></html>");
        nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        centro.add(nombre);
        centro.add(Box.createVerticalStrut(8));
        centro.add(crearEstrellas(producto.getValoracion()));
        centro.add(Box.createVerticalStrut(8));
        tarjeta.add(centro, BorderLayout.CENTER);

        JButton lanzar = new UiStyle.RoundedButton("Lanzar oferta", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 18);
        lanzar.setPreferredSize(new Dimension(160, 34));
        lanzar.addActionListener(e -> {
            productoSeleccionado = producto;
            eligiendoProductoPropio = false;
            construirVista();
        });
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        botonPanel.setOpaque(false);
        botonPanel.add(lanzar);
        tarjeta.add(botonPanel, BorderLayout.SOUTH);

        return tarjeta;
    }

    private JPanel crearPanelDetalle(ProductoSegundaMano producto) {
        JPanel pantalla = new JPanel(new BorderLayout(18, 0));
        pantalla.setOpaque(false);

        JPanel izquierda = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        izquierda.setOpaque(true);
        izquierda.setLayout(new BorderLayout(0, 14));
        izquierda.setBorder(new EmptyBorder(20, 20, 20, 20));
        izquierda.setPreferredSize(new Dimension(420, 0));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        topBar.setOpaque(false);
        JButton btnVolver = new UiStyle.RoundedButton("Volver al catálogo", UiStyle.COLOR_TARJETA, UiStyle.COLOR_TEXTO, 14);
        btnVolver.setPreferredSize(new Dimension(180, 34));
        btnVolver.addActionListener(e -> {
            productoSeleccionado = null;
            construirVista();
        });
        topBar.add(btnVolver);
        izquierda.add(topBar, BorderLayout.NORTH);

        izquierda.add(crearImagenGrande(producto), BorderLayout.CENTER);

        JPanel infoIzquierda = new JPanel();
        infoIzquierda.setOpaque(false);
        infoIzquierda.setLayout(new BoxLayout(infoIzquierda, BoxLayout.Y_AXIS));
        infoIzquierda.add(crearEtiquetaGrande(producto.getNombre()));
        infoIzquierda.add(Box.createVerticalStrut(10));
        infoIzquierda.add(crearEstrellas(producto.getValoracion()));
        infoIzquierda.add(Box.createVerticalStrut(18));

        JButton boton = new UiStyle.RoundedButton("Hacer oferta", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 20);
        boton.setPreferredSize(new Dimension(260, 44));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(e -> {
            eligiendoProductoPropio = true;
            construirVista();
        });
        infoIzquierda.add(boton);
        izquierda.add(infoIzquierda, BorderLayout.CENTER);

        pantalla.add(izquierda, BorderLayout.WEST);

        JPanel derecha = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        derecha.setOpaque(true);
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBorder(new EmptyBorder(32, 28, 32, 28));

        JLabel tituloDescripcion = new JLabel("Descripción");
        tituloDescripcion.setFont(new Font("SansSerif", Font.BOLD, 20));
        tituloDescripcion.setForeground(UiStyle.COLOR_TEXTO);
        derecha.add(tituloDescripcion);
        derecha.add(Box.createVerticalStrut(12));
        JLabel descripcion = new JLabel("<html>" + producto.getDescripcion() + "</html>");
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descripcion.setForeground(UiStyle.COLOR_TEXTO);
        derecha.add(descripcion);
        derecha.add(Box.createVerticalStrut(22));

        JLabel estadoTitulo = new JLabel("Estado");
        estadoTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        estadoTitulo.setForeground(UiStyle.COLOR_TEXTO);
        derecha.add(estadoTitulo);
        derecha.add(Box.createVerticalStrut(10));
        JLabel estado = new JLabel(producto.getEstadoProducto().toString());
        estado.setFont(new Font("SansSerif", Font.BOLD, 14));
        estado.setOpaque(true);
        estado.setBackground(new Color(175, 80, 62));
        estado.setForeground(Color.WHITE);
        estado.setBorder(new EmptyBorder(8, 14, 8, 14));
        estado.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(estado);

        pantalla.add(derecha, BorderLayout.CENTER);
        return pantalla;
    }

    private JPanel crearPanelSeleccionPropio() {
        JPanel pantalla = new JPanel(new BorderLayout(18, 0));
        pantalla.setOpaque(false);

        JPanel izquierda = crearPanelDetalle(productoSeleccionado);
        JPanel wrapperIzquierda = new JPanel(new BorderLayout());
        wrapperIzquierda.setOpaque(false);
        wrapperIzquierda.add(izquierda, BorderLayout.CENTER);

        JPanel derecho = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        derecho.setOpaque(true);
        derecho.setLayout(new BorderLayout(0, 18));
        derecho.setBorder(new EmptyBorder(20, 20, 20, 20));
        derecho.setPreferredSize(new Dimension(520, 0));

        JLabel titulo = new JLabel("Elige tu producto", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        derecho.add(titulo, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 18, 18));
        grid.setOpaque(false);
        List<ProductoSegundaMano> propiosValorados = new ArrayList<>();
        for (ProductoSegundaMano producto : mainFrame.getClienteActual().getCartera().getProductos()) {
            if (producto.getEstadoProducto() == EstadoProducto.VALORADO) {
                propiosValorados.add(producto);
            }
        }

        if (propiosValorados.isEmpty()) {
            JLabel mensaje = new JLabel("No tienes productos valorados en la cartera.");
            mensaje.setFont(new Font("SansSerif", Font.PLAIN, 14));
            mensaje.setForeground(UiStyle.COLOR_TEXTO);
            mensaje.setHorizontalAlignment(SwingConstants.CENTER);
            derecho.add(mensaje, BorderLayout.CENTER);
            pantalla.add(wrapperIzquierda, BorderLayout.WEST);
            pantalla.add(derecho, BorderLayout.CENTER);
            return pantalla;
        }

        for (ProductoSegundaMano producto : propiosValorados) {
            JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
            tarjeta.setLayout(new BorderLayout(0, 10));
            tarjeta.setBorder(new EmptyBorder(12, 12, 12, 12));
            tarjeta.add(crearMiniatura(producto.getImagen(), 140, 110), BorderLayout.NORTH);

            JLabel nombre = new JLabel("<html><b>" + producto.getNombre() + "</b></html>");
            nombre.setFont(new Font("SansSerif", Font.BOLD, 13));
            nombre.setForeground(UiStyle.COLOR_TEXTO);
            tarjeta.add(nombre, BorderLayout.CENTER);

            JButton elegir = new UiStyle.RoundedButton("Elegir producto", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 14);
            elegir.setPreferredSize(new Dimension(140, 32));
            elegir.addActionListener(e -> {
                mainFrame.proponerIntercambio(productoSeleccionado, producto);
                eligiendoProductoPropio = false;
                productoSeleccionado = null;
                construirVista();
            });
            JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            botonPanel.setOpaque(false);
            botonPanel.add(elegir);
            tarjeta.add(botonPanel, BorderLayout.SOUTH);
            grid.add(tarjeta);
        }

        JScrollPane scroll = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        derecho.add(scroll, BorderLayout.CENTER);

        pantalla.add(wrapperIzquierda, BorderLayout.WEST);
        pantalla.add(derecho, BorderLayout.CENTER);
        return pantalla;
    }


    private JPanel crearEstrellas(int valoracion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        panel.setOpaque(false);
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            texto.append(i < valoracion ? "★" : "☆");
        }
        JLabel label = new JLabel(texto.toString());
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(label);
        return panel;
    }


    private JLabel crearMiniatura(String ruta, int ancho, int alto) {
        JLabel label = new JLabel();
        label.setPreferredSize(new Dimension(ancho, alto));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (ruta != null && !ruta.isBlank()) {
            cargarImagenEnLabel(label, ruta, ancho, alto);
        } else {
            label.setText("SIN IMAGEN");
            label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            label.setForeground(UiStyle.COLOR_TEXTO);
        }
        return label;
    }

    private void cargarImagenEnLabel(JLabel label, String ruta, int ancho, int alto) {
        try {
            File archivo = resolverRutaImagen(ruta);
            if (archivo == null || !archivo.exists()) {
                throw new IOException("Archivo de imagen no encontrado: " + ruta);
            }
            Image imagen = ImageIO.read(archivo);
            if (imagen == null) {
                throw new IOException("No se pudo cargar la imagen: " + ruta);
            }
            imagen = imagen.getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
            label.setIcon(new javax.swing.ImageIcon(imagen));
            label.setText("");
        } catch (IOException ex) {
            label.setText("IMAGEN NO DISPONIBLE");
            label.setFont(new Font("SansSerif", Font.PLAIN, 12));
            label.setForeground(UiStyle.COLOR_TEXTO);
            label.setIcon(null);
        }
    }

    private File resolverRutaImagen(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }

        File archivoDirecto = new File(ruta);
        if (archivoDirecto.exists()) {
            return archivoDirecto;
        }

        File archivoEnFotos = new File("lib/fotos", ruta);
        if (archivoEnFotos.exists()) {
            return archivoEnFotos;
        }

        File archivoPorNombre = new File("lib/fotos", new File(ruta).getName());
        if (archivoPorNombre.exists()) {
            return archivoPorNombre;
        }

        return archivoDirecto;
    }


    private JLabel crearEtiquetaGrande(String texto) {
        JLabel label = new JLabel("<html><b>" + texto + "</b></html>");
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }


    private JLabel crearImagenGrande(ProductoSegundaMano producto) {
        JLabel imagen = new JLabel();
        imagen.setPreferredSize(new Dimension(320, 240));
        imagen.setOpaque(true);
        imagen.setBackground(UiStyle.COLOR_CABECERA);
        imagen.setHorizontalAlignment(SwingConstants.CENTER);
        imagen.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 2));

        if (producto.getImagen() != null && !producto.getImagen().isBlank()) {
            cargarImagenEnLabel(imagen, producto.getImagen(), 320, 240);
        } else {
            imagen.setText("SIN IMAGEN");
            imagen.setFont(new Font("SansSerif", Font.BOLD, 18));
            imagen.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        }
        return imagen;
    }

    private JPanel crearVistaOfertas() {
        JPanel vista = new JPanel(new BorderLayout());
        vista.setOpaque(false);
        vista.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel titulo = new JLabel("Ofertas recibidas", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        vista.add(titulo, BorderLayout.NORTH);

        JPanel lista = new JPanel();
        lista.setOpaque(false);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        List<Intercambio> intercambiosCliente = new ArrayList<>();
        for (Intercambio intercambio : mainFrame.getIntercambiosClienteActual()) {
            if (intercambio.getOferta().getUsuarioReceptor() == mainFrame.getClienteActual()) {
                intercambiosCliente.add(intercambio);
            }
        }

        if (intercambiosCliente.isEmpty()) {
            JLabel vacio = new JLabel("No hay ofertas pendientes.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Intercambio intercambio : intercambiosCliente) {
                lista.add(crearTarjetaOferta(intercambio));
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

    private JPanel crearTarjetaOferta(Intercambio intercambio) {
        Oferta oferta = intercambio.getOferta();
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        tarjeta.setLayout(new BorderLayout(12, 12));
        tarjeta.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Oferta de " + oferta.getUsuarioLanzador().getNombre(), SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(titulo, BorderLayout.NORTH);

        JPanel cuerpo = new JPanel(new BorderLayout(12, 12));
        cuerpo.setOpaque(false);

        JPanel columnas = new JPanel(new GridLayout(1, 3, 12, 12));
        columnas.setOpaque(false);

        JPanel panelDeseado = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 16);
        panelDeseado.setLayout(new BorderLayout(6, 6));
        panelDeseado.setOpaque(true);
        panelDeseado.add(crearMiniatura(oferta.getProductoDeseado().getImagen(), 100, 80), BorderLayout.NORTH);
        JLabel textoDeseado = new JLabel("<html><center>Tu producto<br>" + oferta.getProductoDeseado().getNombre() + "</center></html>");
        textoDeseado.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textoDeseado.setForeground(UiStyle.COLOR_TEXTO);
        panelDeseado.add(textoDeseado, BorderLayout.SOUTH);

        JPanel panelFlecha = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        panelFlecha.setOpaque(false);
        JLabel flecha = new JLabel("↔");
        flecha.setFont(new Font("SansSerif", Font.PLAIN, 20));
        flecha.setForeground(UiStyle.COLOR_TEXTO);
        panelFlecha.add(flecha);

        JPanel panelOfrecido = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 16);
        panelOfrecido.setLayout(new BorderLayout(6, 6));
        panelOfrecido.setOpaque(true);
        panelOfrecido.add(crearMiniatura(oferta.getProductoOfertado().getImagen(), 100, 80), BorderLayout.NORTH);
        JLabel textoOfrecido = new JLabel("<html><center>Ofrecen<br>" + oferta.getProductoOfertado().getNombre() + "</center></html>");
        textoOfrecido.setFont(new Font("SansSerif", Font.PLAIN, 12));
        textoOfrecido.setForeground(UiStyle.COLOR_TEXTO);
        panelOfrecido.add(textoOfrecido, BorderLayout.SOUTH);

        columnas.add(panelDeseado);
        columnas.add(panelFlecha);
        columnas.add(panelOfrecido);
        cuerpo.add(columnas, BorderLayout.CENTER);

        JLabel detalles = new JLabel("Estado: " + oferta.getEstadoOferta()
                + " - Fecha: " + formatoFecha.format(intercambio.getFechaOferta())
                + " - " + textoCuentaAtras(intercambio));
        detalles.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detalles.setForeground(UiStyle.COLOR_TEXTO);
        cuerpo.add(detalles, BorderLayout.SOUTH);

        tarjeta.add(cuerpo, BorderLayout.CENTER);

        if (oferta.getEstadoOferta() == EstadoOferta.PENDIENTE) {
            JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            botones.setOpaque(false);
            JButton rechazar = crearAccionOferta("Rechazar", e -> {
                mainFrame.rechazarIntercambio(intercambio);
                refrescar();
            });
            JButton aceptar = crearAccionOferta("Aceptar", e -> {
                mainFrame.aceptarIntercambio(intercambio);
                refrescar();
            });
            botones.add(rechazar);
            botones.add(aceptar);
            tarjeta.add(botones, BorderLayout.EAST);
        }
        return tarjeta;
    }

    private JButton crearAccionOferta(String texto, ActionListener listener) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 18);
        boton.setPreferredSize(new Dimension(112, 34));
        boton.addActionListener(listener);
        return boton;
    }

    private String textoCuentaAtras(Intercambio intercambio) {
        if (intercambio.getOferta().getEstadoOferta() != EstadoOferta.PENDIENTE) {
            return "Limite: " + formatoFecha.format(intercambio.getFechaLimite());
        }
        long restanteMs = intercambio.getFechaLimite().getTime() - new Date().getTime();
        if (restanteMs <= 0) {
            return "Caducada";
        }
        long totalMinutos = restanteMs / (60L * 1000L);
        long dias = totalMinutos / (24L * 60L);
        long horas = (totalMinutos % (24L * 60L)) / 60L;
        long minutos = totalMinutos % 60L;
        if (dias > 0) {
            return "Caduca en " + dias + "d " + horas + "h";
        }
        if (horas > 0) {
            return "Caduca en " + horas + "h " + minutos + "min";
        }
        return "Caduca en " + Math.max(1, minutos) + "min";
    }


}
