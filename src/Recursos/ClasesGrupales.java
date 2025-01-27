package Recursos;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClasesGrupales {
    private CountDownLatch cdlEsperaAlumnos;
    private static int cantidadAlumnos = 4;

    public ClasesGrupales() {
        cdlEsperaAlumnos = new CountDownLatch(cantidadAlumnos);
    }

    public void participarClase() throws InterruptedException {
        if (cdlEsperaAlumnos.await(100, TimeUnit.SECONDS)) {

        }
    };

    public int cantidadEspera() {
        return (int) cdlEsperaAlumnos.getCount();
    }
}
