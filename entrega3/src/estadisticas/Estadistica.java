package estadisticas;

import java.io.*;
import java.util.*;


import compras.*;
import excepciones.ExcepcionUsuariosAdmin;
import productos.Producto;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import usuarios.*;
import utilidades.EstadoPedido;
import intercambios.*;
import productos.categoria.*;

public class Estadistica {
    
    private String fichero;

    public Estadistica(String fichero) {
        this.fichero = fichero;
    }

    public String getFichero() {return this.fichero;}

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

    public void estadisticaRecaudacionMes(Usuario admin, List<Pedido> pedidos, List<ProductoSegundaMano> productos) throws ExcepcionUsuariosAdmin{
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        double[] cantidadMes = {0};
        int mesNumero;
        Calendar cal = Calendar.getInstance();
        String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
    

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            for(Pedido p : pedidos) {
                if(p.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                    cal.setTime(p.getFechaPago());
                    mesNumero = cal.get(Calendar.MONTH);

                    cantidadMes[mesNumero] += p.calcularPrecioTotal();
                    }
            }
            
            for(ProductoSegundaMano p : productos) {
                cal.setTime(p.getFechaValoracion());
                mesNumero = cal.get(Calendar.MONTH);
                cantidadMes[mesNumero] += p.getValoracionEmpleado();
            }
            

            for(int i = 0; i < 12; i++) {
                bw.write(meses[i] + cantidadMes[i]);
                bw.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }

    }

    public void estadisticaRecaudacionTipo(Usuario admin, List<Pedido> pedidos, List<ProductoSegundaMano> productos) throws ExcepcionUsuariosAdmin{
        
        
        if(!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        double recaudacionVentas = 0, recaudacionValoraciones = 0;


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            for(Pedido p : pedidos) {
                if(p.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                    recaudacionVentas += p.calcularPrecioTotal();
                }
            }

            for(ProductoSegundaMano p : productos) {
                recaudacionValoraciones += p.getValoracionEmpleado();
            }

            bw.write("RECAUDACIÓN POR VENTAS: ");
            bw.write(String.valueOf(recaudacionVentas));
            bw.newLine();
            bw.write("RECAUDACIÓN POR VALORACIONES: ");
            bw.write(String.valueOf(recaudacionValoraciones));

        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }


    public void estadisticasUsuariosMayorActividadCompras(List<ClienteRegistrado> clientes) {

        ClienteRegistrado[] listaOrdenada = new ClienteRegistrado[clientes.size()];

        int i = 0, j = 0;
        for(i = 0; i < clientes.size(); i++) {

            ClienteRegistrado clienteActual = clientes.get(i);
            int numPedidos = clienteActual.getPedidos().size();

            j = i - 1;

            while(j >= 0 && listaOrdenada[j].getPedidos().size() < numPedidos) {
                listaOrdenada[j + 1] = listaOrdenada[j];
                j--;
            }

            listaOrdenada[j+1] = clienteActual;

        }


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            bw.write("CLIENTES Y NÚMERO DE COMPRAS (MAYOR A MENOR)");
            bw.newLine();
            bw.write("NÚMERO DE COMPRAS | DNI");
            bw.newLine();
            
            for(ClienteRegistrado c : listaOrdenada) {
                bw.write(c.getPedidos().size() + " | " + c.getDNI());
            }

        } catch (IOException e) {
             System.err.println("Error al escribir: " + e.getMessage());
        }

    }

    public void estadisticasUsuariosMayorActividadIntercambios(List<ClienteRegistrado> clientes) {

        ClienteRegistrado[] listaOrdenada = new ClienteRegistrado[clientes.size()];

        int i = 0, j = 0;

        for(i = 0; i < clientes.size(); i++) {

            ClienteRegistrado clienteActual = clientes.get(i);
            int numIntercambios = clienteActual.getIntercambios().size();

            j = i - 1;

            while(j >= 0 && listaOrdenada[j].getIntercambios().size() < numIntercambios) {
                listaOrdenada[j + 1] = listaOrdenada[j];
                j--;
            }

            listaOrdenada[j+1] = clienteActual;

        }


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            bw.write("CLIENTES Y NÚMERO DE INTERCAMBIOS (MAYOR A MENOR)");
            bw.newLine();
            bw.write("DNI | INTERCAMBIOS TOTALES | INTERCAMBIOS HECHOS | INTERCAMBIOS PENDIENTES");
            bw.newLine();
            
            for(ClienteRegistrado c : listaOrdenada) {

                int contIntercambiosHechos = 0, contIntercambiosPendientes = 0;

                for(Intercambio intercambio : c.getIntercambios()) {

                    if(intercambio.getIntercambiado()) {
                        contIntercambiosHechos ++;
                    } else {
                        contIntercambiosPendientes ++;
                    }

                }


                bw.write(c.getDNI() + " | " + c.getIntercambios().size() + " | " + contIntercambiosHechos + " | " + contIntercambiosPendientes);
                bw.newLine();
            }

        } catch (IOException e) {
             System.err.println("Error al escribir: " + e.getMessage());
        }

    }

    public void estadisticaCompraUsuarioValoracion(List<Pedido> pedidos) {

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            bw.write("NOMBRE PRODUCTO | DNI USUARIO | VALORACION");
            bw.newLine();

            for(Pedido p: pedidos) {
                Map<ProductoTienda, Integer> valoraciones = p.getValoracionesProductos();
                for(Map.Entry<ProductoTienda, Integer> entry : valoraciones.entrySet()) {
                    ProductoTienda producto = entry.getKey();
                    Integer valoracion = entry.getValue();
                    
                    bw.write(producto.getNombre() + " | " + p.getCliente().getNombre() + " | " + valoracion);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }

    }

    public void estadisticaCompraUsuarioValoracionPorUsuario(ClienteRegistrado c) {

       try(BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {

            bw.write("NOMBRE PRODUCTO | DNI USUARIO | VALORACION | CATEGORÍA | SUBCATEGORÍAS");
            bw.newLine();

            for(Pedido p: c.getPedidos()) {
                Map<ProductoTienda, Integer> valoraciones = p.getValoracionesProductos();
                for(Map.Entry<ProductoTienda, Integer> entry : valoraciones.entrySet()) {
                    ProductoTienda producto = entry.getKey();
                    Integer valoracion = entry.getValue();
                    bw.write(producto.getNombre() + " | " + c.getNombre() + " | " + valoracion + " | " + producto.getCategoria().getNombre() + " | " + producto.getCategoria().getSubcategorias());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }
    


}
