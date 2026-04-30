package GUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Shared visual constants and rounded Swing helpers for GOAT & GET.
 */
final class UiStyle {

    /** Main white background used in the mockups. */
    static final Color COLOR_FONDO = Color.WHITE;

    /** Warm taupe header tone from the mockups. */
    static final Color COLOR_CABECERA = new Color(165, 143, 122);

    /** Dark coffee tone used for active actions and text. */
    static final Color COLOR_TEXTO = new Color(72, 55, 42);

    /** Medium brown used for hover and secondary blocks. */
    static final Color COLOR_MARRON_MEDIO = new Color(137, 113, 88);

    /** Light card tone. */
    static final Color COLOR_TARJETA = new Color(213, 193, 168);

    /** Soft border tone. */
    static final Color COLOR_BORDE = new Color(123, 99, 76);

    /** Text on dark backgrounds. */
    static final Color COLOR_TEXTO_CLARO = Color.WHITE;

    private UiStyle() {
    }

    /**
     * Button with rounded background painted manually.
     */
    static class RoundedButton extends JButton {

        private static final long serialVersionUID = 1L;

        private final int arc;
        private Color normalColor;
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
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(UiStyle.RoundedButton.this.hoverColor);
                }

                @Override
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

    /**
     * Panel with rounded painted background.
     */
    static class RoundedPanel extends JPanel {

        private static final long serialVersionUID = 1L;

        private final int arc;
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
