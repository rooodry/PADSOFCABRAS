package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.*;

/**
 * Representa el componente de la interfaz gráfica que muestra la cesta de la compra.
 * Se encarga de visualizar los productos y packs añadidos por el cliente, calcular
 * los precios finales aplicando descuentos y promociones (como el 2x1), y proporcionar
 * la opción de finalizar la compra.
 */
public class PanelCesta extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Referencia al controlador principal de la aplicación. */
    private final Main mainFrame;
    
    /** Panel contenedor donde se listan dinámicamente los productos y packs de la cesta. */
    private final JPanel listaProductos;
    
    /** Etiqueta gráfica que muestra el precio total acumulado de la cesta. */
    private final JLabel lblTotal;

    /**
     * Crea y configura el panel de la cesta de la compra.
     *
     * @param mainFrame Controlador principal de la aplicación que gestiona el estado y la navegación.
     */
    public PanelCesta(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.listaProductos = new JPanel(new GridBagLayout());
        this.lblTotal = new JLabel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "CESTA"), BorderLayout.NORTH);

        JPanel contenedorCentral = new JPanel(new BorderLayout());
        contenedorCentral.setBackground(UiStyle.COLOR_FONDO);
        contenedorCentral.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel derecha = crearPanelDerecha();
        contenedorCentral.add(derecha, BorderLayout.CENTER);

        add(contenedorCentral, BorderLayout.CENTER);
        add(crearResumen(), BorderLayout.SOUTH);
        refrescar();
    }

    /**
     * Crea el panel con la barra de desplazamiento que contiene la lista de productos.
     *
     * @return El panel configurado con un {@link JScrollPane}.
     */
    private JPanel crearPanelDerecha() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UiStyle.COLOR_FONDO);

        JScrollPane scroll = new JScrollPane(listaProductos);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Actualiza la vista de la cesta.
     * Limpia la lista actual, recupera los datos de los productos y packs del cliente,
     * recalcula el precio total aplicando los descuentos correspondientes y vuelve a renderizar la interfaz.
     */
    public void refrescar() {
        listaProductos.removeAll();
        listaProductos.setBackground(UiStyle.COLOR_FONDO);
        listaProductos.setBorder(new EmptyBorder(12, 12, 12, 12));

        Map<ProductoTienda, Integer> productos = mainFrame.getClienteActual().getCesta().getProductos();
        Map<Pack, Integer> packs = mainFrame.getClienteActual().getCesta().getPacks();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 12, 0);

        double total = 0.0;

        if (productos.isEmpty() && packs.isEmpty()) {
            JLabel vacia = new JLabel("La cesta está vacía.", SwingConstants.CENTER);
            vacia.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacia.setForeground(UiStyle.COLOR_TEXTO);
            listaProductos.add(vacia, gbc);
        } else {
            for (Map.Entry<ProductoTienda, Integer> entrada : productos.entrySet()) {
                ProductoTienda producto = entrada.getKey();
                int cantidad = entrada.getValue();

                double precioTotal = precioUnitarioFinal(producto) * unidadesAPagar(producto, cantidad);
                total += precioTotal;

                Pack packAsociado = mainFrame.getPackAsociadoEnCesta(producto);
                JPanel tarjeta = packAsociado == null
                        ? crearTarjetaProducto(producto, cantidad, precioTotal)
                        : crearTarjetaPack(packAsociado, cantidad, precioTotal, producto);

                listaProductos.add(tarjeta, gbc);
                gbc.gridy++;
            }

            for (Map.Entry<Pack, Integer> entrada : packs.entrySet()) {
                Pack pack = entrada.getKey();
                int cantidad = entrada.getValue();

                double precioTotal = pack.getPrecio() * cantidad;
                total += precioTotal;

                listaProductos.add(
                        crearTarjetaPack(pack, cantidad, precioTotal, null),
                        gbc);
                gbc.gridy++;
            }
        }

        GridBagConstraints relleno = new GridBagConstraints();
        relleno.gridx = 0;
        relleno.gridy = gbc.gridy;
        relleno.weightx = 1;
        relleno.weighty = 1;
        relleno.fill = GridBagConstraints.BOTH;
        JPanel espacio = new JPanel();
        espacio.setBackground(UiStyle.COLOR_FONDO);
        listaProductos.add(espacio, relleno);

        lblTotal.setText(String.format("Total  %.2f€", total));

        listaProductos.revalidate();
        listaProductos.repaint();
    }

    /**
     * Crea un panel visual (tarjeta) para representar un producto individual en la cesta.
     *
     * @param producto    El producto a mostrar.
     * @param cantidad    La cantidad de unidades seleccionadas de dicho producto.
     * @param precioTotal El precio total calculado para estas unidades.
     * @return El panel gráfico configurado.
     */
    private JPanel crearTarjetaProducto(ProductoTienda producto, int cantidad, double precioTotal) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 12);
        tarjeta.setLayout(new BorderLayout(12, 0));
        tarjeta.setBorder(new EmptyBorder(10, 10, 10, 10));
        tarjeta.setPreferredSize(new Dimension(300, 100));

        JLabel imagenLabel = new JLabel();
        imagenLabel.setPreferredSize(new Dimension(80, 80));
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setVerticalAlignment(SwingConstants.CENTER);
        cargarImagenMiniatura(imagenLabel, producto.getImagen());
        tarjeta.add(imagenLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BorderLayout(0, 4));
        infoPanel.setBackground(UiStyle.COLOR_TARJETA);
        infoPanel.setOpaque(false);

        JLabel nombre = new JLabel(producto.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        infoPanel.add(nombre, BorderLayout.NORTH);

        JLabel cantidadLabel = new JLabel(cantidad + " ud.");
        cantidadLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cantidadLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(cantidadLabel, BorderLayout.CENTER);

        JLabel precioLabel = new JLabel(String.format("%.2f€", precioTotal));
        precioLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        precioLabel.setForeground(UiStyle.COLOR_MARRON_MEDIO);
        infoPanel.add(precioLabel, BorderLayout.SOUTH);

        tarjeta.add(infoPanel, BorderLayout.CENTER);

        JButton retirar = crearBotonRetirar();
        retirar.addActionListener(e -> mainFrame.retirarProductoDeCesta(producto));
        tarjeta.add(retirar, BorderLayout.EAST);

        return tarjeta;
    }

    /**
     * Crea un panel visual (tarjeta) para representar un pack o un producto que forma parte de un pack.
     *
     * @param pack        El pack a representar.
     * @param cantidad    La cantidad de unidades seleccionadas.
     * @param precioTotal El precio total calculado del pack.
     * @param lineaCesta  El producto individual si la tarjeta se asocia a un producto que forma un pack; 
     *                    {@code null} si es el pack entero.
     * @return El panel gráfico configurado.
     */
    private JPanel crearTarjetaPack(Pack pack, int cantidad, double precioTotal, ProductoTienda lineaCesta) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 12);

        tarjeta.setLayout(new BorderLayout(12, 0));
        tarjeta.setBorder(new EmptyBorder(10, 10, 10, 10));
        tarjeta.setPreferredSize(new Dimension(300, 100));

        JLabel iconoPack = new JLabel("PACK", SwingConstants.CENTER);
        iconoPack.setPreferredSize(new Dimension(80, 80));
        iconoPack.setFont(new Font("SansSerif", Font.BOLD, 13));
        iconoPack.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        iconoPack.setOpaque(true);
        iconoPack.setBackground(UiStyle.COLOR_MARRON_MEDIO);
        tarjeta.add(iconoPack, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new BorderLayout(0, 4));
        infoPanel.setOpaque(false);

        JLabel nombre = new JLabel(pack.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        infoPanel.add(nombre, BorderLayout.NORTH);

        JLabel cantidadLabel = new JLabel(cantidad + " ud.");
        cantidadLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        cantidadLabel.setForeground(new Color(100, 100, 100));
        infoPanel.add(cantidadLabel, BorderLayout.CENTER);

        JPanel panelInferior = new JPanel(new BorderLayout(8, 0));
        panelInferior.setOpaque(false);

        JLabel precioLabel = new JLabel(String.format("%.2f€", precioTotal));
        precioLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        precioLabel.setForeground(UiStyle.COLOR_MARRON_MEDIO);
        panelInferior.add(precioLabel, BorderLayout.WEST);

        JButton botonVerPack = new UiStyle.RoundedButton("Ver pack", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 12);
        botonVerPack.setFocusPainted(false);
        botonVerPack.setPreferredSize(new Dimension(92, 30));
        botonVerPack.setFont(new Font("SansSerif", Font.BOLD, 11));
        botonVerPack.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botonVerPack.addActionListener(e -> verPack(pack));
        panelInferior.add(botonVerPack, BorderLayout.EAST);

        infoPanel.add(panelInferior, BorderLayout.SOUTH);

        tarjeta.add(infoPanel, BorderLayout.CENTER);

        JButton retirar = crearBotonRetirar();
        retirar.addActionListener(e -> {
            if (lineaCesta == null) {
                mainFrame.retirarPackDeCesta(pack);
            } else {
                mainFrame.retirarProductoDeCesta(lineaCesta);
            }
        });

        tarjeta.add(retirar, BorderLayout.EAST);

        return tarjeta;
    }

    /**
     * Crea un botón estandarizado con un ícono "X" para retirar elementos de la cesta.
     *
     * @return El botón de retiro configurado.
     */
    private JButton crearBotonRetirar() {
        JButton retirar = new UiStyle.RoundedButton("X", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        retirar.setFocusPainted(false);
        retirar.setPreferredSize(new Dimension(52, 52));
        retirar.setMinimumSize(new Dimension(52, 52));
        retirar.setFont(new Font("SansSerif", Font.BOLD, 24));
        retirar.setMargin(new Insets(0, 0, 0, 0));
        retirar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return retirar;
    }

    /**
     * Despliega un cuadro de diálogo que muestra el contenido detallado de un pack
     * (productos y subpacks incluidos).
     *
     * @param pack El pack cuyos detalles se desean visualizar.
     */
    private void verPack(Pack pack) {
        JPanel panelPack = new JPanel(new GridBagLayout());
        panelPack.setBackground(UiStyle.COLOR_FONDO);
        panelPack.setBorder(new EmptyBorder(12, 12, 12, 12));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 10, 0);

        if (pack.getProductos().isEmpty() && pack.getSubpacks().isEmpty()) {
            JLabel vacio = new JLabel("Este pack no contiene productos.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 14));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            panelPack.add(vacio, gbc);
        } else {
            JLabel categoria = new JLabel("Categoría del pack: " + categoriaPack(pack));
            categoria.setFont(new Font("SansSerif", Font.BOLD, 14));
            categoria.setForeground(UiStyle.COLOR_TEXTO);
            panelPack.add(categoria, gbc);
            gbc.gridy++;
            gbc.gridy = agregarLineasPack(pack, panelPack, gbc, gbc.gridy);
        }

        JScrollPane scroll = new JScrollPane(panelPack);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.setPreferredSize(new Dimension(360, 300));

        JOptionPane.showMessageDialog(
                this,
                scroll,
                pack.getNombre(),
                JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Método recursivo auxiliar para agregar las líneas visuales de los productos y subpacks 
     * dentro de la vista de detalle de un pack.
     *
     * @param pack      El pack que se está procesando.
     * @param panelPack El panel contenedor donde se añaden los componentes gráficos.
     * @param gbc       Restricciones de diseño (GridBagConstraints).
     * @param fila      El índice de la fila actual para el layout.
     * @return El índice de la siguiente fila disponible tras añadir los componentes.
     */
    private int agregarLineasPack(Pack pack, JPanel panelPack, GridBagConstraints gbc, int fila) {
        Map<ProductoTienda, Integer> cantidades = new LinkedHashMap<>();
        for (ProductoTienda producto : pack.getProductos()) {
            cantidades.merge(producto, 1, Integer::sum);
        }
        for (Map.Entry<ProductoTienda, Integer> entry : cantidades.entrySet()) {
            gbc.gridy = fila++;
            panelPack.add(crearLineaProductoPack(entry.getKey(), entry.getValue()), gbc);
        }

        for (Pack subpack : pack.getSubpacks()) {
            fila = agregarLineasPack(subpack, panelPack, gbc, fila);
        }

        return fila;
    }

    /**
     * Crea una tarjeta gráfica minimizada para mostrar la información de un producto 
     * como parte del contenido de un pack.
     *
     * @param producto El producto a representar.
     * @param cantidad La cantidad de ese producto dentro del pack.
     * @return El panel gráfico configurado.
     */
    private JPanel crearLineaProductoPack(ProductoTienda producto, int cantidad) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 12);
        tarjeta.setLayout(new BorderLayout(12, 0));
        tarjeta.setBorder(new EmptyBorder(10, 10, 10, 10));
        tarjeta.setPreferredSize(new Dimension(320, 90));

        JLabel imagenLabel = new JLabel();
        imagenLabel.setPreferredSize(new Dimension(70, 70));
        imagenLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagenLabel.setVerticalAlignment(SwingConstants.CENTER);
        cargarImagenMiniatura(imagenLabel, producto.getImagen());
        tarjeta.add(imagenLabel, BorderLayout.WEST);

        JPanel infoPanel = new JPanel(new BorderLayout(0, 4));
        infoPanel.setOpaque(false);

        JLabel nombre = new JLabel(cantidad > 1 ? producto.getNombre() + " x" + cantidad : producto.getNombre());
        nombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        nombre.setForeground(UiStyle.COLOR_TEXTO);
        infoPanel.add(nombre, BorderLayout.NORTH);

        JLabel precio = new JLabel(String.format("%.2f€", precioUnitarioFinal(producto)));
        precio.setFont(new Font("SansSerif", Font.BOLD, 14));
        precio.setForeground(UiStyle.COLOR_MARRON_MEDIO);
        JLabel categoria = new JLabel("Categoría: " + categoriaProducto(producto));
        categoria.setFont(new Font("SansSerif", Font.PLAIN, 12));
        categoria.setForeground(new Color(100, 100, 100));
        infoPanel.add(categoria, BorderLayout.CENTER);
        infoPanel.add(precio, BorderLayout.SOUTH);

        tarjeta.add(infoPanel, BorderLayout.CENTER);

        return tarjeta;
    }

    /**
     * Obtiene la cadena de texto correspondiente a la categoría o categorías a las que pertenece el producto.
     *
     * @param producto El producto a consultar.
     * @return Un {@code String} con las categorías del producto separadas por comas o "sin categoría".
     */
    private String categoriaProducto(ProductoTienda producto) {
        List<String> categorias = producto.getCategoriasTexto();
        if (!categorias.isEmpty()) {
            return String.join(", ", categorias);
        }
        return producto.getCategoria() == null ? "sin categoría" : producto.getCategoria().getNombre();
    }

    /**
     * Obtiene la categoría asignada a un pack.
     *
     * @param pack El pack a consultar.
     * @return Un {@code String} con la categoría del pack, o "sin categoría" si no posee.
     */
    private String categoriaPack(Pack pack) {
        String categoria = pack.getCategoria();
        return categoria == null || categoria.isBlank() ? "sin categoría" : categoria;
    }

    /**
     * Lee un archivo de imagen desde una ruta y lo escala para que sirva de miniatura (80x80 píxeles).
     * Si no encuentra el archivo, asigna un texto por defecto indicando que no hay imagen.
     *
     * @param label      El componente JLabel donde se colocará la imagen.
     * @param rutaImagen La ruta del sistema de archivos correspondiente a la imagen.
     */
    private void cargarImagenMiniatura(JLabel label, String rutaImagen) {
        try {
            if (rutaImagen != null && !rutaImagen.isBlank()) {
                BufferedImage imagen = ImageIO.read(new java.io.File(rutaImagen));
                if (imagen != null) {
                    int ancho = 80;
                    int alto = 80;
                    BufferedImage escalada = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
                    java.awt.Graphics2D g2d = escalada.createGraphics();
                    g2d.drawImage(imagen, 0, 0, ancho, alto, null);
                    g2d.dispose();
                    label.setIcon(new ImageIcon(escalada));
                    return;
                }
            }
        } catch (IOException e) {

        }
        label.setText("SIN IMAGEN");
        label.setFont(new Font("SansSerif", Font.PLAIN, 10));
        label.setForeground(new Color(150, 150, 150));
    }

    /**
     * Crea el panel inferior de la vista que muestra el importe total de la compra 
     * e incluye el botón para finalizar la transacción.
     *
     * @return El panel de resumen de compra.
     */
    private JPanel crearResumen() {
        JPanel resumen = new JPanel(new BorderLayout());
        resumen.setBackground(UiStyle.COLOR_CABECERA);
        resumen.setBorder(new EmptyBorder(16, 40, 16, 40));

        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTotal.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        resumen.add(lblTotal, BorderLayout.WEST);

        JButton finalizar = new UiStyle.RoundedButton("Finalizar compra", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 18);
        finalizar.setFocusPainted(false);
        finalizar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        finalizar.setPreferredSize(new Dimension(170, 38));
        finalizar.addActionListener(e -> mainFrame.finalizarCompra());
        resumen.add(finalizar, BorderLayout.EAST);

        return resumen;
    }

    /**
     * Calcula el precio unitario de un producto teniendo en cuenta sus descuentos activos 
     * (ya sea porcentaje de rebaja o rebaja fija).
     *
     * @param producto El producto a evaluar.
     * @return El precio final tras aplicar las reducciones pertinentes (nunca será inferior a 0).
     */
    private double precioUnitarioFinal(ProductoTienda producto) {
        double precio = producto.getPrecio();
        if (producto.getRebajaPorcentaje() > 0) {
            precio -= precio * (producto.getRebajaPorcentaje() / 100.0);
        } else if (producto.getRebajaFija() > 0) {
            precio -= producto.getRebajaFija();
        }
        return Math.max(0, precio);
    }

    /**
     * Calcula la cantidad efectiva de unidades que el cliente debe pagar, 
     * evaluando si el producto cuenta con la promoción "2x1".
     *
     * @param producto El producto a evaluar.
     * @param cantidad La cantidad de unidades que el cliente lleva en la cesta.
     * @return El número de unidades que se cobrarán en la compra.
     */
    private int unidadesAPagar(ProductoTienda producto, int cantidad) {
        return producto.isTiene2x1() ? cantidad - (cantidad / 2) : cantidad;
    }
}