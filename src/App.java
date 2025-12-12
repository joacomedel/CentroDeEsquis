import java.util.Random;
import Hilos.Persona;
import RecursosCompartidos.ComplejoInvernal;
import interfaz.Interfaz; // Asegúrate de tener esta importación

public class App {
    static Random random = new Random();
    
    public static void main(String[] args) throws Exception {
        // Inicializa la interfaz gráfica si el modo consola está desactivado
        Interfaz.crearYMostrarGUI(); // Esta es la línea que debes agregar

        // Un único recurso compartido, dividido en distintos recursos
    	ComplejoInvernal compInv = new ComplejoInvernal();
        
        Persona[] personas = new Persona[40];
        for (int i = 0; i < personas.length; i++) {
            personas[i] = new Persona(compInv, i);
            personas[i].start();
        }
        try { Thread.sleep(java.util.concurrent.ThreadLocalRandom.current().nextLong(500, 3001)); } catch (InterruptedException e) {}
        //Esperamos una cantidad de tiempo entre medio segundo y 3 segundos asi no entran todos de golpe
    }
}