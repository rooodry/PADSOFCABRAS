package compras;
 
import java.util.UUID;
 
public class Codigo {
    private final String codigo;
 
    public Codigo() {
        this.codigo = UUID.randomUUID().toString();
    }
    

    //GETTER//
    public String getCodigo() {return this.codigo;}
}
