package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;


import productos.*;


public class PanelProductos extends JPanel {


    public PanelProductos(String nombre, List<ProductoTienda> productos) {
        
        List<JButton> botones = new ArrayList<JButton>();
        
        this.setLayout(new FlowLayout());

        this.add(new JLabel(nombre));
        
        for(ProductoTienda p : productos) {

            JButton boton = new JButton(p.getNombre());
            
            boton.addActionListener(new ActionListener() {
                
                @Override
                public void actionPerformed(ActionEvent e) {
                    JDialog dialogo = new JDialog();

                    dialogo.setTitle(p.getNombre());
                    PanelDeProducto detallePanel = new PanelDeProducto(p);

                    dialogo.add(detallePanel);
                    dialogo.pack();
                    dialogo.setLocationRelativeTo(null);
                    dialogo.setVisible(true);
                }
            });



            botones.add(boton);
            this.add(boton);
        }

    
    }
    
}
