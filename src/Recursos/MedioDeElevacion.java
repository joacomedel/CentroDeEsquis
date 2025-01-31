package Recursos;

import java.nio.channels.Pipe.SourceChannel;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MedioDeElevacion {
    private CyclicBarrier molinetes;
    private Semaphore mutex;
    private int usos;
    private static int segundoEspera = 35;
    private int cantMolinetes;

    public MedioDeElevacion(int n) {
        cantMolinetes = n;
        molinetes = new CyclicBarrier(cantMolinetes);
        mutex = new Semaphore(1);
    }

    public void subir(boolean pase) throws InterruptedException, BrokenBarrierException {
        if (!pase) {
            System.out.println("No tiene pase");
        } else {
            // Si tiene pase
            try {
                System.out.println("Espera que vengan las personas necesarias para avanzar en el molinete");
                molinetes.await(segundoEspera, TimeUnit.SECONDS);
                System.out.println("Sube por el medio de elevacion");
                mutex.acquire();
                usos++;
                mutex.release();

            } catch (TimeoutException e) {
                System.out.println("Espero mucho tiempo por molinetes ");
            }

        }
    }

    public void reiniciarUso() throws InterruptedException {
        mutex.acquire();
        usos = 0;
        mutex.release();
    }

    public int getUsos() throws InterruptedException {
        int temp;
        mutex.acquire();
        temp = usos;
        mutex.release();
        return usos;
    }
}
