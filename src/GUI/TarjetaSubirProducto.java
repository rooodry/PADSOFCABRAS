package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Tarjeta especial que muestra el botón "+" para que el usuario suba
 * un nuevo producto de segunda mano.
 *
 * <p>Se incluye siempre al final de la cuadrícula de la pestaña "Subidos"
 * en {@link PanelMisProductos}. Al pulsar el botón "Subir producto" se
 * notifica al controlador mediante el {@link ActionListener} dado.</p>
 *
 * <p>El diseño usa un borde de línea discontinua para diferenciarse
 * visualmente de las tarjetas de producto normales.</p>
 *
 * @see PanelMisProductos
 * @see TarjetaSegundaMano
 */
public class TarjetaSubirProducto extends JPanel {

    /** Color de fondo de la tarjeta (igual al fondo general). */
    private static final Color COLOR_FONDO      = new Color(0xC4, 0xA8, 0x82);

    /** Color del símbolo "+" y del borde. */
    private static final Color COLOR_BORDE      = new Color(0x8B, 0x6A, 0x3E);

    /** Color del texto claro. */
    private static final Color COLOR_TEXTO_CLARO = new Color(0xE8, 0xD5, 0xB0);

    /** Color del botón de acción. */
    private static final Color COLOR_BOTON      = new Color(0x2B, 0x1F, 0x0E);

    /** Ancho fijo (igual que {@link TarjetaSegundaMano}). */
    private static final int ANCHO = 185;

    /** Alto fijo (igual que {@link TarjetaSegundaMano}). */
    private static final int ALTO  = 245;

    // ------------------------------------------------------------------ //
    //  Constructor                                                       //
    // ------------------------------------------------------------------ //

    /**
     * Crea la tarjeta de "subir producto".
     *
     * @param listener acción a ejecutar cuando el usuario pulse "Subir producto"
     */
    public TarjetaSubirProducto(ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(COLOR_FONDO);

        // Borde discontinuo para distinguirla del resto de tarjetas
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createDashedBorder(COLOR_BORDE, 4f, 4f, 2f, false),
                new EmptyBorder(10, 10, 10, 10)));

        setPreferredSize(new Dimension(ANCHO, ALTO));
        setMaximumSize(new Dimension(ANCHO, ALTO));

        add(Box.createVerticalGlue());
        add(crearIconoMas());
        add(Box.createVerticalStrut(14));
        add(crearBoton(listener));
        add(Box.createVerticalGlue());
    }

    // ------------------------------------------------------------------ //
    //  Componentes internos                                              //
    // ------------------------------------------------------------------ //

    /**
     * Crea el símbolo "+" grande que ocupa el centro de la tarjeta.
     * Se dibuja como un círculo con el signo "+" en su interior.
     *
     * @return componente personalizado con el icono "+"
     */
    private JComponent crearIconoMas() {
        JPanel icono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                // Círculo exterior
                g2.setColor(COLOR_BORDE);
                g2.setStroke(new BasicStroke(3f));
                g2.drawOval(4, 4, getWidth() - 8, getHeight() - 8);

                // Signo "+"
                g2.setColor(COLOR_BORDE);
                g2.setFont(new Font("SansSerif", Font.BOLD, 40));
                FontMetrics fm = g2.getFontMetrics();
                String plus = "+";
                int x = (getWidth() - fm.stringWidth(plus)) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(plus, x, y);
            }
        };
        icono.setBackground(COLOR_FONDO);
        icono.setOpaque(false);
        Dimension d = new Dimension(80, 80);
        icono.setPreferredSize(d);
        icono.setMinimumSize(d);
        icono.setMaximumSize(d);
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        return icono;
    }

    /**
     * Crea el botón "Subir producto" con su listener y efecto hover.
     *
     * @param listener acción a ejecutar al pulsar el botón
     * @return botón configurado
     */
    private JButton crearBoton(ActionListener listener) {
        JButton btn = new JButton("Subir producto");
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setBackground(COLOR_BOTON);
        btn.setForeground(COLOR_TEXTO_CLARO);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        int ancho = ANCHO - 30;
        btn.setMaximumSize(new Dimension(ancho, 28));
        btn.setPreferredSize(new Dimension(ancho, 28));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(0x4A, 0x35, 0x20));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(COLOR_BOTON);
            }
        });

        btn.addActionListener(listener);
        return btn;
    }
}