package com.example.biblioteca.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.biblioteca.R;

public class PreferenciasActivity extends ActividadGlobal {


    /*
        Actividad que muestra el menú de preferencias. Contiene:

        - PreferenceFragment con las opciones del menú de preferencias
        - Un botón para volver a la actividad desde la que se había llamado aquí



     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Preparar actividad

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_preferencias);
        // No se settea la toolbar en esta actividad



        // Settear el layout segúl la orientación y el idioma (implementación en ActividadGlobal)

        super.setLayout(R.id.contenedor_preferencias); // Método de ActividadGlobal
        super.setIdioma();

        // Recoger la clase de la instancia que llamó para devolver la ejecución ahí después

        Class<AppCompatActivity> llamo = (Class<AppCompatActivity>) super.getIntent().getExtras().get("actividadQueLlama");
        super.getIntent().putExtra("actividadQueLlama",llamo );


    }

    @Override
    protected void onStart() {
        // Debido a que el fragmento no está debidamente inicializado en onCreate()
        // seguimos con la inicialización de los paneles del fragment aquí

        super.onStart();

        // Recoger el fragmento (exclusivo según la orientación del dispositivo)

        View fragmento = getSupportFragmentManager().findFragmentById(R.id.contenedor_preferencias).getView();

        // Darle el estilo

        this.setEstilo(fragmento);

        // Dar listener a botón para volver

        fragmento.findViewById(R.id.botonPreferenciasVolver).setOnClickListener(new Listener());
    }


    private void setEstilo(View fragmento) {

        // Dar estilo según las preferencias del usuario

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;
        viewGroup.getChildAt(0).setBackgroundColor(Color.WHITE);

        if (estilo.contentEquals("1")) {

            // Estilo día

            fragmento.setBackgroundColor(Color.WHITE);
            Button boton = (Button) viewGroup.getChildAt(1);
            boton.setBackgroundColor(Color.GRAY);
            boton.setTextColor(Color.BLACK);

        } else {

            // Estilo neón

            fragmento.setBackgroundColor(Color.BLACK);
            Button boton = (Button) viewGroup.getChildAt(1);
            boton.setBackgroundColor(Color.rgb(0,100,0));
            boton.setTextColor(Color.WHITE);
        }


    }

    private class Listener implements View.OnClickListener {

        // Listener del botón para volver

        @Override
        public void onClick(View v) {

            // Si se da al botón, volver a la actividad que la llamó.

            Intent intent = new Intent(PreferenciasActivity.this, (Class<AppCompatActivity>)PreferenciasActivity.super.getIntent().getExtras().get("actividadQueLlama"));
            PreferenciasActivity.super.startActivity(intent.putExtras(PreferenciasActivity.super.getIntent().getExtras()));
            PreferenciasActivity.super.finish();

        }
    }
}