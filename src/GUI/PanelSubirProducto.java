package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utilidades.EstadoConservacion;
import java.awt.*;
import java.io.File;

public class PanelSubirProducto extends JPanel {
    private Main mainFrame;
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JTextField txtImagen;
    private JComboBox<EstadoConservacion> cboEstado;

    public PanelSubirProducto(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(crearCabecera("SUBIR NUEVO PRODUCTO"), BorderLayout.NORTH);

        // Bordes Redondeados
        JPanel contenedorForm = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(165, 143, 122)); // Color café de la maqueta
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        contenedorForm.setOpaque(false);
        contenedorForm.setLayout(new BoxLayout(contenedorForm, BoxLayout.Y_AXIS));
        contenedorForm.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Campos del Formulario
        txtNombre = crearCampo("NOMBRE DEL PRODUCTO");

        JLabel lblEstado = new JLabel("ESTADO DEL PRODUCTO");
        lblEstado.setForeground(Color.WHITE);
        lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
        cboEstado = new JComboBox<>(EstadoConservacion.values());
        cboEstado.setMaximumSize(new Dimension(400, 32));
        cboEstado.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblImagen = new JLabel("FOTO DEL PRODUCTO");
        lblImagen.setForeground(Color.WHITE);
        lblImagen.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtImagen = crearCampo("RUTA DE LA FOTO");
        JButton btnBuscarImagen = crearBotonForm("Buscar foto", new Color(80, 60, 44));
        btnBuscarImagen.setPreferredSize(new Dimension(120, 30));
        btnBuscarImagen.addActionListener(e -> seleccionarImagen());
        JPanel panelImagen = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        panelImagen.setOpaque(false);
        panelImagen.add(txtImagen);
        panelImagen.add(btnBuscarImagen);

        JLabel lblDesc = new JLabel("DESCRIPCIÓN");
        lblDesc.setForeground(Color.WHITE);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        txtDescripcion = new JTextArea(5, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);

        // Botones de Acción
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);
        
        JButton btnGuardar = crearBotonForm("GUARDAR", new Color(102, 80, 61));
        JButton btnCancelar = crearBotonForm("CANCELAR", new Color(181, 86, 68));

        // Listeners para navegar
        btnCancelar.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_MIS_PRODUCTOS"));
        btnGuardar.addActionListener(e -> guardarProducto());

        // Ensamblar
        contenedorForm.add(new JLabel("<html><center><h2 style='color:white;'>NUEVO PRODUCTO</h2></center></html>"));
        contenedorForm.add(Box.createVerticalStrut(20));
        contenedorForm.add(txtNombre);
        contenedorForm.add(Box.createVerticalStrut(10));
        contenedorForm.add(lblEstado);
        contenedorForm.add(cboEstado);
        contenedorForm.add(Box.createVerticalStrut(10));
        contenedorForm.add(lblImagen);
        contenedorForm.add(panelImagen);
        contenedorForm.add(Box.createVerticalStrut(10));
        contenedorForm.add(lblDesc);
        contenedorForm.add(scrollDesc);
        contenedorForm.add(Box.createVerticalStrut(25));
        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        contenedorForm.add(panelBotones);

        // Centrar el formulario en la pantalla
        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setBackground(Color.WHITE);
        centrado.add(contenedorForm);
        add(centrado, BorderLayout.CENTER);
    }

    private JPanel crearCabecera(String titulo) {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(165, 143, 122));
        cabecera.setBorder(new EmptyBorder(16, 24, 16, 24));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.LEFT);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        cabecera.add(lblTitulo, BorderLayout.WEST);

        return cabecera;
    }

    private JTextField crearCampo(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setMaximumSize(new Dimension(400, 35));
        return tf;
    }

    private JButton crearBotonForm(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color, 8, true));
        return btn;
    }

    private void seleccionarImagen() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imágenes", "jpg", "jpeg", "png", "gif"));
        int resultado = chooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = chooser.getSelectedFile();
            txtImagen.setText(archivo.getAbsolutePath());
        }
    }

    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String imagen = txtImagen.getText().trim();
        EstadoConservacion estado = (EstadoConservacion) cboEstado.getSelectedItem();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes indicar al menos el nombre y la descripción del producto.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.anadirProductoALaCartera(nombre, descripcion, imagen, estado);
        JOptionPane.showMessageDialog(this, "¡Producto subido con éxito!");
        mainFrame.cambiarPantalla("PANTALLA_MIS_PRODUCTOS");
    }
}
