package Hilos;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

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
                complInv.entrarAComplejo(); // Utiliza entrada
                complInv.subir(); //Utiliza medio de elevacion
                //Se podria intentar ver si esta cerrado o no para ver si sigue o no haciendo cosas 
                if (random.nextInt(2) == 0) {
                	System.out.println("Persona " + getName() + " decide ir a la Confitería.");
                    complInv.entrarConfiteria(random.nextInt(2)); //Utiliza confiteria
                }
                if (random.nextInt(2) == 0) {
                	System.out.println("Persona " + getName() + " decide participar en una clase grupal.");
                    complInv.participarClase(); //Utiliza centro de clases grupales
                }
            } while (true);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
