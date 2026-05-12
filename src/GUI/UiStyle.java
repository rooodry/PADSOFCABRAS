package GUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;


final class UiStyle {


    static final Color COLOR_FONDO = Color.WHITE;


    static final Color COLOR_CABECERA = new Color(165, 143, 122);


    static final Color COLOR_TEXTO = new Color(72, 55, 42);


    static final Color COLOR_MARRON_MEDIO = new Color(137, 113, 88);


    static final Color COLOR_TARJETA = new Color(213, 193, 168);


    static final Color COLOR_BORDE = new Color(123, 99, 76);


    static final Color COLOR_TEXTO_CLARO = Color.WHITE;


    static final String ICONO_PERFIL_CABRA = "lib/fotos/icono_perfil_cabra.png";


    static final String ICONO_NOTIFICACIONES = "lib/fotos/icono_notificaciones_campana.png";

    private UiStyle() {
    }

    static JButton crearBotonImagen(String rutaIcono, String texto, String tooltip, int ancho, int alto, int iconSize) {
        JButton boton = new JButton(texto);
        ImageIcon icono = new ImageIcon(rutaIcono);
        Image imagen = icono.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        boton.setIcon(new ImageIcon(imagen));
        boton.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        boton.setForeground(COLOR_TEXTO_CLARO);
        boton.setBackground(COLOR_CABECERA);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setToolTipText(tooltip);
        boton.setPreferredSize(new Dimension(ancho, alto));
        boton.setHorizontalTextPosition(SwingConstants.RIGHT);
        boton.setIconTextGap(1);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }


    static class RoundedButton extends JButton {

        private static final long serialVersionUID = 1L;

        /** Dato interno asociado a arc. */
        private final int arc;
        /** Dato interno asociado a normalColor. */
        private Color normalColor;
        /** Dato interno asociado a hoverColor. */
        private Color hoverColor;

        RoundedButton(String text, Color normalColor, Color hoverColor, int arc) {
            super(text);
            this.arc = arc;
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            setOpaque(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setForeground(COLOR_TEXTO_CLARO);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(42, 36));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                /**
                 * Gestiona la accion de mouseEntered.
                 * @param e valor recibido por el metodo
                 */
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(UiStyle.RoundedButton.this.hoverColor);
                }

                @Override
                /**
                 * Gestiona la accion de mouseExited.
                 * @param e valor recibido por el metodo
                 */
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setBackground(UiStyle.RoundedButton.this.normalColor);
                }
            });
            setBackground(normalColor);
        }

        void setButtonColors(Color normalColor, Color hoverColor) {
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            setBackground(normalColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }


    static class RoundedPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        /** Dato interno asociado a arc. */
        private final int arc;
        /** Dato interno asociado a fillColor. */
        private Color fillColor;

        RoundedPanel(Color fillColor, int arc) {
            this.fillColor = fillColor;
            this.arc = arc;
            setOpaque(false);
        }

        void setFillColor(Color fillColor) {
            this.fillColor = fillColor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(fillColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
