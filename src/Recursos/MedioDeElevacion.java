package Recursos;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import interfaz.Interfaz;

/**
 * Recurso compartido utilizando CyclicBarrier (Versión Limpia).
 * La barrera agrupa a los esquiadores Y maneja el viaje.
 */
public class MedioDeElevacion {

	private AtomicInteger usos; // Contador de viajes completos
    private final int cantMolinetes; // Capacidad de la aerosilla (n)
    private final CyclicBarrier barreraGrupo;
    private final int segundoMaximosEspera = 40; // Tiempo de espera para formar grupo
    private final int msTiempoDeViaje = 500; // Duración del viaje

    public MedioDeElevacion(int n) {
        this.cantMolinetes = n;
        usos = new AtomicInteger(0);

        this.barreraGrupo = new CyclicBarrier(n, () -> {
            // Esta es la acción de "viaje" del grupo completo.
            try {
                Interfaz.grupoCompletoViaja(cantMolinetes);
                Interfaz.viajandoEnMedio();
                Thread.sleep(msTiempoDeViaje);
                Interfaz.ultimaPersonaBaja(); // El viaje terminó
                this.incrementarUso(); // Contabiliza el viaje
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
    }

    /**
     * Lo llama el hilo Persona.
     */
    public boolean subir() throws InterruptedException {
        // 1. La persona llega al molinete (la barrera).
    	Interfaz.esperandoGrupo();
        Interfaz.llegadaMolinete(barreraGrupo.getNumberWaiting(), cantMolinetes);
        try {
            barreraGrupo.await(segundoMaximosEspera, TimeUnit.SECONDS);
            Interfaz.personaSeBajo();
            return true;

        } catch (TimeoutException e) {
            // 4. El hilo se cansó de esperar (cumple el enunciado).
            Interfaz.esperandoMuchoTiempo();
            return false;
        } catch (BrokenBarrierException e) {
            // 5. Otro hilo se fue por Timeout. Este hilo no subió.
        	Interfaz.esperandoMuchoTiempo();
            return false;
        }
    }

    // --- Métodos de Ayuda (sin cambios) ---

    private int incrementarUso() {
        return usos.addAndGet(cantMolinetes);
    }
    public void reiniciarUso() {
        usos.set(0);
    }
    public int getUsos() {
        return usos.get();
    }
    public boolean estaEnUso() {
    	return true;
    }
}