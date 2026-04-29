package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

import productos.ProductoTienda;

/**
 * Panel que muestra el detalle completo de un {@link ProductoTienda}.
 *
 * <p>Replica el diseño de la pantalla "GOAT &amp; GET": imagen del producto
 * a la izquierda, y a la derecha la descripción, la valoración en estrellas,
 * el precio y el botón "Añadir a la cesta". Debajo de la descripción se
 * listan los comentarios de los usuarios.</p>
 *
 * <p>Esquema de colores de la aplicación:</p>
 * <ul>
 *   <li>Fondo general: beige arena {@code #C4A882}</li>
 *   <li>Cabecera y botón: marrón oscuro {@code #2B1F0E}</li>
 *   <li>Texto sobre fondo oscuro: crema {@code #E8D5B0}</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>{@code
 * ProductoTienda p = ...;
 * p.addComentario("juan15", "¡Un clásico total!");
 *
 * JDialog dialogo = new JDialog();
 * dialogo.add(new PanelDeProducto(p));
 * dialogo.pack();
 * dialogo.setLocationRelativeTo(null);
 * dialogo.setVisible(true);
 * }</pre>
 *
 * @see PanelProductos
 * @see ProductoTienda
 */
public class PanelDeProducto extends JPanel {

    // ------------------------------------------------------------------ //
    //  Constantes de diseño                                               //
    // ------------------------------------------------------------------ //

    /** Color de fondo principal (beige arena). */
    private static final Color COLOR_FONDO       = new Color(0xC4, 0xA8, 0x82);

    /** Color de cabecera y botón (marrón oscuro). */
    private static final Color COLOR_OSCURO      = new Color(0x2B, 0x1F, 0x0E);

    /** Color del texto sobre fondos oscuros. */
    private static final Color COLOR_TEXTO_CLARO = new Color(0xE8, 0xD5, 0xB0);

    /** Color de fondo de cada tarjeta de comentario. */
    private static final Color COLOR_COMENTARIO  = new Color(0xD4, 0xB8, 0x8A);

    /** Color dorado para estrellas rellenas. */
    private static final Color COLOR_ESTRELLA    = new Color(0xC4, 0x84, 0x1A);

    /** Color apagado para estrellas vacías. */
    private static final Color COLOR_ESTRELLA_VACIA = new Color(0xA0, 0x84, 0x5A);

    /** Color del borde de la imagen. */
    private static final Color COLOR_BORDE_IMG   = new Color(0x6B, 0x4E, 0x2A);

    /** Ancho del panel izquierdo en píxeles. */
    private static final int ANCHO_IZQ   = 210;

    /** Número de estrellas máximo. */
    private static final int MAX_ESTRELLAS = 5;

    // ------------------------------------------------------------------ //
    //  Atributos                                                         //
    // ------------------------------------------------------------------ //

    /** Producto cuya información se muestra en este panel. */
    private final ProductoTienda producto;

    /**
     * Listeners que serán notificados cuando el usuario pulse
     * el botón "Añadir a la cesta".
     */
    private final List<ActionListener> listenersCesta = new ArrayList<>();

    // ------------------------------------------------------------------ //
    //  Constructor                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Crea el panel de detalle de producto.
     *
     * @param producto producto de tienda que se va a mostrar; no puede ser {@code null}
     * @throws IllegalArgumentException si {@code producto} es {@code null}
     */
    public PanelDeProducto(ProductoTienda producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        this.producto = producto;
        construirUI();
    }

    // ------------------------------------------------------------------ //
    //  API pública                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Registra un {@link ActionListener} que recibirá el evento cuando el
     * usuario pulse "Añadir a la cesta".
     *
     * @param listener listener a registrar
     */
    public void addListenerCesta(ActionListener listener) {
        listenersCesta.add(listener);
    }

    // ------------------------------------------------------------------ //
    //  Construcción de la UI                                             //
    // ------------------------------------------------------------------ //

    /**
     * Ensambla todos los subpaneles y los añade al layout principal.
     * Llamado una sola vez desde el constructor.
     */
    private void construirUI() {
        setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(680, 510));

        add(crearCabecera(),  BorderLayout.NORTH);
        add(crearCuerpo(),    BorderLayout.CENTER);
    }

    // ------------------------------------------------------------------ //
    //  Cabecera                                                          //
    // ------------------------------------------------------------------ //

    /**
     * Crea la barra superior con el logotipo "GOAT &amp; GET",
     * el botón de menú hamburguesa y los iconos de notificación y perfil.
     *
     * @return panel de cabecera configurado
     */
    private JPanel crearCabecera() {
        JPanel cab = new JPanel(new BorderLayout());
        cab.setBackground(COLOR_OSCURO);
        cab.setPreferredSize(new Dimension(680, 48));
        cab.setBorder(new EmptyBorder(0, 10, 0, 10));

        cab.add(crearBotonIcono("≡", 22), BorderLayout.WEST);

        JLabel titulo = new JLabel("GOAT & GET", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 20));
        titulo.setForeground(COLOR_TEXTO_CLARO);
        cab.add(titulo, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 8));
        derecha.setBackground(COLOR_OSCURO);
        derecha.add(crearBotonIcono("🔔", 16));
        derecha.add(crearBotonIcono("👤", 16));
        cab.add(derecha, BorderLayout.EAST);

        return cab;
    }

    /**
     * Crea un botón sin borde ni fondo, pensado para los iconos de la cabecera.
     *
     * @param icono    texto o carácter que se muestra como icono
     * @param fontSize tamaño de la fuente del icono en puntos
     * @return botón transparente listo para añadir
     */
    private JButton crearBotonIcono(String icono, int fontSize) {
        JButton btn = new JButton(icono);
        btn.setFont(new Font("Dialog", Font.PLAIN, fontSize));
        btn.setForeground(COLOR_TEXTO_CLARO);
        btn.setBackground(COLOR_OSCURO);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ------------------------------------------------------------------ //
    //  Cuerpo principal                                                  //
    // ------------------------------------------------------------------ //

    /**
     * Crea el cuerpo dividido en panel izquierdo (imagen + datos de compra)
     * y panel derecho (descripción + comentarios).
     *
     * @return panel con los dos bloques de contenido
     */
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(COLOR_FONDO);
        cuerpo.add(crearPanelIzquierdo(), BorderLayout.WEST);
        cuerpo.add(crearPanelDerecho(),   BorderLayout.CENTER);
        return cuerpo;
    }

    // ------------------------------------------------------------------ //
    //  Panel izquierdo                                                   //
    // ------------------------------------------------------------------ //

    /**
     * Construye el panel izquierdo con la imagen del producto, su nombre,
     * la valoración en estrellas, el precio y el botón de compra.
     *
     * @return panel izquierdo con layout vertical
     */
    private JPanel crearPanelIzquierdo() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setPreferredSize(new Dimension(ANCHO_IZQ, 0));
        panel.setBorder(new EmptyBorder(16, 12, 16, 12));

        panel.add(crearImagen());
        panel.add(Box.createVerticalStrut(10));
        panel.add(crearNombre());
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearEstrellas(producto.getValoracion()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(crearPrecio());
        panel.add(Box.createVerticalStrut(12));
        panel.add(crearBotonCesta());

        return panel;
    }

    /**
     * Crea la etiqueta con la imagen del producto.
     * Si la ruta de imagen es {@code null} o no se puede cargar, muestra
     * un rectángulo de color con el texto "Sin imagen".
     *
     * @return {@code JLabel} con la imagen o con el placeholder visual
     */
    private JLabel crearImagen() {
        JLabel lbl = new JLabel();
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setPreferredSize(new Dimension(160, 200));
        lbl.setMinimumSize(new Dimension(160, 200));
        lbl.setMaximumSize(new Dimension(160, 200));
        lbl.setBorder(BorderFactory.createLineBorder(COLOR_BORDE_IMG, 2));
        lbl.setHorizontalAlignment(SwingConstants.CENTER);

        String ruta = producto.getImagen();
        if (ruta != null && !ruta.isBlank()) {
            try {
                ImageIcon icono = new ImageIcon(ruta);
                Image img = icono.getImage().getScaledInstance(160, 200, Image.SCALE_SMOOTH);
                lbl.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                aplicarPlaceholderImagen(lbl);
            }
        } else {
            aplicarPlaceholderImagen(lbl);
        }
        return lbl;
    }

    /**
     * Configura un {@code JLabel} como placeholder cuando no hay imagen disponible.
     *
     * @param lbl etiqueta a la que aplicar el placeholder
     */
    private void aplicarPlaceholderImagen(JLabel lbl) {
        lbl.setText("<html><center><br>Sin<br>imagen</center></html>");
        lbl.setBackground(new Color(0x88, 0x66, 0x44));
        lbl.setForeground(COLOR_TEXTO_CLARO);
        lbl.setOpaque(true);
    }

    /**
     * Crea la etiqueta con el nombre del producto en negrita y centrada.
     *
     * @return etiqueta con el nombre
     */
    private JLabel crearNombre() {
        JLabel lbl = new JLabel(
                "<html><div style='text-align:center'>" + producto.getNombre() + "</div></html>",
                SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 13));
        lbl.setForeground(COLOR_OSCURO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Genera un panel con {@code n} estrellas rellenas y el resto vacías.
     *
     * @param n número de estrellas rellenas (0–5)
     * @return panel horizontal con las estrellas
     */
    private JPanel crearEstrellas(int n) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setBackground(COLOR_FONDO);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (int i = 0; i < MAX_ESTRELLAS; i++) {
            JLabel estrella = new JLabel("★");
            estrella.setFont(new Font("Dialog", Font.PLAIN, 22));
            estrella.setForeground(i < n ? COLOR_ESTRELLA : COLOR_ESTRELLA_VACIA);
            panel.add(estrella);
        }
        return panel;
    }

    /**
     * Crea la etiqueta con el precio formateado ({@code "XX.XX€"}).
     *
     * @return etiqueta con el precio
     */
    private JLabel crearPrecio() {
        JLabel lbl = new JLabel(String.format("%.2f€", producto.getPrecio()), SwingConstants.CENTER);
        lbl.setFont(new Font("Serif", Font.BOLD, 26));
        lbl.setForeground(COLOR_OSCURO);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea el botón "Añadir a la cesta" con efecto hover y delega los
     * clics en los listeners registrados mediante {@link #addListenerCesta}.
     *
     * @return botón de compra configurado
     */
    private JButton crearBotonCesta() {
        JButton btn = new JButton("Añadir a la cesta");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBackground(COLOR_OSCURO);
        btn.setForeground(COLOR_TEXTO_CLARO);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        int ancho = ANCHO_IZQ - 24;
        btn.setMaximumSize(new Dimension(ancho, 36));
        btn.setPreferredSize(new Dimension(ancho, 36));

        // Efecto hover: aclara el fondo al pasar el ratón
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x4A, 0x35, 0x20));
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_OSCURO);
            }
        });

        // Propaga el evento a todos los listeners registrados
        btn.addActionListener(e -> listenersCesta.forEach(l -> l.actionPerformed(e)));

        return btn;
    }

    // ------------------------------------------------------------------ //
    //  Panel derecho                                                      //
    // ------------------------------------------------------------------ //

    /**
     * Crea el panel derecho con la descripción del producto y los comentarios,
     * envuelto en un {@link JScrollPane} con scroll vertical.
     *
     * @return scroll pane con el contenido derecho
     */
    private JScrollPane crearPanelDerecho() {
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(COLOR_FONDO);
        contenido.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BORDE_IMG),
                new EmptyBorder(16, 14, 16, 16)));

        contenido.add(crearSeccionDescripcion());
        contenido.add(Box.createVerticalStrut(14));
        contenido.add(crearSeccionComentarios());

        JScrollPane scroll = new JScrollPane(contenido,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(COLOR_FONDO);
        return scroll;
    }

    /**
     * Crea la sección "Descripción" con su título y el texto del producto.
     *
     * @return panel con el bloque de descripción
     */
    private JPanel crearSeccionDescripcion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(crearTituloSeccion("Descripción"));
        panel.add(Box.createVerticalStrut(6));

        JTextArea txt = new JTextArea(producto.getDescripcion());
        txt.setFont(new Font("SansSerif", Font.PLAIN, 13));
        txt.setForeground(new Color(0x3C, 0x2A, 0x10));
        txt.setBackground(COLOR_FONDO);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setAlignmentX(Component.LEFT_ALIGNMENT);
        txt.setBorder(null);
        panel.add(txt);

        return panel;
    }

    /**
     * Crea la sección "Comentarios" con una tarjeta por cada comentario
     * del producto. Si no hay comentarios muestra un texto informativo.
     *
     * @return panel con todas las tarjetas de comentario
     */
    private JPanel crearSeccionComentarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(COLOR_FONDO);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(crearTituloSeccion("Comentarios"));
        panel.add(Box.createVerticalStrut(8));

        List<String[]> comentarios = producto.getComentarios();
        if (comentarios == null || comentarios.isEmpty()) {
            JLabel vacio = new JLabel("Aún no hay comentarios.");
            vacio.setFont(new Font("SansSerif", Font.ITALIC, 12));
            vacio.setForeground(new Color(0x6B, 0x4E, 0x2A));
            panel.add(vacio);
        } else {
            for (String[] c : comentarios) {
                // c[0] = nombre de usuario, c[1] = texto del comentario
                panel.add(crearTarjetaComentario(c[0], c[1]));
                panel.add(Box.createVerticalStrut(8));
            }
        }
        return panel;
    }

    /**
     * Crea una etiqueta con el estilo de título de sección (serif negrita).
     *
     * @param texto texto que se mostrará como título
     * @return etiqueta estilizada como título de sección
     */
    private JLabel crearTituloSeccion(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Serif", Font.BOLD, 16));
        lbl.setForeground(COLOR_OSCURO);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /**
     * Crea una tarjeta visual para un comentario de usuario, con avatar
     * circular (iniciales), nombre de usuario y texto del comentario.
     *
     * @param usuario nombre del usuario autor del comentario
     * @param texto   contenido textual del comentario
     * @return panel con la tarjeta de comentario
     */
    private JPanel crearTarjetaComentario(String usuario, String texto) {
        JPanel tarjeta = new JPanel(new BorderLayout(8, 4));
        tarjeta.setBackground(COLOR_COMENTARIO);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xA0, 0x84, 0x5A), 1),
                new EmptyBorder(8, 10, 8, 10)));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Fila superior: avatar + "@usuario"
        JPanel fila = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        fila.setBackground(COLOR_COMENTARIO);
        fila.add(crearAvatar(usuario));
        JLabel lblNombre = new JLabel("@" + usuario);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblNombre.setForeground(COLOR_OSCURO);
        fila.add(lblNombre);

        // Texto del comentario con ajuste de línea
        JTextArea txtArea = new JTextArea(texto);
        txtArea.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txtArea.setForeground(new Color(0x3C, 0x2A, 0x10));
        txtArea.setBackground(COLOR_COMENTARIO);
        txtArea.setEditable(false);
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);
        txtArea.setBorder(null);

        tarjeta.add(fila,    BorderLayout.NORTH);
        tarjeta.add(txtArea, BorderLayout.CENTER);

        return tarjeta;
    }

    /**
     * Pinta un componente circular con las dos primeras letras del nombre
     * del usuario como avatar visual.
     *
     * @param usuario nombre del usuario; se usan sus dos primeras letras en mayúsculas
     * @return componente que pinta el avatar
     */
    private JComponent crearAvatar(String usuario) {
        String iniciales = usuario.length() >= 2
                ? usuario.substring(0, 2).toUpperCase()
                : usuario.toUpperCase();

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_OSCURO);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(COLOR_TEXTO_CLARO);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(iniciales)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(iniciales, x, y);
            }
        };
        avatar.setBackground(COLOR_COMENTARIO);
        Dimension d = new Dimension(28, 28);
        avatar.setPreferredSize(d);
        avatar.setMinimumSize(d);
        avatar.setMaximumSize(d);
        return avatar;
    }
}
