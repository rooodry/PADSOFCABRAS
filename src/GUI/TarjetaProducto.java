package GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;

/**
 * Rounded catalogue card for a shop product.
 */
public class TarjetaProducto extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final int MAX_ESTRELLAS = 5;

    /**
     * Creates the product card.
     *
     * @param producto product represented by the card
     */
    public TarjetaProducto(ProductoTienda producto) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(14, 14, 14, 14));
        setPreferredSize(new Dimension(200, 258));
        setMaximumSize(new Dimension(200, 258));
        setMinimumSize(new Dimension(200, 258));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        add(crearImagen(producto.getImagen()));
        add(Box.createVerticalStrut(10));
        add(crearNombre(producto.getNombre()));
        add(Box.createVerticalStrut(4));
        add(crearEstrellas(producto.getValoracion()));
        add(Box.createVerticalStrut(6));
        add(crearPrecio(producto.getPrecio()));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiStyle.COLOR_CABECERA);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 28, 28);
        g2.dispose();
        super.paintComponent(g);
    }

    private JLabel crearImagen(String ruta) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(126, 144));
        label.setMinimumSize(new Dimension(126, 144));
        label.setMaximumSize(new Dimension(126, 144));
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            Image imagen = icono.getImage().getScaledInstance(126, 144, Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(imagen));
        } else {
            label.setText("<html><center>SIN<br>IMAGEN</center></html>");
            label.setBackground(UiStyle.COLOR_MARRON_MEDIO);
            label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
            label.setOpaque(true);
        }
        return label;
    }

    private JLabel crearNombre(String nombre) {
        String texto = nombre.length() > 34 ? nombre.substring(0, 31) + "..." : nombre;
        JLabel label = new JLabel("<html><center>" + texto + "</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel crearEstrellas(int valoracion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        for (int i = 0; i < MAX_ESTRELLAS; i++) {
            JLabel estrella = new JLabel("*");
            estrella.setFont(new Font("Dialog", Font.BOLD, 18));
            estrella.setForeground(i < valoracion ? new Color(248, 231, 175) : new Color(214, 198, 180));
            panel.add(estrella);
        }
        return panel;
    }

    private JLabel crearPrecio(double precio) {
        JLabel label = new JLabel(String.format("%.2f EUR", precio), SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }
}
