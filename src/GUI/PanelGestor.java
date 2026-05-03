package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import productos.ProductoTienda;
import productos.Stock;
import usuarios.Empleado;
import utilidades.TiposEmpleado;

/**
 * Initial manager dashboard interface with stock controls.
 */
public class PanelGestor extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JPanel contenido;

    public PanelGestor(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.contenido = new JPanel();

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "GESTION"), BorderLayout.NORTH);
        add(crearScroll(), BorderLayout.CENTER);
        refrescar();
    }

    public void refrescar() {
        contenido.removeAll();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(UiStyle.COLOR_FONDO);
        contenido.setBorder(new EmptyBorder(22, 34, 22, 34));

        contenido.add(crearTitulo("Panel Gestor"));
        contenido.add(crearSubtitulo("Accede al inventario y gestiona el stock del establecimiento."));

        contenido.add(crearTitulo("Inventario de tienda"));
        JPanel stockPanel = new JPanel();
        stockPanel.setOpaque(false);
        stockPanel.setLayout(new BoxLayout(stockPanel, BoxLayout.Y_AXIS));
        Stock stock = mainFrame.getStock();
        List<ProductoTienda> productos = mainFrame.getProductosTienda();

        if (productos.isEmpty()) {
            stockPanel.add(crearEtiqueta("No hay productos en el inventario."));
        } else {
            for (ProductoTienda producto : productos) {
                stockPanel.add(crearFilaStock(producto, stock.getNumProductos(producto)));
            }
        }
        contenido.add(stockPanel);

        contenido.add(crearTitulo("Resumen rapido"));
        contenido.add(crearEtiqueta("Productos en catalogo: " + mainFrame.getProductosTienda().size()));
        contenido.add(crearEtiqueta("Intercambios en curso: " + mainFrame.getIntercambios().size()));
        JButton herramientas = crearBoton("Herramientas tienda", 180);
        herramientas.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_EMPLEADO));
        contenido.add(herramientas);

        contenido.add(crearTitulo("Empleados"));
        JButton nuevoEmpleado = crearBoton("Nuevo empleado", 150);
        nuevoEmpleado.addActionListener(e -> mostrarDialogoEmpleado(null));
        contenido.add(nuevoEmpleado);
        JPanel empleados = new JPanel();
        empleados.setOpaque(false);
        empleados.setLayout(new BoxLayout(empleados, BoxLayout.Y_AXIS));
        for (Empleado empleado : mainFrame.getEmpleados()) {
            empleados.add(crearFilaEmpleado(empleado));
        }
        contenido.add(empleados);

        contenido.revalidate();
        contenido.repaint();
    }

    private JScrollPane crearScroll() {
        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UiStyle.COLOR_FONDO);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JLabel crearTitulo(String texto) {
        JLabel titulo = new JLabel(texto, SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO);
        titulo.setBorder(new EmptyBorder(14, 0, 10, 0));
        return titulo;
    }

    private JLabel crearSubtitulo(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.LEFT);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        label.setBorder(new EmptyBorder(0, 0, 18, 0));
        return label;
    }

    private JPanel crearFilaStock(ProductoTienda producto, int cantidad) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(10, 14, 10, 14));

        JLabel label = crearEtiqueta(producto.getNombre() + " | stock: " + cantidad
                + " | precio: " + String.format("%.2f EUR", producto.getPrecio()));
        fila.add(label, BorderLayout.CENTER);

        JButton sumar = crearBoton("+5 stock", 108);
        sumar.addActionListener(e -> {
            mainFrame.sumarStockProducto(producto, 5);
            refrescar();
        });
        fila.add(sumar, BorderLayout.EAST);
        return fila;
    }

    private JPanel crearFilaEmpleado(Empleado empleado) {
        JPanel fila = new UiStyle.RoundedPanel(UiStyle.COLOR_TARJETA, 18);
        fila.setLayout(new BorderLayout(10, 0));
        fila.setBorder(new EmptyBorder(10, 14, 10, 14));
        fila.add(crearEtiqueta(empleado.getNombre() + " | " + empleado.getPermisos()), BorderLayout.CENTER);

        JButton permisos = crearBoton("Permisos", 120);
        permisos.addActionListener(e -> mostrarDialogoEmpleado(empleado));
        fila.add(permisos, BorderLayout.EAST);
        return fila;
    }

    private void mostrarDialogoEmpleado(Empleado empleado) {
        JTextField nombre = new JTextField(empleado == null ? "" : empleado.getNombre());
        nombre.setEnabled(empleado == null);
        JPasswordField contrasena = new JPasswordField(empleado == null ? "" : empleado.getContraseña());
        JCheckBox producto = new JCheckBox("Productos y stock");
        JCheckBox pedido = new JCheckBox("Pedidos");
        JCheckBox intercambio = new JCheckBox("Intercambios");
        if (empleado != null) {
            producto.setSelected(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO));
            pedido.setSelected(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_PEDIDO));
            intercambio.setSelected(empleado.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO));
        }

        JPanel panel = new JPanel(new GridLayout(0, 1, 6, 6));
        panel.add(new JLabel("Nombre"));
        panel.add(nombre);
        panel.add(new JLabel("Contrasena"));
        panel.add(contrasena);
        panel.add(producto);
        panel.add(pedido);
        panel.add(intercambio);

        int respuesta = JOptionPane.showConfirmDialog(this, panel,
                empleado == null ? "Nuevo empleado" : "Permisos de empleado",
                JOptionPane.OK_CANCEL_OPTION);
        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }

        Set<TiposEmpleado> permisos = new HashSet<>();
        if (producto.isSelected()) {
            permisos.add(TiposEmpleado.EMPLEADOS_PRODUCTO);
        }
        if (pedido.isSelected()) {
            permisos.add(TiposEmpleado.EMPLEADOS_PEDIDO);
        }
        if (intercambio.isSelected()) {
            permisos.add(TiposEmpleado.EMPLEADOS_INTERCAMBIO);
        }

        if (empleado == null) {
            mainFrame.crearEmpleadoDesdeGestor(nombre.getText(), new String(contrasena.getPassword()), permisos);
        } else {
            empleado.setContraseña(new String(contrasena.getPassword()));
            mainFrame.configurarPermisosEmpleado(empleado, permisos);
        }
        refrescar();
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel label = new JLabel("<html>" + texto + "</html>");
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        label.setForeground(UiStyle.COLOR_TEXTO);
        return label;
    }

    private JButton crearBoton(String texto, int ancho) {
        JButton boton = new UiStyle.RoundedButton(texto, UiStyle.COLOR_TEXTO, UiStyle.COLOR_MARRON_MEDIO, 18);
        boton.setPreferredSize(new Dimension(ancho, 32));
        return boton;
    }
}
