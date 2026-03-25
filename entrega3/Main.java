import sistema.Sistema;
import usuarios.*;
import productos.*;
import compras.*;
import utilidades.*;
import excepciones.*;
 
import java.util.List;
 
public class Main {
 
    public static void main(String[] args) {
 
        System.out.println("=== INICIANDO SISTEMA ===\n");
 
        // 1. Crear sistema y stock
        Sistema sistema = new Sistema();
        Stock stock = new Stock();
        sistema.setStock(stock);
 
        // 2. Crear gestor
        Gestor gestor = new Gestor("admin", "1234");
        sistema.addUsuario(gestor);
        System.out.println("✔ Gestor creado: " + gestor.getNombre());
 
        // 3. Cargar productos desde fichero y añadirlos al stock
        List<ProductoTienda> productosCargados = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("txt/cargaproductos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split("\\|");
                if (partes.length < 4) {
                    System.err.println("✘ Línea mal formateada, se ignora: " + linea);
                    continue;
                }
                ProductoTienda p = new ProductoTienda(partes[0].trim(), partes[1].trim(), partes[2].trim());
                p.setPrecio(Double.parseDouble(partes[3].trim()));
                stock.añadirProducto(p, 5); // cantidad inicial por defecto
                productosCargados.add(p);
                System.out.println("  + " + p.getNombre() + " (" + p.getPrecio() + "€)");
            }
            System.out.println("✔ " + productosCargados.size() + " productos cargados desde cargaproductos.txt");
        } catch (java.io.FileNotFoundException e) {
            System.err.println("✘ No se encontró cargaproductos.txt");
            return;
        } catch (java.io.IOException e) {
            System.err.println("✘ Error leyendo cargaproductos.txt: " + e.getMessage());
            return;
        }
 
        // Usamos los dos primeros productos para el resto del main
        if (productosCargados.size() < 2) {
            System.err.println("✘ El fichero necesita al menos 2 productos para continuar el test");
            return;
        }
        ProductoTienda producto1 = productosCargados.get(0);
        ProductoTienda producto2 = productosCargados.get(1);
 
        // 4. Crear empleado de productos
        try {
            sistema.darAltaEmpleado(gestor, "empleado1", "pass1", TiposEmpleado.EMPLEADOS_PRODUCTO);
            System.out.println("✔ EmpleadoProducto creado");
        } catch (ExcepcionUsuariosAdmin e) {
            System.err.println("✘ Error dando de alta empleado: " + e.getMessage());
        }
 
        // 5. Crear cliente registrado
        ClienteRegistrado cliente = new ClienteRegistrado("juan123", "pass", "12345678A");
        sistema.addUsuario(cliente);
        System.out.println("✔ ClienteRegistrado creado: " + cliente.getNombre());
 
        // 6. Cliente añade productos a la cesta
        cliente.añadirALaCesta(producto1, stock);
        cliente.añadirALaCesta(producto2, stock);
        System.out.println("✔ Productos añadidos a la cesta");
        System.out.println("  Stock tras añadir a cesta: " + producto1.getNombre() + " x" + stock.getNumProductos(producto1)
                + ", " + producto2.getNombre() + " x" + stock.getNumProductos(producto2));
 
        // 7. Cliente compra (cesta -> pedido)
        Status statusCompra = cliente.comprar();
        System.out.println("✔ comprar() → " + statusCompra);
        System.out.println("  Pedidos del cliente: " + cliente.getPedidos().size());
 
        // 8. Cliente paga el pedido
        Pedido pedido = cliente.getPedidos().get(0);
        Status statusPago = cliente.pagarPedido(pedido);
        System.out.println("✔ pagarPedido() → " + statusPago);
        System.out.println("  Estado pedido: " + pedido.getEstadoPedido());
        System.out.println("  Precio total: " + pedido.calcularPrecioTotal() + "€");
 
        // 9. Sistema registra el pedido
        sistema.registrarPedido(pedido);
        System.out.println("✔ Pedido registrado en el sistema");
 
        // 10. Empleado procesa el pedido
        EmpleadoPedido empleadoPedido = new EmpleadoPedido("empPedido", "pass2");
        empleadoPedido.marcarComoListo(pedido);
        System.out.println("✔ marcarComoListo() → estado: " + pedido.getEstadoPedido());
 
        empleadoPedido.entregarPedido(pedido);
        System.out.println("✔ entregarPedido() → estado: " + pedido.getEstadoPedido());
 
        // 11. Producto segunda mano
        System.out.println("\n=== SEGUNDA MANO ===\n");
        ProductoSegundaMano psm = new ProductoSegundaMano("Catan usado", "Buen estado", "catan_used.jpg", cliente);
        Status statusSubir = cliente.subirProducto(psm);
        System.out.println("✔ subirProducto() → " + statusSubir);
        System.out.println("  Productos en cartera: " + cliente.getCartera().getNumProductos());
 
        // Empleado intercambio valora el producto
        EmpleadoIntercambio empIntercambio = new EmpleadoIntercambio("empIntercambio", "pass3");
        empIntercambio.valorarProducto(psm, 8, 25.0, EstadoConservacion.MUY_BUENO);
        System.out.println("✔ Producto valorado");
        System.out.println("  Estado conservación: " + psm.getEstadoConservacion());
        System.out.println("  Valor estimado: " + psm.getValorEstimado() + "€");
 
        System.out.println("\n=== FIN ===");
    }
}