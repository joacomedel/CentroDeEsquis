package interfaz;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import Recursos.MedioDeElevacion;

/**
 * Interfaz Gráfica para el Simulador del Complejo Invernal.
 *
 * @version 8.0 - Paneles de Espera y Acción
 * Esta versión distingue visualmente entre las zonas de espera (colas) y las
 * zonas donde se realiza la acción correspondiente.
 */
public class Interfaz {

	public static final boolean MODO_CONSOLA = true;
	public static final boolean MODO_GRAFICO = true;

	private static JFrame frame;
	private static PanelSimulacion panelSimulacion;
	private static final Map<String, EntidadVisual> entidadesVisuales = new ConcurrentHashMap<>();
	private static final Map<String, GestorDeZona> gestoresDeZona = new ConcurrentHashMap<>();

	// ... [Clases Internas: EntidadVisual, PanelSimulacion, GestorDeZona no necesitan cambios] ...
	private static class EntidadVisual {
		private int x, y;
		private final int size = 14;
		private Color color;
		private final String nombre;
		private String zonaActual = "";
		public EntidadVisual(String n, Color c) { this.nombre = n; this.color = c; }
		public void setPosicion(int x, int y) { this.x = x; this.y = y; }
		public void setColor(Color c) { this.color = c; }
		public void setZonaActual(String z) { this.zonaActual = z; }
		public String getZonaActual() { return this.zonaActual; }
		public void dibujar(Graphics2D g2d) {
			g2d.setColor(color);
			g2d.fill(new Ellipse2D.Double(x, y, size, size));
			String numero = nombre.substring(nombre.lastIndexOf('-') + 1);
			Font font = new Font("Arial", Font.BOLD, 9);
			g2d.setFont(font);
			FontMetrics fm = g2d.getFontMetrics();
			int textoAncho = fm.stringWidth(numero);
			int textoX = x + (size - textoAncho) / 2;
			int textoY = y + (size - fm.getAscent()) / 2 + fm.getAscent();
			double brillo = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
			g2d.setColor(brillo > 128 ? Color.BLACK : Color.WHITE);
			g2d.drawString(numero, textoX, textoY);
		}
	}
	private static class PanelSimulacion extends JPanel {
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			this.setBackground(new Color(240, 248, 255));
			gestoresDeZona.values().forEach(zona -> zona.dibujar(g2d, this));
			entidadesVisuales.values().forEach(entidad -> entidad.dibujar(g2d));
		}
	}
	private static class GestorDeZona {
		private final String nombre;
		private final double relX, relY, relW, relH;
		private final List<String> ocupantes = new ArrayList<>();
		private final int padding = 15, spacing = 18;
		public GestorDeZona(String n, double rx, double ry, double rw, double rh) { this.nombre = n; this.relX = rx; this.relY = ry; this.relW = rw; this.relH = rh; }
		private Rectangle2D.Double getAreaAbsoluta(JPanel p) { return new Rectangle2D.Double(relX * p.getWidth(), relY * p.getHeight(), relW * p.getWidth(), relH * p.getHeight()); }
		public synchronized int[] getPosicion(String nombreEntidad, JPanel p) {
			if (!ocupantes.contains(nombreEntidad)) ocupantes.add(nombreEntidad);
			int index = ocupantes.indexOf(nombreEntidad);
			Rectangle2D.Double area = getAreaAbsoluta(p);
			int startX = (int) area.getX() + padding;
			int startY = (int) area.getY() + padding + 10;
			int itemsPorFila = Math.max(1, ((int) area.getWidth() - padding) / spacing);
			int fila = index / itemsPorFila;
			int col = index % itemsPorFila;
			return new int[]{startX + (col * spacing), startY + (fila * spacing)};
		}
		public synchronized void remover(String nombreEntidad) { ocupantes.remove(nombreEntidad); }
		public void dibujar(Graphics2D g2d, JPanel p) {
			Rectangle2D.Double area = getAreaAbsoluta(p);
			g2d.setColor(new Color(220, 220, 220));
			g2d.draw(area);
			g2d.setColor(Color.DARK_GRAY);
			g2d.setFont(new Font("Arial", Font.BOLD, 12));
			g2d.drawString(nombre, (int) area.getX() + 5, (int) area.getY() + 15);
		}
	}

	public static void crearYMostrarGUI() {
		if (MODO_GRAFICO) {
			SwingUtilities.invokeLater(() -> {
				frame = new JFrame("Complejo Invernal 'Caída Rápida'");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// CAMBIO: Nueva distribución de paneles para separar Espera de Acción
				// Fila Superior: Actividades principales
				gestoresDeZona.put("ESPERA_ENTRADA",  new GestorDeZona("Cola Entrada",       0.01, 0.02, 0.15, 0.45));
				gestoresDeZona.put("ESPERA_MEDIO_A",  new GestorDeZona("Cola Medio A",         0.17, 0.02, 0.15, 0.30));
				gestoresDeZona.put("VIAJANDO_MEDIO_A",new GestorDeZona("Viajando Medio A",     0.17, 0.35, 0.15, 0.12));
				gestoresDeZona.put("ESPERA_CLASES",   new GestorDeZona("Espera Clases",        0.33, 0.02, 0.15, 0.30));
				gestoresDeZona.put("EN_CLASE",        new GestorDeZona("En Clase",             0.33, 0.35, 0.15, 0.12));
				gestoresDeZona.put("ESQUIANDO",       new GestorDeZona("Pistas de Ski",        0.50, 0.02, 0.49, 0.45));
				
				// Fila Inferior: Confitería
				gestoresDeZona.put("ESPERA_CAJA",     new GestorDeZona("Cola Caja",            0.01, 0.50, 0.15, 0.48));
				gestoresDeZona.put("PAGANDO_CAJA",    new GestorDeZona("Pagando",              0.17, 0.50, 0.10, 0.48));
				gestoresDeZona.put("ESPERA_COMIDA",   new GestorDeZona("Cola Comida",          0.28, 0.50, 0.15, 0.48));
				gestoresDeZona.put("SIRVIENDO_COMIDA",new GestorDeZona("Sirviendo Comida",     0.44, 0.50, 0.10, 0.48));
				gestoresDeZona.put("ESPERA_POSTRE",   new GestorDeZona("Cola Postre",          0.55, 0.50, 0.15, 0.48));
				gestoresDeZona.put("COMIENDO_MESA",   new GestorDeZona("Comiendo en Mesas",    0.71, 0.50, 0.28, 0.48));

				panelSimulacion = new PanelSimulacion();
				frame.add(panelSimulacion);
				
				panelSimulacion.addComponentListener(new ComponentAdapter() {
					public void componentResized(ComponentEvent e) {
						for (EntidadVisual entidad : entidadesVisuales.values()) {
							String zona = entidad.getZonaActual();
							if (zona != null && gestoresDeZona.containsKey(zona)) {
								int[] pos = gestoresDeZona.get(zona).getPosicion(entidad.nombre, panelSimulacion);
								entidad.setPosicion(pos[0], pos[1]);
							}
						}
					}
				});

				frame.setSize(1200, 800);
				frame.setMinimumSize(new Dimension(1000, 700));
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			});
		}
	}

	private static void actualizarEntidad(String nombreHilo, String nuevaZona, Color color) {
		if (MODO_GRAFICO) {
			SwingUtilities.invokeLater(() -> {
				if (panelSimulacion == null) return;
				EntidadVisual entidad = entidadesVisuales.computeIfAbsent(nombreHilo, k -> new EntidadVisual(k, generarColorPorNombre(k)));
				String zonaAnterior = entidad.getZonaActual();
				if (zonaAnterior != null && !zonaAnterior.equals(nuevaZona) && gestoresDeZona.containsKey(zonaAnterior)) {
					gestoresDeZona.get(zonaAnterior).remover(nombreHilo);
				}
				int[] pos = gestoresDeZona.get(nuevaZona).getPosicion(nombreHilo, panelSimulacion);
				entidad.setPosicion(pos[0], pos[1]);
				entidad.setColor(color);
				entidad.setZonaActual(nuevaZona);
				panelSimulacion.repaint();
			});
		}
	}

	private static Color generarColorPorNombre(String n) { return new Color(Color.HSBtoRGB((float) (n.hashCode() % 360) / 360f, 0.8f, 0.9f)); }

	// =========================================================================
	// --- API PÚBLICA (Lógica actualizada para los nuevos paneles) ---
	// =========================================================================
	
	public static void llegadaComplejoCerrado() {
		if (MODO_CONSOLA) System.out.println("⌚ Persona " + Thread.currentThread().getName() + " llega y espera.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESPERA_ENTRADA", Color.GRAY);
	}
	public static void entradaExitosa() {
		if (MODO_CONSOLA) System.out.println("✅ Persona " + Thread.currentThread().getName() + " ha entrado.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESQUIANDO", Color.GREEN);
	}
	public static void esperandoCaja() {
		if (MODO_CONSOLA) System.out.println("💰 Persona " + Thread.currentThread().getName() + " espera en caja.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESPERA_CAJA", Color.YELLOW);
	}
	public static void pagandoEnCaja() {
		if (MODO_CONSOLA) System.out.println("💰 Persona " + Thread.currentThread().getName() + " está pagando.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "PAGANDO_CAJA", Color.ORANGE);
	}
	public static void esperandoComidaRapida() {
		if (MODO_CONSOLA) System.out.println("🍔 Persona " + Thread.currentThread().getName() + " espera comida.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESPERA_COMIDA", Color.YELLOW);
	}
	public static void conseguiComidaRapida() {
		if (MODO_CONSOLA) System.out.println("🍔 Persona " + Thread.currentThread().getName() + " está sirviéndose comida.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "SIRVIENDO_COMIDA", Color.ORANGE);
	}
	public static void esperandoPostre() {
		if (MODO_CONSOLA) System.out.println("🍨 Persona " + Thread.currentThread().getName() + " espera postre.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESPERA_POSTRE", Color.YELLOW);
	}
	public static void comiendo() {
		if (MODO_CONSOLA) System.out.println("😋 Persona " + Thread.currentThread().getName() + " está comiendo.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "COMIENDO_MESA", Color.GREEN);
	}
	public static void terminoDeComer() {
		if (MODO_CONSOLA) System.out.println("✅ Persona " + Thread.currentThread().getName() + " ha terminado de comer.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESQUIANDO", Color.CYAN);
	}
	public static void llegadaMolinete(int uso, int cap) {
		if (MODO_CONSOLA) System.out.println("🚠 Persona " + Thread.currentThread().getName() + " entra al molinete.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESPERA_MEDIO_A", Color.BLUE);
	}
	// NUEVO MÉTODO para la acción de viajar
	public static void viajandoEnMedio() {
		if (MODO_CONSOLA) System.out.println("🚀 Persona " + Thread.currentThread().getName() + " está viajando en el medio.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "VIAJANDO_MEDIO_A", Color.CYAN);
	}
	public static void personaSeBajo() {
		if (MODO_CONSOLA) System.out.println("⏬ Persona " + Thread.currentThread().getName() + " se bajó.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESQUIANDO", Color.GREEN);
	}
	public static void alumnoEsperando() {
		if (MODO_CONSOLA) System.out.println("🙋 Alumno " + Thread.currentThread().getName() + " espera clase.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESPERA_CLASES", Color.MAGENTA);
	}
	public static void claseIniciada() {
		if (MODO_CONSOLA) System.out.println("📣 La clase ha comenzado para " + Thread.currentThread().getName());
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "EN_CLASE", Color.ORANGE);
	}
	public static void alumnoSeVa() {
		if (MODO_CONSOLA) System.out.println("🏃 Alumno " + Thread.currentThread().getName() + " se fue.");
		if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESQUIANDO", Color.DARK_GRAY);
	}

	// --- Métodos de estado general (sin cambios importantes) ---
	public static void esperandoGrupo() { if (MODO_CONSOLA) System.out.println("⏳ Persona " + Thread.currentThread().getName() + " espera al grupo."); }
	public static void esperandoMuchoTiempo() { if (MODO_CONSOLA) System.out.println("⏳ Persona " + Thread.currentThread().getName() + " se fue de la cola."); if (MODO_GRAFICO) actualizarEntidad(Thread.currentThread().getName(), "ESQUIANDO", Color.DARK_GRAY); }
	public static void grupoCompletoViaja(int cant) { if (MODO_CONSOLA) System.out.println("🚀 El medio de elevación viaja con " + cant + " personas."); }
	public static void ultimaPersonaBaja() { if (MODO_CONSOLA) System.out.println("🎉 El medio de elevación está libre."); }
	public static void instructorEsperando() { if (MODO_CONSOLA) System.out.println("👨‍🏫 Instructor " + Thread.currentThread().getName() + " espera alumnos."); }
	public static void complejoAbre(int h) { if (MODO_CONSOLA) System.out.println("🔔 Complejo abierto a las " + h + "hs."); }
	public static void complejoCierra(int h) { if (MODO_CONSOLA) System.out.println("⛔ Complejo cerrado a las " + h + "hs."); }
	public static void complejoCerrado() { if (MODO_CONSOLA) System.out.println("\n*** REPORTE FINAL ***"); }
	public static void mostrandoUsos() { if (MODO_CONSOLA) System.out.println("📊 --- REPORTE DE USO ---"); }
	public static void mostrarReporteUsos(MedioDeElevacion[] m) { if (MODO_CONSOLA) { /* Lógica de reporte */ } }
	
	// --- Métodos redundantes o remapeados (ya no se usan directamente o se llaman desde otros) ---
	public static void esperandoMesa() { esperandoCaja(); }
	public static void conseguiMesa() { comiendo(); }
	public static void terminoCaja() { esperandoComidaRapida(); }
	public static void conseguiPostre() { comiendo(); }
}