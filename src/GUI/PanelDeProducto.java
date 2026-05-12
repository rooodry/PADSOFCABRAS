package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;


/**
 * Representa el componente PanelDeProducto de la interfaz grafica.
 * Muestra la información detallada de un producto, sus comentarios, y permite
 * la interacción como añadir a la cesta o editar sus datos si se tiene el permiso.
 */
public class PanelDeProducto extends JPanel {

    /** Número de serie para la serialización de la clase. */
    private static final long serialVersionUID = 1L;

    /** Número máximo de estrellas para la valoración del producto. */
    private static final int MAX_ESTRELLAS = 5;
    
    /** Ancho predeterminado para el panel lateral izquierdo. */
    private static final int ANCHO_IZQUIERDA = 270;
    
    /** Color de fondo utilizado para las tarjetas de comentarios. */
    private static final Color COLOR_COMENTARIO = new Color(145, 124, 101);

    /** Producto cuyos detalles se están visualizando o editando. */
    private final ProductoTienda producto;
    
    /** Referencia al controlador principal de la interfaz. */
    private final Main mainFrame;
    
    /** Lista de acciones registradas para añadir productos a la cesta. */
    private final List<ActionListener> listenersCesta = new ArrayList<>();
    
    /** Indica si el panel está en modo edición. */
    private final boolean editable;
    
    /** Botón para añadir el producto a la cesta de la compra. */
    private JButton botonCesta;
    
    /** Campo de texto para el nombre del producto (visible en modo edición). */
    private JTextField campoNombre;
    
    /** Campo de texto para el precio del producto (visible en modo edición). */
    private JTextField campoPrecio;
    
    /** Selector numérico para establecer el stock del producto (visible en modo edición). */
    private JSpinner campoStock;
    
    /** Selector numérico para elegir la cantidad de unidades a añadir a la cesta. */
    private JSpinner campoCantidadCesta;
    
    /** Campo de texto para la ruta de la imagen del producto (visible en modo edición). */
    private JTextField campoImagen;
    
    /** Campo de texto para las categorías del producto (visible en modo edición). */
    private JTextField campoCategorias;
    
    /** Área de texto para la descripción del producto. */
    private JTextArea campoDescripcion;
    
    /** Panel con barras de desplazamiento que contiene la lista de comentarios. */
    private JScrollPane scrollComentarios;
    
    /** Listener para gestionar los eventos de confirmación o cancelación en la edición. */
    private ListenerEdicion listenerEdicion;

    /**
     * Crea un panel de detalle para el producto indicado.
     *
     * @param producto producto que se muestra
     */
    public PanelDeProducto(ProductoTienda producto) {
        this(producto, null);
    }

    /**
     * Crea un panel de detalle conectado a la ventana principal.
     *
     * @param producto producto que se muestra
     * @param mainFrame controlador principal de la interfaz
     */
    public PanelDeProducto(ProductoTienda producto, Main mainFrame) {
        this(producto, mainFrame, false);
    }

    /**
     * Crea un panel de detalle que puede permitir la edición del producto.
     *
     * @param producto producto que se muestra
     * @param mainFrame controlador principal de la interfaz
     * @param editable true si se deben mostrar campos editables
     */
    public PanelDeProducto(ProductoTienda producto, Main mainFrame, boolean editable) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        this.producto = producto;
        this.mainFrame = mainFrame;
        this.editable = editable;
        construirUI();
    }

    /**
     * Registra una acción para el botón de añadir a la cesta.
     *
     * @param listener acción que se ejecuta al pulsar el botón
     */
    public void addListenerCesta(ActionListener listener) {
        listenersCesta.add(listener);
    }

    /**
     * Cambia el texto y el estado del botón de añadir a la cesta.
     *
     * @param texto texto del botón
     * @param activo true si el botón puede pulsarse
     */
    public void configurarBotonCesta(String texto, boolean activo) {
        if (botonCesta != null) {
            botonCesta.setText(texto);
            botonCesta.setEnabled(activo);
        }
    }

    /**
     * Devuelve la cantidad seleccionada para añadir a la cesta.
     *
     * @return cantidad seleccionada
     */
    public int getCantidadCestaSeleccionada() {
        if (campoCantidadCesta == null) {
            return 1;
        }
        return (Integer) campoCantidadCesta.getValue();
    }

    /**
     * Ejecuta la operacion publica setListenerEdicion.
     * Asigna el listener que manejará las acciones de edición (confirmar/cancelar).
     * 
     * @param listenerEdicion parametro utilizado por la operacion
     */
    public void setListenerEdicion(ListenerEdicion listenerEdicion) {
        this.listenerEdicion = listenerEdicion;
    }

    /**
     * Construye y organiza la interfaz de usuario principal del panel.
     */
    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        setPreferredSize(new Dimension(820, 520));

        JScrollPane scroll = new JScrollPane(crearCuerpo(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(820, 520));
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        add(scroll, BorderLayout.CENTER);
        if (editable) {
            add(crearBotoneraEdicion(), BorderLayout.SOUTH);
        }
    }

    /**
     * Crea el cuerpo central de la interfaz que contiene los paneles izquierdo y derecho.
     *
     * @return JPanel con el cuerpo estructurado del producto.
     */
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(14, 0));
        cuerpo.setBackground(UiStyle.COLOR_FONDO);
        cuerpo.setBorder(new EmptyBorder(14, 24, 14, 28));
        cuerpo.add(crearPanelIzquierdo(), BorderLayout.WEST);
        cuerpo.add(crearPanelDerecho(), BorderLayout.CENTER);
        return cuerpo;
    }

    /**
     * Crea el panel lateral izquierdo que contiene la imagen, nombre, precio, 
     * valoración y campos de edición si corresponde.
     *
     * @return JPanel correspondiente al lado izquierdo.
     */
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UiStyle.COLOR_FONDO);
        panel.setPreferredSize(new Dimension(ANCHO_IZQUIERDA, 0));
        panel.setBorder(new EmptyBorder(0, 0, 0, 22));

        panel.add(crearImagen());
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearNombre());
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearEstrellas(producto.getValoracion()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearPrecio());
        if (!editable) {
            panel.add(Box.createVerticalStrut(8));
            panel.add(crearSelectorCantidad());
        }
        if (editable) {
            panel.add(Box.createVerticalStrut(8));
            panel.add(crearCamposProducto());
        }
        panel.add(Box.createVerticalStrut(12));
        if (!editable) {
            panel.add(crearBotonCesta());
        }
        return panel;
    }

    /**
     * Crea la etiqueta que muestra la imagen del producto.
     * Si no hay imagen, muestra un texto alternativo.
     *
     * @return JLabel configurado con la imagen del producto.
     */
    private JLabel crearImagen() {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(150, 202));
        label.setMinimumSize(new Dimension(150, 202));
        label.setMaximumSize(new Dimension(150, 202));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 1));

        String ruta = producto.getImagen();
        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(150, 202, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imagen));
        } else {
            label.setText("<html><center>Sin<br>imagen</center></html>");
            label.setBackground(new Color(145, 106, 70));
            label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            label.setOpaque(true);
        }
        return label;
    }

    /**
     * Crea el componente visual para el nombre del producto.
     * En modo edición será un JTextField, de lo contrario un JLabel.
     *
     * @return JComponent que representa el nombre del producto.
     */
    private JComponent crearNombre() {
        if (editable) {
            campoNombre = new JTextField(producto.getNombre());
            campoNombre.setFont(new Font("SansSerif", Font.BOLD, 16));
            campoNombre.setHorizontalAlignment(SwingConstants.CENTER);
            campoNombre.setAlignmentX(Component.CENTER_ALIGNMENT);
            campoNombre.setMaximumSize(new Dimension(ANCHO_IZQUIERDA - 8, 30));
            return envoltorioCampo(campoNombre, ANCHO_IZQUIERDA - 8, 30);
        }
        JLabel label = new JLabel("<html><center>" + producto.getNombre() + "</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(ANCHO_IZQUIERDA - 8, 42));
        return label;
    }

    /**
     * Genera un panel con iconos de estrellas representando la valoración del producto.
     *
     * @param valoracion número de estrellas activas.
     * @return JPanel con las estrellas dibujadas.
     */
    private JPanel crearEstrellas(int valoracion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 1, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (int i = 0; i < MAX_ESTRELLAS; i++) {
            JLabel estrella = new JLabel("\u2605");
            estrella.setFont(new Font("Dialog", Font.BOLD, 32));
            estrella.setForeground(i < valoracion ? new Color(135, 116, 91) : new Color(183, 169, 151));
            panel.add(estrella);
        }
        return panel;
    }

    /**
     * Crea el componente visual para el precio del producto.
     * En modo edición será un JTextField, de lo contrario un JLabel formateado.
     *
     * @return JComponent que representa el precio del producto.
     */
    private JComponent crearPrecio() {
        if (editable) {
            campoPrecio = new JTextField(String.format("%.2f", producto.getPrecio()).replace(',', '.'));
            campoPrecio.setFont(new Font("SansSerif", Font.BOLD, 24));
            campoPrecio.setHorizontalAlignment(SwingConstants.CENTER);
            campoPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);
            campoPrecio.setMaximumSize(new Dimension(150, 34));
            return envoltorioCampo(campoPrecio, 150, 34);
        }
        JLabel label = new JLabel(String.format("%.2f\u20ac", precioFinal(producto)).replace('.', ','));
        label.setFont(new Font("SansSerif", Font.BOLD, 32));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Calcula el precio unitario mostrado al cliente.
     *
     * @param producto producto consultado
     * @return precio final con descuento aplicado
     */
    private double precioFinal(ProductoTienda producto) {
        double precio = producto.getPrecio();
        if (producto.getRebajaPorcentaje() > 0) {
            precio -= precio * (producto.getRebajaPorcentaje() / 100.0);
        } else if (producto.getRebajaFija() > 0) {
            precio -= producto.getRebajaFija();
        }
        return Math.max(0, precio);
    }

    /**
     * Envuelve un campo de edición en un JLabel para forzar unas dimensiones estrictas.
     *
     * @param campo el campo a envolver.
     * @param ancho ancho deseado.
     * @param alto alto deseado.
     * @return JLabel que actúa como contenedor con dimensiones fijas.
     */
    private JLabel envoltorioCampo(JComponent campo, int ancho, int alto) {
        JLabel envoltorio = new JLabel();
        envoltorio.setLayout(new BorderLayout());
        envoltorio.setAlignmentX(Component.CENTER_ALIGNMENT);
        envoltorio.setPreferredSize(new Dimension(ancho, alto));
        envoltorio.setMinimumSize(new Dimension(ancho, alto));
        envoltorio.setMaximumSize(new Dimension(ancho, alto));
        envoltorio.add(campo, BorderLayout.CENTER);
        return envoltorio;
    }

    /**
     * Crea el selector de unidades que el cliente quiere añadir a la cesta.
     *
     * @return panel con la etiqueta y el selector de cantidad
     */
    private JPanel crearSelectorCantidad() {
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        fila.setOpaque(false);
        fila.setAlignmentX(Component.CENTER_ALIGNMENT);
        fila.setMaximumSize(new Dimension(ANCHO_IZQUIERDA - 8, 32));

        JLabel etiqueta = new JLabel("Cantidad");
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 12));
        etiqueta.setForeground(Color.BLACK);

        int stockDisponible = mainFrame == null ? 99 : mainFrame.getStock().getNumProductos(producto);
        int maximo = Math.max(1, stockDisponible);
        campoCantidadCesta = new JSpinner(new SpinnerNumberModel(1, 1, maximo, 1));
        campoCantidadCesta.setPreferredSize(new Dimension(62, 28));
        campoCantidadCesta.setEnabled(stockDisponible > 0);

        fila.add(etiqueta);
        fila.add(campoCantidadCesta);
        return fila;
    }

    /**
     * Crea el panel con los campos editables adicionales del producto
     * (Stock, Imagen y Categorías).
     *
     * @return JPanel con los campos de edición.
     */
    private JPanel crearCamposProducto() {
        JPanel campos = new JPanel(new GridLayout(0, 1, 4, 4));
        campos.setOpaque(false);
        campos.setAlignmentX(Component.CENTER_ALIGNMENT);
        campos.setMaximumSize(new Dimension(ANCHO_IZQUIERDA - 18, 132));

        campoStock = new JSpinner(new SpinnerNumberModel(
                mainFrame == null ? 0 : mainFrame.getStock().getNumProductos(producto), 0, 9999, 1));
        campoImagen = new JTextField(producto.getImagen() == null ? "" : producto.getImagen());
        campoCategorias = new JTextField(String.join(", ", producto.getCategoriasTexto()));
        campos.add(crearFilaEdicion("Stock", campoStock));
        campos.add(crearFilaEdicion("Imagen", campoImagen));
        campos.add(crearFilaEdicion("Categorías", campoCategorias));
        return campos;
    }

    /**
     * Crea una fila estandarizada para los formularios de edición.
     *
     * @param etiqueta texto a mostrar junto al campo.
     * @param campo componente interactivo de entrada de datos.
     * @return JPanel organizado con BorderLayout.
     */
    private JPanel crearFilaEdicion(String etiqueta, JComponent campo) {
        JPanel fila = new JPanel(new BorderLayout(6, 0));
        fila.setOpaque(false);
        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        label.setForeground(Color.BLACK);
        fila.add(label, BorderLayout.WEST);
        fila.add(campo, BorderLayout.CENTER);
        return fila;
    }

    /**
     * Instancia y configura el botón principal de añadir a la cesta.
     *
     * @return JButton configurado.
     */
    private JButton crearBotonCesta() {
        JButton boton = new UiStyle.RoundedButton("A\u00f1adir a la cesta", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        boton.setPreferredSize(new Dimension(244, 31));
        boton.setMaximumSize(new Dimension(244, 31));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> listenersCesta.forEach(listener -> listener.actionPerformed(e)));
        botonCesta = boton;
        return boton;
    }

    /**
     * Crea el panel lateral derecho, que contiene la descripción del producto 
     * y la sección de comentarios, organizado mediante GridBagLayout.
     *
     * @return JPanel correspondiente al lado derecho.
     */
    private JPanel crearPanelDerecho() {
        JPanel derecho = new JPanel(new GridBagLayout());
        derecho.setBackground(UiStyle.COLOR_FONDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(0, 0, 12, 0);
        derecho.add(crearSeccionDescripcion(), gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        derecho.add(crearSeccionComentarios(), gbc);
        return derecho;
    }

    /**
     * Construye la sección visual que alberga la descripción del producto.
     * Puede comportarse como un área editable si el modo edición está activo.
     *
     * @return JPanel con el título y texto de descripción.
     */
    private JPanel crearSeccionDescripcion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(crearTitulo("Descripci\u00f3n"));
        panel.add(Box.createVerticalStrut(4));

        JTextArea descripcion = new JTextArea(producto.getDescripcion());
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descripcion.setForeground(Color.BLACK);
        descripcion.setOpaque(editable);
        descripcion.setEditable(editable);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setBorder(editable ? BorderFactory.createLineBorder(UiStyle.COLOR_BORDE, 1) : null);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        if (editable) {
            descripcion.setRows(7);
            campoDescripcion = descripcion;
            panel.add(new JScrollPane(descripcion));
        } else {
            panel.add(descripcion);
        }

        return panel;
    }

    /**
     * Crea un scroll pane que contiene el panel de comentarios.
     *
     * @return JScrollPane configurado para desplazar comentarios.
     */
    private JScrollPane crearSeccionComentarios() {
        scrollComentarios = new JScrollPane(crearPanelComentarios(), ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollComentarios.setBorder(null);
        scrollComentarios.getViewport().setBackground(UiStyle.COLOR_FONDO);
        return scrollComentarios;
    }

    /**
     * Genera la lista completa de comentarios y valoraciones del producto.
     * Si procede, también añade el formulario para crear nuevas reseñas.
     *
     * @return JPanel con todos los comentarios iterados.
     */
    private JPanel crearPanelComentarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UiStyle.COLOR_FONDO);
        panel.add(crearTitulo("Comentarios"));
        panel.add(Box.createVerticalStrut(6));

        List<String[]> comentarios = producto.getComentarios();
        if (comentarios.isEmpty()) {
            panel.add(crearComentario("GO", "Aún no hay comentarios."));
        } else {
            for (String[] comentario : comentarios) {
                panel.add(crearComentario(comentario[0], comentario[1]));
                panel.add(Box.createVerticalStrut(5));
            }
        }

        if (mainFrame != null && mainFrame.isSesionRegistrada()
                && mainFrame.clienteHaCompradoProducto(producto)) {
            panel.add(Box.createVerticalStrut(8));
            panel.add(crearFormularioResena());
        }
        return panel;
    }

    /**
     * Actualiza la vista de los comentarios forzando un repintado en el JScrollPane.
     */
    private void refrescarComentarios() {
        if (scrollComentarios != null) {
            scrollComentarios.setViewportView(crearPanelComentarios());
        }
    }

    /**
     * Crea un formulario que permite al usuario actual publicar una reseña
     * (puntuación y comentario) si ha adquirido previamente el producto.
     *
     * @return JPanel con los controles necesarios para publicar una reseña.
     */
    private JPanel crearFormularioResena() {
        JPanel formulario = new UiStyle.RoundedPanel(new Color(231, 219, 203), 12);
        formulario.setLayout(new BorderLayout(6, 6));
        formulario.setBorder(new EmptyBorder(8, 8, 8, 8));
        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        formulario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 114));

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        superior.setOpaque(false);
        JLabel label = new JLabel("Tu valoración");
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(UiStyle.COLOR_TEXTO);
        JComboBox<Integer> valoracion = new JComboBox<>(new Integer[] {1, 2, 3, 4, 5});
        valoracion.setSelectedItem(5);
        superior.add(label);
        superior.add(valoracion);

        JTextArea texto = new JTextArea();
        texto.setLineWrap(true);
        texto.setWrapStyleWord(true);
        texto.setRows(2);
        texto.setFont(new Font("SansSerif", Font.PLAIN, 12));

        JButton publicar = new UiStyle.RoundedButton("Publicar reseña", UiStyle.COLOR_TEXTO,
                UiStyle.COLOR_MARRON_MEDIO, 12);
        publicar.setPreferredSize(new Dimension(136, 30));
        publicar.addActionListener(e -> {
            String comentario = texto.getText().trim();
            if (comentario.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Escribe un comentario para publicarlo.",
                        "Reseña", JOptionPane.WARNING_MESSAGE);
                return;
            }
            mainFrame.comentarYValorarProducto(producto, (Integer) valoracion.getSelectedItem(), comentario);
            texto.setText("");
            refrescarComentarios();
        });

        formulario.add(superior, BorderLayout.NORTH);
        formulario.add(texto, BorderLayout.CENTER);
        formulario.add(publicar, BorderLayout.EAST);
        return formulario;
    }

    /**
     * Construye un panel inferior con los botones "Confirmar" y "Cancelar"
     * utilizados exclusivamente en el modo de edición de producto.
     *
     * @return JPanel con los botones de control de edición.
     */
    private JPanel crearBotoneraEdicion() {
        JPanel botonera = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        botonera.setBackground(UiStyle.COLOR_FONDO);
        botonera.setBorder(new EmptyBorder(0, 24, 8, 24));

        JButton cancelar = new UiStyle.RoundedButton("Cancelar", UiStyle.COLOR_CABECERA,
                UiStyle.COLOR_MARRON_MEDIO, 12);
        cancelar.setPreferredSize(new Dimension(112, 32));
        cancelar.addActionListener(e -> {
            if (listenerEdicion != null) {
                listenerEdicion.cancelar();
            }
        });

        JButton confirmar = new UiStyle.RoundedButton("Confirmar", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        confirmar.setPreferredSize(new Dimension(122, 32));
        confirmar.addActionListener(e -> {
            if (listenerEdicion != null) {
                listenerEdicion.confirmar(new DatosEdicion(
                        campoNombre.getText(),
                        campoPrecio.getText(),
                        ((Integer) campoStock.getValue()).intValue(),
                        campoDescripcion.getText(),
                        campoImagen.getText(),
                        campoCategorias.getText()));
            }
        });

        botonera.add(cancelar);
        botonera.add(confirmar);
        return botonera;
    }

    /**
     * Crea un JLabel con el formato estandarizado para los títulos de sección.
     *
     * @param texto texto del título.
     * @return JLabel formateado.
     */
    private JLabel crearTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    /**
     * Dibuja un comentario individual con el nombre del usuario y su avatar.
     *
     * @param usuario nombre del usuario que hace el comentario.
     * @param texto cuerpo del mensaje publicado.
     * @return JPanel formateado con los datos del comentario.
     */
    private JPanel crearComentario(String usuario, String texto) {
        JPanel tarjeta = new UiStyle.RoundedPanel(COLOR_COMENTARIO, 14);
        tarjeta.setLayout(new BorderLayout(8, 2));
        tarjeta.setBorder(new EmptyBorder(4, 8, 6, 10));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));

        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.setOpaque(false);
        fila.add(crearAvatar(usuario));

        JLabel nombre = new JLabel("@" + usuario);
        nombre.setFont(new Font("SansSerif", Font.BOLD, 12));
        nombre.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        fila.add(nombre);

        JTextArea cuerpo = new JTextArea(texto);
        cuerpo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        cuerpo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        cuerpo.setOpaque(false);
        cuerpo.setEditable(false);
        cuerpo.setLineWrap(true);
        cuerpo.setWrapStyleWord(true);
        cuerpo.setBorder(null);

        tarjeta.add(fila, BorderLayout.NORTH);
        tarjeta.add(cuerpo, BorderLayout.CENTER);
        return tarjeta;
    }

    /**
     * Genera un círculo decorativo con las iniciales del usuario
     * a modo de avatar gráfico.
     *
     * @param usuario nombre completo o username del que extraer las iniciales.
     * @return JComponent pintado a medida como avatar circular.
     */
    private JComponent crearAvatar(String usuario) {
        String iniciales = usuario.length() >= 2 ? usuario.substring(0, 2).toUpperCase() : usuario.toUpperCase();
        JPanel avatar = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UiStyle.COLOR_TEXTO_CLARO);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(COLOR_COMENTARIO);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(iniciales)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(iniciales, x, y);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        Dimension dimension = new Dimension(22, 22);
        avatar.setPreferredSize(dimension);
        avatar.setMinimumSize(dimension);
        avatar.setMaximumSize(dimension);
        return avatar;
    }

    /**
     * Estructura de datos inmutable que encapsula toda la información extraída
     * del formulario de edición cuando se confirma la modificación de un producto.
     */
    static class DatosEdicion {
        
        /** Nombre modificado del producto. */
        final String nombre;
        
        /** Precio modificado en formato de texto. */
        final String precio;
        
        /** Cantidad en stock disponible. */
        final int stock;
        
        /** Texto con la descripción actualizada. */
        final String descripcion;
        
        /** Ruta o URL de la imagen. */
        final String imagen;
        
        /** Cadena de texto con las categorías separadas por coma. */
        final String categorias;

        /**
         * Constructor que inicializa los datos editados.
         * 
         * @param nombre nuevo nombre.
         * @param precio nuevo precio.
         * @param stock nuevo stock.
         * @param descripcion nueva descripción.
         * @param imagen nueva imagen.
         * @param categorias nuevas categorías.
         */
        DatosEdicion(String nombre, String precio, int stock, String descripcion, String imagen, String categorias) {
            this.nombre = nombre;
            this.precio = precio;
            this.stock = stock;
            this.descripcion = descripcion;
            this.imagen = imagen;
            this.categorias = categorias;
        }
    }

    /**
     * Interfaz para delegar la responsabilidad de manejar
     * la confirmación o cancelación del formulario de edición.
     */
    interface ListenerEdicion {
        
        /**
         * Método invocado cuando el usuario hace clic en Confirmar.
         *
         * @param datos objeto que contiene toda la información de los campos del formulario.
         */
        void confirmar(DatosEdicion datos);

        /**
         * Método invocado cuando el usuario hace clic en Cancelar.
         */
        void cancelar();
    }
}