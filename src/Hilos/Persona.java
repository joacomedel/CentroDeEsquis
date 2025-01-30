package Hilos;

import java.util.Random;
import RecursosCompartidos.ComplejoInvernal;

public class Persona extends Thread {
    private int actividadAHacer;
    ComplejoInvernal complInv;
    Random random;

    // el nombre del thread es la id que se le pasa por parametro a la persona
    public Persona(ComplejoInvernal complejoInvIn, int idIn) {
        random = new Random();
        complInv = complejoInvIn;
        setName("" + idIn);
    }

    @Override
    public void run() {

        try {
            do {

                System.out.println("Persona intenta entrar al complejo");
                complInv.entrarAComplejo();
                System.out.println("Persona entro al complejo");
                // complInv.entrarConfiteria(random.nextInt(2));
                complInv.participarClase();
            } while (true);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
