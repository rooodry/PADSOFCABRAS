package GUI;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import notificaciones.Notificacion;
import utilidades.TipoNotificacion;


/**
 * Representa el componente PanelNotificaciones de la interfaz grafica.
 * Muestra una lista de notificaciones del usuario con opciones para filtrar,
 * marcar como leídas, ver detalles y eliminarlas.
 */
public class PanelNotificaciones extends JPanel {

    private static final long serialVersionUID = 1L;

    /**
     * Enumeración que define los diferentes filtros de visualización para las notificaciones.
     */
    private enum Filtro {
        /** Muestra todas las notificaciones (leídas y no leídas). */
        TODAS, 
        /** Muestra únicamente las notificaciones no leídas. */
        PENDIENTES, 
        /** Muestra únicamente las notificaciones ya leídas. */
        VISTAS
    }

    /** Color personalizado para el icono del botón de borrar (papelera). */
    private static final Color COLOR_PAPELERA = new Color(154, 76, 60);
    
    /** Tamaño en píxeles (ancho y alto) para la imagen del avatar del usuario. */
    private static final int AVATAR_SIZE = 150;

    /** Referencia al controlador de la ventana principal de la aplicación. */
    private final Main mainFrame;
    
    /** Panel contenedor que agrupa y muestra visualmente las filas de notificaciones. */
    private final JPanel lista;
    
    /** Arreglo que almacena los botones utilizados para cambiar de filtro. */
    private final JButton[] botonesFiltro;
    
    /** Formatea las fechas mostradas en las notificaciones con el patrón dd/MM/yyyy HH:mm. */
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    
    /** Filtro de visualización activo actualmente en el panel. Por defecto es TODAS. */
    private Filtro filtroActivo = Filtro.TODAS;

    /**
     * Construye la pantalla de notificaciones, inicializando sus componentes
     * e integrando la barra de navegación superior.
     *
     * @param mainFrame controlador principal de la interfaz
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
     * Reconstruye la lista de notificaciones visuales según el filtro activo.
     * Limpia el panel, filtra los datos, crea los componentes de cada notificación
     * y repinta la interfaz.
     */
    public void refrescar() {
        lista.removeAll();
        lista.setBackground(UiStyle.COLOR_FONDO);
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        List<Notificacion> visibles = filtrarNotificaciones();
        if (visibles.isEmpty()) {
            JLabel vacio = new JLabel("No hay notificaciones en esta sección.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.BOLD, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            vacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            lista.add(Box.createVerticalStrut(34));
            lista.add(vacio);
        } else {
            for (Notificacion notificacion : visibles) {
                lista.add(crearFila(notificacion));
                lista.add(Box.createVerticalStrut(18));
            }
        }

        actualizarFiltros();
        lista.revalidate();
        lista.repaint();
    }

    /**
     * Crea y ensambla la sección central del panel, compuesta por un menú lateral
     * y el área principal de contenido con desplazamiento (scroll).
     *
     * @return un {@code JPanel} que representa el cuerpo de la vista
     */
    private JPanel crearCuerpo() {
        JPanel cuerpo = new JPanel(new BorderLayout(20, 0));
        cuerpo.setBackground(UiStyle.COLOR_FONDO);
        cuerpo.setBorder(new EmptyBorder(22, 22, 22, 22));

        cuerpo.add(crearLateral(), BorderLayout.WEST);

        JPanel contenido = new JPanel(new BorderLayout(0, 14));
        contenido.setOpaque(false);
        JLabel titulo = new JLabel("Notificaciones", SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        contenido.add(titulo, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(lista, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(18);
        contenido.add(scroll, BorderLayout.CENTER);
        cuerpo.add(contenido, BorderLayout.CENTER);

        return cuerpo;
    }

    /**
     * Construye el panel lateral izquierdo que contiene el avatar del usuario
     * y los botones de filtrado (TODAS, PENDIENTES, VISTAS).
     *
     * @return un {@code JPanel} con el menú lateral
     */
    private JPanel crearLateral() {
        JPanel lateral = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.setPreferredSize(new Dimension(300, 0));
        lateral.setBorder(new EmptyBorder(20, 16, 20, 16));

        lateral.add(crearAvatarGrande());
        lateral.add(Box.createVerticalStrut(20));
        lateral.add(crearBotonFiltro("TODAS", Filtro.TODAS, 0));
        lateral.add(crearSeparadorFiltro());
        lateral.add(crearBotonFiltro("PENDIENTES", Filtro.PENDIENTES, 1));
        lateral.add(crearSeparadorFiltro());
        lateral.add(crearBotonFiltro("VISTAS", Filtro.VISTAS, 2));
        lateral.add(Box.createVerticalGlue());
        return lateral;
    }

    /**
     * Crea un {@code JLabel} con el avatar del usuario cargado o un icono por defecto
     * si no se encuentra disponible ninguna imagen de perfil.
     *
     * @return un {@code JLabel} configurado como avatar
     */
    private JLabel crearAvatarGrande() {
        JLabel avatar = new JLabel("\uD83D\uDC64", SwingConstants.CENTER);
        avatar.setOpaque(false);
        avatar.setFont(new Font("SansSerif", Font.PLAIN, 92));
        avatar.setForeground(UiStyle.COLOR_TEXTO);
        Dimension dimension = new Dimension(268, AVATAR_SIZE);
        avatar.setPreferredSize(dimension);
        avatar.setMaximumSize(dimension);
        avatar.setAlignmentX(Component.CENTER_ALIGNMENT);
        cargarAvatar(avatar);
        return avatar;
    }

    /**
     * Crea un botón de estilo redondeado para seleccionar un filtro en específico.
     *
     * @param texto el texto que se mostrará en el botón
     * @param filtro el estado del enum {@code Filtro} que aplicará el botón al pulsarse
     * @param indice el índice dentro del arreglo {@code botonesFiltro} donde se guardará
     * @return un {@code JButton} configurado y con su respectivo evento añadido
     */
    private JButton crearBotonFiltro(String texto, Filtro filtro, int indice) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TARJETA, UiStyle.COLOR_MARRON_MEDIO, 16);
        boton.setForeground(UiStyle.COLOR_TEXTO);
        boton.setPreferredSize(new Dimension(268, 40));
        boton.setMaximumSize(new Dimension(268, 40));
        boton.setAlignmentX(Component.CENTER_ALIGNMENT);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> {
            filtroActivo = filtro;
            refrescar();
        });
        botonesFiltro[indice] = boton;
        return boton;
    }

    /**
     * Actualiza los colores de fondo y texto de los botones de filtrado
     * para reflejar visualmente cuál es el que está activo en ese momento.
     */
    private void actualizarFiltros() {
        for (JButton boton : botonesFiltro) {
            if (boton != null) {
                boton.setBackground(UiStyle.COLOR_TARJETA);
                boton.setForeground(UiStyle.COLOR_TEXTO);
            }
        }
        botonesFiltro[filtroActivo.ordinal()].setBackground(UiStyle.COLOR_CABECERA);
        botonesFiltro[filtroActivo.ordinal()].setForeground(UiStyle.COLOR_TEXTO_CLARO);
    }

    /**
     * Crea un pequeño separador horizontal estilizado entre los botones de filtro.
     *
     * @return un {@code JPanel} configurado como línea separadora
     */
    private JPanel crearSeparadorFiltro() {
        JPanel separador = new JPanel();
        separador.setBackground(UiStyle.COLOR_BORDE);
        separador.setPreferredSize(new Dimension(268, 2));
        separador.setMaximumSize(new Dimension(268, 2));
        separador.setAlignmentX(Component.CENTER_ALIGNMENT);
        return separador;
    }

    /**
     * Carga e inserta la imagen de perfil del usuario en el componente proporcionado.
     *
     * @param avatar el componente {@code JLabel} donde se establecerá la imagen de perfil
     */
    private void cargarAvatar(JLabel avatar) {
        String rutaFoto = mainFrame.getClienteActual().getFotoPerfil();
        if (rutaFoto == null || rutaFoto.isBlank()) {
            return;
        }
        File archivo = new File(rutaFoto);
        if (!archivo.exists()) {
            return;
        }
        ImageIcon original = new ImageIcon(rutaFoto);
        Image imagen = original.getImage().getScaledInstance(AVATAR_SIZE, AVATAR_SIZE, Image.SCALE_SMOOTH);
        avatar.setIcon(new ImageIcon(imagen));
        avatar.setText("");
    }

    /**
     * Filtra la lista de notificaciones del cliente actual omitiendo las borradas y 
     * aplicando el filtro activo (Todas, Pendientes, Vistas). Además, ordena
     * el resultado del más reciente al más antiguo.
     *
     * @return una lista de {@code Notificacion} filtrada y ordenada
     */
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
        resultado.sort(Comparator.comparing(Notificacion::getFechaCreacion).reversed());
        return resultado;
    }

    /**
     * Crea un componente de panel (fila) que representa visualmente una notificación en la lista.
     * Contiene su icono de tipo, título, descripción corta, y los controles para marcar como vista o borrar.
     *
     * @param notificacion el objeto de datos de la notificación a renderizar
     * @return un {@code JPanel} estilizado para la notificación indicada
     */
    private JPanel crearFila(Notificacion notificacion) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 20);
        fila.setLayout(new GridBagLayout());
        fila.setBorder(new EmptyBorder(14, 16, 14, 16));
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 86));
        fila.setMinimumSize(new Dimension(620, 86));
        fila.setPreferredSize(new Dimension(620, 86));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        fila.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            /**
             * Muestra el cuadro de diálogo con los detalles de la notificación
             * al detectar un clic sobre el panel de la fila.
             * 
             * @param e objeto que contiene los detalles del evento del ratón
             */
            public void mouseClicked(java.awt.event.MouseEvent e) {
                mostrarDetalleNotificacion(notificacion);
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 0, 0, 12);
        gbc.anchor = GridBagConstraints.CENTER;
        fila.add(crearIconoTipo(notificacion.getTipoNotificacion()), gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 0, 6);
        JLabel titulo = new JLabel(tituloNotificacion(notificacion.getTipoNotificacion()), SwingConstants.CENTER);
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        fila.add(titulo, gbc);

        gbc.gridy = 1;
        JLabel mensaje = new JLabel(acortar(notificacion.getMensaje(), 72), SwingConstants.CENTER);
        mensaje.setForeground(UiStyle.COLOR_TEXTO);
        mensaje.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fila.add(mensaje, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 2, 0, 4);
        JButton visto = crearBotonIcono(new IconoTic(UiStyle.COLOR_TEXTO));
        visto.setToolTipText("Marcar como vista");
        visto.addActionListener(e -> {
            mainFrame.marcarNotificacionLeida(notificacion);
            refrescar();
        } );
        fila.add(visto, gbc);

        gbc.gridx = 3;
        JButton borrar = crearBotonIcono(new IconoPapelera(COLOR_PAPELERA));
        borrar.setToolTipText("Eliminar notificación");
        borrar.addActionListener(e -> mostrarConfirmacionBorrado(notificacion));
        fila.add(borrar, gbc);

        return fila;
    }

    /**
     * Muestra una ventana de diálogo modal (JOptionPane) que presenta todos los detalles
     * de la notificación dada e implícitamente la marca como leída.
     *
     * @param notificacion la notificación a expandir y mostrar
     */
    private void mostrarDetalleNotificacion(Notificacion notificacion) {
        if (notificacion == null) {
            return;
        }
        mainFrame.marcarNotificacionLeida(notificacion);

        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titulo = new JLabel(tituloNotificacion(notificacion.getTipoNotificacion()), SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(titulo, BorderLayout.NORTH);

        JTextArea mensaje = new JTextArea(notificacion.getMensaje());
        mensaje.setEditable(false);
        mensaje.setLineWrap(true);
        mensaje.setWrapStyleWord(true);
        mensaje.setFont(new Font("SansSerif", Font.PLAIN, 14));
        mensaje.setForeground(UiStyle.COLOR_TEXTO);
        mensaje.setBackground(UiStyle.COLOR_FONDO);
        mensaje.setBorder(new EmptyBorder(8, 8, 8, 8));
        JScrollPane scroll = new JScrollPane(mensaje,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setPreferredSize(new Dimension(440, 170));
        panel.add(scroll, BorderLayout.CENTER);

        JLabel detalle = new JLabel("<html>Fecha: " + formatoFecha.format(notificacion.getFechaCreacion())
                + "<br>Estado: " + (notificacion.getLeida() ? "Vista" : "Pendiente") + "</html>");
        detalle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        detalle.setForeground(UiStyle.COLOR_TEXTO);
        panel.add(detalle, BorderLayout.SOUTH);

        JOptionPane.showMessageDialog(this, panel, "Detalle de notificación", JOptionPane.INFORMATION_MESSAGE);
        refrescar();
    }

    /**
     * Genera un {@code JLabel} con un texto/emoji representativo basado en el
     * tipo de notificación proporcionado.
     *
     * @param tipo el enumerado {@code TipoNotificacion} a evaluar
     * @return un {@code JLabel} configurado para usarse como icono
     */
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
            case OFERTA_CADUCA:
                texto = "\u23F0";
                break;
            case PEDIDO_ENTREGADO:
                texto = "\u2705";
                break;
            case PAGO_REALIZADO:
                texto = "\uD83D\uDCB3";
                break;
            default:
                texto = "\uD83D\uDCE6";
                break;
        }
        JLabel icono = new JLabel(texto, SwingConstants.CENTER);
        icono.setFont(new Font("Dialog", Font.BOLD, 28));
        icono.setForeground(UiStyle.COLOR_TEXTO);
        icono.setPreferredSize(new Dimension(54, 32));
        return icono;
    }

    /**
     * Retorna una cadena de texto descriptiva amigable orientada al usuario en función
     * del tipo de notificación proporcionado.
     *
     * @param tipo el enumerado {@code TipoNotificacion} a evaluar
     * @return el texto que servirá como título de la notificación
     */
    private String tituloNotificacion(TipoNotificacion tipo) {
        switch (tipo) {
            case PAGO_REALIZADO:
                return "PAGO REALIZADO";
            case PEDIDO_ENTREGADO:
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
            case OFERTA_CADUCA:
                return "OFERTA A PUNTO DE CADUCAR";
            case NUEVO_DESCUENTO:
                return "NUEVO DESCUENTO DISPONIBLE";
            case INTERCAMBIO_REALIZADO:
                return "INTERCAMBIO REALIZADO";
            case PRODUCTO_SUBIDO:
                return "Producto subido";
            case VALORACION_REALIZADA:
                return "VALORACION REALIZADA";
            default:
                return tipo.toString();
        }
    }

    /**
     * Crea un botón transparente y sin bordes destinado a alojar un icono de acción.
     *
     * @param icono la interfaz {@code Icon} que se desea mostrar en el botón
     * @return un {@code JButton} configurado para presentar solo el icono
     */
    private JButton crearBotonIcono(Icon icono) {
        JButton boton = new JButton(icono);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(false);
        boton.setFocusPainted(false);
        boton.setOpaque(false);
        boton.setPreferredSize(new Dimension(38, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }

    /**
     * Implementación de la interfaz {@code Icon} que dibuja un símbolo de 'tic' o verificación (check)
     * mediante la clase {@code Graphics2D}.
     */
    private static final class IconoTic implements Icon {
        /** Tamaño de ancho y alto del icono de tic en píxeles. */
        private static final int SIZE = 24;
        
        /** Color del trazado con el que se dibujará el tic. */
        private final Color color;

        /**
         * Constructor para inicializar el color del tic.
         *
         * @param color el color que tendrá el dibujo
         */
        private IconoTic(Color color) {
            this.color = color;
        }

        @Override
        /**
         * Obtiene el ancho del icono.
         * 
         * @return la anchura del icono de verificación
         */
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        /**
         * Obtiene la altura del icono.
         * 
         * @return la altura del icono de verificación
         */
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        /**
         * Dibuja los trazos del icono de verificación en el componente gráfico.
         * 
         * @param c el componente en el que se pinta el icono
         * @param g el contexto gráfico utilizado para dibujar
         * @param x la coordenada X inicial del icono
         * @param y la coordenada Y inicial del icono
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(x + 5, y + 13, x + 10, y + 18);
            g2.drawLine(x + 10, y + 18, x + 20, y + 6);
            g2.dispose();
        }
    }

    /**
     * Implementación de la interfaz {@code Icon} que dibuja un símbolo de papelera de reciclaje
     * mediante la clase {@code Graphics2D}.
     */
    private static final class IconoPapelera implements Icon {
        /** Tamaño de ancho y alto del icono de papelera en píxeles. */
        private static final int SIZE = 24;
        
        /** Color del trazado con el que se dibujará la papelera. */
        private final Color color;

        /**
         * Constructor para inicializar el color de la papelera.
         *
         * @param color el color que tendrá el dibujo
         */
        private IconoPapelera(Color color) {
            this.color = color;
        }

        @Override
        /**
         * Obtiene el ancho del icono.
         * 
         * @return la anchura del icono de la papelera
         */
        public int getIconWidth() {
            return SIZE;
        }

        @Override
        /**
         * Obtiene la altura del icono.
         * 
         * @return la altura del icono de la papelera
         */
        public int getIconHeight() {
            return SIZE;
        }

        @Override
        /**
         * Dibuja los trazos que forman una papelera en el componente gráfico.
         * 
         * @param c el componente en el que se pinta el icono
         * @param g el contexto gráfico utilizado para dibujar
         * @param x la coordenada X inicial del icono
         * @param y la coordenada Y inicial del icono
         */
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            g2.drawLine(x + 7, y + 8, x + 17, y + 8);
            g2.drawLine(x + 10, y + 5, x + 14, y + 5);
            g2.drawLine(x + 11, y + 5, x + 11, y + 7);
            g2.drawLine(x + 13, y + 5, x + 13, y + 7);

            g2.drawRoundRect(x + 8, y + 10, 8, 10, 2, 2);
            g2.drawLine(x + 10, y + 12, x + 10, y + 18);
            g2.drawLine(x + 12, y + 12, x + 12, y + 18);
            g2.drawLine(x + 14, y + 12, x + 14, y + 18);
            g2.dispose();
        }
    }

    /**
     * Muestra un cuadro de diálogo de confirmación preguntando al usuario
     * si realmente desea borrar la notificación seleccionada.
     * En caso afirmativo, se procede con la eliminación.
     *
     * @param notificacion la notificación que el usuario intenta eliminar
     */
    private void mostrarConfirmacionBorrado(Notificacion notificacion) {
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "Vas a eliminar una notificación.\n¿Seguro que quieres borrarla?",
                "Eliminar notificación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (opcion == JOptionPane.YES_OPTION) {
            mainFrame.borrarNotificacion(notificacion);
        }
    }

    /**
     * Recorta una cadena de texto para que no exceda una longitud máxima,
     * añadiendo puntos suspensivos ("...") al final si resulta acortada.
     *
     * @param texto el texto original que se evaluará
     * @param maximo el número máximo de caracteres permitidos antes de aplicar recorte
     * @return el texto truncado con puntos suspensivos si excedió la longitud, de lo contrario devuelve el texto intacto
     */
    private String acortar(String texto, int maximo) {
        if (texto == null || texto.length() <= maximo) {
            return texto;
        }
        return texto.substring(0, maximo - 3) + "...";
    }
}