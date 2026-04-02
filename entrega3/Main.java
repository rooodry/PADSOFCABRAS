import sistema.Sistema;
import usuarios.*;
import productos.*;
import compras.*;
import utilidades.*;
import intercambios.*;
import excepciones.*;
import estadisticas.Estadistica;
import productos.categoria.*; // Para las categorías

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================================");
        System.out.println("      BIENVENIDO A LA SIMULACIÓN DE GOAT & GET       ");
        System.out.println("=====================================================\n");

        // =================================================================
        // FASE 1: INICIALIZACIÓN Y CARGA DE DATOS
        // =================================================================
        Sistema sistema = new Sistema();
        Stock stock = new Stock();
        sistema.setStock(stock);

        Gestor gestor = new Gestor("admin", "1234");
        sistema.addUsuario(gestor);
        System.out.println("✅ Sistema iniciado y Gestor (" + gestor.getNombre() + ") operativo.");

        List<ProductoTienda> productosCargados = new java.util.ArrayList<>();
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("entrega3/txt/cargaproductos.txt"))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split("\\|");
                if (partes.length < 4) continue;
                
                ProductoTienda p = new ProductoTienda(partes[0].trim(), partes[1].trim(), partes[2].trim());
                p.setPrecio(Double.parseDouble(partes[3].trim()));
                stock.añadirProducto(p, 10); // Metemos 10 unidades de cada
                productosCargados.add(p);
            }
            System.out.println("📦 Stock cargado: " + productosCargados.size() + " productos distintos en el almacén.");
        } catch (Exception e) {
            System.err.println("X Error leyendo fichero: " + e.getMessage());
            return;
        }

        // Rescatamos los productos principales para las pruebas
        ProductoTienda batman = productosCargados.get(0);     // 24.95€
        ProductoTienda spiderman = productosCargados.get(1);  // 15.50€
        ProductoTienda catan = productosCargados.get(2);      // 45.00€
        ProductoTienda vader = productosCargados.get(4);      // 149.99€

        // Asignamos categorías para que las estadísticas y recomendaciones no den fallo
        batman.setCategoria(new Comic("Superheroes", 120, "DC", "ECC", Genero.AVENTURA));
        catan.setCategoria(new Juego("Estrategia", 4, 10, TipoJuego.JUEGO_MESA));


        // =================================================================
        // FASE 2: EL GESTOR APLICA DESCUENTOS EN LA TIENDA
        // =================================================================
        System.out.println("\n--- GESTIÓN DE OFERTAS (ADMINISTRADOR) ---");
        batman.setTiene2x1(true);
        System.out.println("🏷️ Promoción 2x1 activada en: " + batman.getNombre());
        
        spiderman.setRebajaPorcentaje(0.20);
        System.out.println("🏷️ Rebaja del 20% activada en: " + spiderman.getNombre());
        
        catan.setRebajaFija(5.0);
        System.out.println("🏷️ Descuento de 5€ directo en: " + catan.getNombre());


        // =================================================================
        // FASE 3: COMPRA MASIVA Y DESCUENTOS AUTOMÁTICOS
        // =================================================================
        System.out.println("\n--- COMPRA DE CLIENTE (TEST DE MATEMÁTICAS) ---");
        ClienteRegistrado juan = new ClienteRegistrado("juan123", "pass", "12345678A");
        sistema.addUsuario(juan);

        // Juan mete productos en la cesta
        juan.añadirALaCesta(batman, stock);
        juan.añadirALaCesta(batman, stock);
        juan.añadirALaCesta(batman, stock); // Lleva 3 Batmans (Debería pagar 2 por el 2x1)
        juan.añadirALaCesta(spiderman, stock);
        juan.añadirALaCesta(spiderman, stock); // Lleva 2 Spidermans (Con 20% dto)
        juan.añadirALaCesta(catan, stock); // Lleva 1 Catan (-5€)
        juan.añadirALaCesta(vader, stock); // Lleva 1 Vader (Precio normal)

        juan.comprar();
        Pedido pedidoJuan = juan.getPedidos().get(0);
        
        juan.pagarPedido(pedidoJuan);
        sistema.registrarPedido(pedidoJuan); // Calcula precio total y asigna regalo

        System.out.println("💰 Precio Total Calculado: " + String.format("%.2f", pedidoJuan.calcularPrecioTotal()) + "€");
        System.out.println("   (Nota: El total sin descuentos era > 300€. Se han aplicado las rebajas individuales y luego un 15% extra por pasar de 150€)");
        
        if(pedidoJuan.getRegalo() != null) {
            System.out.println("🎁 ¡REGALO AUTOMÁTICO AÑADIDO! Al pasar de 200€, Juan se lleva gratis: " + pedidoJuan.getRegalo().getNombre());
        }

        EmpleadoPedido empPedido = new EmpleadoPedido("empPedido", "pass2");
        empPedido.marcarComoListo(pedidoJuan);
        empPedido.entregarPedido(pedidoJuan);
        System.out.println("🚚 Pedido entregado correctamente.");


        // =================================================================
        // FASE 4: VALORACIONES Y ESTADÍSTICAS
        // =================================================================
        System.out.println("\n--- VALORACIONES Y REPORTES ---");
        Map<ProductoTienda, Integer> reviews = new HashMap<>();
        reviews.put(batman, 5); // Le da 5 estrellas a Batman
        reviews.put(catan, 4);  // Le da 4 estrellas al Catan
        juan.añadirValoraciones(pedidoJuan, reviews);
        System.out.println("⭐ Juan ha valorado sus compras.");

        // Generamos el archivo TXT de estadísticas de Juan
        Estadistica statsJuan = new Estadistica("comprasClientejuan123"); // Mismo nombre que buscará el recomendador
        statsJuan.estadisticaCompraUsuarioValoracionPorUsuario(juan);
        System.out.println("📊 Archivo de estadísticas 'comprasClientejuan123.txt' generado con éxito.");


        // =================================================================
        // FASE 5: SEGUNDA MANO Y TRUEQUES (SISTEMA P2P)
        // =================================================================
        System.out.println("\n--- PLATAFORMA DE TRUEQUE ---");
        EmpleadoIntercambio empIntercambio = new EmpleadoIntercambio("empIntercambio", "pass3");

        // Juan sube un producto
        ProductoSegundaMano psmJuan = new ProductoSegundaMano("Catan Edición 1995", "Esquinas gastadas", "catan_viejo.jpg", juan);
        juan.subirProducto(psmJuan);
        empIntercambio.valorarProducto(psmJuan, 6, 20.0, EstadoConservacion.USO_EVIDENTE);
        
        // María se registra y sube un producto
        ClienteRegistrado maria = new ClienteRegistrado("maria456", "pass", "87654321B");
        sistema.addUsuario(maria);
        ProductoSegundaMano psmMaria = new ProductoSegundaMano("Goku Super Saiyan", "Casi nueva", "goku.jpg", maria);
        maria.subirProducto(psmMaria);
        empIntercambio.valorarProducto(psmMaria, 9, 25.0, EstadoConservacion.MUY_BUENO);

        // María ofrece su Goku por el Catan de Juan
        Oferta oferta = new Oferta(psmMaria, psmJuan, maria, juan);
        maria.getOfertasRealizadas().add(oferta);
        juan.getOfertasRecibidas().add(oferta);
        System.out.println("🤝 " + maria.getNombre() + " ha ofrecido [" + psmMaria.getNombre() + "] a cambio de [" + psmJuan.getNombre() + "]");

        // Se procesa el intercambio
        Intercambio intercambio = new Intercambio(new Date(), oferta);
        intercambio.aceptarOferta();
        sistema.asignarIntercambio(intercambio, empIntercambio);
        
        empIntercambio.marcarIntercambioListo(intercambio); // Dispara notificaciones
        empIntercambio.transferirPropiedad(intercambio); // Cambia los dueños
        System.out.println("✅ Intercambio finalizado y propiedades transferidas en tienda.");

        // Comprobamos notificaciones
        if(!juan.getNotificaciones().isEmpty()) {
            System.out.println("🔔 Juan tiene una nueva notificación: '" + juan.getNotificaciones().get(0).getMensaje() + "'");
        }

        System.out.println("\n=====================================================");
        System.out.println("   SIMULACIÓN COMPLETADA SIN ERRORES. ¡BUEN TRABAJO! ");
        System.out.println("=====================================================");
    }
}