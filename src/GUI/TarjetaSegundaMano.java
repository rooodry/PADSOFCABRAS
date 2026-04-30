package GUI;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import productos.ProductoSegundaMano;
import utilidades.EstadoProducto;

/**
 * Tarjeta grafica para un producto de segunda mano del cliente.
 */
public class TarjetaSegundaMano extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int ANCHO = 200;
    private static final int ALTO = 280;

    /**
     * Crea una tarjeta con datos y accion opcional.
     *
     * @param producto producto que se representa
     * @param listener accion del boton; puede ser {@code null}
     */
    public TarjetaSegundaMano(ProductoSegundaMano producto, ActionListener listener) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(ANCHO, ALTO));
        setMaximumSize(new Dimension(ANCHO, ALTO));
        setMinimumSize(new Dimension(ANCHO, ALTO));

        add(crearImagen(producto.getImagen()));
        add(Box.createVerticalStrut(10));
        add(crearNombre(producto.getNombre()));
        add(Box.createVerticalStrut(5));
        add(crearEstrellas(producto.getValoracionEmpleado()));
        add(Box.createVerticalStrut(5));
        add(crearPrecio(producto.getValorEstimado()));

        if (listener != null) {
            add(Box.createVerticalGlue());
            add(crearBoton(etiquetaBoton(producto.getEstadoProducto()), listener));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(UiStyle.COLOR_CABECERA);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
    }

    private String etiquetaBoton(EstadoProducto estado) {
        if (estado == EstadoProducto.VALORADO) {
            return "Publicar";
        }
        return "Pedir valoracion";
    }

    private JLabel crearImagen(String ruta) {
        JLabel label = new JLabel();
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setPreferredSize(new Dimension(130, 150));
        label.setMaximumSize(new Dimension(130, 150));
        label.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (ruta != null && !ruta.isBlank()) {
            ImageIcon icono = new ImageIcon(ruta);
            label.setIcon(new ImageIcon(icono.getImage().getScaledInstance(130, 150, java.awt.Image.SCALE_SMOOTH)));
        } else {
            label.setText("<html><center>SIN<br>IMAGEN</center></html>");
            label.setBackground(new Color(130, 110, 90));
            label.setForeground(Color.WHITE);
            label.setOpaque(true);
        }
        return label;
    }

    private JLabel crearNombre(String nombre) {
        JLabel label = new JLabel("<html><center>" + nombre + "</center></html>");
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JPanel crearEstrellas(int valoracion) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
        panel.setOpaque(false);
        for (int i = 0; i < 5; i++) {
            JLabel estrella = new JLabel("*");
            estrella.setFont(new Font("Dialog", Font.BOLD, 18));
            estrella.setForeground(i < valoracion ? new Color(245, 230, 180) : new Color(220, 205, 190));
            panel.add(estrella);
        }
        return panel;
    }

    private JLabel crearPrecio(double precio) {
        JLabel label = new JLabel(precio > 0 ? String.format("%.2f EUR", precio) : "Sin tasar");
        label.setFont(new Font("SansSerif", Font.BOLD, 16));
        label.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private JButton crearBoton(String etiqueta, ActionListener listener) {
        JButton boton = new JButton(etiqueta);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        boton.setBackground(UiStyle.COLOR_TEXTO);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(1.0f), UiStyle.COLOR_TEXTO));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setMaximumSize(new Dimension(160, 30));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(listener);
        return boton;
    }
}
