package com.example.asteroides;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;


/**
 * Clase privada para gestionar los elementos gráficos del juego.
 */
class Grafico {
	private Drawable drawable; // Imagen que dibujaremos
	private int cenX, cenY; // Posición el centro el gráfico
	private int ancho, alto; // Dimensiones de la imagen
	private double incX, incY; // Velocidad desplazamiento
	private double angulo, rotacion;// Ángulo y velocidad rotación
	private int radioColision; // Para determinar colisión
	private int xAnterior, yAnterior; // Posición anterior
	private View view; // Usada en view.invalidate()
	private int radioInval;
	
	public Grafico(View view, Drawable drawable) {
		this.view = view;
		this.drawable = drawable;
		ancho = drawable.getIntrinsicWidth();
		alto = drawable.getIntrinsicHeight();
		radioColision = (alto + ancho) / 4;
		radioInval = (int) Math.hypot(ancho, alto)/2;
	}

	public void dibujaGrafico(Canvas canvas) {
		int x = (cenX - ancho / 2);
		int y = (cenY - alto / 2);
		drawable.setBounds(x, y, x + ancho, y + alto);
		
		// TODO 06: Activar el save y restore del canvas
		
		canvas.rotate((float) angulo, cenX, cenY);
		drawable.draw(canvas);

		view.invalidate(cenX - radioInval, cenY - radioInval,
				cenX + radioInval, cenY + radioInval);
		view.invalidate(xAnterior - radioInval, yAnterior - radioInval,
				xAnterior + radioInval, yAnterior + radioInval);

		xAnterior = cenX;
		yAnterior = cenY;
	}

	public void incrementaPos(double factor) {
		// Desplazamos el elemento.
		cenX += incX * factor;
		cenY += incY * factor;
		angulo += rotacion * factor;

		// Si salimos de la pantalla, corregimos posición
		if (cenX < -ancho / 2) {
			cenX = view.getWidth() - ancho / 2;
		}
		if (cenX > view.getWidth() - ancho / 2) {
			cenX = -ancho / 2;
		}
		if (cenY < -alto / 2) {
			cenY = view.getHeight() - alto / 2;
		}
		if (cenY > view.getHeight() - alto / 2) {
			cenY = -alto / 2;
		}
	}

	public double distancia(Grafico g) {
		return Math.hypot(cenX - g.cenX, cenY - g.cenY);
	}

	public boolean verificaColision(Grafico g) {
		return (distancia(g) < (radioColision + g.radioColision));
	}

	public void setIncX(double incX) {
		this.incX = incX;
	}

	public void setIncY(double incY) {
		this.incY = incY;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public void setRotacion(double rotacion) {
		this.rotacion = rotacion;
	}

	public void setCenX(int cenX) {
		this.cenX = cenX;
	}

	public void setCenY(int cenY) {
		this.cenY = cenY;
	}

	public int getAncho() {
		return ancho;
	}

	public int getAlto() {
		return alto;
	}

	public double getIncX() {
		return incX;
	}

	public double getIncY() {
		return incY;
	}

	public double getAngulo() {
		return angulo;
	}

	public int getCenX() {
		return cenX;
	}

	public int getCenY() {
		return cenY;
	}

	public Drawable getDrawable() {
		return drawable;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}
	
	
}
