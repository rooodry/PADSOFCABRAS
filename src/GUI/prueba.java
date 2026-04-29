package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class prueba {

    JFrame ventana = new JFrame("GOAT & GET");

    public prueba() {

        Container contenedor = ventana.getContentPane();
        contenedor.setLayout(new FlowLayout());

        JPanel pantalla1 = new JPanel();
        JLabel etiqueta = new JLabel("Identificación: ");
        JTextField campo = new JTextField(10);
        JButton boton = new JButton("Registrarse");

        pantalla1.add(etiqueta);
        pantalla1.add(campo);
        pantalla1.add(boton);
        pantalla1.setVisible(true);

        JPanel pantalla2 = new JPanel();
        JLabel etiqueta2 = new JLabel("Nombre: ");
        JButton boton2 = new JButton("logIn");
        pantalla2.add(boton2);
        pantalla2.add(etiqueta2);
        pantalla2.setVisible(false);

        boton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pantalla1.setVisible(false);
                pantalla2.setVisible(true);
            }
        });
        
        boton2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pantalla1.setVisible(true);
                pantalla2.setVisible(false);
            }
        });

        contenedor.add(pantalla1);
        contenedor.add(pantalla2);

        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(300, 200);
        ventana.setVisible(true);
    }

    public static void main(String[] args) {
        new prueba();
    }
}