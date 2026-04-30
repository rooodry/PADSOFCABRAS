package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import intercambios.Intercambio;
import intercambios.Oferta;
import productos.ProductoSegundaMano;
import utilidades.EstadoOferta;

/**
 * Screen that lets the registered customer review exchange proposals.
 */
public class PanelIntercambios extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel lista;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Builds the exchange screen.
     *
     * @param mainFrame main GUI controller
     */
    public PanelIntercambios(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.lista = new JPanel();
        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "INTERCAMBIOS"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Rebuilds the exchange proposal list.
     */
    public void refrescar() {
        lista.removeAll();
        lista.setBackground(UiStyle.COLOR_FONDO);
        lista.setBorder(new EmptyBorder(22, 34, 22, 34));
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));

        if (mainFrame.getIntercambios().isEmpty()) {
            JLabel vacio = new JLabel("No hay intercambios pendientes.");
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Intercambio intercambio : mainFrame.getIntercambios()) {
                lista.add(crearTarjeta(intercambio));
                lista.add(Box.createVerticalStrut(12));
            }
        }

        lista.add(Box.createVerticalStrut(18));
        JLabel mercado = new JLabel("Productos de segunda mano disponibles");
        mercado.setFont(new Font("SansSerif", Font.BOLD, 18));
        mercado.setForeground(UiStyle.COLOR_TEXTO);
        lista.add(mercado);
        lista.add(Box.createVerticalStrut(10));
        for (ProductoSegundaMano producto : mainFrame.getProductosSegundaManoDisponibles()) {
            lista.add(crearTarjetaMercado(producto));
            lista.add(Box.createVerticalStrut(10));
        }

        lista.revalidate();
        lista.repaint();
    }

    private JScrollPane crearContenido() {
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        return scroll;
    }

    private JPanel crearTarjeta(Intercambio intercambio) {
        Oferta oferta = intercambio.getOferta();
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 26);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(new EmptyBorder(18, 22, 18, 22));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 8, 0);

        JLabel titulo = new JLabel("Oferta de @" + oferta.getUsuarioLanzador().getNombre());
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(titulo, gbc);

        gbc.gridy++;
        JLabel productos = new JLabel("<html>Ofrece <b>" + oferta.getProductoOfertado().getNombre()
                + "</b> por <b>" + oferta.getProductoDeseado().getNombre() + "</b></html>");
        productos.setFont(new Font("SansSerif", Font.PLAIN, 14));
        productos.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(productos, gbc);

        gbc.gridy++;
        JLabel fechas = new JLabel("Limite: " + formatoFecha.format(intercambio.getFechaLimite())
                + "   Estado: " + oferta.getEstadoOferta());
        fechas.setFont(new Font("SansSerif", Font.PLAIN, 13));
        fechas.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(fechas, gbc);

        gbc.gridy++;
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        JButton rechazar = crearBoton("Rechazar");
        rechazar.setEnabled(oferta.getEstadoOferta() == EstadoOferta.PENDIENTE);
        rechazar.addActionListener(e -> {
            mainFrame.rechazarIntercambio(intercambio);
            refrescar();
        });
        JButton aceptar = crearBoton("Aceptar");
        aceptar.setEnabled(oferta.getEstadoOferta() == EstadoOferta.PENDIENTE);
        aceptar.addActionListener(e -> {
            mainFrame.aceptarIntercambio(intercambio);
            refrescar();
        });
        botones.add(rechazar);
        botones.add(aceptar);
        tarjeta.add(botones, gbc);

        return tarjeta;
    }

    private JPanel crearTarjetaMercado(ProductoSegundaMano producto) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(new EmptyBorder(14, 20, 14, 20));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 134));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 5, 0);

        JLabel titulo = new JLabel(producto.getNombre() + "  -  " + String.format("%.2f EUR", producto.getValorEstimado()));
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(titulo, gbc);

        gbc.gridy++;
        JLabel descripcion = new JLabel("<html>" + producto.getDescripcion() + "</html>");
        descripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descripcion.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(descripcion, gbc);

        gbc.gridy++;
        JButton proponer = crearBoton("Proponer intercambio");
        proponer.setPreferredSize(new Dimension(180, 34));
        proponer.addActionListener(e -> mainFrame.proponerIntercambio(producto));
        tarjeta.add(proponer, gbc);
        return tarjeta;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(110, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }
}
