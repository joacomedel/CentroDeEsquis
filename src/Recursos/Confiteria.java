package Recursos;

import java.util.Random;
import java.util.concurrent.Semaphore;

import interfaz.Interfaz;

//Recurso compartido usando Semaforos

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
    	Interfaz.esperandoMesa();
        semMesas.acquire();
        Interfaz.conseguiMesa();
        // Hay mesas disponibles / espera a que se libren mesas
        pagaEnCaja(tipoUso);
        servirComidaRapida();
        if (tipoUso == 1) {
            servirPostre();
        }
        // Come en la mesa
        Interfaz.comiendo();
        Thread.sleep(obtenerValorRandomEntre(5000, 30000));
        Interfaz.terminoDeComer();
        semMesas.release();
    }

    private void servirComidaRapida() throws InterruptedException {
        // dos mostradores
    	Interfaz.esperandoComidaRapida();
        semMostradorCD.acquire();
        Interfaz.conseguiComidaRapida();
        Thread.sleep(obtenerValorRandomEntre(1000, 2500));
        System.out.println("Termino de servirse CD");
        semMostradorCD.release();
    }

    private void servirPostre() throws InterruptedException {
    	Interfaz.esperandoPostre();
        semMostradorPostre.acquire();
        Interfaz.conseguiPostre();
        Thread.sleep(obtenerValorRandomEntre(1000, 2500));
        // Se sirve postre
        Interfaz.conseguiPostre();
        semMostradorPostre.release();

    }

    private void pagaEnCaja(int tipoUso) throws InterruptedException {
    	Interfaz.esperandoCaja();
        semCaja.acquire();
        Interfaz.pagandoEnCaja();
        Thread.sleep(obtenerValorRandomEntre(500, 5000));
        // Paga en caja
        Interfaz.terminoCaja();
        semCaja.release();
    }

    private int obtenerValorRandomEntre(int n, int m) {
        // valor random entre n y m
        return random.nextInt(m - n) + n;

    }
}
