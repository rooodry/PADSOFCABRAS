package estadisticas;

import java.io.*;
import java.util.*;

import compras.*;
import excepciones.ExcepcionUsuariosAdmin;
import productos.ProductoSegundaMano;
import productos.ProductoTienda;
import usuarios.*;
import utilidades.EstadoPedido;
import intercambios.*;

/**
 * Proporciona métodos para generar informes estadísticos del sistema y
 * volcarlos a ficheros de texto.
 *
 * <p>Cada instancia está asociada a un fichero de salida identificado por
 * su ruta ({@code fichero}). Los distintos métodos de estadística escriben
 * sus resultados en ese fichero, sobreescribiéndolo cada vez.</p>
 *
 * <p>Los métodos que requieren privilegios de administrador comprueban que
 * el usuario sea una instancia de {@link Gestor} y lanzan
 * {@link ExcepcionUsuariosAdmin} en caso contrario.</p>
 *
 * <p>Todos los métodos de escritura capturan {@link IOException} internamente
 * y muestran el error por {@code System.err} sin propagarlo.</p>
 */
public class Estadistica {

    /** Ruta al fichero donde se escriben los resultados del informe. */
    private String fichero;

    /**
     * Construye una instancia de estadística asociada al fichero indicado.
     *
     * @param fichero ruta al fichero de salida (se creará o sobreescribirá
     *                al generar cualquier informe)
     */
    public Estadistica(String fichero) {
        this.fichero = fichero;
    }

    /**
     * Devuelve la ruta del fichero asociado a esta estadística.
     *
     * @return ruta del fichero de salida
     */
    public String getFichero() {
        return this.fichero;
    }

    /**
     * Genera un informe con el resumen de todos los pedidos de la lista.
     *
     * <p>Formato de cada línea:</p>
     * <pre>
     * DNI USUARIO | PRODUCTOS | PRECIO | FECHA PAGO | FECHA PREPARACION | FECHA RECOGIDA
     * </pre>
     * <p>La columna PRODUCTOS muestra cada producto seguido de su cantidad
     * entre paréntesis, separados por espacios.</p>
     *
     * @param pedidos lista de pedidos a incluir en el informe
     */
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
                bw.write(p.getCliente().getDNI() + " | " + buffProductos + " | "
                        + p.calcularPrecioTotal() + " | " + p.getFechaPago() + " | "
                        + p.getFechaPreparacion() + " | " + p.getFechaRecogida());
                bw.newLine();
                buffProductos = "";
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    /**
     * Genera un informe de recaudación mensual para el año en curso.
     *
     * <p>Suma el total de los pedidos con estado {@link EstadoPedido#ENTREGADO}
     * y el importe de valoración de los productos de segunda mano, agrupados
     * por mes. Solo un {@link Gestor} puede ejecutar este método.</p>
     *
     * <p>Formato de cada línea:</p>
     * <pre>ENERO: 1250.50</pre>
     *
     * @param admin    usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param pedidos  lista de pedidos del sistema
     * @param productos lista de productos de segunda mano con valoración
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
     */
    public void estadisticaRecaudacionMes(Usuario admin, List<Pedido> pedidos,
                                           List<ProductoSegundaMano> productos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        double[] cantidadMes = new double[12];
        int mesNumero;
        Calendar cal = Calendar.getInstance();
        String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO",
                          "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {
            for (Pedido p : pedidos) {
                if (p.getEstadoPedido() == EstadoPedido.ENTREGADO && p.getFechaPago() != null) {
                    cal.setTime(p.getFechaPago());
                    mesNumero = cal.get(Calendar.MONTH);
                    cantidadMes[mesNumero] += p.calcularPrecioTotal();
                }
            }

            for (ProductoSegundaMano p : productos) {
                if (p.getFechaValoracion() != null) {
                    cal.setTime(p.getFechaValoracion());
                    mesNumero = cal.get(Calendar.MONTH);
                    cantidadMes[mesNumero] += p.getValoracionEmpleado();
                }
            }

            for (int i = 0; i < 12; i++) {
                bw.write(meses[i] + ": " + cantidadMes[i]);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    /**
     * Genera un informe de recaudación desglosada por tipo de ingreso:
     * ventas de pedidos y tasaciones de productos de segunda mano.
     *
     * <p>Solo cuenta los pedidos con estado {@link EstadoPedido#ENTREGADO}.
     * Solo un {@link Gestor} puede ejecutar este método.</p>
     *
     * <p>Formato del fichero generado:</p>
     * <pre>
     * RECAUDACIÓN POR VENTAS: 5000.0
     * RECAUDACIÓN POR VALORACIONES: 300.0
     * </pre>
     *
     * @param admin    usuario que ejecuta la acción; debe ser instancia de {@link Gestor}
     * @param pedidos  lista de pedidos del sistema
     * @param productos lista de productos de segunda mano con valoración
     * @throws ExcepcionUsuariosAdmin si {@code admin} no es un {@link Gestor}
     */
    public void estadisticaRecaudacionTipo(Usuario admin, List<Pedido> pedidos,
                                            List<ProductoSegundaMano> productos)
            throws ExcepcionUsuariosAdmin {
        if (!(admin instanceof Gestor)) {
            throw new ExcepcionUsuariosAdmin(admin.getNombre());
        }

        double recaudacionVentas = 0, recaudacionValoraciones = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {
            for (Pedido p : pedidos) {
                if (p.getEstadoPedido() == EstadoPedido.ENTREGADO) {
                    recaudacionVentas += p.calcularPrecioTotal();
                }
            }
            for (ProductoSegundaMano p : productos) {
                recaudacionValoraciones += p.getValoracionEmpleado();
            }

            bw.write("RECAUDACIÓN POR VENTAS: ");
            bw.write(String.valueOf(recaudacionVentas));
            bw.newLine();
            bw.write("RECAUDACIÓN POR VALORACIONES: ");
            bw.write(String.valueOf(recaudacionValoraciones));
            bw.newLine();
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    /**
     * Genera un informe de clientes ordenados de mayor a menor número de
     * pedidos realizados, usando inserción directa.
     *
     * <p>Formato del fichero generado:</p>
     * <pre>
     * CLIENTES Y NÚMERO DE COMPRAS (MAYOR A MENOR)
     * NÚMERO DE COMPRAS | DNI
     * 15 | 12345678A
     * ...
     * </pre>
     *
     * @param clientes lista de clientes registrados a analizar
     */
    public void estadisticasUsuariosMayorActividadCompras(List<ClienteRegistrado> clientes) {
        ClienteRegistrado[] listaOrdenada = new ClienteRegistrado[clientes.size()];

        int i = 0, j = 0;
        for (i = 0; i < clientes.size(); i++) {
            ClienteRegistrado clienteActual = clientes.get(i);
            int numPedidos = clienteActual.getPedidos().size();
            j = i - 1;
            while (j >= 0 && listaOrdenada[j].getPedidos().size() < numPedidos) {
                listaOrdenada[j + 1] = listaOrdenada[j];
                j--;
            }
            listaOrdenada[j + 1] = clienteActual;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {
            bw.write("CLIENTES Y NÚMERO DE COMPRAS (MAYOR A MENOR)");
            bw.newLine();
            bw.write("NÚMERO DE COMPRAS | DNI");
            bw.newLine();
            for (ClienteRegistrado c : listaOrdenada) {
                bw.write(c.getPedidos().size() + " | " + c.getDNI());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    /**
     * Genera un informe de clientes ordenados de mayor a menor número de
     * intercambios, detallando los completados y los pendientes.
     *
     * <p>La ordenación se realiza mediante inserción directa.</p>
     *
     * <p>Formato del fichero generado:</p>
     * <pre>
     * CLIENTES Y NÚMERO DE INTERCAMBIOS (MAYOR A MENOR)
     * DNI | INTERCAMBIOS TOTALES | INTERCAMBIOS HECHOS | INTERCAMBIOS PENDIENTES
     * 12345678A | 10 | 7 | 3
     * ...
     * </pre>
     *
     * @param clientes lista de clientes registrados a analizar
     */
    public void estadisticasUsuariosMayorActividadIntercambios(List<ClienteRegistrado> clientes) {
        ClienteRegistrado[] listaOrdenada = new ClienteRegistrado[clientes.size()];

        int i = 0, j = 0;
        for (i = 0; i < clientes.size(); i++) {
            ClienteRegistrado clienteActual = clientes.get(i);
            int numIntercambios = clienteActual.getIntercambios().size();
            j = i - 1;
            while (j >= 0 && listaOrdenada[j].getIntercambios().size() < numIntercambios) {
                listaOrdenada[j + 1] = listaOrdenada[j];
                j--;
            }
            listaOrdenada[j + 1] = clienteActual;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {
            bw.write("CLIENTES Y NÚMERO DE INTERCAMBIOS (MAYOR A MENOR)");
            bw.newLine();
            bw.write("DNI | INTERCAMBIOS TOTALES | INTERCAMBIOS HECHOS | INTERCAMBIOS PENDIENTES");
            bw.newLine();

            for (ClienteRegistrado c : listaOrdenada) {
                int contHechos = 0, contPendientes = 0;
                for (Intercambio intercambio : c.getIntercambios()) {
                    if (intercambio.getIntercambiado()) {
                        contHechos++;
                    } else {
                        contPendientes++;
                    }
                }
                bw.write(c.getDNI() + " | " + c.getIntercambios().size()
                        + " | " + contHechos + " | " + contPendientes);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }


    /**
     * Genera un informe con las valoraciones de productos de todos los pedidos.
     *
     * <p>Formato de cada línea:</p>
     * <pre>NOMBRE PRODUCTO | DNI USUARIO | VALORACION</pre>
     *
     * @param pedidos lista de pedidos cuyas valoraciones se exportan
     */
    public void estadisticaCompraUsuarioValoracion(List<Pedido> pedidos) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {
            bw.write("NOMBRE PRODUCTO | DNI USUARIO | VALORACION");
            bw.newLine();

            for (Pedido p : pedidos) {
                Map<ProductoTienda, Integer> valoraciones = p.getValoracionesProductos();
                for (Map.Entry<ProductoTienda, Integer> entry : valoraciones.entrySet()) {
                    ProductoTienda producto = entry.getKey();
                    Integer valoracion = entry.getValue();
                    bw.write(producto.getNombre() + " | " + p.getCliente().getNombre()
                            + " | " + valoracion);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }

    /**
     * Genera un informe detallado de las valoraciones de compra de un cliente
     * concreto, incluyendo la categoría y subcategoría de cada producto.
     *
     * <p>Este método es utilizado por el sistema de recomendaciones para
     * construir el perfil de intereses del cliente.</p>
     *
     * <p>Formato de cada línea:</p>
     * <pre>NOMBRE PRODUCTO | DNI USUARIO | VALORACION | CATEGORÍA | SUBCATEGORÍAS</pre>
     *
     * @param c cliente registrado cuyo historial de valoraciones se exporta
     */
    public void estadisticaCompraUsuarioValoracionPorUsuario(ClienteRegistrado c) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.fichero))) {
            bw.write("NOMBRE PRODUCTO | DNI USUARIO | VALORACION | CATEGORÍA | SUBCATEGORÍAS");
            bw.newLine();

            for (Pedido p : c.getPedidos()) {
                Map<ProductoTienda, Integer> valoraciones = p.getValoracionesProductos();
                for (Map.Entry<ProductoTienda, Integer> entry : valoraciones.entrySet()) {
                    ProductoTienda producto = entry.getKey();
                    Integer valoracion = entry.getValue();
                    bw.write(producto.getNombre() + " | " + c.getNombre() + " | "
                            + valoracion + " | " + producto.getCategoria().getNombre()
                            + " | " + producto.getCategoria().getSubcategoria());
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al escribir: " + e.getMessage());
        }
    }
}