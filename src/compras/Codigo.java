package compras;
 
import java.io.Serializable;
import java.util.UUID;
 
/**
 * Clase envoltorio que representa un identificador único alfanumérico.
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
     * * @return Cadena de texto con el código UUID.
     */
    public String getCodigo() {return this.codigo;}
}
