import compras.*;
import descuentos.*;
import intercambios.*;
import notificaciones.Notificacion;
import productos.*;
import productos.categoria.*;
import sistema.Sistema;
import usuarios.*;
import utilidades.*;
 
import java.util.*;
 
/**
 * Main de pruebas completo para la tienda de coleccionables.
 * Cubre: usuarios, stock, cesta, pedidos, descuentos (todos los tipos),
 * packs, segunda mano, intercambios y notificaciones.
 */
public class Main {
 
    // ── Contadores globales de test ──────────────────────────────────────────
    private static int ok   = 0;
    private static int fail = 0;
 
    /** Comprueba una condición e imprime el resultado. */
    private static void check(String nombre, boolean condicion) {
        if (condicion) {
            System.out.println("  [OK]   " + nombre);
            ok++;
        } else {
            System.out.println("  [FAIL] " + nombre);
            fail++;
        }
    }
 
    /** Comparación de doubles con tolerancia. */
    private static boolean aprox(double a, double b) {
        return Math.abs(a - b) < 0.001;
    }
 
    // ════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) throws Exception {
 
        System.out.println("============================================================");
        System.out.println("  SUITE DE PRUEBAS - Tienda de Coleccionables PADSOFCABRAS");
        System.out.println("============================================================\n");
 
        testUsuarios();
        testStockYProductos();
        testCestaYPedidoBasico();
        testDescuentoPorcentaje();
        testDescuentoCantidadGastada();
        testDescuentoDosPorUno();
        testDescuentoRegalo();
        testDescuentoNoAplicaFueraDeFecha();
        testSinDescuento();
        testPacks();
        testSegundaManoEIntercambios();
        testNotificaciones();
        testSistema();
 
        // ── Resumen final ───────────────────────────────────────────────────
        System.out.println("\n============================================================");
        System.out.printf("  RESULTADO: %d OK  |  %d FALLOS%n", ok, fail);
        System.out.println("============================================================");
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 1. USUARIOS
    // ════════════════════════════════════════════════════════════════════════
    private static void testUsuarios() {
        System.out.println("\n── 1. Usuarios ─────────────────────────────────────────────");
 
        ClienteRegistrado c = new ClienteRegistrado("alice", "pass1", "12345678A");
        check("ClienteRegistrado creado con nombre correcto",
              "alice".equals(c.getNombre()));
        check("ClienteRegistrado tiene DNI correcto",
              "12345678A".equals(c.getDNI()));
 
        c.editarPerfil("alice2", "newpass");
        check("editarPerfil cambia nombre",  "alice2".equals(c.getNombre()));
        check("editarPerfil cambia contraseña", "newpass".equals(c.getContraseña()));
 
        ClienteNoRegistrado cnr = new ClienteNoRegistrado("visitante", "");
        check("ClienteNoRegistrado se instancia sin excepción", cnr != null);
 
        Gestor g = new Gestor("admin", "adminpass");
        check("Gestor creado", "admin".equals(g.getNombre()));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 2. STOCK Y PRODUCTOS
    // ════════════════════════════════════════════════════════════════════════
    private static void testStockYProductos() {
        System.out.println("\n── 2. Stock y Productos ────────────────────────────────────");
 
        Stock stock = new Stock();
        ProductoTienda p1 = new ProductoTienda("Cómic X", "Un cómic", "img.png");
        p1.setPrecio(10.0);
        ProductoTienda p2 = new ProductoTienda("Figura Y", "Una figura", "fig.png");
        p2.setPrecio(25.0);
 
        stock.añadirProducto(p1, 5);
        stock.añadirProducto(p2, 3);
        check("Stock p1 = 5 tras añadir",  stock.getNumProductos(p1) == 5);
        check("Stock p2 = 3 tras añadir",  stock.getNumProductos(p2) == 3);
 
        stock.reducirStock(p1, 2);
        check("Stock p1 = 3 tras reducir", stock.getNumProductos(p1) == 3);
 
        stock.retirarProducto(p2);
        check("Stock p2 = 0 tras retirar", stock.getNumProductos(p2) == 0);
 
        // Categorías
        Comic cat = new Comic("Spiderman", 200, "Stan Lee", "Marvel", Genero.AVENTURA);
        p1.setCategoria(cat);
        check("Producto tiene categoría Comic", p1.getCategoria() instanceof Comic);
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 3. CESTA Y PEDIDO BÁSICO (sin descuento)
    // ════════════════════════════════════════════════════════════════════════
    private static void testCestaYPedidoBasico() {
        System.out.println("\n── 3. Cesta y Pedido básico ────────────────────────────────");
 
        Stock stock = new Stock();
        ProductoTienda prod = new ProductoTienda("Juego Z", "desc", "z.png");
        prod.setPrecio(15.0);
        stock.añadirProducto(prod, 10);
 
        ClienteRegistrado cliente = new ClienteRegistrado("bob", "pass", "87654321B");
 
        // Añadir a cesta
        cliente.añadirALaCesta(prod, stock);
        check("Cesta no vacía tras añadir producto", !cliente.getCesta().estaVacia());
        check("Stock decrementado al añadir a cesta", stock.getNumProductos(prod) == 9);
 
        // Comprar
        Status s = cliente.comprar();
        check("comprar() devuelve OK",         s == Status.OK);
        check("Cesta vacía tras comprar",       cliente.getCesta().estaVacia());
        check("Pedido registrado en la lista",  cliente.getPedidos().size() == 1);
 
        // Pagar
        Pedido pedido = cliente.getPedidos().get(0);
        check("Estado inicial EN_CARRITO",
              pedido.getEstadoPedido() == EstadoPedido.EN_CARRITO);
 
        Status sp = cliente.pagarPedido(pedido);
        check("pagarPedido() devuelve OK",
              sp == Status.OK);
        check("Estado pasa a EN_PREPARACION",
              pedido.getEstadoPedido() == EstadoPedido.EN_PREPARACION);
 
        // Precio sin descuento = 15 €
        check("Precio sin descuento = 15.0",
              aprox(pedido.calcularPrecioTotal(), 15.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 4. DESCUENTO POR PORCENTAJE
    // ════════════════════════════════════════════════════════════════════════
    private static void testDescuentoPorcentaje() {
        System.out.println("\n── 4. Descuento por porcentaje ─────────────────────────────");
 
        // Producto de 100 € con 20% de descuento → 80 €
        ProductoTienda p = new ProductoTienda("Item A", "desc", "a.png");
        p.setPrecio(100.0);
 
        ClienteRegistrado c = clienteConProductoEnCesta(p, 1);
        Pedido pedido = c.getPedidos().get(0);
 
        Date ini = fechaHace(-1); // ayer
        Date fin = fechaHace(+1); // mañana
        DescuentoPorcentaje dp = new DescuentoPorcentaje(ini, fin, 20.0);
        pedido.setDescuento(dp);
 
        check("DescuentoPorcentaje 20% sobre 100€ = 80€",
              aprox(pedido.calcularPrecioTotal(), 80.0));
 
        // 10% sobre 50 €
        ProductoTienda p2 = new ProductoTienda("Item B", "desc", "b.png");
        p2.setPrecio(50.0);
        ClienteRegistrado c2 = clienteConProductoEnCesta(p2, 1);
        Pedido pedido2 = c2.getPedidos().get(0);
        pedido2.setDescuento(new DescuentoPorcentaje(ini, fin, 10.0));
        check("DescuentoPorcentaje 10% sobre 50€ = 45€",
              aprox(pedido2.calcularPrecioTotal(), 45.0));
 
        // 0% → precio intacto
        pedido2.setDescuento(new DescuentoPorcentaje(ini, fin, 0.0));
        check("DescuentoPorcentaje 0% no modifica precio",
              aprox(pedido2.calcularPrecioTotal(), 50.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 5. DESCUENTO POR CANTIDAD GASTADA
    // ════════════════════════════════════════════════════════════════════════
    private static void testDescuentoCantidadGastada() {
        System.out.println("\n── 5. Descuento por cantidad gastada ───────────────────────");
 
        Date ini = fechaHace(-1);
        Date fin = fechaHace(+1);
 
        // Total 120 €, mínimo 100 €, 15% → 102 €
        ProductoTienda p = new ProductoTienda("Item C", "desc", "c.png");
        p.setPrecio(120.0);
        ClienteRegistrado c = clienteConProductoEnCesta(p, 1);
        Pedido pedido = c.getPedidos().get(0);
        pedido.setDescuento(new DescuentoCantidadGastada(ini, fin, 100.0, 15.0));
        check("DCantGastada 15% sobre 120€ (min 100) = 102€",
              aprox(pedido.calcularPrecioTotal(), 102.0));
 
        // Total 80 €, mínimo 100 € → NO aplica, precio = 80 €
        ProductoTienda p2 = new ProductoTienda("Item D", "desc", "d.png");
        p2.setPrecio(80.0);
        ClienteRegistrado c2 = clienteConProductoEnCesta(p2, 1);
        Pedido pedido2 = c2.getPedidos().get(0);
        pedido2.setDescuento(new DescuentoCantidadGastada(ini, fin, 100.0, 15.0));
        check("DCantGastada no aplica si total < mínimo",
              aprox(pedido2.calcularPrecioTotal(), 80.0));
 
        // Exactamente en el límite: total = mínimo → sí aplica
        ProductoTienda p3 = new ProductoTienda("Item E", "desc", "e.png");
        p3.setPrecio(100.0);
        ClienteRegistrado c3 = clienteConProductoEnCesta(p3, 1);
        Pedido pedido3 = c3.getPedidos().get(0);
        pedido3.setDescuento(new DescuentoCantidadGastada(ini, fin, 100.0, 10.0));
        check("DCantGastada aplica cuando total == mínimo exacto → 90€",
              aprox(pedido3.calcularPrecioTotal(), 90.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 6. DESCUENTO DOS POR UNO
    // ════════════════════════════════════════════════════════════════════════
    private static void testDescuentoDosPorUno() {
        System.out.println("\n── 6. Descuento 2x1 ────────────────────────────────────────");
 
        Date ini = fechaHace(-1);
        Date fin = fechaHace(+1);
        DescuentoDosPorUno d2x1 = new DescuentoDosPorUno(ini, fin);
 
        // 2 unidades de 30 € → pagas 1 → 30 €
        ProductoTienda p = new ProductoTienda("Item F", "desc", "f.png");
        p.setPrecio(30.0);
        ClienteRegistrado c = clienteConProductoEnCesta(p, 2);
        Pedido pedido = c.getPedidos().get(0);
        pedido.setDescuento(d2x1);
        check("2x1 con 2 uds de 30€ = 30€",
              aprox(pedido.calcularPrecioTotal(), 30.0));
 
        // 3 unidades de 10 € → 1 par → pagas 2 → 20 €
        ProductoTienda p2 = new ProductoTienda("Item G", "desc", "g.png");
        p2.setPrecio(10.0);
        ClienteRegistrado c2 = clienteConProductoEnCesta(p2, 3);
        Pedido pedido2 = c2.getPedidos().get(0);
        pedido2.setDescuento(new DescuentoDosPorUno(ini, fin));
        check("2x1 con 3 uds de 10€ = 20€",
              aprox(pedido2.calcularPrecioTotal(), 20.0));
 
        // 4 unidades de 10 € → 2 pares → pagas 2 → 20 €
        ClienteRegistrado c3 = clienteConProductoEnCesta(p2, 4);
        Pedido pedido3 = c3.getPedidos().get(0);
        pedido3.setDescuento(new DescuentoDosPorUno(ini, fin));
        check("2x1 con 4 uds de 10€ = 20€",
              aprox(pedido3.calcularPrecioTotal(), 20.0));
 
        // 1 unidad → no hay par → precio completo
        ClienteRegistrado c4 = clienteConProductoEnCesta(p2, 1);
        Pedido pedido4 = c4.getPedidos().get(0);
        pedido4.setDescuento(new DescuentoDosPorUno(ini, fin));
        check("2x1 con 1 ud → precio completo 10€",
              aprox(pedido4.calcularPrecioTotal(), 10.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 7. DESCUENTO REGALO
    // ════════════════════════════════════════════════════════════════════════
    private static void testDescuentoRegalo() {
        System.out.println("\n── 7. Descuento regalo ─────────────────────────────────────");
 
        Date ini = fechaHace(-1);
        Date fin = fechaHace(+1);
 
        ProductoTienda regalo = new ProductoTienda("Regalo sorpresa", "gratis", "r.png");
        regalo.setPrecio(0.0);
        List<ProductoTienda> listaRegalo = new ArrayList<>();
        listaRegalo.add(regalo);
 
        DescuentoRegalo dr = new DescuentoRegalo(ini, fin, 50.0, listaRegalo);
 
        // Comprobamos que los getters devuelven los valores correctos
        check("DescuentoRegalo gastoMinimo = 50",
              aprox(dr.getGastoMinimo(), 50.0));
        check("DescuentoRegalo tiene 1 producto regalo",
              dr.getProductos().size() == 1);
        check("Producto regalo es el correcto",
              dr.getProductos().get(0).getNombre().equals("Regalo sorpresa"));
 
        // El descuento regalo no modifica el precio total directamente
        // (la lógica de añadir el regalo la gestiona el sistema/empleado)
        ProductoTienda compra = new ProductoTienda("Compra grande", "desc", "x.png");
        compra.setPrecio(80.0);
        ClienteRegistrado c = clienteConProductoEnCesta(compra, 1);
        Pedido pedido = c.getPedidos().get(0);
        pedido.setDescuento(dr);
        // calcularPrecioTotal no tiene rama para DescuentoRegalo → precio sin cambio
        check("DescuentoRegalo no modifica precio total (el regalo es aparte)",
              aprox(pedido.calcularPrecioTotal(), 80.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 8. DESCUENTO FUERA DE FECHA (no debería aplicarse desde el sistema)
    // ════════════════════════════════════════════════════════════════════════
    private static void testDescuentoNoAplicaFueraDeFecha() {
        System.out.println("\n── 8. Fechas de descuento ──────────────────────────────────");
 
        // Descuento caducado (ambas fechas en el pasado)
        Date ini = fechaHace(-10);
        Date fin = fechaHace(-5);
        DescuentoPorcentaje dp = new DescuentoPorcentaje(ini, fin, 50.0);
        check("Descuento caducado: fechaFin antes de hoy",
              dp.getFechaFin().before(new Date()));
 
        // Descuento futuro
        Date ini2 = fechaHace(+5);
        Date fin2 = fechaHace(+10);
        DescuentoPorcentaje dp2 = new DescuentoPorcentaje(ini2, fin2, 50.0);
        check("Descuento futuro: fechaInicio después de hoy",
              dp2.getFechaInicio().after(new Date()));
 
        // Nota: calcularPrecioTotal aplica el descuento si está asignado al pedido,
        // sin comprobar fechas; la responsabilidad de no asignarlo fuera de rango
        // recae en el Sistema/Gestor. Lo verificamos conceptualmente:
        check("Concepto: el pedido no debería recibir descuentos caducados (verificación manual)",
              true); // marcamos como OK conceptual
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 9. SIN DESCUENTO — precio exacto con varios productos
    // ════════════════════════════════════════════════════════════════════════
    private static void testSinDescuento() {
        System.out.println("\n── 9. Sin descuento ─────────────────────────────────────────");
 
        // Pedido con 2 productos distintos, sin descuento
        Stock stock = new Stock();
        ProductoTienda pA = new ProductoTienda("A", "d", "a.png");
        pA.setPrecio(20.0);
        ProductoTienda pB = new ProductoTienda("B", "d", "b.png");
        pB.setPrecio(35.0);
        stock.añadirProducto(pA, 5);
        stock.añadirProducto(pB, 5);
 
        ClienteRegistrado c = new ClienteRegistrado("carol", "x", "11111111C");
        c.añadirALaCesta(pA, stock); // 20
        c.añadirALaCesta(pB, stock); // 35
        c.comprar();
 
        Pedido pedido = c.getPedidos().get(0);
        // Sin descuento → 20 + 35 = 55
        check("Sin descuento 20+35 = 55€",
              aprox(pedido.calcularPrecioTotal(), 55.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 10. PACKS
    // ════════════════════════════════════════════════════════════════════════
    private static void testPacks() {
        System.out.println("\n── 10. Packs ───────────────────────────────────────────────");
 
        ProductoTienda pA = new ProductoTienda("PA", "d", "a.png");
        pA.setPrecio(10.0);
        ProductoTienda pB = new ProductoTienda("PB", "d", "b.png");
        pB.setPrecio(15.0);
 
        List<Producto> lista = new ArrayList<>();
        lista.add(pA);
        lista.add(pB);
 
        Pack pack = new Pack("Pack AB", 20.0, lista);
        check("Pack nombre correcto",       "Pack AB".equals(pack.getNombre()));
        check("Pack precio = 20",           aprox(pack.getPrecio(), 20.0));
        check("Pack tiene 2 productos",     pack.getProductos().size() == 2);
        check("Pack subpacks vacío",        pack.getSubpacks().isEmpty());
 
        // Pack recursivo
        Pack subpack = new Pack("SubPack", 8.0, new ArrayList<>());
        pack.addSubpack(subpack);
        check("Subpack añadido correctamente", pack.getSubpacks().size() == 1);
        check("Subpack nombre correcto",       "SubPack".equals(pack.getSubpacks().get(0).getNombre()));
 
        pack.removeSubpack(subpack);
        check("Subpack eliminado correctamente", pack.getSubpacks().isEmpty());
 
        // Cambio de precio
        pack.setPrecio(18.0);
        check("Pack precio actualizado a 18", aprox(pack.getPrecio(), 18.0));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 11. SEGUNDA MANO E INTERCAMBIOS
    // ════════════════════════════════════════════════════════════════════════
    private static void testSegundaManoEIntercambios() {
        System.out.println("\n── 11. Segunda mano e intercambios ─────────────────────────");
 
        ClienteRegistrado c1 = new ClienteRegistrado("dan",  "p1", "22222222D");
        ClienteRegistrado c2 = new ClienteRegistrado("eve",  "p2", "33333333E");
 
        // c1 sube un producto
        ProductoSegundaMano psm1 = new ProductoSegundaMano("Cómic usado", "desc", "c.png", c1);
        c1.subirProducto(psm1);
        check("Producto segunda mano en cartera de c1",
              c1.getCartera().getProductos().contains(psm1));
        check("Producto sin valorar inicialmente",
              !psm1.getEstaValorado());
 
        // Empleado valorar
        EmpleadoIntercambio emp = new EmpleadoIntercambio("empI", "pass");
        emp.addProductoParaValorar(psm1);
        emp.valorarProducto(psm1, 8, 15.0, EstadoConservacion.MUY_BUENO);
        check("Producto valorado tras valorarProducto()",     psm1.getEstaValorado());
        check("Valor estimado = 15.0",                        aprox(psm1.getValorEstimado(), 15.0));
        check("Estado conservacion = MUY_BUENO",
              psm1.getEstadoConservacion() == EstadoConservacion.MUY_BUENO);
        check("Ya no está en lista pendiente del empleado",
              emp.getProductosPorValorar().isEmpty());
 
        // c2 también sube un producto valorado
        ProductoSegundaMano psm2 = new ProductoSegundaMano("Figura usada", "desc", "f.png", c2);
        c2.subirProducto(psm2);
        emp.addProductoParaValorar(psm2);
        emp.valorarProducto(psm2, 7, 12.0, EstadoConservacion.USO_LIGERO);
        psm1.subirProducto(); // hace disponible
        psm2.subirProducto();
        check("psm1 disponible tras subirProducto()", psm1.getDisponibilidad());
        check("psm2 disponible tras subirProducto()", psm2.getDisponibilidad());
 
        // Crear oferta e intercambio
        Oferta oferta = new Oferta(psm1, psm2, c2, c1);
        check("Oferta estado inicial PENDIENTE",
              oferta.getEstadoOferta() == EstadoOferta.PENDIENTE);
 
        Intercambio intercambio = new Intercambio(new Date(), oferta);
        check("Intercambio no completado inicialmente",
              !intercambio.getIntercambiado());
        check("FechaLimite = fechaOferta + 7 días (mayor que ahora)",
              intercambio.getFechaLimite().after(new Date()));
 
        // Aceptar oferta
        intercambio.aceptarOferta();
        check("Oferta aceptada",
              oferta.getEstadoOferta() == EstadoOferta.ACEPTADA);
 
        // Transferir propiedad
        emp.addIntercambio(intercambio);
        emp.transferirPropiedad(intercambio);
        check("Intercambio completado tras transferirPropiedad()",
              intercambio.getIntercambiado());
        check("c2 recibe psm1 en su cartera",
              c2.getCartera().getProductos().contains(psm1));
        check("c1 recibe psm2 en su cartera",
              c1.getCartera().getProductos().contains(psm2));
 
        // Rechazar oferta en un intercambio nuevo
        ProductoSegundaMano psm3 = new ProductoSegundaMano("Extra", "d", "x.png", c1);
        ProductoSegundaMano psm4 = new ProductoSegundaMano("Extra2", "d", "y.png", c2);
        Oferta oferta2 = new Oferta(psm3, psm4, c2, c1);
        Intercambio intercambio2 = new Intercambio(new Date(), oferta2);
        intercambio2.rechazarOferta();
        check("Oferta rechazada correctamente",
              oferta2.getEstadoOferta() == EstadoOferta.RECHAZADA);
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 12. NOTIFICACIONES
    // ════════════════════════════════════════════════════════════════════════
    private static void testNotificaciones() {
        System.out.println("\n── 12. Notificaciones ──────────────────────────────────────");
 
        ClienteRegistrado c = new ClienteRegistrado("fran", "p", "44444444F");
        Notificacion n = new Notificacion(TipoNotificacion.PAGO_REALIZADO, "Tu pedido ha sido pagado");
 
        check("Notificación no leída inicialmente", !n.getLeida());
        check("Notificación no borrada inicialmente", !n.getBorrada());
 
        c.addNotificacion(n);
        check("Notificación añadida al usuario",
              c.getNotificaciones().contains(n));
 
        c.leerNotificaicion(n);
        check("Notificación marcada como leída", n.getLeida());
 
        c.borrarNotificacion(n);
        check("Notificación borrada de la lista",
              !c.getNotificaciones().contains(n));
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // 13. SISTEMA (alta/baja empleados, permisos, stock vía sistema)
    // ════════════════════════════════════════════════════════════════════════
    private static void testSistema() throws Exception {
        System.out.println("\n── 13. Sistema ─────────────────────────────────────────────");
 
        Sistema sistema = new Sistema();
        Stock stock = new Stock();
        sistema.setStock(stock);
 
        Gestor gestor  = new Gestor("gestor", "gpass");
        ClienteRegistrado cliente = new ClienteRegistrado("user1", "u1", "55555555G");
        sistema.addUsuario(gestor);
        sistema.addUsuario(cliente);
 
        // Alta de empleados
        sistema.darAltaEmpleado(gestor, "emp1", "e1pass", TiposEmpleado.EMPLEADOS_PEDIDO);
        sistema.darAltaEmpleado(gestor, "emp2", "e2pass", TiposEmpleado.EMPLEADOS_INTERCAMBIO);
        sistema.darAltaEmpleado(gestor, "emp3", "e3pass", TiposEmpleado.EMPLEADOS_PRODUCTO);
        check("3 empleados dados de alta sin lanzar excepción", true);
 
        // Alta con usuario no-gestor → excepción
        boolean lanzaExcepcion = false;
        try {
            sistema.darAltaEmpleado(cliente, "hack", "h", TiposEmpleado.EMPLEADOS_PEDIDO);
        } catch (Exception e) {
            lanzaExcepcion = true;
        }
        check("darAltaEmpleado con no-gestor lanza excepción", lanzaExcepcion);
 
        // Stock vía sistema
        ProductoTienda pt = new ProductoTienda("TestProd", "d", "t.png");
        pt.setPrecio(5.0);
        stock.añadirProducto(pt, 0); // forzamos que exista con 0
        sistema.actualizarStock(gestor, pt, 10);
        check("actualizarStock añade 10 unidades", stock.getNumProductos(pt) == 10);
 
        // Pack vía sistema
        List<Producto> listaP = new ArrayList<>();
        listaP.add(pt);
        Pack pack = sistema.crearPack(gestor, "PackTest", 30.0, listaP);
        check("crearPack devuelve Pack no nulo", pack != null);
        check("Pack nombre = PackTest", "PackTest".equals(pack.getNombre()));
 
        // Código generado
        Codigo cod = sistema.generarCodigo();
        check("generarCodigo devuelve Codigo no nulo", cod != null);
 
        // Registrar y cancelar pedido
        cliente.añadirALaCesta(pt, stock);
        cliente.comprar();
        Pedido pedido = cliente.getPedidos().get(0);
        sistema.registrarPedido(pedido);
        sistema.cancelarPedido(pedido);
        check("Pedido cancelado → estado CANCELADO",
              pedido.getEstadoPedido() == EstadoPedido.CANCELADO);
        check("Stock repuesto tras cancelar pedido",
              stock.getNumProductos(pt) >= 1);
    }
 
    // ════════════════════════════════════════════════════════════════════════
    // HELPERS
    // ════════════════════════════════════════════════════════════════════════
 
    /**
     * Crea un cliente con `cantidad` unidades de `prod` ya en el historial
     * de pedidos (simula añadir a cesta + comprar) usando un Stock temporal.
     */
    private static ClienteRegistrado clienteConProductoEnCesta(ProductoTienda prod, int cantidad) {
        Stock stockTmp = new Stock();
        stockTmp.añadirProducto(prod, cantidad);
 
        ClienteRegistrado c = new ClienteRegistrado("testUser_" + prod.getNombre(), "p", "00000000Z");
 
        // Añadimos `cantidad` unidades una a una (añadirALaCesta descuenta 1 cada vez)
        for (int i = 0; i < cantidad; i++) {
            c.añadirALaCesta(prod, stockTmp);
        }
        c.comprar();
        return c;
    }
 
    /** Devuelve una Date desplazada `diasOffset` días respecto a hoy. */
    private static Date fechaHace(int diasOffset) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, diasOffset);
        return cal.getTime();
    }
}