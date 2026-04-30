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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;

/**
 * Product detail panel shown when the customer opens a catalogue card.
 *
 * <p>The layout follows the mockup: a warm top bar, product summary on the
 * left and description/comments on the right.</p>
 */
public class PanelDeProducto extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int MAX_ESTRELLAS = 5;
    private static final int ANCHO_IZQUIERDA = 270;
    private static final Color COLOR_COMENTARIO = new Color(145, 124, 101);

    private final ProductoTienda producto;
    private final List<ActionListener> listenersCesta = new ArrayList<>();

    /**
     * Creates a detail panel for the given shop product.
     *
     * @param producto product to show
     */
    public PanelDeProducto(ProductoTienda producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null.");
        }
        this.producto = producto;
        construirUI();
    }

    /**
     * Registers a listener for the add-to-basket button.
     *
     * @param listener listener to notify
     */
    public void addListenerCesta(ActionListener listener) {
        listenersCesta.add(listener);
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        setPreferredSize(new Dimension(752, 374));
        add(crearCuerpo(), BorderLayout.CENTER);
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(UiStyle.COLOR_FONDO);
        cuerpo.setBorder(new EmptyBorder(14, 24, 12, 28));
        cuerpo.add(crearPanelIzquierdo(), BorderLayout.WEST);
        cuerpo.add(crearPanelDerecho(), BorderLayout.CENTER);
        return cuerpo;
    }

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
        panel.add(Box.createVerticalStrut(6));
        panel.add(crearBotonCesta());
        return panel;
    }

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

    private JLabel crearNombre() {
        JLabel label = new JLabel("<html><center>" + producto.getNombre() + "</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(ANCHO_IZQUIERDA - 8, 42));
        return label;
    }

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

    private JLabel crearPrecio() {
        JLabel label = new JLabel(String.format("%.2f\u20ac", producto.getPrecio()).replace('.', ','));
        label.setFont(new Font("SansSerif", Font.BOLD, 32));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton crearBotonCesta() {
        JButton boton = new UiStyle.RoundedButton("A\u00f1adir a la cesta", new Color(94, 75, 57),
                UiStyle.COLOR_MARRON_MEDIO, 12);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        boton.setPreferredSize(new Dimension(244, 31));
        boton.setMaximumSize(new Dimension(244, 31));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> listenersCesta.forEach(listener -> listener.actionPerformed(e)));
        return boton;
    }

    private JPanel crearPanelDerecho() {
        JPanel derecho = new JPanel(new GridBagLayout());
        derecho.setBackground(UiStyle.COLOR_FONDO);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(2, 0, 8, 0);

        derecho.add(crearSeccionDescripcion(), gbc);

        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        derecho.add(crearSeccionComentarios(), gbc);
        return derecho;
    }

    private JPanel crearSeccionDescripcion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        panel.add(crearTitulo("Descripci\u00f3n"));
        panel.add(Box.createVerticalStrut(4));

        JTextArea descripcion = new JTextArea(producto.getDescripcion());
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descripcion.setForeground(Color.BLACK);
        descripcion.setOpaque(false);
        descripcion.setEditable(false);
        descripcion.setLineWrap(true);
        descripcion.setWrapStyleWord(true);
        descripcion.setBorder(null);
        descripcion.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descripcion);

        return panel;
    }

    private JScrollPane crearSeccionComentarios() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(UiStyle.COLOR_FONDO);
        panel.add(crearTitulo("Comentarios"));
        panel.add(Box.createVerticalStrut(6));

        List<String[]> comentarios = producto.getComentarios();
        if (comentarios.isEmpty()) {
            panel.add(crearComentario("GO", "Aun no hay comentarios."));
        } else {
            for (String[] comentario : comentarios) {
                panel.add(crearComentario(comentario[0], comentario[1]));
                panel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scroll = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        return scroll;
    }

    private JLabel crearTitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.BOLD, 17));
        label.setForeground(Color.BLACK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

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
}
