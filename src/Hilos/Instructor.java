package Hilos;

import java.util.Random;
import RecursosCompartidos.ComplejoInvernal;

public class Instructor extends Thread {
    private int actividadAHacer;
    ComplejoInvernal complInv;
    int id;
    Random random;

    public Instructor(ComplejoInvernal complejoInv) {
        System.out.println("se creo el instructor");
        this.complInv = complejoInv;
    }

    @Override
    public void run() {
        try {
            do {
                System.out.println("Intenta instruir");
                complInv.instruirClase();
            } while (true);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
