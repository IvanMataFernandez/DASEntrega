package com.example.biblioteca.actividades;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.biblioteca.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public abstract class ActividadGlobal extends AppCompatActivity {

    /* Toda actividad que no sea la de las preferencias van a tener la toolbar definida
       exáctamente igual. Esta clase define los métodos de la toolbar para no definirlos
       en todas las actividades después

       Toda actividad que no es ActividadEscritura tienen definidos dos layouts según su rotación,
       esta actividad settea el fragmento correcto según la orientación



     */



    private static HashMap<Integer, Integer> mapeoLayoutPortrait;
    private static HashMap<Integer, Integer> mapeoLayoutLandscape;
    private HashMap<String, Integer> parametrosDeIntentExtra;
    private static int layout;

    static {
        // Inicializar los HM para mappear cada contenedor de layout base al layout adecuado según orientación.
        // Esto se ejecuta al principio de la app, para inicializar las variables estáticas

        mapeoLayoutPortrait = new HashMap<Integer, Integer>();
        mapeoLayoutPortrait.put(R.id.contenedor_main, R.layout.activity_main_portrait);
        mapeoLayoutPortrait.put(R.id.contenedor_preferencias, R.layout.activity_preferencias_portrait);
        mapeoLayoutPortrait.put(R.id.contenedor_libros_en_venta, R.layout.activity_libros_en_venta_portrait);
        mapeoLayoutPortrait.put(R.id.contenedor_datos_libro_en_venta, R.layout.activity_datos_libro_en_venta_portrait);
        mapeoLayoutPortrait.put(R.id.contenedor_libros_alquilados, R.layout.activity_libros_alquilados_portrait);
        mapeoLayoutPortrait.put(R.id.contenedor_datos_libro_en_alquiler, R.layout.activity_datos_libro_en_alquiler_portrait);


        mapeoLayoutLandscape = new HashMap<Integer, Integer>();
        mapeoLayoutLandscape.put(R.id.contenedor_main, R.layout.activity_main_landscape);
        mapeoLayoutLandscape.put(R.id.contenedor_preferencias, R.layout.activity_preferencias_landscape);
        mapeoLayoutLandscape.put(R.id.contenedor_libros_en_venta, R.layout.activity_libros_en_venta_landscape);
        mapeoLayoutLandscape.put(R.id.contenedor_datos_libro_en_venta, R.layout.activity_datos_libro_en_venta_landscape);
        mapeoLayoutLandscape.put(R.id.contenedor_libros_alquilados, R.layout.activity_libros_alquilados_landscape);
        mapeoLayoutLandscape.put(R.id.contenedor_datos_libro_en_alquiler, R.layout.activity_datos_libro_en_alquiler_landscape);


    }


    // Los fragments se crean aquí, no necesito clases definidas en ficheros aparte porque cada par
    // de fragment (horizontal / vertical) vienen a hacer lo mismo y la actividad en sí puede
    // linkar las funcionalidades fácilmente para que funcione en tanto vertical como horizontal.

    // El layout cargado se elige según el atributo layout (setteado justo antes de la creación de estas clases)
    // y este atributo layout indica el contenedor que se quiere inflar. Cada contenedor apunta a dos layouts
    // de fragments usando los dos HM de arriba (uno por orientación).

    public static class OrientacionLandscape extends Fragment {


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(ActividadGlobal.mapeoLayoutLandscape.get(layout), container, false);
        }

    }

    public static class OrientacionPortrait extends Fragment {


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            return inflater.inflate(ActividadGlobal.mapeoLayoutPortrait.get(layout), container, false);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.parametrosDeIntentExtra = new HashMap<String,Integer>();
    }

    // Objeto Preferencias necesita acceder aquí, no solo clases hija. Public en vez de Protected
    public void setIdioma() {

        // Post: Settear el idioma del usuario para asegurarse de que la actividad hija
        //       se cargue en el idioma correcto

        // Recoger las preferencias del usuario

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idioma","es");
        System.out.println(idioma);
        Locale nuevaloc = new Locale(idioma);

        // Settear el idioma usando el código de eGela

        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
    }

    private void setEstiloToolbar() {

        // Cada actividad hija tiene su propio setEstilo() según los elementos de UI que se quieren
        // editar, pero todas comparten la misma toolbar, por lo que la edición de la toolbar se
        // hace aquí

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String estilo = prefs.getString("estilo","1");

        if (estilo.contentEquals("1")) {

            // Estilo día

            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setBackgroundColor(Color.GRAY); // Establece el color de fondo de la Toolbar
                toolbar.setTitleTextColor(Color.BLACK); // Color titulo

            }

        } else {

            // Estilo neón

            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setBackgroundColor(Color.GREEN); // Establece el color de fondo de la Toolbar
                toolbar.setTitleTextColor(Color.WHITE); // Color titulo

            }

        }
    }

    protected void setLayout(int contendor) {

        // Pre: El contenedor fragment de la actividad
        // Post: Se pone al fragmento el contenido del layout adecuado, según el contenedor de fragmento
        //       (identifica a la actividad que hace la llamada) y la orientación del dispositivo


        this.layout = contendor;

        if (super.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Cargar el Fragmento para pantallas grandes en modo horizontal
            super.getSupportFragmentManager().beginTransaction()
                    .replace(layout, new OrientacionLandscape())
                    .commit();
        } else {

            // Cargar el Fragmento para pantallas pequeñas en modo vertical
            super.getSupportFragmentManager().beginTransaction()
                    .replace(layout, new OrientacionPortrait())
                    .commit();
        }

        this.setEstiloToolbar();

    }

    protected void añadirIntegerAlIntentPadre(String key, Integer value) {
        this.parametrosDeIntentExtra.put(key, value);
    }

    // Métodos para la actionBar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();

        // Ver que botón de la toolbar se pulsó

        if (id == R.id.llamar) {
            // Llamar

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:123456789"));
            super.startActivity(intent);
            super.finish();

        } else {
            // Abrir menú preferencias

            // Guardar la clase de la actividad que llamó (es una de las hijas de esta clase)
            // para poder volver a esa actividad tras cerrar las preferencias

            Intent intent = new Intent(this, PreferenciasActivity.class);
            intent.putExtra("actividadQueLlama", this.getClass());

            // Guardar en el intent algun dato más que la clase hija pidió a ActivityGlobal
            // de que almacenase para mandarlo aquí e informar al menú de notificaciones de ello
            // (Generalmente es información de variables concretas de algun layout en concreto
            // como el libro que se seleccionó en la interfaz de detalles del libro antes de pedir la
            // llamada a las notificaciones)

            for (Map.Entry<String, Integer> par : this.parametrosDeIntentExtra.entrySet()) {
                intent.putExtra(par.getKey(), par.getValue());

            }


            super.startActivity(intent);
            super.finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
