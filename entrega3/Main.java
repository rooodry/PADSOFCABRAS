import sistema.Sistema;
import usuarios.*;
import productos.*;
import compras.*;
import utilidades.*;
import excepciones.*;


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

        // 3. Crear productos y añadirlos al stock
        ProductoTienda producto1 = new ProductoTienda("Catan", "Juego de mesa clásico", "catan.jpg");
        producto1.setPrecio(39.99);
        ProductoTienda producto2 = new ProductoTienda("One Piece Vol.1", "Manga de One Piece", "op1.jpg");
        producto2.setPrecio(8.99);

        stock.añadirProducto(producto1, 5);
        stock.añadirProducto(producto2, 10);
        System.out.println("✔ Stock inicial: Catan x5, One Piece x10");

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
        System.out.println("  Stock tras añadir a cesta: Catan x" + stock.getNumProductos(producto1)
                + ", One Piece x" + stock.getNumProductos(producto2));

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