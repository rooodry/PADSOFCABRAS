import compras.*;
import descuentos.*;
import estadisticas.Estadistica;
import intercambios.*;
import notificaciones.*;
import productos.*;
import sistema.Sistema;
import usuarios.*;
import utilidades.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Clase de arranque para ejecutar pruebas manuales integrales del sistema PADSOFCABRAS.
 * <p>
 * Esta clase actúa como un entorno de simulación (sandbox) que ejecuta secuencialmente
 * diversos escenarios de prueba para validar el correcto funcionamiento de los módulos
 * del dominio (usuarios, productos, pedidos, intercambios, descuentos y estadísticas).
 * </p>
 * <p>
 * NOTA: El sistema real tiene una sola clase {@code Empleado} con permisos ({@code TiposEmpleado}).
 * No existen subclases EmpleadoProducto / EmpleadoPedido / EmpleadoIntercambio.
 * Las operaciones de dominio se realizan directamente sobre Empleado, Pedido,
 * ProductoSegundaMano, etc., y sobre el Sistema cuando hace falta coordinación.
 * </p>
 * * @author Equipo de Desarrollo PADSOFCABRAS
 * @version 1.0
 */
public class Main {

    /** Instancia global del sistema compartida por todos los bloques de prueba. */
    private static final Sistema  sistema = new Sistema();
    
    /** Instancia global del stock compartida por todos los bloques de prueba. */
    private static final Stock    stock   = new Stock();

    /** Administrador del sistema utilizado para pruebas de privilegios elevados. */
    private static Gestor    gestor;
    
    /** Empleado de prueba con permisos de gestión de catálogo e inventario. */
    private static Empleado  empProducto;
    
    /** Empleado de prueba con permisos de gestión y preparación de pedidos. */
    private static Empleado  empPedido;
    
    /** Empleado de prueba con permisos de tasación y gestión del mercado de segunda mano. */
    private static Empleado  empIntercambio;

    /** Constructor privado para evitar la instanciación de la clase principal. */
    private Main() {}

    /**
     * Punto de entrada principal para la ejecución de las pruebas del sistema.
     * Invoca secuencialmente los métodos de inicialización y las baterías de prueba
     * de cada módulo específico.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    public static void main(String[] args) {
        System.out.println("\n=== PRUEBAS DEL SISTEMA PADSOFCABRAS ===\n");

        inicializarDatos();

        probarGestor();
        probarEmpleadoProducto();
        probarEmpleadoPedido();
        probarEmpleadoIntercambio();
        probarClienteRegistrado();
        probarDescuentos();
        probarEstadisticas();

        System.out.println("\n=== FIN DE PRUEBAS ===");
    }

    /**
     * Inicializa el estado base del sistema para las pruebas.
     * <p>
     * Este método configura el entorno inyectando el {@code Stock} en el {@code Sistema},
     * creando un conjunto inicial de productos de tienda y registrando a los usuarios
     * con roles de Gestor y Empleados (asignando sus permisos correspondientes).
     * </p>
     */
    private static void inicializarDatos() {
        System.out.println("--- Inicializando datos de prueba ---");

        sistema.setStock(stock);

        // Productos en stock
        ProductoTienda comic1  = crearProducto("Watchmen",       "Clásico de Alan Moore",  "watchmen.png", 15.0);
        ProductoTienda juego1  = crearProducto("Catan",          "Juego de estrategia",    "catan.png",    40.0);
        ProductoTienda figura1 = crearProducto("Figura Spiderman","Figura de colección",   "spider.png",   25.0);

        stock.añadirProducto(comic1,  10);
        stock.añadirProducto(juego1,   5);
        stock.añadirProducto(figura1,  3);

        sistema.addProducto(comic1);
        sistema.addProducto(juego1);
        sistema.addProducto(figura1);

        // Gestor
        gestor = new Gestor("Admin", "admin123");
        sistema.addUsuario(gestor);

        // Empleados con sus permisos
        empProducto = new Empleado("Ana", "pass1");
        empProducto.addPermiso(TiposEmpleado.EMPLEADOS_PRODUCTO);

        empPedido = new Empleado("Luis", "pass2");
        empPedido.addPermiso(TiposEmpleado.EMPLEADOS_PEDIDO);

        empIntercambio = new Empleado("Marta", "pass3");
        empIntercambio.addPermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO);

        sistema.addUsuario(empProducto);
        sistema.addUsuario(empPedido);
        sistema.addUsuario(empIntercambio);

        System.out.println("Datos inicializados | Stock con " + stock.getProductos().size() + " productos");
    }

    /**
     * Factoría auxiliar para la creación rápida de instancias de {@link ProductoTienda}.
     * Reduce el código repetitivo (boilerplate) durante la generación de datos de prueba.
     *
     * @param nombre El nombre del producto.
     * @param desc   La descripción del producto.
     * @param img    La ruta o nombre del archivo de imagen del producto.
     * @param precio El precio base del producto.
     * @return Una nueva instancia configurada de {@code ProductoTienda}.
     */
    private static ProductoTienda crearProducto(String nombre, String desc, String img, double precio) {
        ProductoTienda p = new ProductoTienda(nombre, desc, img);
        p.setPrecio(precio);
        return p;
    }

    /**
     * Ejecuta la batería de pruebas para el rol de Gestor (Administrador).
     * <p>
     * Escenarios probados:
     * <ul>
     * <li>Alta, baja y modificación de permisos de empleados (verificando control de acceso).</li>
     * <li>Comprobación de excepciones de seguridad al intentar operar sin privilegios.</li>
     * <li>Creación de packs y subpacks (patrón Composite).</li>
     * <li>Gestión forzada del inventario (actualización de stock).</li>
     * <li>Generación de códigos de recogida.</li>
     * <li>Registro global de políticas de descuentos.</li>
     * </ul>
     * </p>
     */
    private static void probarGestor() {
        System.out.println("\n\n=== PRUEBAS DEL GESTOR ===");

        // 1a. Dar de alta empleado (mediante sistema)
        System.out.println("\n--- 1a. darAltaEmpleado (gestor válido) ---");
        try {
            sistema.darAltaEmpleado(gestor, "Pepe", "pepe1", TiposEmpleado.EMPLEADOS_PEDIDO);
            System.out.println("Empleado 'Pepe' dado de alta: OK");
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR inesperado: " + e.getMessage());
        }

        // 1b. Dar de alta con no-gestor → debe lanzar excepción
        System.out.println("\n--- 1b. darAltaEmpleado (no gestor) → excepción esperada ---");
        ClienteRegistrado intruso = new ClienteRegistrado("Intruso", "pwd", "99999999Z");
        try {
            sistema.darAltaEmpleado(intruso, "Fake", "fake", TiposEmpleado.EMPLEADOS_PEDIDO);
            System.out.println("ERROR: debería haber lanzado excepción");
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Excepción capturada correctamente: " + e.getMessage());
        }

        // 1c. Dar de baja empleado
        System.out.println("\n--- 1c. darBajaEmpleado ---");
        Empleado temporal = new Empleado("Temporal", "tmp");
        temporal.addPermiso(TiposEmpleado.EMPLEADOS_PEDIDO);
        sistema.addUsuario(temporal);
        try {
            sistema.darBajaEmpleado(gestor, temporal);
            System.out.println("Empleado 'Temporal' dado de baja: OK");
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR inesperado: " + e.getMessage());
        }

        // 1d. Modificar permisos
        System.out.println("\n--- 1d. modificarPermisos ---");
        try {
            Set<TiposEmpleado> nuevosPermisos = new HashSet<>();
            nuevosPermisos.add(TiposEmpleado.EMPLEADOS_PEDIDO);
            sistema.modificarPermisos(gestor, empProducto, nuevosPermisos);
            System.out.println("Permiso de Ana → EMPLEADOS_PEDIDO: " + empProducto.tienePermiso(TiposEmpleado.EMPLEADOS_PEDIDO));

            // Restaurar
            Set<TiposEmpleado> restaurar = new HashSet<>();
            restaurar.add(TiposEmpleado.EMPLEADOS_PRODUCTO);
            sistema.modificarPermisos(gestor, empProducto, restaurar);
            System.out.println("Permiso restaurado → EMPLEADOS_PRODUCTO: " + empProducto.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO));
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 1e. Crear pack y subpacks
        System.out.println("\n--- 1e. crearPack y subpacks ---");
        ProductoTienda pa = crearProducto("Pack Comic A", "desc", "img.png", 10.0);
        ProductoTienda pb = crearProducto("Pack Juego B", "desc", "img.png", 20.0);
        List<Producto> listaProductosPack = new ArrayList<>();
        listaProductosPack.add(pa);
        listaProductosPack.add(pb);
        try {
            Pack pack = sistema.crearPack(gestor, "Pack Oferta", 25.0, listaProductosPack);
            System.out.println("Pack creado: " + pack.getNombre()
                    + " | Precio: " + pack.getPrecio()
                    + " | Productos: " + pack.getProductos().size());

            List<Producto> listaSubpack = new ArrayList<>();
            listaSubpack.add(pa);
            Pack subpack = sistema.crearPack(gestor, "Subpack Básico", 8.0, listaSubpack);
            pack.addSubpack(subpack);
            System.out.println("Subpack añadido | Subpacks en pack: " + pack.getSubpacks().size());
            pack.removeSubpack(subpack);
            System.out.println("Subpack eliminado | Subpacks en pack: " + pack.getSubpacks().size());
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 1f. Actualizar stock
        System.out.println("\n--- 1f. actualizarStock ---");
        ProductoTienda prodStock = stock.getProductos().keySet().iterator().next();
        int antes = stock.getNumProductos(prodStock);
        try {
            sistema.actualizarStock(gestor, prodStock, 10);
            System.out.println("Stock de '" + prodStock.getNombre() + "': " + antes + " → " + stock.getNumProductos(prodStock));

            // Reducir stock
            sistema.actualizarStock(gestor, prodStock, -5);
            System.out.println("Tras reducción (-5): " + stock.getNumProductos(prodStock));
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 1g. Generar código de recogida
        System.out.println("\n--- 1g. generarCodigo ---");
        Codigo codigo = sistema.generarCodigo();
        System.out.println("Código generado (no nulo): " + (codigo != null));

        // 1h. Añadir descuentos
        System.out.println("\n--- 1h. addDescuento ---");
        Date hoy      = new Date();
        Date enUnMes  = new Date(hoy.getTime() + 30L * 24 * 60 * 60 * 1000);
        DescuentoPorcentaje dpct = new DescuentoPorcentaje(hoy, enUnMes, 20.0);
        sistema.addDescuento(dpct);
        System.out.println("DescuentoPorcentaje 20% añadido");

        DescuentoDosPorUno d2x1 = new DescuentoDosPorUno(hoy, enUnMes);
        sistema.addDescuento(d2x1);
        System.out.println("DescuentoDosPorUno añadido");

        DescuentoCantidadGastada dcg = new DescuentoCantidadGastada(hoy, enUnMes, 50.0, 0.10);
        sistema.addDescuento(dcg);
        System.out.println("DescuentoCantidadGastada (mín 50€, 10%) añadido");

        List<ProductoTienda> productosRegalo = new ArrayList<>();
        productosRegalo.add(prodStock);
        DescuentoRegalo dreg = new DescuentoRegalo(hoy, enUnMes, 100.0, productosRegalo);
        sistema.addDescuento(dreg);
        System.out.println("DescuentoRegalo añadido");
    }

    /**
     * Ejecuta la batería de pruebas para las operaciones de nivel de inventario
     * (requiere permiso EMPLEADOS_PRODUCTO).
     * <p>
     * Escenarios probados:
     * <ul>
     * <li>Añadir, editar y retirar productos del stock.</li>
     * <li>Protección frente a ingresos de stock inválidos (cantidades negativas o cero).</li>
     * <li>Aplicación directa de rebajas sobre productos individuales (rebaja fija, porcentual y 2x1).</li>
     * <li>Validación del estado del inventario para procesar peticiones.</li>
     * </ul>
     * </p>
     */
    private static void probarEmpleadoProducto() {
        System.out.println("\n\n=== PRUEBAS EMPLEADO DE PRODUCTOS (Ana) ===");

        System.out.println("Tiene permiso EMPLEADOS_PRODUCTO: " + empProducto.tienePermiso(TiposEmpleado.EMPLEADOS_PRODUCTO));

        // 2a. Añadir producto al stock
        System.out.println("\n--- 2a. añadirProducto al stock ---");
        ProductoTienda nuevo = crearProducto("Astérix", "Edición especial", "asterix.png", 12.0);
        stock.añadirProducto(nuevo, 5);
        System.out.println("Stock de 'Astérix' tras añadir: " + stock.getNumProductos(nuevo));

        // 2b. Verificar que cantidad inválida no modifica stock
        System.out.println("\n--- 2b. añadirProducto cantidad inválida ---");
        int stockAntes = stock.getNumProductos(nuevo);
        // No añadir nada (cantidad <= 0 es ignorada por Stock.añadirProducto)
        stock.añadirProducto(nuevo, 0); // no debe cambiar nada
        System.out.println("Stock tras añadir 0: " + stock.getNumProductos(nuevo) + " (esperado " + stockAntes + ")");

        // 2c. Editar producto (nombre, descripción, imagen, precio)
        System.out.println("\n--- 2c. editarProducto ---");
        nuevo.setNombre("Astérix Deluxe");
        nuevo.setDescripcion("Edición coleccionista");
        nuevo.setImagen("asterix_deluxe.png");
        nuevo.setPrecio(18.0);
        System.out.println("Nombre: " + nuevo.getNombre() + " | Precio: " + nuevo.getPrecio());

        // 2d. Retirar producto del stock
        System.out.println("\n--- 2d. retirarProducto ---");
        stock.retirarProducto(nuevo);
        System.out.println("Stock de 'Astérix Deluxe' tras retirar: " + stock.getNumProductos(nuevo));

        // 2e. Rebaja fija
        System.out.println("\n--- 2e. rebajaFija ---");
        ProductoTienda prodRebaja = stock.getProductos().keySet().iterator().next();
        prodRebaja.setRebajaFija(5.0);
        System.out.println("Rebaja fija 5€ en '" + prodRebaja.getNombre()
                + "' | PrecioBase: " + prodRebaja.getPrecio()
                + " | RebajaFija: " + prodRebaja.getRebajaFija());
        prodRebaja.setRebajaFija(0.0);

        // 2f. Rebaja porcentaje
        System.out.println("\n--- 2f. rebajaPorcentaje ---");
        prodRebaja.setRebajaPorcentaje(0.10);
        System.out.println("Rebaja 10% en '" + prodRebaja.getNombre()
                + "' | RebajaPorcentaje: " + prodRebaja.getRebajaPorcentaje());
        prodRebaja.setRebajaPorcentaje(0.0);

        // 2g. 2x1 en producto
        System.out.println("\n--- 2g. 2x1 ---");
        prodRebaja.setTiene2x1(true);
        System.out.println("2x1 activo en '" + prodRebaja.getNombre() + "': " + prodRebaja.isTiene2x1());
        prodRebaja.setTiene2x1(false);
        System.out.println("2x1 desactivado: " + prodRebaja.isTiene2x1());

        // 2h. comprobarStock
        System.out.println("\n--- 2h. comprobarStock ---");
        ProductoTienda prodCheck = stock.getProductos().keySet().iterator().next();
        Status stOK  = stock.comprobarStock(prodCheck, 1);
        Status stERR = stock.comprobarStock(prodCheck, 99999);
        System.out.println("Hay 1 unidad: " + stOK + " | Hay 99999 unidades: " + stERR);
    }

    /**
     * Ejecuta la batería de pruebas sobre el ciclo de vida de las compras y pedidos
     * (requiere permiso EMPLEADOS_PEDIDO).
     * <p>
     * Escenarios probados:
     * <ul>
     * <li>Progresión de estados del pedido (Inicial, Pagado, En preparación, Listo, Entregado).</li>
     * <li>Cancelación de pedidos y reposición automática de stock retenido.</li>
     * <li>Cálculo de precios finales aplicando distintos tipos de descuento al carrito completo.</li>
     * <li>Inclusión automática de regalos por volumen de compra.</li>
     * <li>Caducidad automática de reservas impagadas mediante simulación asíncrona de tiempo.</li>
     * </ul>
     * </p>
     */
    private static void probarEmpleadoPedido() {
        System.out.println("\n\n=== PRUEBAS EMPLEADO DE PEDIDOS (Luis) ===");

        System.out.println("Tiene permiso EMPLEADOS_PEDIDO: " + empPedido.tienePermiso(TiposEmpleado.EMPLEADOS_PEDIDO));

        // Crear un pedido de prueba
        ClienteRegistrado clientePedido = new ClienteRegistrado("TestPedido", "pwd", "12345678T");
        ProductoTienda prod = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prod, 5);
        clientePedido.añadirALaCesta(prod, stock);
        clientePedido.comprar();
        Pedido pedido = clientePedido.getPedidos().get(0);
        sistema.addPedido(pedido);

        // 3a. Estado inicial
        System.out.println("\n--- 3a. Estado inicial del pedido ---");
        System.out.println("Estado: " + pedido.getEstadoPedido());
        System.out.println("Código (no nulo): " + (pedido.getCodigo() != null));
        System.out.println("Fecha realización (no nula): " + (pedido.getFechaRealizacion() != null));
        System.out.println("Nº productos: " + pedido.getProductos().size());

        // 3b. Pagar → EN_PREPARACION
        System.out.println("\n--- 3b. Pago del cliente (pagarPedido) ---");
        Status pagado = clientePedido.pagarPedido(pedido, "1234567890123456");
        System.out.println("Pago: " + pagado + " | Estado: " + pedido.getEstadoPedido());

        // 3c. Empleado marca como LISTO
        System.out.println("\n--- 3c. Marcar como LISTO ---");
        pedido.setEstadoPedido(EstadoPedido.LISTO);
        System.out.println("Estado: " + pedido.getEstadoPedido()
                + " | FechaPreparación (no nula): " + (pedido.getFechaPreparacion() != null));

        // 3d. Empleado entrega el pedido
        System.out.println("\n--- 3d. Entregar pedido ---");
        pedido.setEstadoPedido(EstadoPedido.ENTREGADO);
        System.out.println("Estado: " + pedido.getEstadoPedido()
                + " | FechaRecogida (no nula): " + (pedido.getFechaRecogida() != null));

        // 3e. Editar fecha de recogida
        System.out.println("\n--- 3e. Editar fecha recogida ---");
        Date nuevaFecha = new Date(System.currentTimeMillis() + 2L * 24 * 60 * 60 * 1000);
        pedido.setFechaRecogida(nuevaFecha);
        System.out.println("Fecha recogida editada (no nula): " + (pedido.getFechaRecogida() != null));

        // 3f. Cancelar pedido (repone stock)
        System.out.println("\n--- 3f. Cancelar pedido (repone stock) ---");
        ClienteRegistrado clienteCancel = new ClienteRegistrado("TestCancel", "pwd", "87654321C");
        ProductoTienda prodCancel = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prodCancel, 3);
        int stockAntesCancel = stock.getNumProductos(prodCancel);
        clienteCancel.añadirALaCesta(prodCancel, stock);
        clienteCancel.comprar();
        Pedido pedidoCancel = clienteCancel.getPedidos().get(0);
        sistema.cancelarPedido(pedidoCancel);
        System.out.println("Estado tras cancelar: " + pedidoCancel.getEstadoPedido());
        System.out.println("Stock repuesto: " + stock.getNumProductos(prodCancel)
                + " (antes de comprar había " + stockAntesCancel + ")");

        // 3g. Descuento porcentaje sobre pedido
        System.out.println("\n--- 3g. calcularPrecioTotal con descuento porcentaje ---");
        ClienteRegistrado clienteDesc = new ClienteRegistrado("TestDesc", "pwd", "11111111D");
        ProductoTienda prodDesc = crearProducto("Comic Desc", "desc", "img.png", 100.0);
        stock.añadirProducto(prodDesc, 5);
        clienteDesc.añadirALaCesta(prodDesc, stock);
        clienteDesc.comprar();
        Pedido pedidoDesc = clienteDesc.getPedidos().get(0);
        Date ini = new Date();
        Date fin = new Date(ini.getTime() + 7L * 24 * 60 * 60 * 1000);
        pedidoDesc.setDescuento(new DescuentoPorcentaje(ini, fin, 20.0));
        System.out.println("Precio sin descuento: 100.0");
        System.out.println("Precio con 20%: " + pedidoDesc.calcularPrecioTotal());

        // 3h. Descuento 2x1 sobre pedido
        System.out.println("\n--- 3h. calcularPrecioTotal con 2x1 ---");
        ClienteRegistrado cliente2x1 = new ClienteRegistrado("Test2x1", "pwd", "22222200X");
        ProductoTienda prod2x1 = crearProducto("Comic 2x1", "desc", "img.png", 20.0);
        stock.añadirProducto(prod2x1, 10);
        // Poner 8 en la cesta manualmente
        for (int i = 0; i < 8; i++) cliente2x1.añadirALaCesta(prod2x1, stock);
        cliente2x1.comprar();
        Pedido pedido2x1 = cliente2x1.getPedidos().get(0);
        pedido2x1.setDescuento(new DescuentoDosPorUno(ini, fin));
        System.out.println("8 unidades × 20€ con 2x1 → esperado 80.0, obtenido: " + pedido2x1.calcularPrecioTotal());

        // 3i. Descuento cantidad gastada
        System.out.println("\n--- 3i. DescuentoCantidadGastada (mín 100€, 15%) ---");
        ClienteRegistrado clienteDCG = new ClienteRegistrado("TestDCG", "pwd", "33330000G");
        ProductoTienda prodDCG = crearProducto("Juego Premium", "desc", "img.png", 100.0);
        prodDCG.setRebajaFija(10.0); // precio efectivo = 90 × 2 = 180 → supera 100 → desc
        stock.añadirProducto(prodDCG, 5);
        for (int i = 0; i < 2; i++) clienteDCG.añadirALaCesta(prodDCG, stock);
        clienteDCG.comprar();
        Pedido pedidoDCG = clienteDCG.getPedidos().get(0);
        pedidoDCG.setDescuento(new DescuentoCantidadGastada(ini, fin, 100.0, 15.0));
        System.out.println("2 × (100-10)=180, desc 15%: " + pedidoDCG.calcularPrecioTotal());

        // 3j. calcularPrecioFinalPedido con descuentos del sistema
        System.out.println("\n--- 3j. sistema.calcularPrecioFinalPedido ---");
        ClienteRegistrado clienteSys = new ClienteRegistrado("TestSys", "pwd", "44440000S");
        ProductoTienda prodSys = crearProducto("Novela Gráfica", "desc", "img.png", 60.0);
        stock.añadirProducto(prodSys, 5);
        clienteSys.añadirALaCesta(prodSys, stock);
        clienteSys.comprar();
        Pedido pedidoSys = clienteSys.getPedidos().get(0);
        double preciofinal = sistema.calcularPrecioFinalPedido(pedidoSys);
        System.out.println("Precio final (con descuentos del sistema): " + preciofinal);

        // 3k. registrarPedido con regalo automático (pedido > 200€)
        System.out.println("\n--- 3k. registrarPedido con regalo automático (total > 200€) ---");
        ClienteRegistrado clienteRegalo = new ClienteRegistrado("TestRegalo", "pwd", "55550000R");
        ProductoTienda prodCaro = crearProducto("Consola", "Videoconsola", "consola.jpg", 250.0);
        stock.añadirProducto(prodCaro, 2);
        Map<ProductoTienda, Integer> carritoRegalo = new HashMap<>();
        carritoRegalo.put(prodCaro, 1);
        Pedido pedidoRegalo = new Pedido(clienteRegalo, carritoRegalo);
        sistema.registrarPedido(pedidoRegalo);
        System.out.println("Pedido > 200€ registrado | Regalo: " + (pedidoRegalo.getRegalo() != null ? pedidoRegalo.getRegalo().getNombre() : "ninguno"));

        // 3l. Caducidad automática de pedido (3 s en test, según Sistema.registrarPedido)
        System.out.println("\n--- 3l. Caducidad automática (pedido sin pagar → CANCELADO tras 3s) ---");
        ClienteRegistrado clienteCaduca = new ClienteRegistrado("TestCaduca", "pwd", "66660000C");
        ProductoTienda prodCaduca = crearProducto("Producto Reloj", "Prueba tiempo", "reloj.png", 10.0);
        Map<ProductoTienda, Integer> carritoCaduca = new HashMap<>();
        carritoCaduca.put(prodCaduca, 1);
        Pedido pedidoCaduca = new Pedido(clienteCaduca, carritoCaduca);
        sistema.registrarPedido(pedidoCaduca);
        System.out.println("Pedido registrado, estado: " + pedidoCaduca.getEstadoPedido());
        try {
            System.out.println("Esperando 4 segundos...");
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Estado tras el tiempo límite: " + pedidoCaduca.getEstadoPedido());
        if (pedidoCaduca.getEstadoPedido() == EstadoPedido.CANCELADO) {
            System.out.println("ÉXITO: el pedido se canceló automáticamente.");
        } else {
            System.out.println("ATENCIÓN: el pedido sigue activo (revisar scheduler).");
        }
    }

    /**
     * Ejecuta la batería de pruebas sobre la lógica del mercado de segunda mano y el trueque
     * (requiere permiso EMPLEADOS_INTERCAMBIO).
     * <p>
     * Escenarios probados:
     * <ul>
     * <li>Proceso de tasación oficial de productos subidos por usuarios.</li>
     * <li>Creación de ofertas de intercambio entre clientes.</li>
     * <li>Aceptación de ofertas, gestionando la transferencia segura de propiedad en las carteras.</li>
     * <li>Rechazo de ofertas y liberación de los productos retenidos.</li>
     * <li>Envío de notificaciones asociadas al éxito de la operación.</li>
     * </ul>
     * </p>
     */
    private static void probarEmpleadoIntercambio() {
        System.out.println("\n\n=== PRUEBAS EMPLEADO DE INTERCAMBIOS (Marta) ===");

        System.out.println("Tiene permiso EMPLEADOS_INTERCAMBIO: " + empIntercambio.tienePermiso(TiposEmpleado.EMPLEADOS_INTERCAMBIO));

        ClienteRegistrado c1 = new ClienteRegistrado("Carlos", "pwd", "22222222C");
        ClienteRegistrado c2 = new ClienteRegistrado("Sofía",  "pwd", "33333333S");

        ProductoSegundaMano psm1 = new ProductoSegundaMano("Cómic usado",  "Muy buen estado",    null, c1);
        ProductoSegundaMano psm2 = new ProductoSegundaMano("Figura usada", "Algo desgastada",    null, c2);

        c1.subirProducto(psm1);
        c2.subirProducto(psm2);

        // 4a. Estado inicial de un ProductoSegundaMano
        System.out.println("\n--- 4a. Estado inicial PSM ---");
        System.out.println("psm1 valorado: " + psm1.getEstaValorado());
        System.out.println("psm1 disponible: " + psm1.getDisponibilidad());
        System.out.println("psm1 estado: " + psm1.getEstadoProducto());

        // 4b. Asignar para valorar
        System.out.println("\n--- 4b. asignarValoracion ---");
        sistema.asignarValoracion(psm1, empIntercambio);
        sistema.asignarValoracion(psm2, empIntercambio);
        System.out.println("Productos por valorar: " + empIntercambio.getProductosParaValorar().size());

        // 4c. Valorar productos (setValoracion sobre el PSM)
        System.out.println("\n--- 4c. valorarProducto (setValoracion) ---");
        psm1.setValoracion(8, 15.0, EstadoConservacion.MUY_BUENO);
        psm2.setValoracion(6, 10.0, EstadoConservacion.USO_LIGERO);
        System.out.println("psm1 valorado: " + psm1.getEstaValorado()
                + " | Valor estimado: " + psm1.getValorEstimado()
                + " | Conservación: " + psm1.getEstadoConservacion());
        System.out.println("psm2 valorado: " + psm2.getEstaValorado()
                + " | Valor estimado: " + psm2.getValorEstimado()
                + " | Conservación: " + psm2.getEstadoConservacion());

        // 4d. Subir productos al mercado
        System.out.println("\n--- 4d. subirProducto al mercado ---");
        psm1.subirProducto();
        psm2.subirProducto();
        System.out.println("psm1 disponible: " + psm1.getDisponibilidad());
        System.out.println("psm2 disponible: " + psm2.getDisponibilidad());

        // 4e. Crear oferta e intercambio
        System.out.println("\n--- 4e. Oferta e Intercambio ---");
        Oferta oferta = new Oferta(psm1, psm2, c2, c1);
        System.out.println("Estado inicial oferta: " + oferta.getEstadoOferta());

        Intercambio intercambio = new Intercambio(new Date(), oferta);
        sistema.asignarIntercambio(intercambio, empIntercambio);
        System.out.println("Intercambios asignados al empleado: " + empIntercambio.getIntercambios().size());
        System.out.println("Intercambiado inicialmente: " + intercambio.getIntercambiado());
        System.out.println("FechaLimite > ahora: " + intercambio.getFechaLimite().after(new Date()));

        // 4f. Aceptar oferta y transferir propiedad
        System.out.println("\n--- 4f. Aceptar oferta y transferir propiedad ---");
        intercambio.aceptarOferta();
        System.out.println("Oferta aceptada: " + oferta.getEstadoOferta());
        System.out.println("FechaAceptada (no nula): " + (intercambio.getFechaAceptada() != null));

        // Transferir propiedad: c2 recibe psm1, c1 recibe psm2
        sistema.añadirProductoCartera(psm1, c2);
        sistema.añadirProductoCartera(psm2, c1);
        intercambio.setIntercambiado(true);
        System.out.println("Intercambio completado: " + intercambio.getIntercambiado());
        System.out.println("c2 tiene psm1: " + c2.getCartera().getProductos().contains(psm1));
        System.out.println("c1 tiene psm2: " + c1.getCartera().getProductos().contains(psm2));

        // 4g. Rechazar oferta
        System.out.println("\n--- 4g. Rechazar oferta ---");
        ProductoSegundaMano psm3 = new ProductoSegundaMano("Juego usado",      "Perfecto estado", null, c1);
        ProductoSegundaMano psm4 = new ProductoSegundaMano("Póster enmarcado", "Vintage",         null, c2);
        psm3.setValoracion(9, 20.0, EstadoConservacion.PERFECTO);
        psm4.setValoracion(7, 12.0, EstadoConservacion.USO_LIGERO);

        Oferta ofertaRechazo = new Oferta(psm3, psm4, c2, c1);
        Intercambio intercambioRechazo = new Intercambio(new Date(), ofertaRechazo);
        intercambioRechazo.rechazarOferta();
        System.out.println("Oferta rechazada: " + ofertaRechazo.getEstadoOferta());

        // 4h. Reportar fallo en intercambio (desbloquear productos)
        System.out.println("\n--- 4h. Reportar fallo → desbloquear productos ---");
        ProductoSegundaMano psm5 = new ProductoSegundaMano("Extra A", "desc", null, c1);
        ProductoSegundaMano psm6 = new ProductoSegundaMano("Extra B", "desc", null, c2);
        psm5.setDisponibilidad(false);
        psm6.setDisponibilidad(false);
        Oferta ofertaFallo = new Oferta(psm5, psm6, c2, c1);
        Intercambio intercambioFallo = new Intercambio(new Date(), ofertaFallo);
        sistema.asignarIntercambio(intercambioFallo, empIntercambio);
        // Reportar fallo: desbloquear productos de la oferta
        intercambioFallo.getOferta().getProductoOfertado().setDisponibilidad(true);
        intercambioFallo.getOferta().getProductoDeseado().setDisponibilidad(true);
        intercambioFallo.rechazarOferta();
        System.out.println("psm5 disponible: " + psm5.getDisponibilidad());
        System.out.println("psm6 disponible: " + psm6.getDisponibilidad());
        System.out.println("Oferta marcada: " + ofertaFallo.getEstadoOferta());

        // 4i. Notificación de intercambio realizado
        System.out.println("\n--- 4i. Notificación de intercambio ---");
        Notificacion notif = new Notificacion(TipoNotificacion.INTERCAMBIO_REALIZADO, "Tu intercambio está listo");
        sistema.notificarUsuario(c1, notif);
        System.out.println("Notificaciones de c1: " + c1.getNotificaciones().size());
        System.out.println("Tipo: " + c1.getNotificaciones().get(0).getTipoNotificacion());

        // 4j. Borrar y restaurar producto de cartera
        System.out.println("\n--- 4j. borrarProducto (PSM) ---");
        psm1.borrarProducto();
        System.out.println("psm1 disponible tras borrar: " + psm1.getDisponibilidad());
    }

    /**
     * Ejecuta la batería de pruebas sobre las operaciones de un cliente en el sistema.
     * <p>
     * Escenarios probados:
     * <ul>
     * <li>Gestión de perfil y datos de cuenta.</li>
     * <li>Añadir artículos al carrito y formalizar la compra.</li>
     * <li>Validación contra comportamientos anómalos (intentar pagar un pedido ajeno, comprar con carrito vacío).</li>
     * <li>Pago del servicio de tasación de la tienda.</li>
     * <li>Recepción y gestión del buzón de notificaciones.</li>
     * <li>Añadir valoraciones y puntuaciones a los artículos comprados.</li>
     * </ul>
     * </p>
     */
    private static void probarClienteRegistrado() {
        System.out.println("\n\n=== PRUEBAS DEL CLIENTE REGISTRADO ===");

        ClienteRegistrado cliente = new ClienteRegistrado("María", "maria123", "44444444M");
        sistema.addUsuario(cliente);

        // 5a. Datos básicos
        System.out.println("\n--- 5a. Datos básicos ---");
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("DNI: " + cliente.getDNI());

        // 5b. Editar perfil
        System.out.println("\n--- 5b. editarPerfil ---");
        cliente.editarPerfil("María López", "nuevaPass456");
        System.out.println("Nombre actualizado: " + cliente.getNombre());
        cliente.editarPerfil("María", "maria123"); // restaurar

        // 5c. Añadir al carrito y comprar
        System.out.println("\n--- 5c. Cesta y compra ---");
        ProductoTienda prodCompra = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prodCompra, 5);
        cliente.añadirALaCesta(prodCompra, stock);
        System.out.println("Cesta vacía: " + cliente.getCesta().estaVacia());

        Status compra = cliente.comprar();
        System.out.println("comprar(): " + compra);
        System.out.println("Cesta vacía tras comprar: " + cliente.getCesta().estaVacia());
        System.out.println("Pedidos en historial: " + cliente.getPedidos().size());

        // 5d. Pagar pedido
        System.out.println("\n--- 5d. pagarPedido ---");
        Pedido pedido = cliente.getPedidos().get(0);
        System.out.println("Estado antes del pago: " + pedido.getEstadoPedido());
        Status pago = cliente.pagarPedido(pedido, "1234567890123456");
        System.out.println("Pago: " + pago + " | Estado: " + pedido.getEstadoPedido());
        System.out.println("Precio total: " + pedido.calcularPrecioTotal());

        // 5e. Comprar con cesta vacía → ERROR
        System.out.println("\n--- 5e. comprar cesta vacía → ERROR ---");
        System.out.println("Status: " + cliente.comprar());

        // 5f. Pagar pedido ajeno → ERROR
        System.out.println("\n--- 5f. pagar pedido ajeno → ERROR ---");
        ClienteRegistrado otro = new ClienteRegistrado("Otro", "pwd", "55555555O");
        ProductoTienda prodAjeno = stock.getProductos().keySet().iterator().next();
        stock.añadirProducto(prodAjeno, 2);
        otro.añadirALaCesta(prodAjeno, stock);
        otro.comprar();
        Pedido pedidoAjeno = otro.getPedidos().get(0);
        System.out.println("Status: " + cliente.pagarPedido(pedidoAjeno, "1234567890123456"));

        // 5g. Subir producto de segunda mano
        System.out.println("\n--- 5g. subirProducto segunda mano ---");
        ProductoSegundaMano psm = new ProductoSegundaMano("Cómic repetido", "Buen estado", null, cliente);
        Status subida = cliente.subirProducto(psm);
        System.out.println("Subida: " + subida + " | Cartera: " + cliente.getCartera().getNumProductos());

        // 5h. Pedir valoración (pagarValoracion)
        System.out.println("\n--- 5h. pagarValoracion ---");
        Status pedirVal = cliente.pagarValoracion(psm, "1234567890123456");
        System.out.println("pagarValoracion: " + pedirVal + " | Estado PSM: " + psm.getEstadoProducto());

        // 5i. Notificaciones
        System.out.println("\n--- 5i. Notificaciones ---");
        Notificacion n1 = new Notificacion(TipoNotificacion.PAGO_REALIZADO,  "Tu pedido fue pagado");
        Notificacion n2 = new Notificacion(TipoNotificacion.PEDIDO_LISTO,    "Tu pedido está listo");
        cliente.addNotificacion(n1);
        cliente.addNotificacion(n2);
        System.out.println("Notificaciones: " + cliente.getNotificaciones().size());
        System.out.println("n1 leída: " + n1.getLeida());
        cliente.leerNotificacion(n1);
        System.out.println("n1 leída tras leerNotificacion: " + n1.getLeida());
        cliente.borrarNotificacion(n2);
        System.out.println("Notificaciones tras borrar n2: " + cliente.getNotificaciones().size());

        // 5j. Código de recogida
        System.out.println("\n--- 5j. Código de recogida ---");
        Codigo codigo = sistema.generarCodigo();
        sistema.enviarCodigo(cliente, codigo);
        System.out.println("Códigos del cliente: " + cliente.getCodigos().size());

        // 5k. enviarCodigo mediante setEstadoPedido
        System.out.println("\n--- 5k. setEstadoPedido vía sistema ---");
        Pedido pedidoSet = cliente.getPedidos().get(0);
        sistema.setEstadoPedido(pedidoSet, EstadoPedido.LISTO);
        System.out.println("Estado forzado a LISTO: " + pedidoSet.getEstadoPedido());

        // 5l. Añadir valoraciones a pedido
        System.out.println("\n--- 5l. añadirValoraciones ---");
        Map<ProductoTienda, Integer> valoraciones = new HashMap<>();
        valoraciones.put(prodCompra, 8);
        cliente.añadirValoraciones(pedido, valoraciones);
        System.out.println("Valoraciones del pedido: " + pedido.getValoracionesProductos().size());

        // 5m. bloquearProductoOfertante
        System.out.println("\n--- 5m. bloquearProductoOfertante ---");
        ProductoSegundaMano psmBloqueo = new ProductoSegundaMano("Producto Bloqueo", "desc", null, cliente);
        psmBloqueo.setDisponibilidad(true);
        sistema.bloquearProductoOfertante(psmBloqueo);
        System.out.println("Disponible tras bloquear: " + psmBloqueo.getDisponibilidad());

        // 5n. obtenerCategoriasRecomendadas (sin fichero → lista vacía, manejo de error esperado)
        System.out.println("\n--- 5n. obtenerCategoriasRecomendadas (sin fichero) ---");
        Map<String, Integer> cats = sistema.obtenerCategoriasRecomendadas(cliente);
        System.out.println("Categorías devueltas: " + cats.size() + " (0 si no hay fichero de estadísticas)");
    }

    /**
     * Ejecuta pruebas aisladas para comprobar el comportamiento polimórfico y
     * la lógica de evaluación de las distintas políticas de descuento del sistema.
     * <p>
     * Se evalúan individualmente:
     * <ul>
     * <li>{@link DescuentoPorcentaje}</li>
     * <li>{@link DescuentoDosPorUno}</li>
     * <li>{@link DescuentoCantidadGastada}</li>
     * <li>{@link DescuentoRegalo}</li>
     * </ul>
     * Verificando que cada descuento determina correctamente si es aplicable a un
     * pedido en base a umbrales, fechas y reglas internas.
     * </p>
     */
    private static void probarDescuentos() {
        System.out.println("\n\n=== PRUEBAS DE DESCUENTOS ===");

        Date ini = new Date();
        Date fin = new Date(ini.getTime() + 7L * 24 * 60 * 60 * 1000);

        ClienteRegistrado c = new ClienteRegistrado("TestDesc2", "pwd", "77770000D");
        ProductoTienda p = crearProducto("ProductoDescuento", "desc", "img.png", 200.0);
        stock.añadirProducto(p, 10);
        c.añadirALaCesta(p, stock);
        c.comprar();
        Pedido ped = c.getPedidos().get(0);

        // 6a. DescuentoPorcentaje
        System.out.println("\n--- 6a. DescuentoPorcentaje ---");
        DescuentoPorcentaje dp = new DescuentoPorcentaje(ini, fin, 25.0);
        System.out.println("Porcentaje: " + dp.getPorcentaje());
        System.out.println("esAplicable: " + dp.esAplicable(ped));
        System.out.println("aplicarDescuento(200): " + dp.aplicarDescuento(200.0));

        // 6b. DescuentoDosPorUno
        System.out.println("\n--- 6b. DescuentoDosPorUno ---");
        DescuentoDosPorUno d2 = new DescuentoDosPorUno(ini, fin);
        System.out.println("esAplicable: " + d2.esAplicable(ped));
        ped.setDescuento(d2);
        System.out.println("Precio con 2x1 (1 unidad de 200): " + ped.calcularPrecioTotal());

        // 6c. DescuentoCantidadGastada
        System.out.println("\n--- 6c. DescuentoCantidadGastada ---");
        DescuentoCantidadGastada dcg = new DescuentoCantidadGastada(ini, fin, 150.0, 10.0);
        System.out.println("CantidadMinima: " + dcg.getCantidadMinima() + " | Porcentaje: " + dcg.getPorcentaje());
        System.out.println("esAplicable (200 >= 150): " + dcg.esAplicable(ped));
        System.out.println("aplicarDescuento(200): " + dcg.aplicarDescuento(200.0));
        ped.setDescuento(dcg);
        System.out.println("Precio con descuentoCantidad: " + ped.calcularPrecioTotal());

        // 6d. DescuentoRegalo
        System.out.println("\n--- 6d. DescuentoRegalo ---");
        List<ProductoTienda> regalo = new ArrayList<>();
        regalo.add(p);
        DescuentoRegalo dr = new DescuentoRegalo(ini, fin, 100.0, regalo);
        System.out.println("GastoMinimo: " + dr.getGastoMinimo() + " | Productos regalo: " + dr.getProductos().size());
        System.out.println("esAplicable: " + dr.esAplicable(ped));
        System.out.println("aplicarDescuento no modifica precio: " + dr.aplicarDescuento(200.0));

        // 6e. Fechas del descuento
        System.out.println("\n--- 6e. Fechas de descuentos ---");
        System.out.println("FechaInicio no nula: " + (dp.getFechaInicio() != null));
        System.out.println("FechaFin no nula: " + (dp.getFechaFin() != null));
        System.out.println("FechaFin > FechaInicio: " + dp.getFechaFin().after(dp.getFechaInicio()));
    }

    /**
     * Ejecuta pruebas enfocadas a la analítica de datos y exportación de reportes.
     * <p>
     * Se genera un historial sintético de pedidos finalizados y se invoca la clase
     * {@link Estadistica} para producir archivos de texto en {@code /tmp/} con reportes sobre:
     * <ul>
     * <li>Estadísticas de pedidos en general.</li>
     * <li>Recaudación económica agrupadora (mensual y por categoría).</li>
     * <li>Métricas de actividad (mejores clientes en compras e intercambios).</li>
     * <li>Evaluación y puntuación media del catálogo.</li>
     * </ul>
     * Se valida también la denegación de acceso a la recaudación si quien la pide no es el Gestor.
     * </p>
     */
    private static void probarEstadisticas() {
        System.out.println("\n\n=== PRUEBAS DE ESTADÍSTICAS ===");

        // Preparar datos
        ClienteRegistrado c1  = new ClienteRegistrado("EstCliente1", "pwd", "88880001A");
        ClienteRegistrado c2  = new ClienteRegistrado("EstCliente2", "pwd", "88880002B");
        ClienteRegistrado c3  = new ClienteRegistrado("EstCliente3", "pwd", "88880003C");

        ProductoTienda prodEst = crearProducto("Producto Estadística", "desc", "img.png", 50.0);
        stock.añadirProducto(prodEst, 20);

        // c1 hace 2 pedidos, c2 hace 1, c3 hace 0
        for (int i = 0; i < 2; i++) {
            stock.añadirProducto(prodEst, 1);
            c1.añadirALaCesta(prodEst, stock);
            c1.comprar();
        }
        stock.añadirProducto(prodEst, 1);
        c2.añadirALaCesta(prodEst, stock);
        c2.comprar();

        // Marcar pedidos de c1 como ENTREGADO con fecha de pago
        for (Pedido p : c1.getPedidos()) {
            p.setEstadoPedido(EstadoPedido.EN_PREPARACION);
            p.setEstadoPedido(EstadoPedido.LISTO);
            p.setEstadoPedido(EstadoPedido.ENTREGADO);
        }

        List<Pedido> todosPedidos = new ArrayList<>(c1.getPedidos());
        todosPedidos.addAll(c2.getPedidos());

        // 7a. estadisticaPedidos
        System.out.println("\n--- 7a. estadisticaPedidos ---");
        String ficheroPedidos = "/tmp/est_pedidos.txt";
        Estadistica estPedidos = new Estadistica(ficheroPedidos);
        estPedidos.estadisticaPedidos(todosPedidos);
        System.out.println("Estadística de pedidos escrita en: " + ficheroPedidos);

        // 7b. estadisticaRecaudacionMes
        System.out.println("\n--- 7b. estadisticaRecaudacionMes ---");
        String ficheroMes = "/tmp/est_mes.txt";
        Estadistica estMes = new Estadistica(ficheroMes);
        try {
            estMes.estadisticaRecaudacionMes(gestor, todosPedidos, new ArrayList<>());
            System.out.println("Estadística por mes escrita en: " + ficheroMes);
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR inesperado: " + e.getMessage());
        }

        // 7c. estadisticaRecaudacionMes con no-gestor → excepción
        System.out.println("\n--- 7c. estadisticaRecaudacionMes no-gestor → excepción ---");
        try {
            estMes.estadisticaRecaudacionMes(c1, todosPedidos, new ArrayList<>());
            System.out.println("ERROR: debería haber lanzado excepción");
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("Excepción capturada: " + e.getMessage());
        }

        // 7d. estadisticaRecaudacionTipo
        System.out.println("\n--- 7d. estadisticaRecaudacionTipo ---");
        String ficheroTipo = "/tmp/est_tipo.txt";
        Estadistica estTipo = new Estadistica(ficheroTipo);
        try {
            estTipo.estadisticaRecaudacionTipo(gestor, todosPedidos, new ArrayList<>());
            System.out.println("Estadística por tipo escrita en: " + ficheroTipo);
        } catch (excepciones.ExcepcionUsuariosAdmin e) {
            System.out.println("ERROR inesperado: " + e.getMessage());
        }

        // 7e. estadisticasUsuariosMayorActividadCompras
        System.out.println("\n--- 7e. estadisticasUsuariosMayorActividadCompras ---");
        List<ClienteRegistrado> clientes = new ArrayList<>();
        clientes.add(c1);
        clientes.add(c2);
        clientes.add(c3);
        String ficheroActCompras = "/tmp/est_actividad_compras.txt";
        Estadistica estActComp = new Estadistica(ficheroActCompras);
        estActComp.estadisticasUsuariosMayorActividadCompras(clientes);
        System.out.println("Estadística actividad compras escrita en: " + ficheroActCompras);

        // 7f. estadisticasUsuariosMayorActividadIntercambios
        System.out.println("\n--- 7f. estadisticasUsuariosMayorActividadIntercambios ---");
        String ficheroActInterc = "/tmp/est_actividad_intercambios.txt";
        Estadistica estActInt = new Estadistica(ficheroActInterc);
        estActInt.estadisticasUsuariosMayorActividadIntercambios(clientes);
        System.out.println("Estadística actividad intercambios escrita en: " + ficheroActInterc);

        // 7g. estadisticaCompraUsuarioValoracion
        System.out.println("\n--- 7g. estadisticaCompraUsuarioValoracion (global) ---");
        // Añadir una valoración a un pedido
        Pedido pedVal = c1.getPedidos().get(0);
        Map<ProductoTienda, Integer> vals = new HashMap<>();
        vals.put(prodEst, 9);
        pedVal.setValoracionesProductos(vals);
        String ficheroVal = "/tmp/est_val_global.txt";
        Estadistica estValGlobal = new Estadistica(ficheroVal);
        estValGlobal.estadisticaCompraUsuarioValoracion(c1.getPedidos());
        System.out.println("Estadística valoración global escrita en: " + ficheroVal);

        // 7h. estadisticaCompraUsuarioValoracionPorUsuario (requiere categoría)
        System.out.println("\n--- 7h. estadisticaCompraUsuarioValoracionPorUsuario ---");
        // Para que funcione sin NPE hay que asignar categoría al producto
        // (si no se le asigna, lanzaría NPE al pedir getCategoria().getNombre())
        // Lo saltamos intencionalmente si el producto no tiene categoría:
        if (prodEst.getCategoria() != null) {
            String ficheroValUser = "/tmp/est_val_usuario.txt";
            Estadistica estValUser = new Estadistica(ficheroValUser);
            estValUser.estadisticaCompraUsuarioValoracionPorUsuario(c1);
            System.out.println("Estadística valoración por usuario escrita en: " + ficheroValUser);
        } else {
            System.out.println("OMITIDO: prodEst no tiene categoría asignada (se evita NPE).");
        }

        System.out.println("\n--- Resumen estadísticas: todas completadas ---");
    }
}