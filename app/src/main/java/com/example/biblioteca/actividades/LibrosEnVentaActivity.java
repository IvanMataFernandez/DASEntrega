package com.example.biblioteca.actividades;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.biblioteca.R;
import com.example.biblioteca.clasesExtra.ArrayAdapterDeLibrosEnVenta;
import com.example.biblioteca.clasesExtra.BaseDeDatos;

import java.util.ArrayList;

public class LibrosEnVentaActivity extends ActividadGlobal {


    /* Esta es la actividad que muestra el listado de libros disponibles
       para comprar. Contiene:

       - ListView de CardViews. Info sobre layout de cardview en clasesExtra.ArrayAdapterDeLibrosEnVenta
       - Botón de volver a menú principal

     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Crear la actividad

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_libros_en_venta);

        // Asignarle la toolbar

        super.setSupportActionBar(findViewById(R.id.toolbar));

        // Settear el idioma y el fragment según la orientación del dispositivo (implementado en ActividadGlobal)


        super.setIdioma();
        super.setLayout(R.id.contenedor_libros_en_venta);



    }

    private void setEstilo(View fragmento) {

        // Crear el estilo según la preferencia del usuario

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

    @Override
    protected void onStart() {
        // Debido a que el fragmento no está debidamente inicializado en onCreate()
        // seguimos con la inicialización de los paneles del fragment aquí

        super.onStart();

        // Obtener el fragmento del que está compuesto el layout

        View fragmento = getSupportFragmentManager().findFragmentById(R.id.contenedor_libros_en_venta).getView();

        // Darle un estilo

        this.setEstilo(fragmento);

        // Obtener el ListView donde poner los libros disponibles

        ListView listView = fragmento.findViewById(R.id.listaVentas);

        // Cargar el estilo del cardview para cada elemento del listview según la orientación del dispositivo

        int layout;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layout = R.layout.cardview_libro_en_venta_landscape;
        } else {
            layout = R.layout.cardview_libro_en_venta_portrait;
        }

        // Hacer llamada a BD y obtener los titulos de los libros en venta en orden según sus IDs

        BaseDeDatos bd = new BaseDeDatos(this, "MiBaseDeDatos", null, 1);
        ArrayList<String> listaTitulos = bd.obtenerTitulosDeLibrosEnVenta();

        String[] listaValores = listaTitulos.toArray(new String[listaTitulos.size()]);

        // Crear el ListView en sí con sus CardViews

        ArrayAdapterDeLibrosEnVenta adapter = new ArrayAdapterDeLibrosEnVenta(super.getApplicationContext(),listaValores, layout, this);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        // Dar listener al botón de volver

        fragmento.findViewById(R.id.volverDeVentas).setOnClickListener(new Listener());



    }


    public void crearNotificacion() {

        // Método que crea la notificación de si se quiere obtener la factura tras rentar un libro


        // Llamar a BD y obtener la info del último libro rentado (el que se acaba de comprar)

        BaseDeDatos bd = new BaseDeDatos(this, "MiBaseDeDatos", null, 1);
        String[] datos = bd.obtenerDatosDeLibroPrestado(bd.cantidadLibrosRentados()-1);

        // Crear los campos relevantes para escribir en la notificación

        String contenido = "";
        String titulo = "";
        String subtitulo = "";
        String boton = "";


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idioma","es");

        // Llenar los campos según el idioma (son strings tal cual, no se pueden cargar por referencia con strings.xml)


        if (idioma.contentEquals("es")) {
            contenido = "Título del libro: "+datos[0]+" \n" +
                    "Autor del libro: "+datos[1]+ "\n"+
                    "Descripción: "+datos[2]+ "\n" +
                    "Prestado desde:"+datos[4]+"\n"+
                    "Prestado hasta:"+datos[5];
            titulo = "Alquiler realizado correctamente";
            subtitulo = "Puedes obtener el recibo aquí";
            boton = "Obtener recibo";

        } else {
            contenido ="Title of the book: "+datos[0]+" \n" +
                    "Author of the book: "+datos[1]+ "\n"+
                    "Description: "+datos[3]+ "\n" +
                    "Borrowed since:"+datos[4]+"\n"+
                    "Borrowed until:"+datos[5];
            titulo = "Book borrowed successfully";
            subtitulo = "You can get your receipt here";
            boton = "Get receipt";
        }

        // Crear la notificación

        NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            //Configuración del canal
            elCanal.setDescription("Descripción del canal");
            elCanal.enableLights(true);
            elCanal.setLightColor(Color.RED);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);


            elManager.createNotificationChannel(elCanal);
        }

        // Crear un intent para redirigir a ActividadEscritura (quien hace la llamada al gestor de ficheros)
        // y darle como parámetro el contenido a escribir en el fichero de texto)

        Intent intent = new Intent(this, ActividadEscritura.class);
        intent.putExtra("contenido", contenido);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE);



        elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), com.google.android.material.R.drawable.abc_btn_check_material))
                .setSmallIcon(android.R.drawable.checkbox_on_background)
                .setContentTitle(titulo)
                .setContentText(subtitulo)
                .setAutoCancel(true)
                .addAction(android.R.drawable.btn_plus, boton, pendingIntent);


        elManager.notify(1, elBuilder.build());


    }


    private class Listener implements View.OnClickListener {

        // Listener del botón para volver al menú principal

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(LibrosEnVentaActivity.this, MainActivity.class);
            LibrosEnVentaActivity.super.startActivity(intent);
            LibrosEnVentaActivity.super.finish();
        }
    }

}