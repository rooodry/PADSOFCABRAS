package estadisticas;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.time.*;

import compras.*;
import excepciones.ExcepcionUsuariosAdmin;
import productos.ProductoTienda;
import usuarios.*;
import utilidades.EstadoPedido;

public class Estadistica {
    
    private String fichero;

    public Estadistica(String fichero) {
        this.fichero = fichero;
    }

    public void estadisticaPedidos(List<Pedido> pedidos) {
        
        String buffProductos = "";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            bw.write("DNI USUARIO | PRODUCTOS | PRECIO | FECHA PAGO | FECHA PREPARACION | FECHA RECOGIDA");
            bw.newLine();


            for (Pedido p : pedidos) {

                for (Map.Entry<ProductoTienda, Integer> entry : p.getProductos().entrySet()) {
                    ProductoTienda producto = entry.getKey();
                    Integer cantidad = entry.getValue();
            
                    buffProductos += producto.getNombre() + "(" + cantidad + ") ";
                }
                bw.write(p.getCliente().getDNI() + " | " + buffProductos + " | " + p.calcularPrecioTotal() + " | " + p.getFechaPago() + " | " + p.getFechaPreparacion() + " | " + p.getFechaRecogida());
                bw.newLine();
                buffProductos = "";
            }


        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    public void estadisticaRecaudacionMes(Usuario admin, List<Pedido> pedidos) throws ExcepcionUsuariosAdmin{
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        int[] cantidadMes = {0};
        int mesNumero;
        Calendar cal = Calendar.getInstance();
    

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            for(Pedido p : pedidos) {
                if(p.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                    cal.setTime(p.getFechaPago());
                    mesNumero = cal.get(Calendar.MONTH);

                    cantidadMes[mesNumero] += p.calcularPrecioTotal();
                    }
                }
            
             for(int i = 0; i < 12; i++) {
                bw.write("ENERO: " + cantidadMes[i]);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }

    }



}
