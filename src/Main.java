import org.iesalandalus.programacion.ficheros.alturasedad.negocio.Alumnos;

public class Main {
    public static void main(String[] args) {
        Alumnos alumnos = new Alumnos();
        alumnos.leer();
        alumnos.clasificar();
        alumnos.escribir();
    }
}