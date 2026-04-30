package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import compras.Pedido;
import productos.ProductoTienda;
import utilidades.EstadoPedido;

/**
 * Pantalla de historial y seguimiento de pedidos del cliente registrado.
 */
public class PanelPedidos extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel lista;
    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Builds the order-history panel.
     *
     * @param mainFrame main GUI controller
     */
    public PanelPedidos(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.lista = new JPanel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "PEDIDOS"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        refrescar();
    }

    /**
     * Rebuilds the list of orders from the active customer.
     */
    public void refrescar() {
        lista.removeAll();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(UiStyle.COLOR_FONDO);
        lista.setBorder(new EmptyBorder(22, 34, 22, 34));

        if (mainFrame.getClienteActual().getPedidos().isEmpty()) {
            JLabel vacio = new JLabel("Todavia no tienes pedidos.", SwingConstants.CENTER);
            vacio.setFont(new Font("SansSerif", Font.PLAIN, 16));
            vacio.setForeground(UiStyle.COLOR_TEXTO);
            lista.add(vacio);
        } else {
            for (Pedido pedido : mainFrame.getClienteActual().getPedidos()) {
                lista.add(crearTarjetaPedido(pedido));
                lista.add(Box.createVerticalStrut(12));
            }
        }

        lista.revalidate();
        lista.repaint();
    }

    private JScrollPane crearContenido() {
        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel crearTarjetaPedido(Pedido pedido) {
        JPanel tarjeta = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 24);
        tarjeta.setLayout(new GridBagLayout());
        tarjeta.setBorder(new EmptyBorder(16, 20, 16, 20));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 7, 0);

        JLabel titulo = new JLabel("Pedido " + pedido.getCodigo().getCodigo());
        titulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        tarjeta.add(titulo, gbc);

        gbc.gridy++;
        tarjeta.add(crearLinea("Estado: " + pedido.getEstadoPedido()
                + "   Fecha: " + formatoFecha.format(pedido.getFechaRealizacion())), gbc);

        gbc.gridy++;
        tarjeta.add(crearLinea("Documento de recogida: " + mainFrame.getClienteActual().getDNI()), gbc);

        gbc.gridy++;
        tarjeta.add(crearLinea(productosPedido(pedido)), gbc);

        gbc.gridy++;
        tarjeta.add(crearLinea(String.format("Total: %.2f EUR", pedido.calcularPrecioTotal())), gbc);

        gbc.gridy++;
        tarjeta.add(crearBotonera(pedido), gbc);

        return tarjeta;
    }

    private JLabel crearLinea(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 13));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private String productosPedido(Pedido pedido) {
        StringBuilder builder = new StringBuilder("Productos: ");
        boolean primero = true;
        for (Map.Entry<ProductoTienda, Integer> entry : pedido.getProductos().entrySet()) {
            if (!primero) {
                builder.append(", ");
            }
            builder.append(entry.getKey().getNombre()).append(" x").append(entry.getValue());
            primero = false;
        }
        return builder.toString();
    }

    private JPanel crearBotonera(Pedido pedido) {
        JPanel botones = new JPanel();
        botones.setOpaque(false);

        JButton cancelar = crearBoton("Cancelar");
        cancelar.setEnabled(pedido.getEstadoPedido() == EstadoPedido.EN_CARRITO);
        cancelar.addActionListener(e -> mainFrame.cancelarPedidoCliente(pedido));

        JButton pagar = crearBoton("Pagar");
        pagar.setEnabled(pedido.getEstadoPedido() == EstadoPedido.EN_CARRITO);
        pagar.addActionListener(e -> mainFrame.pagarPedido(pedido));

        JButton preparar = crearBoton("Marcar listo");
        preparar.setEnabled(pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION);
        preparar.addActionListener(e -> mainFrame.prepararPedido(pedido));

        JButton entregar = crearBoton("Entregar");
        entregar.setEnabled(pedido.getEstadoPedido() == EstadoPedido.LISTO);
        entregar.addActionListener(e -> mainFrame.entregarPedido(pedido));

        botones.add(cancelar);
        botones.add(pagar);
        botones.add(preparar);
        botones.add(entregar);
        return botones;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(118, 34));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }
}
