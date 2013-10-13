package com.example.asteroides;

import java.util.Vector;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * Vista que permite mostrar todos los elementos del juego.
 */
public class VistaJuego extends View {

	// //// ASTEROIDES //////
	private Drawable drawableAsteroide[] = new Drawable[3];
	private Vector<Grafico> asteroides; // Vector con los Asteroides
	private int numAsteroides = 5; // Número inicial de asteroides
	private int numFragmentos = 3; // Fragmentos en que se divide

	// //// NAVE //////
	private Grafico nave; // Gráfico de la nave
	private int giroNave; // Incremento de dirección
	private float aceleracionNave; // aumento de velocidad
	// Incremento estándar de giro y aceleración
	private static final int PASO_GIRO_NAVE = 5;
	private static final float PASO_ACELERACION_NAVE = 0.5f;
	public static final int MAX_VELOCIDAD_NAVE = 20;

	// //// MISIL //////
	private Drawable drawableMisil;
	private Grafico misil;
	private static int PASO_VELOCIDAD_MISIL = 12;
	private int tiempoMisil;

	// //// THREAD Y TIEMPO //////
	// Thread encargado de procesar el juego
	private ThreadJuego thread = new ThreadJuego();
	// Cada cuanto queremos procesar cambios (ms)
	private static int PERIODO_PROCESO = 50;
	// Cuando se realizó el último proceso
	private long ultimoProceso = 0;

	// SENSORES
	private float mX = 0, mY = 0;
	private boolean disparo = false;

	public VistaJuego(Context context, AttributeSet attrs) {
		super(context, attrs);
		Drawable drawableNave = null;

		// Obtenemos el valor de las opciones
		SharedPreferences pref = context.getSharedPreferences(
				"com.example.asteroides_preferences", Context.MODE_PRIVATE);
		numAsteroides = Integer.parseInt(pref.getString("asteroides", "5"));
		numFragmentos = Integer.parseInt(pref.getString("fragmentos", "3"));

		// Gráficos vectoriales
		if (pref.getString("graficos", "1").equals("0")) { // Gráficos vectoriales
			desactivarAceleracionHardware();

			Path pathAsteroide = new Path();
			pathAsteroide.moveTo((float) 0.3, (float) 0.0);
			pathAsteroide.lineTo((float) 0.6, (float) 0.0);
			pathAsteroide.lineTo((float) 0.6, (float) 0.3);
			pathAsteroide.lineTo((float) 0.8, (float) 0.2);
			pathAsteroide.lineTo((float) 1.0, (float) 0.4);
			pathAsteroide.lineTo((float) 0.8, (float) 0.6);
			pathAsteroide.lineTo((float) 0.9, (float) 0.9);
			pathAsteroide.lineTo((float) 0.8, (float) 1.0);
			pathAsteroide.lineTo((float) 0.4, (float) 1.0);
			pathAsteroide.lineTo((float) 0.0, (float) 0.6);
			pathAsteroide.lineTo((float) 0.0, (float) 0.2);
			pathAsteroide.lineTo((float) 0.3, (float) 0.0);

			for (int i = 0; i < numAsteroides; i++) {
				ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(
						pathAsteroide, 1, 1));
				dAsteroide.getPaint().setColor(Color.WHITE);
				dAsteroide.getPaint().setStyle(Style.STROKE);
				dAsteroide.setIntrinsicWidth(50 - i * 14);
				dAsteroide.setIntrinsicHeight(50 - i * 14);
				drawableAsteroide[i] = dAsteroide;
			}

			// TODO 18: Crear el path y el drawable para la nave

			// TODO 19: Crear Crear el path y el drawable misil

			setBackgroundColor(Color.BLACK);
		} else { // Gráficos por bitmap
			drawableAsteroide[0] = context.getResources().getDrawable(
					R.drawable.asteroide1);
			drawableAsteroide[1] = context.getResources().getDrawable(
					R.drawable.asteroide2);
			drawableAsteroide[2] = context.getResources().getDrawable(
					R.drawable.asteroide3);

			// TODO 07: Asignar las drawable para la nave
			drawableNave = context.getResources().getDrawable(R.drawable.nave);

			// TODO 15: Asignar las drawable para el misil
			drawableMisil = context.getResources().getDrawable(
					R.drawable.misil1);
		}

		asteroides = new Vector<Grafico>();
		for (int i = 0; i < numAsteroides; i++) {
			Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
			asteroide.setIncY(Math.random() * 4 - 2);
			asteroide.setIncX(Math.random() * 4 - 2);
			asteroide.setAngulo((int) (Math.random() * 360));
			asteroide.setRotacion((int) (Math.random() * 8 - 4));
			asteroides.add(asteroide);
		}

		// TODO 08: Crear el objeto Gráfico para la nave
		nave = new Grafico(this, drawableNave);


	}

	@Override
	protected void onSizeChanged(int ancho, int alto, int ancho_anter,
			int alto_anter) {
		super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);

		// Una vez que conocemos nuestro ancho y alto.

		// TODO 09: Asignar el tamaño a la nave 
		nave.setCenX((ancho - nave.getAncho()) / 2);
		nave.setCenY((alto - nave.getAlto()) / 2);

		for (Grafico asteroide : asteroides) {
			// TODO 14: Hacer que no se pinten asteroides sobre la nave
			do {
				asteroide.setCenX((int) (Math.random() * (ancho - asteroide
						.getAncho())));
				asteroide.setCenY((int) (Math.random() * (alto - asteroide
						.getAlto())));
			} while (asteroide.distancia(nave) < (ancho + alto) / 5);
		}

		ultimoProceso = System.currentTimeMillis();
		thread.start();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// TODO 10: Pintar nave
		nave.dibujaGrafico(canvas);

		for (Grafico asteroide : asteroides) {
			asteroide.dibujaGrafico(canvas);
		}

		// TODO 16: Pintar misil
		if (misil != null)
			misil.dibujaGrafico(canvas);

	}

	protected synchronized void actualizaFisica() {
		long ahora = System.currentTimeMillis();
		// No hagas nada si el período de proceso no se ha cumplido.
		if (ultimoProceso + PERIODO_PROCESO > ahora) {
			return;
		}
		// Para una ejecución en tiempo real calculamos retardo
		double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
		ultimoProceso = ahora; // Para la próxima vez
		// Actualizamos velocidad y dirección de la nave a partir de
		// giroNave y aceleracionNave (según la entrada del jugador)

		// TODO 11: Actualizar posicion de la nave
		nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
		double nIncX = nave.getIncX() + aceleracionNave
				* Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
		double nIncY = nave.getIncY() + aceleracionNave
				* Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
		// Actualizamos si el módulo de la velocidad no excede el máximo
		if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIDAD_NAVE) {
			nave.setIncX(nIncX);
			nave.setIncY(nIncY);
		}
		// Actualizamos posiciones X e Y

		// TODO 12: Incrementar posición nave
		nave.incrementaPos(retardo);

		for (Grafico asteroide : asteroides) {
			asteroide.incrementaPos(retardo);
		}

		// Actualizamos posición de misil
		if (misil != null) {
			misil.incrementaPos(retardo);
			tiempoMisil -= retardo;
			if (tiempoMisil >= 0) {
				for (int j = 0; j < asteroides.size(); j++) {
					if (misil.verificaColision(asteroides.elementAt(j))) {
						destruyeAsteroide(j);
						misil = null;
						break;
					}
				}
			}
		}

		// TODO 13: Verificar colision asteroide-nave
		for (Grafico asteroide : asteroides) {
			if (asteroide.verificaColision(nave)) {
				salir();
			}
		}

	}

	private void destruyeAsteroide(int i) {
		int tam;

		if (asteroides.get(i).getDrawable() != drawableAsteroide[2]) {
			tam = asteroides.get(i).getDrawable() == drawableAsteroide[1] ? 2
					: 1;
			for (int n = 0; n < numFragmentos; n++) {
				Grafico asteroide = new Grafico(this, drawableAsteroide[tam]);
				asteroide.setCenX(asteroides.get(i).getCenX());
				asteroide.setCenY(asteroides.get(i).getCenY());
				asteroide.setIncX(Math.random() * 7 - 2 - tam);
				asteroide.setIncY(Math.random() * 7 - 2 - tam);
				asteroide.setAngulo((int) (Math.random() * 360));
				asteroide.setRotacion((int) (Math.random() * 8 - 4));
				asteroides.add(asteroide);
			}
		}
		asteroides.remove(i);

		if (asteroides.isEmpty()) {
			salir();
		}
	}

	private void ActivaMisil() {

		// TODO 17: Activar misil
		misil = new Grafico(this, drawableMisil);
		misil.setCenX(nave.getCenX());
		misil.setCenY(nave.getCenY());
		misil.setAngulo(nave.getAngulo());
		misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo()))
				* PASO_VELOCIDAD_MISIL);
		misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo()))
				* PASO_VELOCIDAD_MISIL);

		tiempoMisil = (int) Math.min(
				this.getWidth() / Math.abs(misil.getIncX()), this.getHeight()
				/ Math.abs(misil.getIncY())) - 2;

	}


	@Override
	public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
		super.onKeyDown(codigoTecla, evento);

		// Suponemos que vamos a procesar la pulsación
		boolean procesada = true;
		switch (codigoTecla) {
		case KeyEvent.KEYCODE_DPAD_UP:
			aceleracionNave = +PASO_ACELERACION_NAVE;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			giroNave = -PASO_GIRO_NAVE;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			giroNave = +PASO_GIRO_NAVE;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			ActivaMisil();
			break;
		default:
			// Si estamos aquí, no hay pulsación que nos interese
			procesada = false;
			break;
		}
		return procesada;
	}

	@Override
	public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
		super.onKeyUp(codigoTecla, evento);

		// Suponemos que vamos a procesar la pulsación
		boolean procesada = true;
		switch (codigoTecla) {
		case KeyEvent.KEYCODE_DPAD_UP:
			aceleracionNave = 0;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			giroNave = 0;
			break;
		default:
			// Si estamos aquí, no hay pulsación que nos interese
			procesada = false;
			break;
		}
		return procesada;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			disparo = true;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dy < 6 && dx > 6) {
				giroNave = Math.round((x - mX) / 2);
				disparo = false;
			} else if (dx < 6 && dy > 6) {
				aceleracionNave = Math.round((mY - y) / 25);
				disparo = false;
			}
			if (aceleracionNave < 0)
				aceleracionNave = 0;
			break;
		case MotionEvent.ACTION_UP:
			giroNave = 0;
			aceleracionNave = 0;
			if (disparo) {
				ActivaMisil();
			}
			break;
		}
		mX = x;
		mY = y;
		return true;
	}

	private void salir() {
		((Activity) getContext()).finish();
	}



	// Thread para pintar cada X segundos
	class ThreadJuego extends Thread {

		private boolean pausa, corriendo;

		public synchronized void pausar() {
			pausa = true;
		}

		public synchronized void reanudar() {
			pausa = false;
			notify();
		}

		public void detener() {
			corriendo = false;
			if (pausa)
				reanudar();
		}

		@Override
		public void run() {
			corriendo = true;
			while (corriendo) {
				actualizaFisica();
				synchronized (this) {
					while (pausa) {
						try {
							wait();
						} catch (Exception e) {
						}
					}
				}
			}
		}
	}

	public ThreadJuego getThread() {
		return thread;
	}


	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void desactivarAceleracionHardware() {
		if (android.os.Build.VERSION.SDK_INT >= 11) {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		}
	}
}