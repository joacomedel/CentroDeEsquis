import java.util.Random;

import Hilos.Persona;
import RecursosCompartidos.ComplejoInvernal;

public class App {
    static Random random = new Random();

    public static void main(String[] args) throws Exception {
        ComplejoInvernal compInv = new ComplejoInvernal();
        Persona[] personas = new Persona[150];
        for (int i = 0; i < personas.length; i++) {
            Persona persona = personas[i];
            persona = new Persona(compInv, i);
            persona.start();
        }
    }
}
