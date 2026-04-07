import compras.*;
import descuentos.*;
import intercambios.*;
import notificaciones.*;
import productos.*;
import productos.categoria.*;
import sistema.Sistema;
import usuarios.*;
import utilidades.*;
 
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
 
/**
 * Clase de arranque para ejecutar pruebas manuales del sistema PADSOFCABRAS.
 */
public class Main {
 
    // Sistema compartido por todas las pruebas
    private static Sistema sistema      = new Sistema();
    private static Stock stock          = new Stock();
    private static Gestor gestor;
    // Referencias locales a empleados (Sistema no expone getUsuarios())
    private static EmpleadoProducto    empProducto;
    private static EmpleadoPedido      empPedido;
    private static EmpleadoIntercambio empIntercambio;
 
    private Main() {}
 
    public static void main(String[] args) {
        System.out.println("\n=== PRUEBAS DEL SISTEMA PADSOFCABRAS ===\n");
 
        // Inicializar datos de prueba compartidos
        inicializarDatos();
 
        // Descomentar el bloque que quieras probar:
 
        probarGestor();
        probarEmpleadoProducto();
        probarEmpleadoPedido();
        probarEmpleadoIntercambio();
        probarClienteRegistrado();
 
        System.out.println("\n=== FIN DE PRUEBAS ===");
    }
 
    // =========================================================================
    // INICIALIZACIÓN DE DATOS COMUNES
    // =========================================================================
 
    /**
     * Crea los objetos base que necesitan todas las pruebas:
     * stock, productos de tienda, gestor y empleados de los tres tipos.
     */
    private static void inicializarDatos() {
        System.out.println("--- Inicializando datos de prueba ---");
 
        // Stock
        sistema.setStock(stock);
 
        // Productos de tienda
        ProductoTienda comic1 = new ProductoTienda("Watchmen", "Clásico de Alan Moore", "watchmen.png");
        comic1.setPrecio(15.0);
        ProductoTienda juego1 = new ProductoTienda("Catan", "Juego de estrategia", "catan.png");
        juego1.setPrecio(40.0);
        ProductoTienda figura1 = new ProductoTienda("Figura Spiderman", "Figura de colección", "spider.png");
        figura1.setPrecio(25.0);
 
        stock.añadirProducto(comic1, 10);
        stock.añadirProducto(juego1, 5);
        stock.añadirProducto(figura1, 3);
 
        sistema.addProducto(comic1);
        sistema.addProducto(juego1);
        sistema.addProducto(figura1);
 
        // Gestor
        gestor = new Gestor("Admin", "admin123");
        sistema.addUsuario(gestor);
 
        // Empleados (referencias guardadas localmente para poder buscarlos después)
        empProducto    = new EmpleadoProducto("Ana",  "pass1", stock);
        empPedido      = new EmpleadoPedido("Luis",   "pass2");
        empIntercambio = new EmpleadoIntercambio("Marta", "pass3");
        sistema.addUsuario(empProducto);
        sistema.addUsuario(empPedido);
        sistema.addUsuario(empIntercambio);
 
        System.out.println("Datos inicializados: " + sistema.getClass().getSimpleName()
                + " | Stock con " + stock.getProductos().size() + " productos");
    }
 
    // =========================================================================
    // 1. PRUEBAS DEL GESTOR
    // =========================================================================
 
    private static void probarGestor() {
        System.out.println("\n\n=== PRUEBAS DEL GESTOR ===");
 
        System.out.println("\n--- 1. Dar de alta empleados ---");
        try {
            sistema.darAltaEmpleado(gestor, "Ana", "pass1", TiposEmpleado.EMPLEADOS_PRODUCTO);
            sistema.darAltaEmpleado(gestor, "Luis", "pass2", TiposEmpleado.EMPLEADOS_PEDIDO);
            sistema.darAltaEmpleado(gestor, "Marta", "pass3", TiposEmpleado.EMPLEADOS_INTERCAMBIO);
            System.out.println("Empleados dados de alta correctamente");
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Error (inesperado): " + e.getMessage());
        }
 
        System.out.println("\n--- 2. Intentar dar de alta con no-gestor (debe lanzar excepción) ---");
        ClienteRegistrado clienteNoAdmin = new ClienteRegistrado("Intruso", "pwd", "99999999Z");
        try {
            sistema.darAltaEmpleado(clienteNoAdmin, "Fake", "fake", TiposEmpleado.EMPLEADOS_PEDIDO);
            System.out.println("ERROR: debería haber lanzado excepción");
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        }
 
        System.out.println("\n--- 3. Crear pack ---");
        ProductoTienda p1 = new ProductoTienda("Pack Comic A", "desc", "img.png");
        p1.setPrecio(10.0);
        ProductoTienda p2 = new ProductoTienda("Pack Juego B", "desc", "img.png");
        p2.setPrecio(20.0);
        List<Producto> listaProductosPack = new ArrayList<>();
        listaProductosPack.add(p1);
        listaProductosPack.add(p2);
        try {
            Pack pack = sistema.crearPack(gestor, "Pack Oferta", 25.0, listaProductosPack);
            System.out.println("Pack creado: " + pack.getNombre()
                    + " | Precio: " + pack.getPrecio()
                    + " | Nº productos: " + pack.getProductos().size());
 
            // Subpack recursivo
            List<Producto> listaSubpack = new ArrayList<>();
            listaSubpack.add(p1);
            Pack subpack = sistema.crearPack(gestor, "Subpack Básico", 8.0, listaSubpack);
            pack.addSubpack(subpack);
            System.out.println("Subpack añadido: " + subpack.getNombre()
                    + " | Subpacks del pack: " + pack.getSubpacks().size());
            pack.removeSubpack(subpack);
            System.out.println("Subpack eliminado | Subpacks del pack: " + pack.getSubpacks().size());
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Error: " + e.getMessage());
        }
 
        System.out.println("\n--- 4. Actualizar stock ---");
        ProductoTienda productoStock = stock.getProductos().keySet().iterator().next();
        int stockAntes = stock.getNumProductos(productoStock);
        try {
            sistema.actualizarStock(gestor, productoStock, 10);
            System.out.println("Stock de '" + productoStock.getNombre() + "': "
                    + stockAntes + " → " + stock.getNumProductos(productoStock));
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Error: " + e.getMessage());
        }
 
        System.out.println("\n--- 5. Generar código de recogida ---");
        Codigo codigo = sistema.generarCodigo();
        System.out.println("Código generado (no nulo): " + (codigo != null));
 
        System.out.println("\n--- 6. Configurar descuentos (sobre un producto) ---");
        // Descuento por porcentaje
        ProductoTienda prodDesc = stock.getProductos().keySet().iterator().next();
        Date hoy   = new Date();
        Date enUnMes = new Date(hoy.getTime() + 30L * 24 * 60 * 60 * 1000);
        DescuentoPorcentaje dpct = new DescuentoPorcentaje(hoy, enUnMes, 20.0);
        sistema.addDescuento(dpct);
        System.out.println("Descuento 20% creado para: " + prodDesc.getNombre());
 
        // Descuento 2x1
        DescuentoDosPorUno d2x1 = new DescuentoDosPorUno(hoy, enUnMes);
        sistema.addDescuento(d2x1);
        System.out.println("Descuento 2x1 creado");
 
        System.out.println("\n--- 7. Modificar permisos de un empleado ---");
        // Buscar el EmpleadoProducto "Ana" y cambiarle a EmpleadoPedido
        try {
            Empleado empleadoAna = encontrarEmpleadoPorNombre("Ana");
            if (empleadoAna != null) {
                Empleado empleadoModificado = sistema.modificarPermiso(
                        gestor, empleadoAna, TiposEmpleado.EMPLEADOS_PEDIDO);
                System.out.println("Permiso de Ana cambiado a: "
                        + empleadoModificado.getClass().getSimpleName());
                // Restaurar
                sistema.modificarPermiso(gestor, empleadoModificado, TiposEmpleado.EMPLEADOS_PRODUCTO);
                System.out.println("Permiso restaurado a EmpleadoProducto");
            }
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
 
    // =========================================================================
    // 2. PRUEBAS DEL EMPLEADO DE PRODUCTOS
    // =========================================================================
 
    private static void probarEmpleadoProducto() {
        System.out.println("\n\n=== PRUEBAS DEL EMPLEADO DE PRODUCTOS ===");
 
        EmpleadoProducto emp = empProducto; // reutilizar empleado creado en inicializarDatos()
 
        System.out.println("\n--- 1. Añadir producto al stock ---");
        ProductoTienda nuevo = new ProductoTienda("Astérix", "Edición especial", "asterix.png");
        nuevo.setPrecio(12.0);
        Status resultado = emp.añadirProducto(nuevo, 5);
        System.out.println("Añadir 5 unidades de 'Astérix': " + resultado
                + " | Stock: " + stock.getNumProductos(nuevo));
 
        System.out.println("\n--- 2. Intentar añadir con cantidad inválida ---");
        Status resultadoInvalido = emp.añadirProducto(nuevo, -1);
        System.out.println("Añadir -1 unidades (debe ser ERROR): " + resultadoInvalido);
 
        System.out.println("\n--- 3. Editar producto ---");
        emp.editarProducto(nuevo, "Astérix Deluxe", "Edición coleccionista", "asterix_deluxe.png", 18.0);
        System.out.println("Nombre actualizado: " + nuevo.getNombre()
                + " | Precio actualizado: " + nuevo.getPrecio());
 
        System.out.println("\n--- 4. Retirar producto del stock ---");
        emp.retirarProducto(nuevo);
        System.out.println("Stock de 'Astérix Deluxe' tras retiro: " + stock.getNumProductos(nuevo));
 
        System.out.println("\n--- 5. Aplicar rebaja de precio fija a un producto ---");
        ProductoTienda prodRebaja = stock.getProductos().keySet().iterator().next();
        prodRebaja.setRebajaFija(5.0);
        System.out.println("Rebaja fija de 5€ aplicada a: " + prodRebaja.getNombre()
                + " | Precio base: " + prodRebaja.getPrecio()
                + " | Rebaja fija: " + prodRebaja.getRebajaFija());
        prodRebaja.setRebajaFija(0.0); // restaurar
 
        System.out.println("\n--- 6. Aplicar rebaja por porcentaje a un producto ---");
        prodRebaja.setRebajaPorcentaje(0.10); // 10%
        System.out.println("Rebaja del 10% aplicada a: " + prodRebaja.getNombre()
                + " | Rebaja porcentaje: " + prodRebaja.getRebajaPorcentaje());
        prodRebaja.setRebajaPorcentaje(0.0); // restaurar
 
        System.out.println("\n--- 7. Activar 2x1 en un producto ---");
        prodRebaja.setTiene2x1(true);
        System.out.println("2x1 activo en '" + prodRebaja.getNombre() + "': " + prodRebaja.isTiene2x1());
        prodRebaja.setTiene2x1(false); // restaurar
    }
 
    // =========================================================================
    // 3. PRUEBAS DEL EMPLEADO DE PEDIDOS
    // =========================================================================
 
    private static void probarEmpleadoPedido() {
        System.out.println("\n\n=== PRUEBAS DEL EMPLEADO DE PEDIDOS ===");
 
        EmpleadoPedido emp = empPedido;
 
        // Crear un pedido de prueba
        ClienteRegistrado clientePedido = new ClienteRegistrado("TestPedido", "pwd", "12345678T");
        ProductoTienda prod = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prod, 5); // asegurar stock
        clientePedido.añadirALaCesta(prod, stock);
        clientePedido.comprar();
        Pedido pedido = clientePedido.getPedidos().get(0);
        sistema.addPedido(pedido);
 
        System.out.println("\n--- 1. Estado inicial del pedido ---");
        System.out.println("Estado: " + pedido.getEstadoPedido());
        System.out.println("Código (no nulo): " + (pedido.getCodigo() != null));
        System.out.println("Fecha de realización (no nula): " + (pedido.getFechaRealizacion() != null));
        System.out.println("Nº productos: " + pedido.getProductos().size());
 
        System.out.println("\n--- 2. Flujo completo de estados ---");
        // Pagar → EN_PREPARACION
        Status pagado = clientePedido.pagarPedido(pedido);
        System.out.println("Pago del cliente: " + pagado + " | Estado: " + pedido.getEstadoPedido());
 
        // Empleado marca como LISTO
        emp.marcarComoListo(pedido);
        System.out.println("Marcar como LISTO | Estado: " + pedido.getEstadoPedido());
        System.out.println("Fecha preparación (no nula): " + (pedido.getFechaPreparacion() != null));
 
        // Empleado entrega
        emp.entregarPedido(pedido);
        System.out.println("Entregar pedido | Estado: " + pedido.getEstadoPedido());
        System.out.println("Fecha recogida (no nula): " + (pedido.getFechaRecogida() != null));
 
        System.out.println("\n--- 3. Cancelar pedido (repone stock) ---");
        // Crear otro pedido para cancelarlo
        ClienteRegistrado clienteCancelacion = new ClienteRegistrado("TestCancel", "pwd", "87654321C");
        ProductoTienda prodCancel = stock.getProductos().keySet().iterator().next();
        int stockAntes = stock.getNumProductos(prodCancel);
        stock.añadirProducto(prodCancel, 3);
        clienteCancelacion.añadirALaCesta(prodCancel, stock);
        clienteCancelacion.comprar();
        Pedido pedidoCancel = clienteCancelacion.getPedidos().get(0);
        sistema.cancelarPedido(pedidoCancel);
        System.out.println("Pedido cancelado | Estado: " + pedidoCancel.getEstadoPedido());
        System.out.println("Stock repuesto tras cancelación: "
                + stock.getNumProductos(prodCancel) + " (antes: " + stockAntes + ")");
 
        System.out.println("\n--- 4. Calcular precio total del pedido con descuento ---");
        ClienteRegistrado clienteDesc = new ClienteRegistrado("TestDesc", "pwd", "11111111D");
        ProductoTienda prodDesc = new ProductoTienda("Comic Desc", "desc", "img.png");
        prodDesc.setPrecio(100.0);
        stock.añadirProducto(prodDesc, 5);
        clienteDesc.añadirALaCesta(prodDesc, stock);
        clienteDesc.comprar();
        Pedido pedidoDesc = clienteDesc.getPedidos().get(0);
 
        Date ini = new Date();
        Date fin = new Date(ini.getTime() + 7L * 24 * 60 * 60 * 1000);
        pedidoDesc.setDescuento(new DescuentoPorcentaje(ini, fin, 20.0));
        System.out.println("Precio sin descuento: 100.0");
        System.out.println("Precio con 20% de descuento: " + pedidoDesc.calcularPrecioTotal());
 
        System.out.println("\n--- 5. Editar fecha de recogida ---");
        Date nuevaFecha = new Date(System.currentTimeMillis() + 2L * 24 * 60 * 60 * 1000);
        emp.editarPedido(pedido, nuevaFecha);
        System.out.println("Fecha de recogida editada (no nula): " + (pedido.getFechaRecogida() != null));
    }
 
    // =========================================================================
    // 4. PRUEBAS DEL EMPLEADO DE INTERCAMBIOS
    // =========================================================================
 
    private static void probarEmpleadoIntercambio() {
        System.out.println("\n\n=== PRUEBAS DEL EMPLEADO DE INTERCAMBIOS ===");
 
        EmpleadoIntercambio emp = empIntercambio;
 
        // Clientes y productos de segunda mano
        ClienteRegistrado c1 = new ClienteRegistrado("Carlos", "pwd", "22222222C");
        ClienteRegistrado c2 = new ClienteRegistrado("Sofía",  "pwd", "33333333S");
 
        ProductoSegundaMano psm1 = new ProductoSegundaMano("Cómic usado", "Muy buen estado", null, c1);
        ProductoSegundaMano psm2 = new ProductoSegundaMano("Figura usada", "Algo desgastada", null, c2);
 
        c1.subirProducto(psm1);
        c2.subirProducto(psm2);
 
        System.out.println("\n--- 1. Estado inicial de los productos ---");
        System.out.println("psm1 valorado: " + psm1.getEstaValorado());
        System.out.println("psm1 disponible: " + psm1.getDisponibilidad());
        System.out.println("psm1 estado: " + psm1.getEstadoProducto());
 
        System.out.println("\n--- 2. Asignar productos para valorar ---");
        sistema.asignarValoracion(psm1, emp);
        sistema.asignarValoracion(psm2, emp);
        System.out.println("Productos por valorar: " + emp.getProductosPorValorar().size());
 
        System.out.println("\n--- 3. Valorar productos ---");
        emp.valorarProducto(psm1, 8, 15.0, EstadoConservacion.MUY_BUENO);
        emp.valorarProducto(psm2, 6, 10.0, EstadoConservacion.USO_LIGERO);
        System.out.println("Tras valorar → pendientes: " + emp.getProductosPorValorar().size());
        System.out.println("psm1 valorado: " + psm1.getEstaValorado()
                + " | Valor estimado: " + psm1.getValorEstimado()
                + " | Conservación: " + psm1.getEstadoConservacion());
 
        System.out.println("\n--- 4. Subir productos al mercado ---");
        psm1.subirProducto();
        psm2.subirProducto();
        System.out.println("psm1 disponible: " + psm1.getDisponibilidad());
        System.out.println("psm2 disponible: " + psm2.getDisponibilidad());
 
        System.out.println("\n--- 5. Crear oferta e intercambio ---");
        Oferta oferta = new Oferta(psm1, psm2, c2, c1);
        System.out.println("Estado inicial oferta: " + oferta.getEstadoOferta());
 
        Intercambio intercambio = new Intercambio(new Date(), oferta);
        sistema.asignarIntercambio(intercambio, emp);
        System.out.println("Intercambios asignados al empleado: " + emp.getIntercambiosPendientes().size());
        System.out.println("Intercambiado inicialmente: " + intercambio.getIntercambiado());
        System.out.println("Fecha límite > ahora: " + intercambio.getFechaLimite().after(new Date()));
 
        System.out.println("\n--- 6. Aceptar oferta y transferir propiedad ---");
        intercambio.aceptarOferta();
        System.out.println("Oferta aceptada: " + oferta.getEstadoOferta());
 
        emp.transferirPropiedad(intercambio);
        System.out.println("Intercambio completado: " + intercambio.getIntercambiado());
        System.out.println("c2 recibe psm1: " + c2.getCartera().getProductos().contains(psm1));
        System.out.println("c1 recibe psm2: " + c1.getCartera().getProductos().contains(psm2));
 
        System.out.println("\n--- 7. Rechazar una oferta (flujo alternativo) ---");
        ProductoSegundaMano psm3 = new ProductoSegundaMano("Juego usado", "Estado perfecto", null, c1);
        ProductoSegundaMano psm4 = new ProductoSegundaMano("Póster enmarcado", "Vintage", null, c2);
        c1.subirProducto(psm3);
        c2.subirProducto(psm4);
        emp.valorarProducto(psm3, 9, 20.0, EstadoConservacion.PERFECTO);
        emp.valorarProducto(psm4, 7, 12.0, EstadoConservacion.USO_LIGERO);
 
        Oferta ofertaRechazo = new Oferta(psm3, psm4, c2, c1);
        Intercambio intercambioRechazo = new Intercambio(new Date(), ofertaRechazo);
        intercambioRechazo.rechazarOferta();
        System.out.println("Oferta rechazada: " + ofertaRechazo.getEstadoOferta());
 
        System.out.println("\n--- 8. Notificar intercambio listo ---");
        Notificacion notif = new Notificacion(TipoNotificacion.INTERCAMBIO_REALIZADO, "Tu intercambio está listo");
        c1.addNotificacion(notif);
        System.out.println("Notificaciones de c1: " + c1.getNotificaciones().size());
 
        System.out.println("\n--- 9. Reportar fallo en intercambio ---");
        ProductoSegundaMano psm5 = new ProductoSegundaMano("Extra A", "desc", null, c1);
        ProductoSegundaMano psm6 = new ProductoSegundaMano("Extra B", "desc", null, c2);
        psm5.setDisponibilidad(false);
        psm6.setDisponibilidad(false);
        Oferta ofertaFallo = new Oferta(psm5, psm6, c2, c1);
        Intercambio intercambioFallo = new Intercambio(new Date(), ofertaFallo);
        sistema.asignarIntercambio(intercambioFallo, emp);
        emp.reportarFallo(intercambioFallo);
        System.out.println("Productos desbloqueados tras fallo:");
        System.out.println("  psm5 disponible: " + psm5.getDisponibilidad());
        System.out.println("  psm6 disponible: " + psm6.getDisponibilidad());
    }
 
    // =========================================================================
    // 5. PRUEBAS DEL CLIENTE REGISTRADO
    // =========================================================================
 
    private static void probarClienteRegistrado() {
        System.out.println("\n\n=== PRUEBAS DEL CLIENTE REGISTRADO ===");
 
        ClienteRegistrado cliente = new ClienteRegistrado("María", "maria123", "44444444M");
        sistema.addUsuario(cliente);
 
        System.out.println("\n--- 1. Datos básicos ---");
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("DNI: " + cliente.getDNI());
 
        System.out.println("\n--- 2. Editar perfil ---");
        cliente.editarPerfil("María López", "nuevaPass456");
        System.out.println("Nombre actualizado: " + cliente.getNombre());
        cliente.editarPerfil("María", "maria123"); // restaurar
 
        System.out.println("\n--- 3. Añadir al carrito y comprar ---");
        // Asegurar stock
        ProductoTienda prodCompra = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prodCompra, 5);
 
        boolean add1 = (stock.getNumProductos(prodCompra) > 0);
        cliente.añadirALaCesta(prodCompra, stock);
        System.out.println("Producto añadido a cesta: " + add1);
        System.out.println("Cesta vacía: " + cliente.getCesta().estaVacia());
        System.out.println("Precio producto: " + prodCompra.getPrecio());
 
        Status compra = cliente.comprar();
        System.out.println("Comprar (cesta → pedido): " + compra);
        System.out.println("Cesta vacía tras comprar: " + cliente.getCesta().estaVacia());
        System.out.println("Pedidos en historial: " + cliente.getPedidos().size());
 
        System.out.println("\n--- 4. Pagar pedido ---");
        Pedido pedido = cliente.getPedidos().get(0);
        System.out.println("Estado antes del pago: " + pedido.getEstadoPedido());
        Status pago = cliente.pagarPedido(pedido);
        System.out.println("Pago: " + pago + " | Estado: " + pedido.getEstadoPedido());
        System.out.println("Total pedido: " + pedido.calcularPrecioTotal());
 
        System.out.println("\n--- 5. Comprar con cesta vacía (debe fallar) ---");
        Status cestaVacia = cliente.comprar();
        System.out.println("Comprar con cesta vacía (ERROR esperado): " + cestaVacia);
 
        System.out.println("\n--- 6. Pagar pedido ajeno (debe fallar) ---");
        ClienteRegistrado otroCliente = new ClienteRegistrado("Otro", "pwd", "55555555O");
        ProductoTienda prodAjeno = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prodAjeno, 2);
        otroCliente.añadirALaCesta(prodAjeno, stock);
        otroCliente.comprar();
        Pedido pedidoAjeno = otroCliente.getPedidos().get(0);
        Status pagoAjeno = cliente.pagarPedido(pedidoAjeno);
        System.out.println("Pagar pedido ajeno (ERROR esperado): " + pagoAjeno);
 
        System.out.println("\n--- 7. Subir producto de segunda mano ---");
        ProductoSegundaMano psm = new ProductoSegundaMano("Cómic repetido", "Buen estado", null, cliente);
        Status subida = cliente.subirProducto(psm);
        System.out.println("Subir producto segunda mano: " + subida);
        System.out.println("Productos en cartera: " + cliente.getCartera().getProductos().size());
 
        System.out.println("\n--- 8. Pedir valoración ---");
        Status pedirVal = cliente.pagarValoracion(psm);
        System.out.println("Pedir valoración: " + pedirVal
                + " | Estado: " + psm.getEstadoProducto());
 
        System.out.println("\n--- 9. Notificaciones ---");
        Notificacion n1 = new Notificacion(TipoNotificacion.PAGO_REALIZADO, "Tu pedido fue pagado con éxito");
        Notificacion n2 = new Notificacion(TipoNotificacion.PEDIDO_LISTO, "Tu pedido está listo para recoger");
 
        cliente.addNotificacion(n1);
        cliente.addNotificacion(n2);
        System.out.println("Notificaciones recibidas: " + cliente.getNotificaciones().size());
        System.out.println("n1 leída inicialmente: " + n1.getLeida());
 
        cliente.leerNotificacion(n1);
        System.out.println("n1 leída tras leerNotificacion: " + n1.getLeida());
 
        cliente.borrarNotificacion(n2);
        System.out.println("Notificaciones tras borrar n2: " + cliente.getNotificaciones().size());
 
        System.out.println("\n--- 10. Código de recogida ---");
        Codigo codigo = sistema.generarCodigo();
        sistema.enviarCodigo(cliente, codigo);
        System.out.println("Códigos del cliente: " + cliente.getCodigos().size());
        System.out.println("Código no nulo: " + (cliente.getCodigos().get(0) != null));
    }
 
    // =========================================================================
    // UTILIDADES
    // =========================================================================
 
    /**
     * Devuelve el empleado predefinido cuyo nombre coincida.
     * Funciona con las referencias locales porque Sistema no expone getUsuarios().
     */
    private static Empleado encontrarEmpleadoPorNombre(String nombre) {
        if (nombre.equals(empProducto.getNombre()))    return empProducto;
        if (nombre.equals(empPedido.getNombre()))      return empPedido;
        if (nombre.equals(empIntercambio.getNombre())) return empIntercambio;
        return null;
    }
}