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

        public boolean isTiene2x1() { return tiene2x1; }

        public void setPrecio(double precio) {this.precio = precio;}
        
        @Override
        public void setValoracion(int valoracion) {
            super.setValoracion(valoracion);
        }
        
        public void setTiene2x1(boolean tiene2x1) { this.tiene2x1 = tiene2x1; }
        public void setRebajaPorcentaje(double rebajaPorcentaje) { this.rebajaPorcentaje = rebajaPorcentaje; }
        public void setRebajaFija(double rebajaFija) { this.rebajaFija = rebajaFija; }

        public double getPrecio() {return this.precio;}
        public double getRebajaPorcentaje() { return rebajaPorcentaje; }
        public double getRebajaFija() { return rebajaFija; }

}