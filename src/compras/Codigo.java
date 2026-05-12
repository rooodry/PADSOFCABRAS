package compras;

import java.io.Serializable;
import java.util.UUID;

/**
 * Codigo unico asociado a un pedido o promocion.
 *
 * <p>El valor se genera automaticamente mediante UUID al construir la
 * instancia.</p>
 */
public class Codigo implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String codigo;

    /**
     * Constructor de la clase Codigo.
     * Genera un identificador aleatorio basado en UUID.
     */
    public Codigo() {
        this.codigo = UUID.randomUUID().toString();
    }

    /**
     * Obtiene el valor textual del código generado.
     *
     * @return Cadena de texto con el código UUID.
     */
    public String getCodigo() {return this.codigo;}
}
