package GUI;

import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import javax.swing.JPanel;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PanelCambiarNombre extends JPanel {

    private JTextField nombreNuevo;
    private JButton botonGuardar;
    

    PanelCambiarNombre() {

        setLayout(new FlowLayout());

        this.botonGuardar = new JButton("GUARDAR");
        this.nombreNuevo = new JTextField(15);
        add(botonGuardar);
        add(nombreNuevo);


        botonGuardar.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Window ventana = SwingUtilities.getWindowAncestor(PanelCambiarNombre.this);
                    ventana.dispose();
                }
            }
        );

    }

    public JButton getBoton() {return this.botonGuardar;}

}
