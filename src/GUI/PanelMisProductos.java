package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoSegundaMano;
import usuarios.ClienteRegistrado;
import utilidades.EstadoProducto;

/**
 * Panel de cartera del cliente registrado.
 *
 * <p>Permite consultar productos subidos, valorados y publicados. Las acciones
 * de subir, solicitar valoracion y publicar se exponen mediante listeners para
 * que {@link Main} las conecte con el modelo.</p>
 */
public class PanelMisProductos extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int TAB_SUBIDOS = 0;
    private static final int TAB_VALORADOS = 1;
    private static final int TAB_PUBLICADOS = 2;

    private final Main mainFrame;
    private ClienteRegistrado cliente;
    private final JPanel panelGrid;
    private final JButton[] botonesTab;

    private int tabActivo = TAB_SUBIDOS;
    private ActionListener listenerSubir;
    private ActionListener listenerPedirValoracion;
    private ActionListener listenerPublicar;

    /**
     * Crea el panel de cartera para el cliente indicado.
     *
     * @param cliente cliente propietario de la cartera
     */
    public PanelMisProductos(Main mainFrame, ClienteRegistrado cliente) {
        this.mainFrame = mainFrame;
        this.cliente = cliente;
        this.panelGrid = new JPanel();
        this.botonesTab = new JButton[3];

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(crearCabeceraSimple(), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
    }

    /**
     * Constructor de compatibilidad para pruebas visuales aisladas.
     *
     * @param cliente cliente propietario de la cartera
     */
    public PanelMisProductos(ClienteRegistrado cliente) {
        this(null, cliente);
    }

    /**
     * Registra el listener del boton de subida.
     *
     * @param listener listener a ejecutar
     */
    public void addListenerSubirProducto(ActionListener listener) {
        this.listenerSubir = listener;
    }

    /**
     * Registra el listener para solicitar valoracion.
     *
     * @param listener listener a ejecutar con el producto como source
     */
    public void addListenerPedirValoracion(ActionListener listener) {
        this.listenerPedirValoracion = listener;
    }

    /**
     * Registra el listener para publicar un producto valorado.
     *
     * @param listener listener a ejecutar con el producto como source
     */
    public void addListenerPublicar(ActionListener listener) {
        this.listenerPublicar = listener;
    }

    /**
     * Actualiza el grid de productos.
     */
    public void refrescar() {
        actualizarGrid();
    }

    /**
     * Cambia el cliente cuyas tarjetas se muestran.
     *
     * @param cliente nuevo cliente activo
     */
    public void setCliente(ClienteRegistrado cliente) {
        this.cliente = cliente;
        refrescar();
    }

    private JPanel crearCabeceraSimple() {
        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(UiStyle.COLOR_FONDO);
        if (mainFrame != null) {
            contenedor.add(new HomePanel.PanelNavegacionCliente(mainFrame, "MIS PRODUCTOS"), BorderLayout.NORTH);
        } else {
            JLabel titulo = new JLabel("Mis productos", SwingConstants.CENTER);
            titulo.setFont(new Font("SansSerif", Font.BOLD, 24));
            titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            contenedor.setBackground(UiStyle.COLOR_CABECERA);
            contenedor.add(titulo, BorderLayout.CENTER);
        }
        return contenedor;
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(UiStyle.COLOR_FONDO);
        cuerpo.add(crearBarraTabs(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(panelGrid,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        cuerpo.add(scroll, BorderLayout.CENTER);

        actualizarGrid();
        return cuerpo;
    }

    private JPanel crearBarraTabs() {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 14));
        barra.setBackground(UiStyle.COLOR_FONDO);

        String[] etiquetas = {"Subidos", "Valorados", "Publicados"};
        for (int i = 0; i < etiquetas.length; i++) {
            final int indice = i;
            JButton boton = new JButton(etiquetas[i]);
            boton.setPreferredSize(new Dimension(150, 34));
            boton.setFocusPainted(false);
            boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            boton.addActionListener(e -> {
                tabActivo = indice;
                actualizarTabs();
                actualizarGrid();
            });
            botonesTab[i] = boton;
            barra.add(boton);
        }
        actualizarTabs();
        return barra;
    }

    private void actualizarTabs() {
        for (int i = 0; i < botonesTab.length; i++) {
            JButton boton = botonesTab[i];
            boton.setBackground(i == tabActivo ? UiStyle.COLOR_CABECERA : UiStyle.COLOR_FONDO);
            boton.setForeground(i == tabActivo ? UiStyle.COLOR_TEXTO_CLARO : UiStyle.COLOR_TEXTO);
            boton.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_CABECERA));
        }
    }

    private void actualizarGrid() {
        panelGrid.removeAll();
        panelGrid.setBackground(UiStyle.COLOR_FONDO);
        panelGrid.setBorder(new EmptyBorder(20, 40, 20, 40));

        List<ProductoSegundaMano> filtrados = filtrarPorTab(cliente.getCartera().getProductos());
        int columnas = 3;
        int total = filtrados.size() + (tabActivo == TAB_SUBIDOS ? 1 : 0);
        int filas = Math.max(1, (int) Math.ceil(total / (double) columnas));
        panelGrid.setLayout(new GridLayout(filas, columnas, 12, 18));

        if (total == 0) {
            JLabel vacio = new JLabel("No hay productos en esta seccion.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            panelGrid.add(vacio);
            for (int i = 1; i < filas * columnas; i++) {
                JPanel relleno = new JPanel();
                relleno.setBackground(UiStyle.COLOR_FONDO);
                panelGrid.add(relleno);
            }
            panelGrid.revalidate();
            panelGrid.repaint();
            return;
        }

        for (ProductoSegundaMano producto : filtrados) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            envoltura.setBackground(UiStyle.COLOR_FONDO);
            envoltura.add(new TarjetaSegundaMano(producto, resolverListener(producto)));
            panelGrid.add(envoltura);
        }

        if (tabActivo == TAB_SUBIDOS) {
            JPanel envoltura = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            envoltura.setBackground(UiStyle.COLOR_FONDO);
            envoltura.add(new TarjetaSubirProducto(e -> {
                if (listenerSubir != null) {
                    listenerSubir.actionPerformed(e);
                }
            }));
            panelGrid.add(envoltura);
        }

        for (int i = total; i < filas * columnas; i++) {
            JPanel vacio = new JPanel();
            vacio.setBackground(UiStyle.COLOR_FONDO);
            panelGrid.add(vacio);
        }

        panelGrid.revalidate();
        panelGrid.repaint();
    }

    private List<ProductoSegundaMano> filtrarPorTab(List<ProductoSegundaMano> productos) {
        if (tabActivo == TAB_VALORADOS) {
            return productos.stream()
                    .filter(p -> p.getEstadoProducto() == EstadoProducto.VALORADO)
                    .collect(Collectors.toList());
        }
        if (tabActivo == TAB_PUBLICADOS) {
            return productos.stream().filter(ProductoSegundaMano::getDisponibilidad).collect(Collectors.toList());
        }
        return productos;
    }

    private ActionListener resolverListener(ProductoSegundaMano producto) {
        if (tabActivo == TAB_PUBLICADOS) {
            return null;
        }
        if (producto.getEstadoProducto() == EstadoProducto.VALORADO) {
            return e -> {
                if (listenerPublicar != null) {
                    listenerPublicar.actionPerformed(new ActionEvent(producto, ActionEvent.ACTION_PERFORMED, "publicar"));
                }
            };
        }
        return e -> {
            if (listenerPedirValoracion != null) {
                listenerPedirValoracion.actionPerformed(
                        new ActionEvent(producto, ActionEvent.ACTION_PERFORMED, "valorar"));
            }
        };
    }
}
