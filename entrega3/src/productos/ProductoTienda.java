package productos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import productos.categoria.Comic;

/**
 * Representa un producto que es vendido directamente por la tienda.
 * Hereda de la clase abstracta Producto y añade atributos de precio y promociones.
 */
public class ProductoTienda extends Producto {

        private double precio;
        private boolean tiene2x1 = false;
        private double rebajaPorcentaje = 0; 
        private double rebajaFija = 0;

        /**
         * Constructor de la clase ProductoTienda.
         *
         * @param nombre      Nombre del producto de la tienda.
         * @param descripcion Descripción detallada del producto.
         * @param imagen      Ruta de la imagen asociada al producto.
         */
        public ProductoTienda(String nombre, String descripcion, String imagen) {
            super(nombre, descripcion, imagen);
        }

        /**
         * Comprueba si el producto tiene activa la promoción 2x1.
         * @return true si tiene la promoción 2x1, false en caso contrario.
         */
        public boolean isTiene2x1() { return tiene2x1; }

        /**
         * Establece el precio base del producto.
         * @param precio Valor monetario del producto.
         */
        public void setPrecio(double precio) {this.precio = precio;}
        
        /**
         * Sobrescribe el método de valoración para productos de tienda.
         * @param valoracion Nueva valoración entera.
         */
        @Override
        public void setValoracion(int valoracion) {
            super.setValoracion(valoracion);
        }
        
        /**
         * Activa o desactiva la promoción 2x1 para el producto.
         * @param tiene2x1 Estado de la promoción.
         */
        public void setTiene2x1(boolean tiene2x1) { this.tiene2x1 = tiene2x1; }

        /**
         * Establece un descuento en formato de porcentaje.
         * @param rebajaPorcentaje Porcentaje a descontar.
         */
        public void setRebajaPorcentaje(double rebajaPorcentaje) { this.rebajaPorcentaje = rebajaPorcentaje; }

        /**
         * Establece un descuento fijo sobre el precio del producto.
         * @param rebajaFija Cantidad monetaria fija a descontar.
         */
        public void setRebajaFija(double rebajaFija) { this.rebajaFija = rebajaFija; }

        /**
         * Obtiene el precio actual base del producto.
         * @return Precio del producto.
         */
        public double getPrecio() {return this.precio;}

        /**
         * Obtiene el porcentaje de rebaja aplicado al producto.
         * @return Porcentaje de rebaja.
         */
        public double getRebajaPorcentaje() { return rebajaPorcentaje; }

        /**
         * Obtiene la cantidad de rebaja fija aplicada al producto.
         * @return Cantidad monetaria fija descontada.
         */
        public double getRebajaFija() { return rebajaFija; }

}