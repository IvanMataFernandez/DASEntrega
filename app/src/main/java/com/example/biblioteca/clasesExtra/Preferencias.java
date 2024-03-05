package com.example.biblioteca.clasesExtra;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;



import androidx.annotation.Nullable;

import androidx.preference.PreferenceFragmentCompat;

import com.example.biblioteca.R;
import com.example.biblioteca.actividades.ActividadGlobal;



public class Preferencias extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    /*
        Fragmento que guarda el menú de preferencias en sí. Contiene:

          - ListPreference que edita el idioma a usar
          - ListPreference que edita el estilo a usar


        A diferencia del resto de layouts que se guardan en res/layout. Este layout está en res/xml

     */


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        super.addPreferencesFromResource(R.xml.pref_config);
    }





    @Override
    public void onResume() {
        super.onResume();
        super.getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        super.getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {

        // Si se ha hecho algún cambio en las preferencias, forzar la recarga de la actividad para ver
        // los cambios

        Intent intent = new Intent(super.getActivity(), super.getActivity().getClass());
        intent.putExtra("actividadQueLlama", (Class<ActividadGlobal>) super.getActivity().getIntent().getExtras().get("actividadQueLlama"));

        super.getActivity().startActivity(intent);
        super.getActivity().finish();




    }
}
