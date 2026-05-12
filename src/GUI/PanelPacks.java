package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.Pack;
import productos.Producto;


/**
 * Representa el componente PanelPacks de la interfaz gráfica.
 * Este panel se encarga de mostrar al usuario una cuadrícula con todos los packs
 * de productos disponibles, permitiendo añadirlos a la cesta de la compra.
 */
public class PanelPacks extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Referencia al controlador de la ventana principal de la aplicación. */
    private final Main mainFrame;
    
    /** Panel contenedor que agrupa y muestra las tarjetas de los packs usando un diseño de cuadrícula. */
    private final JPanel grid;

    /**
     * Construye la pantalla de visualización de los packs.
     * Inicializa los componentes, establece el layout principal y añade
     * la barra de navegación superior y el área de contenido.
     *
     * @param mainFrame controlador principal de la interfaz gráfica
     */
    public PanelPacks(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.grid = new JPanel();
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "PACKS"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Reconstruye dinámicamente las tarjetas de los packs.
     * Limpia la cuadrícula actual, obtiene la lista actualizada de packs desde
     * el controlador principal y genera una nueva tarjeta visual para cada uno de ellos.
     */
    public void refrescar() {
        grid.removeAll();
        grid.setBackground(UiStyle.COLOR_FONDO);
        grid.setBorder(new EmptyBorder(24, 34, 24, 34));
        grid.setLayout(new GridLayout(0, 3, 18, 18));

        for (Pack pack : mainFrame.getPacks()) {
            grid.add(crearTarjetaPack(pack));
        }

        grid.revalidate();
        grid.repaint();
    }

    /**
     * Crea un panel de desplazamiento (scroll) que envuelve a la cuadrícula de packs.
     * Esto permite navegar por los packs si hay demasiados para caber en la pantalla.
     *
     * @return un {@code JScrollPane} configurado que contiene la cuadrícula principal
     */
    private JScrollPane crearContenido() {
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        return scroll;
    }

    /**
     * Crea un componente visual (tarjeta) para representar un pack individual.
     * La tarjeta muestra el nombre del pack, la lista de productos que incluye,
     * el precio total y un botón para añadir el pack a la cesta de la compra.
     *
     * @param pack el objeto de datos {@code Pack} que se va a representar visualmente
     * @return un {@code JPanel} formateado y estilizado como una tarjeta de producto
     */
    private JPanel crearTarjetaPack(Pack pack) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_CABECERA, 28);
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBorder(new EmptyBorder(18, 18, 18, 18));
        tarjeta.setPreferredSize(new Dimension(220, 260));

        JLabel titulo = new JLabel(pack.getNombre(), SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        JLabel precio = new JLabel(String.format("%.2f EUR", pack.getPrecio()), SwingConstants.CENTER);
        precio.setFont(new Font("SansSerif", Font.BOLD, 24));
        precio.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        precio.setAlignmentX(CENTER_ALIGNMENT);

        JPanel productos = new JPanel();
        productos.setOpaque(false);
        productos.setLayout(new BoxLayout(productos, BoxLayout.Y_AXIS));
        productos.setAlignmentX(CENTER_ALIGNMENT);
        for (Producto producto : pack.getProductos()) {
            JLabel linea = new JLabel("- " + producto.getNombre());
            linea.setFont(new Font("SansSerif", Font.PLAIN, 12));
            linea.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            productos.add(linea);
        }

        JButton boton = new UiStyle.RoundedButton("Anadir a la cesta", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(170, 34));
        boton.setMaximumSize(new Dimension(170, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setAlignmentX(CENTER_ALIGNMENT);
        boton.addActionListener(e -> mainFrame.anadirPackACesta(pack));

        tarjeta.add(titulo);
        tarjeta.add(Box.createVerticalStrut(12));
        tarjeta.add(productos);
        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(precio);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(boton);
        return new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)) {
            private static final long serialVersionUID = 1L;
            {
                setOpaque(false);
                add(tarjeta);
            }
        };
    }
}