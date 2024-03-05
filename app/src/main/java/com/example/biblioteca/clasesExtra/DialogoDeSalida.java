package com.example.biblioteca.clasesExtra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.biblioteca.R;

public class DialogoDeSalida extends DialogFragment {

    // Dialogo con la confirmación de salida de la app. Es un pop-up en el que se da la opción
    // de confirmar la salida o quedarse en la app.

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Crear el pop-up en sí

        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.titulo_salir_popup);
        builder.setMessage(R.string.descripcion_salir_popup);
        builder.setPositiveButton(R.string.afirmacion_salir_popup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Listener de confirmar la salida. Se cierra la conexión a BD y se termina la ejecución del programa
                // tras cerrar la actividad

                BaseDeDatos bd = new BaseDeDatos(DialogoDeSalida.super.getActivity(), "MiBaseDeDatos", null, 1);
                bd.close();
                DialogoDeSalida.super.getActivity().finish();
                System.exit(0);

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // Listener de negar la salida, el usuario se queda en la app
                // Por defecto este onClick cierra el pop-up, que es lo que se necesita


                // NOP (No operation)
            }
        });


        return builder.create();
    }
}