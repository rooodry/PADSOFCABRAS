package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class TarjetaSubirProducto extends JPanel {

    private static final int ANCHO = 200;
    private static final int ALTO  = 280;

    public TarjetaSubirProducto(ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        
        // --- Borde Redondeado y Punteado ---
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Forzamos el tamaño
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setMaximumSize(new Dimension(ANCHO, ALTO));
        setMinimumSize(new Dimension(ANCHO, ALTO));

        add(Box.createVerticalGlue());
        add(crearIconoMas());
        add(Box.createVerticalStrut(20));
        add(crearTexto()); // MAQUETA: Texto "Subir producto"
        add(Box.createVerticalGlue());
        
        // Toda la tarjeta es clicable
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (listener != null) {
            this.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    listener.actionPerformed(null);
                }
            });
        }
    }

    // Dibujado del borde punteado negro redondeado
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        
        // Borde punteado negro
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
                
                // MAQUETA: Círculo negro con borde blanco grueso
                int padding = 5;
                g2.setColor(Color.BLACK);
                g2.fillOval(padding, padding, getWidth()-padding*2, getHeight()-padding*2);
                
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(4f)); // Borde blanco
                g2.drawOval(padding, padding, getWidth()-padding*2, getHeight()-padding*2);
                
                // MAQUETA: Signo más blanco
                g2.setFont(new Font("SansSerif", Font.BOLD, 60));
                FontMetrics fm = g2.getFontMetrics();
                String plus = "+";
                g2.drawString(plus, (getWidth() - fm.stringWidth(plus)) / 2, (getHeight() + fm.getAscent() - fm.getDescent() - 10) / 2);
            }
        };
        icono.setOpaque(false);
        icono.setMaximumSize(new Dimension(100, 100)); // Icono grande
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);
        return icono;
    }

    private JLabel crearTexto() {
        // MAQUETA: Texto en mayúsculas
        JLabel lbl = new JLabel("SUBIR PRODUCTO");
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(Color.BLACK);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        return lbl;
    }
}