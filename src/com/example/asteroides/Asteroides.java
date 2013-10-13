package com.example.asteroides;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * Actividad principal del juego. Contiene el menú principal, que da acceso al
 * resto de actividades del juego.
 */
public class Asteroides extends Activity {

	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle estadoGuardado) {
		super.onCreate(estadoGuardado);
		setContentView(R.layout.main);

		//TODO 01: Capturar el evento onClick del botón "Opciones" y llamar al método lanzarPreferencias
		// Se añade otro escuchador para el evento onClick del botón "Opciones"
		((Button) findViewById(R.id.btnConfigurar))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						lanzarPreferencias(v);
					}
				});

		//TODO 02: Capturar el evento onClick del botón "Jugar" y llamar al método lanzarJuego
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	/**
	 * Lanza la actividad Juego.
	 * 
	 * @param view
	 *            Vista que ha generado el evento para lanzar la actividad.
	 */
	public void lanzarJuego(View view) {
		Intent i = new Intent(this, Juego.class);
		startActivity(i);
	}

	/**
	 * Lanza la actividad Preferencias.
	 * 
	 * @param view
	 *            Vista que ha generado el evento para lanzar la actividad.
	 */
	public void lanzarPreferencias(View view) {
		Intent i = new Intent(this, Preferencias.class);
		startActivity(i);
	}
}
