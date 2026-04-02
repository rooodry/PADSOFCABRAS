import sistema.Sistema;
import usuarios.*;
import productos.*;
import compras.*;
import utilidades.*;
import intercambios.*;
import excepciones.*;
import estadisticas.Estadistica;
import productos.categoria.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ⚠️ SI TU ARCHIVO SE LLAMA main2.java, CAMBIA LA PALABRA "Main" por "main2"
public class Main {

    public static void main(String[] args) {

        System.out.println("=====================================================");
        System.out.println("      BIENVENIDO A LA SIMULACIÓN DE GOAT & GET       ");
        System.out.println("=====================================================\n");

        // =================================================================
        // FASE 1: INICIALIZACIÓN Y CARGA DE DATOS DESDE CSV
        // =================================================================
        Sistema sistema = new Sistema();
        Stock stock = new Stock();
        sistema.setStock(stock);

        Gestor gestor = new Gestor("admin", "1234");
        sistema.addUsuario(gestor);
        System.out.println("✅ Sistema iniciado y Gestor (" + gestor.getNombre() + ") operativo.");

        List<ProductoTienda> productosCargados = new java.util.ArrayList<>();
        
        // 🚀 NUEVO LECTOR DE CSV
        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader("entrega3/txt/productos (2).csv"))) {
            String linea;
            br.readLine(); // Saltamos la cabecera
            
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] partes = linea.split(";");
                if (partes.length < 6) continue;
                
                String tipo = partes[0].trim();
                String id = partes[1].trim();
                String nombre = partes[2].trim();
                String desc = partes[3].trim();
                double precio = Double.parseDouble(partes[4].trim());
                int unidadesEnStock = Integer.parseInt(partes[5].trim());
                
                ProductoTienda p = new ProductoTienda(nombre, desc, id + ".jpg");
                p.setPrecio(precio);
                
                if(tipo.equals("C")) {
                    p.setCategoria(new Comic("Superheroes", 100, "Desconocido", "Desconocido", Genero.AVENTURA));
                } else if(tipo.equals("J")) {
                    p.setCategoria(new Juego("Estrategia", 2, 4, TipoJuego.JUEGO_MESA));
                } else if(tipo.equals("F")) {
                    p.setCategoria(new Figura("Merchandising", 15.0)); 
                }

                stock.añadirProducto(p, unidadesEnStock); 
                productosCargados.add(p);
            }
            System.out.println("📦 Stock cargado desde CSV: " + productosCargados.size() + " productos procesados.");
            
        } catch (Exception e) {
            System.err.println("X Error leyendo fichero CSV: " + e.getMessage());
            return;
        }

        if (productosCargados.size() < 5) {
            System.out.println("No hay suficientes productos en el CSV para la prueba");
            return;
        }
        
        ProductoTienda batman = productosCargados.get(0);    
        ProductoTienda spiderman = productosCargados.get(2); 
        ProductoTienda catan = productosCargados.get(3);     
        ProductoTienda vader = productosCargados.get(4);     

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

        juan.añadirALaCesta(batman, stock);
        juan.añadirALaCesta(batman, stock);
        juan.añadirALaCesta(batman, stock); 
        juan.añadirALaCesta(spiderman, stock);
        juan.añadirALaCesta(spiderman, stock); 
        juan.añadirALaCesta(catan, stock); 
        juan.añadirALaCesta(vader, stock); 

        juan.comprar();
        Pedido pedidoJuan = juan.getPedidos().get(0);
        
        juan.pagarPedido(pedidoJuan);
        sistema.registrarPedido(pedidoJuan); 

        System.out.println("💰 Precio Total Calculado: " + String.format("%.2f", pedidoJuan.calcularPrecioTotal()) + "€");
        System.out.println("   (Nota: El total sin descuentos era > 300€. Se han aplicado rebajas individuales y 15% extra)");
        
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
        reviews.put(batman, 5); 
        reviews.put(catan, 4);  
        juan.añadirValoraciones(pedidoJuan, reviews);
        System.out.println("⭐ Juan ha valorado sus compras.");

        Estadistica statsJuan = new Estadistica("comprasClientejuan123"); 
        statsJuan.estadisticaCompraUsuarioValoracionPorUsuario(juan);
        System.out.println("📊 Archivo de estadísticas generado con éxito.");


        // =================================================================
        // FASE 5: SEGUNDA MANO Y TRUEQUES (SISTEMA P2P)
        // =================================================================
        System.out.println("\n--- PLATAFORMA DE TRUEQUE ---");
        EmpleadoIntercambio empIntercambio = new EmpleadoIntercambio("empIntercambio", "pass3");

        ProductoSegundaMano psmJuan = new ProductoSegundaMano("Catan Edición 1995", "Esquinas gastadas", "catan_viejo.jpg", juan);
        juan.subirProducto(psmJuan);
        empIntercambio.valorarProducto(psmJuan, 6, 20.0, EstadoConservacion.USO_EVIDENTE);
        
        ClienteRegistrado maria = new ClienteRegistrado("maria456", "pass", "87654321B");
        sistema.addUsuario(maria);
        ProductoSegundaMano psmMaria = new ProductoSegundaMano("Goku Super Saiyan", "Casi nueva", "goku.jpg", maria);
        maria.subirProducto(psmMaria);
        empIntercambio.valorarProducto(psmMaria, 9, 25.0, EstadoConservacion.MUY_BUENO);

        Oferta oferta = new Oferta(psmMaria, psmJuan, maria, juan);
        maria.getOfertasRealizadas().add(oferta);
        juan.getOfertasRecibidas().add(oferta);
        System.out.println("🤝 " + maria.getNombre() + " ha ofrecido [" + psmMaria.getNombre() + "] a cambio de [" + psmJuan.getNombre() + "]");

        Intercambio intercambio = new Intercambio(new Date(), oferta);
        intercambio.aceptarOferta();
        sistema.asignarIntercambio(intercambio, empIntercambio);
        
        empIntercambio.marcarIntercambioListo(intercambio); 
        empIntercambio.transferirPropiedad(intercambio); 
        System.out.println("✅ Intercambio finalizado y propiedades transferidas en tienda.");

        if(!juan.getNotificaciones().isEmpty()) {
            System.out.println("🔔 Juan tiene una nueva notificación: '" + juan.getNotificaciones().get(0).getMensaje() + "'");
        }

        System.out.println("\n=====================================================");
        System.out.println("   SIMULACIÓN COMPLETADA SIN ERRORES. ¡BUEN TRABAJO! ");
        System.out.println("=====================================================");
    }
}