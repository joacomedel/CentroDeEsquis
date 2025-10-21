package RecursosCompartidos;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;

import Recursos.CentroDeClasesGrupales;
import Recursos.Confiteria;
import Recursos.Entrada;
import Recursos.MedioDeElevacion;
import interfaz.Interfaz;

public class ComplejoInvernal {

    private Entrada entrada;
    private Confiteria confiteria;
    private CentroDeClasesGrupales centroDeClasesGrupales;
    private final int cantMedios = 4;
    private MedioDeElevacion[] mediosDeElevacion;

    public ComplejoInvernal() {
        entrada = new Entrada();
        entrada.iniciar(this);
        confiteria = new Confiteria();
        centroDeClasesGrupales = new CentroDeClasesGrupales();
        centroDeClasesGrupales.iniciar(this);
        Random random = new Random();
        mediosDeElevacion = new MedioDeElevacion[cantMedios];
        for (int i = 0; i < mediosDeElevacion.length; i++) {
            mediosDeElevacion[i] = new MedioDeElevacion(random.nextInt(3) + 1);
        }

    }

    public void entrarConfiteria(int tipoUso) throws InterruptedException {
        confiteria.usaConfiteria(tipoUso);
    }

    public int cambiarHora(int horaActualIn) {
        return entrada.cambiarHora(horaActualIn);
    }

    public void entrarAComplejo() throws InterruptedException {
        entrada.personaIntentaEntrar();
    }

    public void participarClase() throws InterruptedException {
        centroDeClasesGrupales.participarClase();
    }

    public void instruirClase() throws InterruptedException {
        centroDeClasesGrupales.instruirClase();
    }

    public void subir() throws InterruptedException {

        boolean subio = false;
        int i = 0;
        while (!subio && i < mediosDeElevacion.length) {
            if (!mediosDeElevacion[i].estaEnUso()) {
                subio = mediosDeElevacion[i].subir();
            }
            i++;
        }
        if (!subio) {
            // si no subio y todos estaban ocupados espera en alguno random
            Random random = new Random();
            mediosDeElevacion[random.nextInt(cantMedios)].subir();
        }

    }
    public void cerrar() {
    	//Cerre al cambiar la hora , podria cerrar desde aca tmb
    	try {
    		Interfaz.complejoCerrado();
    		Interfaz.mostrarReporteUsos(mediosDeElevacion);
			this.reiniciarUsos();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    public void reiniciarUsos() throws InterruptedException {
    	for (int i = 0; i < mediosDeElevacion.length; i++) {
			mediosDeElevacion[i].reiniciarUso();
		}
    }
}
