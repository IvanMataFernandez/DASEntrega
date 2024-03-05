package com.example.biblioteca.actividades;

import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.biblioteca.R;
import com.example.biblioteca.clasesExtra.ArrayAdapterDeLibrosEnAlquiler;
import com.example.biblioteca.clasesExtra.BaseDeDatos;

import java.util.ArrayList;

public class LibrosAlquiladosActivity extends ActividadGlobal {


    /*
        Actividad que muestra los libros alquilados. Tiene:

        - ListView de Cardviews. Info de layout de CardView en clasesExtra.ArrayAdapterDeLibrosEnAlquiler
        - Botón para volver al menú principal



     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Crear la actividad en sí

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_libros_alquilados);

        // Darle la toolbar

        super.setSupportActionBar(findViewById(R.id.toolbar));

        // Settear el fragment según la orientación y el idioma (implementación en ActividadGlobal)

        super.setIdioma();
        super.setLayout(R.id.contenedor_libros_alquilados); // Método de ActividadGlobal
    }

    @Override
    protected void onStart() {
        // Debido a que el fragmento no está debidamente inicializado en onCreate()
        // seguimos con la inicialización de los paneles del fragment aquí

        super.onStart();

        // Obtener el fragmento del layout

        View fragmento = getSupportFragmentManager().findFragmentById(R.id.contenedor_libros_alquilados).getView();

        // Darle el estilo

        this.setEstilo(fragmento);

        // Buscar el listview

        ListView listView = fragmento.findViewById(R.id.listaAlquileres);

        // Decidir que layout de cardview darle según la orientación del dispositivo

        int layout;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layout = R.layout.cardview_libro_en_alquiler_landscape;
        } else {
            layout = R.layout.cardview_libro_en_alquiler_portrait;
        }

        // Hacer llamada a BD para obtener los títulos de los libros rentados para mostrarlos

        BaseDeDatos bd = new BaseDeDatos(this, "MiBaseDeDatos", null, 1);
        ArrayList<String> listaTitulos = bd.obtenerTitulosDeMisLibros();
        System.out.println(listaTitulos.size());

        String[] listaValores = listaTitulos.toArray(new String[listaTitulos.size()]);

        // Crear el listview en sí

        ArrayAdapterDeLibrosEnAlquiler adapter = new ArrayAdapterDeLibrosEnAlquiler(super.getApplicationContext(),listaValores, layout, this);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);


        // Darle listener al botón para volver al menú principal

        fragmento.findViewById(R.id.volverDeAlquileres).setOnClickListener(new Listener());


    }

    private void setEstilo(View fragmento) {

        // Settear estilo según las preferencias del usuario

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;

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

        // Listener del botón para volver al menú principal

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LibrosAlquiladosActivity.this, MainActivity.class);
            LibrosAlquiladosActivity.super.startActivity(intent);
            LibrosAlquiladosActivity.super.finish();
        }
    }

}