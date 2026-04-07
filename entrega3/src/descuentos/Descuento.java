package descuentos;
import java.util.Date;

public abstract class Descuento {
    private final Date fechaInicio;
    private final Date fechaFin;

    public Descuento(Date fechaInicio, Date fechaFin) {
        this.fechaInicio = new Date(fechaInicio.getTime());
        this.fechaFin = new Date(fechaFin.getTime());
    }

    public Date getFechaInicio() {
        return new Date(this.fechaInicio.getTime());
    }
    
    public Date getFechaFin() {
        return new Date(this.fechaFin.getTime());
    }
}
