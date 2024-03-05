package com.example.biblioteca.actividades;

import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.biblioteca.R;
import com.example.biblioteca.clasesExtra.BaseDeDatos;


public class DatosLibroEnVentaActivity extends ActividadGlobal {

        /*
        Actividad que muestra los datos de un libro en venta. Tiene:

        - TextViews en los que está escrito lo siguiente:

          "Titulo", [Título del libro],
          "Descripción", [Descripción del libro],
          "Autor", [Autor del libro],


        - Un botón para volver al listado de libros en venta

        - Solo si el layout es landscape, entonces también um ImageView con la portada del libro



     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Crear la actividad en sí

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_datos_libro_en_venta);

        // Settear toolbar

        super.setSupportActionBar(findViewById(R.id.toolbar));

        // Settear idioma y layout según la orientación (Implementación en ActividadGlobal)

        super.setIdioma();
        super.setLayout(R.id.contenedor_datos_libro_en_venta);

        // Guardar el ID del libro en venta que se está mostrando (para no perderla en giros / redirecciones
        // al menú de preferencias)
        super.getIntent().putExtra("idLibro", super.getIntent().getExtras().getInt("idLibro"));


    }

    private void setEstilo(View fragmento) {

        // Pintar los distintos elementos de la UI según el estilo elegido

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;


        if (estilo.contentEquals("1")) {

            // Estilo día

            fragmento.setBackgroundColor(Color.WHITE);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento del UI
                View elemento = viewGroup.getChildAt(i);


                if (elemento instanceof Button) {

                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.GRAY);
                    boton.setTextColor(Color.BLACK);



                } else if (elemento instanceof TextView) {


                    TextView texto = (TextView) elemento;
                    texto.setTextColor(Color.rgb(0,0,0));

                    int id = texto.getId();
                    if (id == R.id.tituloVenta || id == R.id.autorVenta || id == R.id.descripcionVenta) {
                        texto.setTextSize(15);


                    } else {
                        texto.setTextSize(20);
                        texto.setTypeface(null, Typeface.BOLD);
                    }

                }



            }



        } else {

            // Estilo neón

            fragmento.setBackgroundColor(Color.BLACK);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento del UI
                View elemento = viewGroup.getChildAt(i);


                if (elemento instanceof Button) {

                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.rgb(0,100,0));
                    boton.setTextColor(Color.WHITE);




                } else if (elemento instanceof TextView) {
                    TextView texto = (TextView) elemento;
                    texto.setTextColor(Color.WHITE);

                    int id = texto.getId();
                    if (id == R.id.tituloVenta || id == R.id.autorVenta || id == R.id.descripcionVenta) {
                        texto.setTextSize(15);


                    } else {
                        texto.setTextSize(20);
                        texto.setTypeface(null, Typeface.BOLD);
                    }

                }



            }


        }
    }

    @Override
    protected void onStart() {
        // Debido a que el fragmento no está debidamente inicializado en onCreate()
        // seguimos con la inicialización de los paneles del fragment aquí

        super.onStart();

        // Hacer llamada a BD para obtener los datos del libro en venta

        BaseDeDatos bd = new BaseDeDatos(this, "MiBaseDeDatos", null, 1);
        int pos = super.getIntent().getExtras().getInt("idLibro");
        super.añadirIntegerAlIntentPadre("idLibro", pos); // La actividad de preferencias se llama desde ActividadGlobal. Para no perder info pasarle el id de lo que se quiere mostrar para recuperarlo a la vuelta del menú de preferencias
        String[] datos = bd.obtenerDatosDeLibroEnVenta(pos);

        // Obtener el fragmento con el layout en la orientación correcta

        View fragmento = getSupportFragmentManager().findFragmentById(R.id.contenedor_datos_libro_en_venta).getView();

        // Settear el estilo

        this.setEstilo(fragmento);

        // La descripción del libro está en dos campos en BD (uno por idioma)
        // Obtener las preferencias del usuario para saber qué descripción mostrar

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idioma","es");

        if (idioma.contentEquals("es")) {
            ((TextView) fragmento.findViewById(R.id.descripcionVenta)).setText(datos[2]);

        } else {
            ((TextView) fragmento.findViewById(R.id.descripcionVenta)).setText(datos[3]);

        }

        // Mostrar el resto de datos relevantes

        ((TextView) fragmento.findViewById(R.id.tituloVenta)).setText(datos[0]);
        ((TextView) fragmento.findViewById(R.id.autorVenta)).setText(datos[1]);

        // Dar listener de volver al botón

        fragmento.findViewById(R.id.volverVenta).setOnClickListener(new Listener());


        // Si el movil está en landscape, se debe añadir la imagen también al fragment

        if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {


            Integer[] listaImagenes = {R.drawable.libro0, R.drawable.libro1, R.drawable.libro2, R.drawable.libro3};
            ((ImageView) super.findViewById(R.id.datosImagenLibroEnVenta)).
                    setImageDrawable(super.getResources().getDrawable(listaImagenes[pos]));


        }

    }

    private class Listener implements View.OnClickListener {

        // Listener del botón para volver a la lista de libros en venta

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DatosLibroEnVentaActivity.this, LibrosEnVentaActivity.class);
            DatosLibroEnVentaActivity.super.startActivity(intent);
            DatosLibroEnVentaActivity.super.finish();
        }
    }

}