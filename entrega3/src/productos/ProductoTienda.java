package productos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import productos.categoria.Comic;

public class ProductoTienda extends Producto {

        private double precio;
        private boolean tiene2x1 = false;
        private double rebajaPorcentaje = 0; 
        private double rebajaFija = 0;

        public ProductoTienda(String nombre, String descripcion, String imagen) {
            super(nombre, descripcion, imagen);
        }

        public ProductoTienda(String archivo) {
            super("", "", "");
            try(BufferedReader br = new BufferedReader(new FileReader(archivo))){
                String linea;
                String[] elementos;
                while ((linea = br.readLine()) != null) {
                    elementos = linea.split("\\;");

                    this.setId(elementos[1]);
                    this.setNombre(elementos[2]);
                    this.setDescripcion(elementos[3]);
                    this.setPrecio(Double.parseDouble(elementos[4]));
                    



                    if(elementos[0] == "C") {
                        this.setCategoria(new Comic(elementos[2], ));
                    }   
                    
                    this.setDescripcion(linea);
                    


                    
                    this.setNombre(elementos[0]);
                    this.setDescripcion(elementos[1]);
                    this.setImagen(elementos[2]);
                    this.precio = Double.parseDouble(elementos[3]);

                    
                }
            } catch (IOException e) {
                System.err.println("Error abriendo archivo " + e.getMessage());
            }
        }

        public boolean isTiene2x1() { return tiene2x1; }

        //SETTER//
        public void setPrecio(double precio) {this.precio = precio;}
        public void setValoracion(int valoracion) {
            //FALTA
        }
        public void setTiene2x1(boolean tiene2x1) { this.tiene2x1 = tiene2x1; }
        public void setRebajaPorcentaje(double rebajaPorcentaje) { this.rebajaPorcentaje = rebajaPorcentaje; }
        public void setRebajaFija(double rebajaFija) { this.rebajaFija = rebajaFija; }


        //GETTER//
        public double getPrecio() {return this.precio;}
        public double getRebajaPorcentaje() { return rebajaPorcentaje; }
        public double getRebajaFija() { return rebajaFija; }



}