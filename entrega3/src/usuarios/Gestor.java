package usuarios;

import estadisticas.Estadistica;
import java.util.*;

public class Gestor extends Usuario {

    private List<Estadistica> Estadistica;

    public Gestor(String nombre, String contraseña) {
        super(nombre, contraseña);
        this.Estadistica = new ArrayList<Estadistica>();
    }

    public void addEstadistica(Estadistica e) {this.Estadistica.add(e);}
}
