package GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Representa el componente PanelSubirProducto de la interfaz gráfica.
 * Este panel proporciona un formulario para que el usuario pueda introducir
 * los datos de un nuevo producto (nombre, descripción e imagen) y darlo de alta en el sistema.
 */
public class PanelSubirProducto extends JPanel {
    
    /** Referencia al marco principal de la aplicación para gestionar la navegación y la lógica de negocio. */
    private Main mainFrame;
    
    /** Campo de texto para introducir el nombre del producto. */
    private JTextField txtNombre;
    
    /** Área de texto para introducir la descripción detallada del producto. */
    private JTextArea txtDescripcion;
    
    /** Campo de texto que almacena y muestra la ruta del archivo de imagen seleccionado. */
    private JTextField txtImagen;

    /**
     * Construye una instancia de PanelSubirProducto.
     * Configura el diseño visual del formulario, incluyendo campos de texto, 
     * selectores de archivos y botones de acción.
     * 
     * @param mainFrame el controlador principal de la interfaz gráfica
     */
    public PanelSubirProducto(Main mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        add(crearCabecera("SUBIR NUEVO PRODUCTO"), BorderLayout.NORTH);

        /**
         * Panel personalizado con bordes redondeados para contener el formulario.
         */
        JPanel contenedorForm = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(165, 143, 122));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
        };
        contenedorForm.setOpaque(false);
        contenedorForm.setLayout(new BoxLayout(contenedorForm, BoxLayout.Y_AXIS));
        contenedorForm.setBorder(new EmptyBorder(30, 40, 30, 40));


        txtNombre = crearCampo("NOMBRE DEL PRODUCTO");

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


        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelBotones.setOpaque(false);

        JButton btnGuardar = crearBotonForm("GUARDAR", new Color(102, 80, 61));
        JButton btnCancelar = crearBotonForm("CANCELAR", new Color(181, 86, 68));


        btnCancelar.addActionListener(e -> mainFrame.cambiarPantalla("PANTALLA_MIS_PRODUCTOS"));
        btnGuardar.addActionListener(e -> guardarProducto());


        contenedorForm.add(new JLabel("<html><center><h2 style='color:white;'>NUEVO PRODUCTO</h2></center></html>"));
        contenedorForm.add(Box.createVerticalStrut(20));
        contenedorForm.add(txtNombre);
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


        JPanel centrado = new JPanel(new GridBagLayout());
        centrado.setBackground(Color.WHITE);
        centrado.add(contenedorForm);
        add(centrado, BorderLayout.CENTER);
    }

    /**
     * Crea el panel superior que contiene el título de la sección.
     * 
     * @param titulo texto que se mostrará en la cabecera
     * @return un JPanel configurado como cabecera estilizada
     */
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

    /**
     * Crea un campo de texto con un texto de sugerencia inicial.
     * 
     * @param placeholder el texto inicial que indica el propósito del campo
     * @return un JTextField configurado con dimensiones máximas
     */
    private JTextField crearCampo(String placeholder) {
        JTextField tf = new JTextField(placeholder);
        tf.setMaximumSize(new Dimension(400, 35));
        return tf;
    }

    /**
     * Crea un botón estilizado para el formulario con colores específicos.
     * 
     * @param texto el texto que mostrará el botón
     * @param color el color de fondo y del borde del botón
     * @return un JButton configurado estéticamente
     */
    private JButton crearBotonForm(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(color, 8, true));
        return btn;
    }

    /**
     * Abre un selector de archivos (JFileChooser) para que el usuario elija una imagen.
     * Filtra los archivos para mostrar únicamente formatos de imagen comunes.
     */
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

    /**
     * Valida los datos introducidos en el formulario y, si son correctos,
     * solicita al frame principal la creación del nuevo producto.
     * Muestra mensajes de error si faltan campos obligatorios o de éxito tras guardar.
     */
    private void guardarProducto() {
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        String imagen = txtImagen.getText().trim();

        if (nombre.isEmpty() || descripcion.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debes indicar al menos el nombre y la descripción del producto.",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        mainFrame.anadirProductoALaCartera(nombre, descripcion, imagen);
        JOptionPane.showMessageDialog(this, "¡Producto subido con éxito!");
        mainFrame.cambiarPantalla("PANTALLA_MIS_PRODUCTOS");
    }
}