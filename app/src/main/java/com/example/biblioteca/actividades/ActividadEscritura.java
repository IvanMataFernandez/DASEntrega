package com.example.biblioteca.actividades;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.biblioteca.R;

import java.io.OutputStream;


/*
    Aunque esta sea la primera clase listada, la información general de la aplicación esta
    comentada en actividades.MainActivity.

 */


public class ActividadEscritura extends AppCompatActivity {


    /*
        Esta actividad no es visible al usuario. Solo se usa para hacer
        la redirección desde la notificación al sistema de ficheros.

     */

    private ActivityResultLauncher documentPickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Crear la actividad en sí

        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_actividad_escritura); // Carga un layout en blanco porque no es visible

        // Obtener contenido del fichero a escribir
        String contenido = super.getIntent().getExtras().getString("contenido");




        // Definir la llamada al sistema de ficheros

        documentPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // Ver si se ha clickado en un archivo
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        // Ver si el archivo es válido
                        if (data != null) {
                            Uri uri = data.getData();
                            writeToFile(uri, contenido); // Escribir al archivo el contenido (método de esta clase)
                        }

                    }
                    super.finish();

                }
        );

        // Si se ha llegado a esta actividad, se había pinchado en la notificación. Aquí la quitamos

        NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        elManager.cancel(1);

        // Hacer la llamada al sistema de ficheros. Esta actividad siempre redirige ahí por lo que se hace
        // directamente desde createView()

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "factura.txt");
        documentPickerLauncher.launch(intent);









    }

    private void writeToFile(Uri uri, String contenido) {

        // Escribir al archivo al que apunta "uri"

        try {
            // Abre un OutputStream para escribir en el archivo
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                // Si el stream es válido, entonces recoger el texto del campo que pedía poner el contenido ahí

                System.out.println(contenido);

                // Escribir al stream dicho contenido
                outputStream.write(contenido.getBytes());
                // Cerrar el stream
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}