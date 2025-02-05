import java.util.Random;

import Hilos.Persona;
import RecursosCompartidos.ComplejoInvernal;

public class App {
    static Random random = new Random();

    public static void main(String[] args) throws Exception {
        ComplejoInvernal compInv = new ComplejoInvernal();
        Persona[] personas = new Persona[100];
        for (int i = 0; i < personas.length; i++) {

            personas[i] = new Persona(compInv, i);
            personas[i].start();
        }
    }
}
