package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente Swing de la interfaz grafica correspondiente a TarjetaSubirProducto.
 */
public class TarjetaSubirProducto extends JPanel {

    /** Dato interno asociado a ANCHO. */
    private static final int ANCHO = 200;
    /** Dato interno asociado a ALTO. */
    private static final int ALTO  = 280;

    /**
     * Construye una instancia de TarjetaSubirProducto.
     * @param listener valor recibido por el metodo
     */
    public TarjetaSubirProducto(ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);


        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));


        setPreferredSize(new Dimension(ANCHO, ALTO));
        setMaximumSize(new Dimension(ANCHO, ALTO));
        setMinimumSize(new Dimension(ANCHO, ALTO));

        add(Box.createVerticalGlue());
        add(crearIconoMas());
        add(Box.createVerticalStrut(20));
        add(crearTexto());
        add(Box.createVerticalGlue());


        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (listener != null) {
            this.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                /**
                 * Gestiona la accion de mouseClicked.
                 * @param e valor recibido por el metodo
                 */
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    listener.actionPerformed(null);
                }
            });
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);


        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{10f, 10f}, 0f));
        g2.drawRoundRect(2, 2, getWidth()-4, getHeight()-4, 30, 30);
    }

    private JComponent crearIconoMas() {
        JPanel icono = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                int padding = 5;
                g2.setColor(Color.BLACK);
                g2.fillOval(padding, padding, getWidth()-padding*2, getHeight()-padding*2);

                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4f));
                g2.drawOval(padding, padding, getWidth()-padding*2, getHeight()-padding*2);


                g2.setFont(new Font("SansSerif", Font.BOLD, 60));
                FontMetrics fm = g2.getFontMetrics();
                String plus = "+";
                g2.drawString(plus, (getWidth() - fm.stringWidth(plus)) / 2, (getHeight() + fm.getAscent() - fm.getDescent() - 10) / 2);
            }
        };
        icono.setOpaque(false);
        icono.setMaximumSize(new Dimension(100, 100));
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        return icono;
    }

    private JLabel crearTexto() {

        JLabel lbl = new JLabel("SUBIR PRODUCTO");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.BLACK);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
}
