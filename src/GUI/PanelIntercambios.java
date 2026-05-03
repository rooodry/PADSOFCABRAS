package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import intercambios.Intercambio;
import intercambios.Oferta;
import productos.ProductoSegundaMano;
import utilidades.EstadoOferta;
import utilidades.EstadoProducto;

/**
 * Pantalla de intercambio más gráfica para clientes.
 */
public class PanelIntercambios extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final String TAB_LANZAR = "LANZAR OFERTA";
    private static final String TAB_OFERTAS = "OFERTAS";

    private final Main mainFrame;
    private final JPanel panelContenido;
    private final JButton btnLanzarOferta;
    private final JButton btnOfertas;
    private final JTextField txtBuscar;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    private String tabActivo = TAB_LANZAR;
    private String terminoBusqueda = "";
    private List<ProductoSegundaMano> mercado;
    private ProductoSegundaMano productoSeleccionado;
    private boolean eligiendoProductoPropio = false;

    public PanelIntercambios(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.panelContenido = new JPanel(new BorderLayout());
        this.btnLanzarOferta = crearTabButton(TAB_LANZAR);
        this.btnOfertas = crearTabButton(TAB_OFERTAS);
        this.txtBuscar = new JTextField(18);
        this.txtBuscar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarBusqueda();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarBusqueda();
            }

            @Override
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

    public void refrescar() {
        actualizarFiltro();
        construirVista();
    }

    private void actualizarBusqueda() {
        terminoBusqueda = txtBuscar.getText().trim();
        actualizarFiltro();
        construirVista();
    }

    private void actualizarFiltro() {
        mercado = new ArrayList<>(mainFrame.getProductosSegundaManoDisponibles());
        String termino = terminoBusqueda.toLowerCase();
        if (!termino.isBlank()) {
            mercado.removeIf(p -> !(p.getNombre().toLowerCase().contains(termino)
                    || p.getDescripcion().toLowerCase().contains(termino)
                    || p.getEstadoProducto().toString().toLowerCase().contains(termino)));
        }
        if (productoSeleccionado == null || !mercado.contains(productoSeleccionado)) {
            productoSeleccionado = mercado.isEmpty() ? null : mercado.get(0);
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
        JPanel vista = new JPanel(new BorderLayout(18, 0));
        vista.setOpaque(false);
        vista.add(crearPanelSeleccion(), BorderLayout.WEST);
        vista.add(crearPanelMercado(), BorderLayout.CENTER);
        return vista;
    }

    private JPanel crearPanelSeleccion() {
        JPanel seleccion = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        seleccion.setLayout(new BorderLayout(0, 18));
        seleccion.setBorder(new EmptyBorder(20, 20, 20, 20));
        seleccion.setPreferredSize(new Dimension(380, 0));

        JLabel titulo = new JLabel("Producto seleccionado", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        seleccion.add(titulo, BorderLayout.NORTH);

        if (productoSeleccionado == null) {
            JLabel vacio = new JLabel("No hay productos disponibles para intercambiar.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 14));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            seleccion.add(vacio, BorderLayout.CENTER);
            return seleccion;
        }

        JLabel subtitulo = new JLabel("Sigue estos pasos:");
        subtitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
        subtitulo.setForeground(UiStyle.COLOR_TEXTO);
        seleccion.add(subtitulo, BorderLayout.SOUTH);

        JPanel cuerpo = new JPanel();
        cuerpo.setOpaque(false);
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.add(crearImagenGrande(productoSeleccionado));
        cuerpo.add(Box.createVerticalStrut(14));
        cuerpo.add(crearEtiquetaGrande(productoSeleccionado.getNombre()));
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(crearEstrellas(productoSeleccionado.getValoracion()));
        cuerpo.add(Box.createVerticalStrut(10));
        cuerpo.add(crearEtiquetaNormal(productoSeleccionado.getDescripcion()));
        cuerpo.add(Box.createVerticalStrut(12));
        cuerpo.add(crearEtiquetaNormal("Valor estimado: " + String.format("%.2f EUR", productoSeleccionado.getValorEstimado())));
        cuerpo.add(Box.createVerticalStrut(8));
        cuerpo.add(crearEtiquetaNormal("Estado: " + productoSeleccionado.getEstadoProducto()));
        cuerpo.add(Box.createVerticalStrut(16));

        JLabel ayuda = new JLabel("1. Selecciona un producto del mercado. 2. Haz clic en Hacer oferta.");
        ayuda.setFont(new Font("SansSerif", Font.PLAIN, 13));
        ayuda.setForeground(UiStyle.COLOR_TEXTO);
        ayuda.setAlignmentX(Component.CENTER_ALIGNMENT);
        cuerpo.add(ayuda);
        cuerpo.add(Box.createVerticalStrut(12));

        JButton boton = new UiStyle.RoundedButton("Hacer oferta", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 18);
        boton.setPreferredSize(new Dimension(220, 42));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.addActionListener(e -> abrirDialogoSeleccionPropio(productoSeleccionado));
        cuerpo.add(boton);

        seleccion.add(cuerpo, BorderLayout.CENTER);
        return seleccion;
    }

    private JPanel crearPanelMercado() {
        JPanel mercadoPanel = new JPanel(new BorderLayout(0, 14));
        mercadoPanel.setOpaque(false);

        JPanel cabecera = new JPanel(new BorderLayout(16, 16));
        cabecera.setOpaque(false);

        JLabel titulo = new JLabel("Productos disponibles", SwingConstants.LEFT);
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

        mercadoPanel.add(cabecera, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 18, 18));
        grid.setOpaque(false);
        if (mercado.isEmpty()) {
            JPanel aviso = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
            aviso.setOpaque(true);
            aviso.setBackground(UiStyle.COLOR_TARJETA);
            aviso.setBorder(new EmptyBorder(24, 24, 24, 24));
            aviso.setLayout(new BorderLayout());
            JLabel mensaje = new JLabel("No hay productos que coincidan con la búsqueda.", SwingConstants.CENTER);
            mensaje.setFont(new Font("SansSerif", Font.PLAIN, 16));
            mensaje.setForeground(UiStyle.COLOR_TEXTO);
            aviso.add(mensaje, BorderLayout.CENTER);
            grid.add(aviso);
        } else {
            for (ProductoSegundaMano producto : mercado) {
                grid.add(crearTarjetaMercado(producto));
            }
        }

        JScrollPane scroll = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        mercadoPanel.add(scroll, BorderLayout.CENTER);
        return mercadoPanel;
    }

    private JPanel crearTarjetaMercado(ProductoSegundaMano producto) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        tarjeta.setLayout(new BorderLayout(0, 14));
        LineBorder resaltado = new LineBorder(UiStyle.COLOR_MARRON_MEDIO, 2, true);
        tarjeta.setBorder(new CompoundBorder(producto.equals(productoSeleccionado) ? resaltado : BorderFactory.createEmptyBorder(14, 14, 14, 14),
                new EmptyBorder(14, 14, 14, 14)));

        JLabel imagen = crearMiniatura(producto.getImagen(), 180, 160);
        tarjeta.add(imagen, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        JLabel nombre = new JLabel("<html><b>" + producto.getNombre() + "</b></html>");
        nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        nombre.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(nombre);
        centro.add(Box.createVerticalStrut(8));
        centro.add(crearEstrellas(producto.getValoracion()));
        centro.add(Box.createVerticalStrut(8));
        JLabel valor = new JLabel("Valor: " + String.format("%.2f EUR", producto.getValorEstimado()));
        valor.setFont(new Font("SansSerif", Font.PLAIN, 12));
        valor.setForeground(UiStyle.COLOR_TEXTO);
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);
        centro.add(valor);
        tarjeta.add(centro, BorderLayout.CENTER);

        JButton seleccionar = new UiStyle.RoundedButton("Seleccionar", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 18);
        seleccionar.setPreferredSize(new Dimension(160, 34));
        seleccionar.addActionListener(e -> {
            productoSeleccionado = producto;
            construirVista();
        });
        JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        botonPanel.setOpaque(false);
        botonPanel.add(seleccionar);
        tarjeta.add(botonPanel, BorderLayout.SOUTH);

        return tarjeta;
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
            File archivo = new File(ruta);
            if (!archivo.exists()) {
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

    private JLabel crearEtiquetaGrande(String texto) {
        JLabel label = new JLabel("<html><b>" + texto + "</b></html>");
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JLabel crearEtiquetaNormal(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
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

        if (mainFrame.getIntercambios().isEmpty()) {
            JLabel vacio = new JLabel("No hay ofertas pendientes.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Intercambio intercambio : mainFrame.getIntercambios()) {
                lista.add(crearTarjetaOferta(intercambio));
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

        JLabel detalles = new JLabel("Estado: " + oferta.getEstadoOferta() + " · Fecha: " + formatoFecha.format(intercambio.getFechaOferta()));
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

    private void abrirDialogoSeleccionPropio(ProductoSegundaMano deseado) {
        List<ProductoSegundaMano> propios = mainFrame.getClienteActual().getCartera().getProductos();
        if (propios.isEmpty()) {
            javax.swing.JOptionPane.showMessageDialog(this,
                    "No tienes productos en la cartera para ofrecer.",
                    "Seleccionar producto", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(mainFrame, "Selecciona tu producto", true);
        dialog.setSize(840, 560);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Selecciona el producto que quieres ofrecer", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(18, 18, 18, 18));
        dialog.add(titulo, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 3, 16, 16));
        grid.setBorder(new EmptyBorder(18, 18, 18, 18));
        grid.setBackground(UiStyle.COLOR_FONDO);

        for (ProductoSegundaMano producto : propios) {
            JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
            tarjeta.setLayout(new BorderLayout(10, 10));
            tarjeta.setBorder(new EmptyBorder(10, 10, 10, 10));
            tarjeta.add(crearMiniatura(producto.getImagen(), 220, 150), BorderLayout.NORTH);

            JLabel nombre = new JLabel("<html><b>" + producto.getNombre() + "</b></html>");
            nombre.setFont(new Font("SansSerif", Font.BOLD, 14));
            nombre.setForeground(UiStyle.COLOR_TEXTO);
            tarjeta.add(nombre, BorderLayout.CENTER);

            JButton seleccionar = new UiStyle.RoundedButton("Seleccionar", UiStyle.COLOR_MARRON_MEDIO, UiStyle.COLOR_CABECERA, 18);
            seleccionar.addActionListener(e -> {
                mainFrame.proponerIntercambio(deseado, producto);
                dialog.dispose();
                refrescar();
            });
            JPanel botonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            botonPanel.setOpaque(false);
            botonPanel.add(seleccionar);
            tarjeta.add(botonPanel, BorderLayout.SOUTH);

            grid.add(tarjeta);
        }

        JScrollPane scroll = new JScrollPane(grid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        dialog.add(scroll, BorderLayout.CENTER);

        dialog.setVisible(true);
    }
}
