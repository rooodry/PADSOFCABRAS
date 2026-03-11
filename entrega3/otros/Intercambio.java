package otros;
import java.util.Calendar;
import java.util.Date;


public class Intercambio {

    private final Date fechaOferta;
    private final Date fechaLimite;
    private final Date fechaAceptada;
    private boolean intercambiado;

    public Intercambio(Date fechaOferta) {

        this.fechaOferta = new Date(fechaOferta.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(this.fechaOferta);
        calendar.add(Calendar.DAY_OF_YEAR, 7);

        
        this.fechaLimite = calendar.getTime();
    }

}

