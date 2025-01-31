package Hilos;

import java.util.Random;
import RecursosCompartidos.ComplejoInvernal;

public class Instructor extends Thread {
    ComplejoInvernal complInv;
    int id;
    Random random;

    public Instructor(ComplejoInvernal complejoInv, int id) {
        this.complInv = complejoInv;
        this.setName("" + id);

    }

    @Override
    public void run() {
        try {
            do {
                complInv.instruirClase();

            } while (true);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
