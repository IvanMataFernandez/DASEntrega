package com.example.biblioteca.clasesExtra;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import com.example.biblioteca.R;
import com.example.biblioteca.actividades.DatosLibroEnAlquilerActivity;
import com.example.biblioteca.actividades.LibrosAlquiladosActivity;

import java.util.Locale;

public class ArrayAdapterDeLibrosEnAlquiler extends BaseAdapter {


    /*
        Este ArrayAdapter crea los CardViews respecto a los libros rentados.
        Cada CardView es una instancia de un libro rentado, y contiene:

        - TextView con el título del libro
        - 3 botones: ver detalles de la compra, alargar periodo de devolución, devolver.
        - Solo si el móvil está en landscape, entonces también un ImageView con la portada del libro



     */

    private Context contexto;
    private int recurso;
    private String[] titulos;

    private LibrosAlquiladosActivity actividad;
    private LayoutInflater inflater;
    public ArrayAdapterDeLibrosEnAlquiler(Context context, String[] nombres, int recurso, LibrosAlquiladosActivity actividad) {
        this.contexto = context;
        this.titulos = nombres;
        this.recurso = recurso;
        this.actividad = actividad;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return this.titulos.length;
    }

    @Override
    public Object getItem(int position) {
        return this.titulos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int pos, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Por alguna razón desde los ArrayAdapter no lee el idioma setteado en las preferencias
        // a no ser que se cargue otra vez. Por lo que eso mismo se hace aquí

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.actividad);
        String idioma = prefs.getString("idioma","es");


        Configuration configuration =  this.contexto.getResources().getConfiguration();
        Locale savedLocale = configuration.locale;
        configuration.locale = new Locale(idioma); // Cambiar al idioma deseado
        this.contexto.getResources().updateConfiguration(configuration, null);

        // Generar el layout según la plantilla dada por la clase que le llama

        View view = inflater.inflate(this.recurso, null);

        // Dar estilo

        this.setEstilo(view);

        // Escribir el título del libro en el TextView

        TextView titulo = view.findViewById(R.id.tituloAlquiler);

        titulo.setText(this.titulos[pos]);

        // Dar los listeners correspondientes a los botones

        Button botones[] = new Button[3];


        botones[0] = view.findViewById(R.id.verInfoAlquiler);
        botones[1] = view.findViewById(R.id.alargarPlazo);
        botones[2] = view.findViewById(R.id.devolver);

        for (int i = 0; i != 3; i++) {
            botones[i].setOnClickListener(new Listener(i,pos));
        }

        // Si el movil está en landscape, se debe añadir la imagen también al cardview


        if (this.actividad.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {


            Integer[] listaImagenes = {R.drawable.libro0, R.drawable.libro1, R.drawable.libro2, R.drawable.libro3};
             ((ImageView) view.findViewById(R.id.imagenLibroEnAlquiler)).
                     setImageDrawable(this.actividad.getResources().getDrawable(listaImagenes[pos]));


        }



        return view;
    }

    private void setEstilo(View fragmento) {

        // Settear estilo según las preferencias del usuario

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.actividad);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;


        if (estilo.contentEquals("1")) {

            // Estilo día

            fragmento.setBackgroundColor(Color.rgb(230, 230, 230));

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // iterar por cada elemento de la UI
                View elemento = viewGroup.getChildAt(i);


                if (elemento instanceof Button) {

                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.GRAY);
                    boton.setTextColor(Color.BLACK);



                } else if (elemento instanceof TextView) {


                    TextView texto = (TextView) elemento;
                    texto.setTextColor(Color.rgb(0,0,0));

                }



            }



        } else {

            // Estilo neón

            fragmento.setBackgroundColor(Color.BLUE);

            for (int i = 0; i != viewGroup.getChildCount(); i++) { // Iterar por cada elemento de la UI
                View elemento = viewGroup.getChildAt(i);


                if (elemento instanceof Button) {

                    Button boton = (Button) elemento;
                    boton.setBackgroundColor(Color.rgb(0,100,0));
                    boton.setTextColor(Color.WHITE);




                } else if (elemento instanceof TextView) {

                    TextView texto = (TextView) elemento;
                    texto.setTextColor(Color.WHITE);

                }



            }


        }
    }

    private class Listener implements View.OnClickListener {

        /*
            Listeners de los botones

            numBoton:
            0 -> ver detalles
            1 -> alargar periodo renta
            2 -> devolver

            fila: el número de instancia de la renta. Ordenadas de manera que la más antigua sea 0
         */

        private int numBoton;
        private int fila;
        public Listener(int numBoton, int fila) {
            this.numBoton = numBoton; this.fila = fila;
        }

        @Override
        public void onClick(View v) {

            // Hacer llamada a BD porque se puede necesitar consultarla tras pulsar un botón
            BaseDeDatos bd = new BaseDeDatos(ArrayAdapterDeLibrosEnAlquiler.this.contexto, "MiBaseDeDatos", null, 1);


            switch (this.numBoton) {
                case 0:
                    // Ver detalles, abrir otra actvidad

                    Intent intent = new Intent(actividad, DatosLibroEnAlquilerActivity.class);
                    intent.putExtra("idLibro", this.fila); // Pasarle el id de la renta
                    actividad.startActivity(intent);
                    actividad.finish();

                    break;
                case 1:
                    // Alargar plazo

                    switch (bd.atrasarEntrega(this.fila)) { // Pedir a BD que trate de alargar el plazo
                        case 0:
                            // Se ha alargado el plazo correctamente
                            this.mostrarMensajeToast(R.string.toast_prorroga_correcta);

                            break;
                        case 1:
                            // No se ha alargado el plazo porque ya se había alargado antes
                            this.mostrarMensajeToast(R.string.toast_prorroga_incorrecta1);

                            break;
                        case 2:
                            // No se ha alargado el plazo porque hay libros sin devolver ya pasados de fecha
                            this.mostrarMensajeToast(R.string.toast_prorroga_incorrecta2);

                    }

                    break;
                case 2:

                    // Borrar renta
                    this.mostrarMensajeToast(R.string.toast_devolucion);

                    bd.quitarLibroAUsuario(this.fila); // Pedir a BD que haga DELETE FROM TABLE

                    // Refrescar cambios a UI, para ello recargar la actividad otra vez
                    intent = new Intent(actividad, LibrosAlquiladosActivity.class);
                    actividad.startActivity(intent);
                    actividad.finish();

            }





            }
            private void mostrarMensajeToast(int msg) {
                Toast.makeText(actividad, msg, Toast.LENGTH_SHORT).show();
            }
        }


    }




