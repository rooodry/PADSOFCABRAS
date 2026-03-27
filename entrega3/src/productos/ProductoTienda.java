package productos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProductoTienda extends Producto {

        private double precio;

        public ProductoTienda(String nombre, String descripcion, String imagen) {
            super(nombre, descripcion, imagen);
        }

        public ProductoTienda(String archivo) {
            super("", "", "");
            try(BufferedReader br = new BufferedReader(new FileReader(archivo))){
                String linea;
                String[] elementos;
                while ((linea = br.readLine()) != null) {
                    elementos = linea.split("\\|");
                    this.setNombre(elementos[0]);
                    this.setDescripcion(elementos[1]);
                    this.setImagen(elementos[2]);
                    this.precio = Double.parseDouble(elementos[3]);
                }
            } catch (IOException e) {
                System.err.println("Error abriendo archivo " + e.getMessage());
            }
        }


        //SETTER//
        public void setPrecio(double precio) {this.precio = precio;}
        public void setValoracion(int valoracion) {
            
        }

        //GETTER//
        public double getPrecio() {return this.precio;}




}