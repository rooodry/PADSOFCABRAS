package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import notificaciones.Notificacion;
import utilidades.TipoNotificacion;

/**
 * Full notification inbox screen.
 */
public class PanelNotificaciones extends JPanel {

    private static final long serialVersionUID = 1L;

    private enum Filtro {
        TODAS, PENDIENTES, VISTAS
    }

    private static final Color COLOR_FILA = new Color(135, 116, 96);
    private static final Color COLOR_BOTON = new Color(91, 73, 55);
    private static final Color COLOR_PAPELERA = new Color(154, 76, 60);

    private final Main mainFrame;
    private final JPanel lista;
    private final JButton[] botonesFiltro;
    private Filtro filtroActivo = Filtro.TODAS;

    /**
     * Builds the notification screen.
     *
     * @param mainFrame main GUI controller
     */
    public PanelNotificaciones(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.lista = new JPanel();
        this.botonesFiltro = new JButton[3];

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "NOTIFICACIONES"), BorderLayout.NORTH);
        add(crearCuerpo(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Refreshes the visible rows.
     */
    public void refrescar() {
        lista.removeAll();
        lista.setBackground(UiStyle.COLOR_FONDO);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        List<Notificacion> visibles = filtrarNotificaciones();
        if (visibles.isEmpty()) {
            JLabel vacio = new JLabel("No hay notificaciones en esta seccion.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.BOLD, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(30));
            lista.add(vacio);
        } else {
            for (Notificacion notificacion : visibles) {
                lista.add(crearFila(notificacion));
                lista.add(Box.createVerticalStrut(1));
            }
        }

        actualizarFiltros();
        lista.revalidate();
        lista.repaint();
    }

    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout());
        cuerpo.setBackground(UiStyle.COLOR_FONDO);
        cuerpo.setBorder(new EmptyBorder(8, 42, 8, 42));

        cuerpo.add(crearLateral(), BorderLayout.WEST);

        JScrollPane scroll = new JScrollPane(lista, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        scroll.setPreferredSize(new Dimension(560, 0));
        cuerpo.add(scroll, BorderLayout.CENTER);

        return cuerpo;
    }

    private JPanel crearLateral() {
        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setBackground(UiStyle.COLOR_FONDO);
        lateral.setPreferredSize(new Dimension(150, 0));
        lateral.setBorder(new EmptyBorder(38, 0, 0, 30));

        lateral.add(crearAvatarGrande());
        lateral.add(Box.createVerticalStrut(8));
        lateral.add(crearBotonFiltro("TODAS", Filtro.TODAS, 0));
        lateral.add(crearBotonFiltro("PENDIENTES", Filtro.PENDIENTES, 1));
        lateral.add(crearBotonFiltro("VISTAS", Filtro.VISTAS, 2));
        return lateral;
    }

    private JPanel crearAvatarGrande() {
        JPanel avatar = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(229, 231, 233));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(176, 181, 185));
                g2.fillOval(44, 25, 48, 52);
                g2.fillOval(24, 80, 88, 48);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        Dimension dimension = new Dimension(124, 124);
        avatar.setPreferredSize(dimension);
        avatar.setMaximumSize(dimension);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        return avatar;
    }

    private JButton crearBotonFiltro(String texto, Filtro filtro, int indice) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        boton.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        boton.setBackground(COLOR_BOTON);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(8, 18, 8, 18));
        boton.setMaximumSize(new Dimension(126, 31));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> {
            filtroActivo = filtro;
            refrescar();
        });
        botonesFiltro[indice] = boton;
        return boton;
    }

    private void actualizarFiltros() {
        for (JButton boton : botonesFiltro) {
            if (boton != null) {
                boton.setBackground(COLOR_BOTON);
            }
        }
        botonesFiltro[filtroActivo.ordinal()].setBackground(UiStyle.COLOR_TEXTO);
    }

    private List<Notificacion> filtrarNotificaciones() {
        List<Notificacion> resultado = new ArrayList<>();
        for (Notificacion notificacion : mainFrame.getClienteActual().getNotificaciones()) {
            if (notificacion.getBorrada()) {
                continue;
            }
            if (filtroActivo == Filtro.PENDIENTES && notificacion.getLeida()) {
                continue;
            }
            if (filtroActivo == Filtro.VISTAS && !notificacion.getLeida()) {
                continue;
            }
            resultado.add(notificacion);
        }
        return resultado;
    }

    private JPanel crearFila(Notificacion notificacion) {
        JPanel fila = new UiStyle.RoundedPanel(COLOR_FILA, 7);
        fila.setLayout(new GridBagLayout());
        fila.setBorder(new EmptyBorder(5, 10, 5, 10));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        fila.setMinimumSize(new Dimension(510, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 14);
        gbc.anchor = GridBagConstraints.CENTER;
        fila.add(crearIconoTipo(notificacion.getTipoNotificacion()), gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 8);
        JLabel titulo = new JLabel(tituloNotificacion(notificacion.getTipoNotificacion()), SwingConstants.CENTER);
        titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        fila.add(titulo, gbc);

        gbc.gridy = 1;
        JLabel mensaje = new JLabel(notificacion.getMensaje(), SwingConstants.CENTER);
        mensaje.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        mensaje.setFont(new Font("SansSerif", Font.BOLD, 13));
        fila.add(mensaje, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 2, 0, 6);
        JButton visto = crearBotonIcono("\u2713", new Color(84, 69, 51));
        visto.setToolTipText("Marcar como vista");
        visto.addActionListener(e -> mainFrame.marcarNotificacionLeida(notificacion));
        fila.add(visto, gbc);

        gbc.gridx = 3;
        JButton borrar = crearBotonIcono("\uD83D\uDDD1", COLOR_PAPELERA);
        borrar.setToolTipText("Eliminar notificacion");
        borrar.addActionListener(e -> mostrarConfirmacionBorrado(notificacion));
        fila.add(borrar, gbc);

        return fila;
    }

    private JLabel crearIconoTipo(TipoNotificacion tipo) {
        String texto;
        switch (tipo) {
            case NUEVA_OFERTA:
                texto = "\uD83C\uDFF7";
                break;
            case NUEVO_DESCUENTO:
                texto = "%";
                break;
            case OFERTA_ACEPTADA:
                texto = "\u2611";
                break;
            case OFERTA_RECHAZADA:
                texto = "\u2612";
                break;
            default:
                texto = "\uD83D\uDCE6";
                break;
        }
        JLabel icono = new JLabel(texto, SwingConstants.CENTER);
        icono.setFont(new Font("Dialog", Font.BOLD, 28));
        icono.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        icono.setPreferredSize(new Dimension(54, 32));
        return icono;
    }

    private String tituloNotificacion(TipoNotificacion tipo) {
        switch (tipo) {
            case PAGO_REALIZADO:
                return "PEDIDO ENTREGADO";
            case PEDIDO_LISTO:
                return "PEDIDO LISTO";
            case PEDIDO_EXPIRADO:
                return "PEDIDO EXPIRADO";
            case NUEVA_OFERTA:
                return "NUEVA OFERTA";
            case OFERTA_ACEPTADA:
                return "OFERTA ACEPTADA";
            case OFERTA_RECHAZADA:
                return "OFERTA RECHAZADA";
            case NUEVO_DESCUENTO:
                return "NUEVO DESCUENTO DISPONIBLE";
            case INTERCAMBIO_REALIZADO:
                return "INTERCAMBIO REALIZADO";
            case VALORACION_REALIZADA:
                return "VALORACION REALIZADA";
            default:
                return tipo.toString();
        }
    }

    private JButton crearBotonIcono(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Dialog", Font.BOLD, 22));
        boton.setForeground(color);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(32, 32));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    private void mostrarConfirmacionBorrado(Notificacion notificacion) {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setOpaque(false);

        JPanel caja = new UiStyle.RoundedPanel(COLOR_FILA, 10);
        caja.setLayout(new BoxLayout(caja, BoxLayout.Y_AXIS));
        caja.setBorder(new EmptyBorder(32, 28, 18, 28));
        caja.setPreferredSize(new Dimension(310, 158));

        JLabel titulo = new JLabel("<html><center>VAS A ELIMINAR UNA<br>NOTIFICACION</center></html>",
                SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(Color.BLACK);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel pregunta = new JLabel("¿Seguro que quieres borrarla?", SwingConstants.CENTER);
        pregunta.setFont(new Font("SansSerif", Font.PLAIN, 11));
        pregunta.setForeground(Color.BLACK);
        pregunta.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 28, 0));
        botones.setOpaque(false);
        JButton cancelar = new UiStyle.RoundedButton("Cancelar", new Color(202, 82, 65),
                new Color(184, 71, 56), 14);
        cancelar.setPreferredSize(new Dimension(112, 28));
        JButton continuar = new UiStyle.RoundedButton("Continuar", COLOR_BOTON, UiStyle.COLOR_TEXTO, 14);
        continuar.setPreferredSize(new Dimension(112, 28));

        botones.add(cancelar);
        botones.add(continuar);

        caja.add(titulo);
        caja.add(Box.createVerticalStrut(22));
        caja.add(pregunta);
        caja.add(Box.createVerticalStrut(10));
        caja.add(botones);
        overlay.add(caja);

        javax.swing.JDialog dialogo = new javax.swing.JDialog(mainFrame, true);
        dialogo.setUndecorated(true);
        dialogo.setBackground(new Color(0, 0, 0, 0));
        dialogo.add(overlay);
        dialogo.setSize(mainFrame.getSize());
        dialogo.setLocationRelativeTo(mainFrame);

        cancelar.addActionListener(e -> dialogo.dispose());
        continuar.addActionListener(e -> {
            mainFrame.borrarNotificacion(notificacion);
            dialogo.dispose();
        });

        dialogo.setVisible(true);
    }
}
