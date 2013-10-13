package com.example.asteroides;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Actividad que permite modificar las opciones del juego.
 */
public class Preferencias extends PreferenceActivity {
	/**
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferencias);
	}
}
