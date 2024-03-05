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
import com.example.biblioteca.actividades.DatosLibroEnVentaActivity;
import com.example.biblioteca.actividades.LibrosEnVentaActivity;

import java.util.Locale;

public class ArrayAdapterDeLibrosEnVenta extends BaseAdapter {
    private Context contexto;
    private int recurso;
    private String[] titulos;


    private LibrosEnVentaActivity actividad;

    private LayoutInflater inflater;
    public ArrayAdapterDeLibrosEnVenta(Context context, String[] nombres, int recurso, LibrosEnVentaActivity actividad) {
        this.contexto = context;
        this.titulos = nombres;
        this.recurso = recurso;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.actividad = actividad;


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

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.actividad);
        String idioma = prefs.getString("idioma","es");


        Configuration configuration =  this.contexto.getResources().getConfiguration();
        Locale savedLocale = configuration.locale;
        configuration.locale = new Locale(idioma); // Cambiar al idioma deseado
        this.contexto.getResources().updateConfiguration(configuration, null);



        View view = inflater.inflate(this.recurso, null);
        this.setEstilo(view);



        int idxIdioma;

        if (idioma.contentEquals("es")) {
            idxIdioma = 0;
        } else {
            idxIdioma = 1;
        }



       // Poner nombres a los labels

        TextView titulo = view.findViewById(R.id.titulo);
        titulo.setText(this.titulos[pos]);


        Button botones[] = new Button[2];


        botones[0] = view.findViewById(R.id.verInfo);
        botones[1] = view.findViewById(R.id.alquilar);

        for (int i = 0; i != 2; i++) {
            botones[i].setOnClickListener(new Listener(i,pos));
        }


        if (this.actividad.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

            // Si el movil está en landscape, se debe añadir la imagen también al cardview

            Integer[] listaImagenes = {R.drawable.libro0, R.drawable.libro1, R.drawable.libro2, R.drawable.libro3};
            ((ImageView) view.findViewById(R.id.imagenLibroEnVenta)).
                    setImageDrawable(this.actividad.getResources().getDrawable(listaImagenes[pos]));


        }


        return view;
    }

    private void setEstilo(View fragmento) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.actividad);
        String estilo = prefs.getString("estilo","1");
        ViewGroup viewGroup = (ViewGroup) fragmento;


        if (estilo.contentEquals("1")) {
            fragmento.setBackgroundColor(Color.rgb(230, 230, 230));

            for (int i = 0; i != viewGroup.getChildCount(); i++) {
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

            fragmento.setBackgroundColor(Color.BLUE);

            for (int i = 0; i != viewGroup.getChildCount(); i++) {
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

        private int numBoton;
        private int fila;
        public Listener(int numBoton, int fila) {
            this.numBoton = numBoton; this.fila = fila;
        }

        @Override
        public void onClick(View v) {


            if (this.numBoton == 0) {
                Intent intent = new Intent(ArrayAdapterDeLibrosEnVenta.this.contexto, DatosLibroEnVentaActivity.class);
                intent.putExtra("idLibro", this.fila);
                actividad.startActivity(intent);
                actividad.finish();


            } else {
                BaseDeDatos bd = new BaseDeDatos(actividad, "MiBaseDeDatos", null, 1);



                switch (bd.añadirLibroAUsuario(this.fila)) {
                    case 0:
                        this.mostrarMensajeToast(R.string.toast_alquilado_correcto);


                        actividad.crearNotificacion();



                        break;
                    case 1:
                        this.mostrarMensajeToast(R.string.toast_alquilado_incorrecto1);

                        break;
                    case 2:
                        this.mostrarMensajeToast(R.string.toast_alquilado_incorrecto2);


                }





            }


        }
        private void mostrarMensajeToast(int msg) {
            Toast.makeText(actividad, msg, Toast.LENGTH_SHORT).show();
        }
    }




}