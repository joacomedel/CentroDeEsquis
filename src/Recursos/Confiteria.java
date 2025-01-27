package Recursos;

import java.nio.channels.Pipe.SourceChannel;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class Confiteria {
    private final int cantMesas = 100;
    private final int cantMostradorComidaRapida = 2;
    private final int cantMostradorPostre = 1;
    private Semaphore semMesas = new Semaphore(cantMesas);
    private Semaphore semCaja = new Semaphore(1);
    private Semaphore semMostradorCD = new Semaphore(cantMostradorComidaRapida);
    private Semaphore semMostradorPostre = new Semaphore(cantMostradorPostre);
    Random random = new Random();

    public Confiteria() {
    }

    public void usaConfiteria(int tipoUso) throws InterruptedException {
        System.out.println("Entro a la confiteria, espero a obtener mesas");
        semMesas.acquire();
        System.out.println("Consegui mesa");
        // Hay mesas disponibles / espera a que se libren mesas
        pagaEnCaja(tipoUso);
        servirComidaRapida();
        if (tipoUso == 1) {
            servirPostre();
        }
        // Come en la mesa
        System.out.println("Persona comienza a comer");
        Thread.sleep(obtenerValorRandomEntre(5000, 30000));
        System.out.println("Persona termino de comer");
        semMesas.release();
    }

    private void servirComidaRapida() throws InterruptedException {
        // dos mostradores
        System.out.println("Espera para servirse comida rapida");
        semMostradorCD.acquire();
        System.out.println("Se sirve CD...");
        Thread.sleep(obtenerValorRandomEntre(1000, 2500));
        System.out.println("Termino de servirse CD");
        semMostradorCD.release();
    }

    private void servirPostre() throws InterruptedException {
        System.out.println("Espera para servirse postre");
        semMostradorPostre.acquire();
        System.out.println("Se sirve postre...");
        Thread.sleep(obtenerValorRandomEntre(1000, 2500));
        // Se sirve postre
        System.out.println("Termino de servirse postre");
        semMostradorPostre.release();

    }

    private void pagaEnCaja(int tipoUso) throws InterruptedException {
        System.out.println("Espera para pagar en caja");
        semCaja.acquire();
        System.out.println("Pagando en caja...");
        Thread.sleep(obtenerValorRandomEntre(500, 5000));
        // Paga en caja
        System.out.println("Terminó de pagar en caja");
        semCaja.release();
    }

    private int obtenerValorRandomEntre(int n, int m) {
        // valor random entre n y m
        return random.nextInt(m - n) + n;

    }
}
