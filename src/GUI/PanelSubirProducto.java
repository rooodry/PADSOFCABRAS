package GUI;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * Formulario para subir un producto de segunda mano a la cartera del cliente.
 */
public class PanelSubirProducto extends JPanel {

    private static final long serialVersionUID = 1L;

    private final Main mainFrame;
    private final JTextField txtNombre;
    private final JTextField txtImagen;
    private final JTextArea txtDescripcion;

    /**
     * Crea el formulario de subida.
     *
     * @param mainFrame controlador principal de la aplicacion
     */
    public PanelSubirProducto(Main mainFrame) {
        this.mainFrame = mainFrame;
        this.txtNombre = crearCampo();
        this.txtImagen = crearCampo();
        this.txtDescripcion = new JTextArea(6, 28);

        setLayout(new BorderLayout());
        setBackground(UiStyle.COLOR_FONDO);
        add(new HomePanel.PanelNavegacionCliente(mainFrame, "MIS PRODUCTOS"), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearContenido() {
        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setBackground(UiStyle.COLOR_FONDO);

        JPanel formulario = new JPanel();
        formulario.setLayout(new BoxLayout(formulario, BoxLayout.Y_AXIS));
        formulario.setBackground(UiStyle.COLOR_CABECERA);
        formulario.setBorder(new EmptyBorder(28, 36, 28, 36));

        JLabel titulo = new JLabel("Nuevo producto", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        titulo.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        titulo.setAlignmentX(CENTER_ALIGNMENT);

        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDescripcion = new JScrollPane(txtDescripcion);
        scrollDescripcion.setMaximumSize(new Dimension(420, 130));

        formulario.add(titulo);
        formulario.add(Box.createVerticalStrut(18));
        formulario.add(crearEtiqueta("Nombre"));
        formulario.add(txtNombre);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearEtiqueta("Ruta de imagen (opcional)"));
        formulario.add(txtImagen);
        formulario.add(Box.createVerticalStrut(10));
        formulario.add(crearEtiqueta("Descripcion"));
        formulario.add(scrollDescripcion);
        formulario.add(Box.createVerticalStrut(20));
        formulario.add(crearBotones());

        centrado.add(formulario);
        return centrado;
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel();
        botones.setOpaque(false);

        JButton cancelar = crearBoton("Cancelar");
        cancelar.addActionListener(e -> mainFrame.cambiarPantalla(Main.PANTALLA_MIS_PRODUCTOS));

        JButton guardar = crearBoton("Guardar");
        guardar.addActionListener(e -> guardarProducto());

        botones.add(cancelar);
        botones.add(guardar);
        return botones;
    }

    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String imagen = txtImagen.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Introduce nombre y descripcion.", "Datos incompletos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.anadirProductoALaCartera(nombre, descripcion, imagen.isBlank() ? null : imagen);
        txtNombre.setText("");
        txtImagen.setText("");
        txtDescripcion.setText("");
        JOptionPane.showMessageDialog(this, "Producto subido a tu cartera.");
        mainFrame.cambiarPantalla(Main.PANTALLA_MIS_PRODUCTOS);
    }

    private JLabel crearEtiqueta(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        etiqueta.setFont(new Font("SansSerif", Font.BOLD, 13));
        etiqueta.setAlignmentX(LEFT_ALIGNMENT);
        return etiqueta;
    }

    private JTextField crearCampo() {
        JTextField campo = new JTextField();
        campo.setMaximumSize(new Dimension(420, 34));
        campo.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return campo;
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setBackground(UiStyle.COLOR_TEXTO);
        boton.setForeground(UiStyle.COLOR_TEXTO_CLARO);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return boton;
    }
}
