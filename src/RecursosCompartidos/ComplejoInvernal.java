package RecursosCompartidos;

import java.util.Random;

import Recursos.CentroDeClasesGrupales;
import Recursos.Confiteria;
import Recursos.Entrada;
import Recursos.MedioDeElevacion;

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
        for (int i = 0; i < mediosDeElevacion.length; i++) {
            mediosDeElevacion[i] = new MedioDeElevacion(random.nextInt(3) + 1);
        }

    }

    public void entrarConfiteria(int tipoUso) throws InterruptedException {
        confiteria.usaConfiteria(tipoUso);
    }

    public void cambiarHora(int horaActualIn) {
        entrada.cambiarHora(horaActualIn);
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

    public void subir() {

    }
}
