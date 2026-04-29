    package GUI;

    import java.awt.FlowLayout;
    import java.awt.Window;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;

    import javax.swing.JButton;
    import javax.swing.JFrame;
    import javax.swing.JLabel;
    import javax.swing.JPanel;
    import javax.swing.JPasswordField;
    import javax.swing.SwingUtilities;



    public class PanelCambiarContraseña extends JPanel {

        private JPasswordField contraseñaNueva;
        private JPasswordField contraseñaNuevaConfirmada;
        private JButton botonGuardar;
        

        PanelCambiarContraseña() {

            setLayout(new FlowLayout());

            this.botonGuardar = new JButton("GUARDAR");
            this.contraseñaNueva = new JPasswordField(15);
            this.contraseñaNuevaConfirmada = new JPasswordField(15);

            JLabel etiquetaContraseña = new JLabel("Las contraseñas no coinciden");

            etiquetaContraseña.setVisible(false);

            botonGuardar.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if(!validarContraseña()) {
                            etiquetaContraseña.setVisible(true);
                        } else {
                            etiquetaContraseña.setVisible(false);
                            Window ventana = SwingUtilities.getWindowAncestor(PanelCambiarContraseña.this);
                            ventana.dispose();
                            revalidate();
                            repaint();
                        }
                    }
                }
            );

        
            add(botonGuardar);
            add(contraseñaNueva);
            add(contraseñaNuevaConfirmada);
            add(etiquetaContraseña);

        }

        public JButton getBoton() {return this.botonGuardar;}

        public boolean validarContraseña() {
            String c1 = new String (this.contraseñaNueva.getPassword());
            String c2 = new String (this.contraseñaNuevaConfirmada.getPassword());

            return c1.equals(c2);

        }
    }
