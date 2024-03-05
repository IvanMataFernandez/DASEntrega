package com.example.biblioteca.actividades;

import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.biblioteca.R;
import com.example.biblioteca.clasesExtra.DialogoDeSalida;

public class MainActivity extends ActividadGlobal {



    /*

        Acerca de la implementación de las activities

        - Cualquier Activity que extienda de ActividadGlobal puede implementar
          la actionBar muy sencillamente usando el método
          super.setSupportActionBar(findViewById(R.id.toolbar));  siempre que
          esté definido un actionBar con id 'toolbar' en el layout ORIGINAL
          de la actividad (o sea en activity_main, NO en activity_main_landscape).


        - Cualquier Activity que extienda de Actividad global puede implementar
          un layout de cada orientación muy sencillamente usando el método
          super.setLayout(R.id.contenedor_activity); siempre que se defina en
          ActividadGlobal el mapeo en sus dos HM al layout correspondiente y se
          incluya un Fragment que ocupe toda la pantalla (excluyendo toolbar si
          implementa tmb).

        - Las acciones de los layouts (de los fragments) están en la actividad
          en vez de en el fragment porque por cada activitidad los dos fragments
          que pueden crear tienen los mismos elementos y no tiene sentido crear
          más clases en las que se repiten las funcionalidades. En vez de tener más
          clases fragment, en su lugar se pone la funcionalidad en la actividad porque
          cada par de fragments hacen lo mismo que la actividad que los llama.

        - Los layouts que no acaban en portrait.xml / landscape.xml son layouts de actividad.
          Los que acaban en uno de esos dos son layouts de fragments que usan las actividades.

     */



    /* Esta es la actividad del menú principal. Tiene 3 botones:
           - Ver libros en venta
           - Ver libros rentados
           - Salir de la app
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Preparar actividad

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        // Vincular toolbar

        super.setSupportActionBar(findViewById(R.id.toolbar));

        // Settear el idioma y el fragment según la orientación del dispositivo (implementado en ActividadGlobal)

        super.setLayout(R.id.contenedor_main);
        super.setIdioma();





    }

    private void setEstilo(View fragmento) {

        // Pintar los distintos elementos de la UI según el estilo elegido

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;

        if (estilo.contentEquals("1")) {
            // Estilo día

            fragmento.setBackgroundColor(Color.WHITE);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento de la UI

                View elemento = viewGroup.getChildAt(i);
                Button boton = (Button) elemento;
                boton.setBackgroundColor(Color.GRAY);
                boton.setTextColor(Color.BLACK);

            }



        } else {
            // Estilo neón

            fragmento.setBackgroundColor(Color.BLACK);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento de la UI
                View elemento = viewGroup.getChildAt(i);
                Button boton = (Button) elemento;
                boton.setBackgroundColor(Color.rgb(0,100,0));
                boton.setTextColor(Color.WHITE);
            }


        }
    }

    @Override
    protected void onStart() {
        // Debido a que el fragmento no está debidamente inicializado en onCreate()
        // seguimos con la inicialización de los paneles del fragment aquí

        super.onStart();

        // Obtener el fragmento con el layout en la orientación correcta

        View fragmento = getSupportFragmentManager().findFragmentById(R.id.contenedor_main).getView();

        // Cambiar su estilo

        this.setEstilo(fragmento);

        // Dar listeners a botones

        ((Button)fragmento.findViewById(R.id.entrarRentar)).setOnClickListener(new Listener(0));
        ((Button)fragmento.findViewById(R.id.entrarTusLibros)).setOnClickListener(new Listener(1));
        ((Button)fragmento.findViewById(R.id.salir)).setOnClickListener(new Listener(2));



    }



    private class Listener implements View.OnClickListener {

        private int index;

        public Listener(int index) {
            this.index = index;

        }

        @Override
        public void onClick(View v) {

            // Se llama aquí cuando se pincha un botón del menú principal

            Intent intent = null;
            switch (this.index) {
                case 0:
                    // El botón de arriba llama a la actividad de libros en venta
                    intent = new Intent(MainActivity.this, LibrosEnVentaActivity.class);
                    break;
                case 1:
                    // El botón del medio llama a la actividad de mis libros rentados
                    intent = new Intent(MainActivity.this, LibrosAlquiladosActivity.class);
                    break;
                case 2:
                    // El botón de abajo nos mantiene en la misma actividad pero crea el dialogo de confirmación
                    // de salida de la app
                    DialogoDeSalida dialogoalerta = new DialogoDeSalida();
                    dialogoalerta.show(MainActivity.super.getSupportFragmentManager(), "etiqueta");
                    break;
            }
            if (intent != null) {
                MainActivity.super.startActivity(intent);
                MainActivity.super.finish();


            }



        }
    }

}

//