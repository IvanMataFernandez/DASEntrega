package com.example.biblioteca.clasesExtra;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class BaseDeDatos extends SQLiteOpenHelper {

    /*
        MANEJAR BD, TABLAS:

        LIBROS(Id, Titulo, Autor, Descripcion, DescripcionEn) | Primary Key = Id
        PRESTADOS(Id, IdLibro, PrestadoDesde, PrestadoHasta)  | Primary Key = Id | IdLibro references Titulo(Id)


        La tabla LIBROS guarda los datos de los libros en venta

        La tabla PRESTADOS guarda los datos de los prestamos del usuario (se asume que solo hay un usuario ya que
        es una app de cliente, no está montada en un servidor con múltiples usuarios). Un usuario no puede tener
        más de 5 prestamos a la vez



        Cuando se presta un libro, se da un límite de dos semanas para devolverlo (permitiendo una prórroga extra de
        una semana si se necesitara). Aunque se pueda mantener un libro por tanto tiempo como se quiera, algunas funciones
        se desactivan cuando se mantiene un libro de más tiempo de lo posible, estas son:

            - No se permite atrasar ninguna fecha de entrega de este u otros libros rentados
            - No se permite rentar nuevos libros

        Estas funcionalidades se vuelven a desbloquear el momento el usuario devuelve todos sus libros atrasados


        Los Id siempre van en orden en las tablas según su antiguedad (más antiguo, id menor). 0 based indexing

        La tabla LIBROS es SOLO lectura y no tiene problemas con los ids

        La tabla PRESTADOS es lectura y escritura, por lo que si se quitan / añaden
        instancias a la tabla, se modifica la tabla entera de forma de que los id
        se reorganicen y encajen.

        La inserción de un elemento es coste O(1). (añadir al final con valor id del length previo de la tabla)
        La eliminación de un elemento es coste O(n) (potencialmente tener que restar 1 al id de todas las instancias
                                                     si se quita la primera. Coste asumible ya que PRESTADOS nunca
                                                     guarda más de 5 instancias a la vez).



     */



    public BaseDeDatos(@Nullable Context context, @Nullable String name,
                       @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // Método que se llama la PRIMERA VEZ que se instancia la BD

        // Crear una tablas relevantes
        db.execSQL("CREATE TABLE LIBROS (Id INT PRIMARY KEY, Titulo VARCHAR(50), Autor VARCHAR(50), Descripcion VARCHAR(100), DescripcionEn VARCHAR(100));");
        db.execSQL("CREATE TABLE PRESTADOS (Id INT PRIMARY KEY, PrestadoDesde DATE, PrestadoHasta DATE, IdLibro INT,  FOREIGN KEY (IdLibro) REFERENCES LIBROS(Id));");


        // Insertar libros

        db.execSQL("INSERT INTO LIBROS VALUES (0,'Cien años de soledad', 'Gabriel García Márquez', 'Una saga familiar en Macondo.', 'A family saga in Macondo.');");
        db.execSQL("INSERT INTO LIBROS VALUES (1,'1984', 'George Orwell', 'Una distopía que reflexiona sobre el poder y la manipulación.', 'A dystopia that reflects on power and manipulation.');");
        db.execSQL("INSERT INTO LIBROS VALUES (2,'Harry Potter y la piedra filosofal', 'J.K. Rowling', 'La primera entrega de la saga de Harry Potter.', 'The first installment of the Harry Potter saga.'); ");
        db.execSQL("INSERT INTO LIBROS VALUES (3,'Don Quijote de la Mancha', 'Miguel de Cervantes', 'Las aventuras del ingenioso hidalgo Don Quijote de la Mancha.', 'The adventures of the ingenious gentleman Don Quixote of la Mancha.');");


    }


    public int cantidadLibrosRentados() {
        // Ver cuantos libros hay rentados. Relevante cuando se tiene que comprobar si el usuario
        // ha alcanzado el límite de reservas

        SQLiteDatabase gestor = super.getReadableDatabase();

        Cursor cursor = gestor.rawQuery("SELECT COUNT(*) FROM PRESTADOS;", null);
        cursor.moveToNext();
        int cantidad = cursor.getInt(0);
        cursor.close();
        return cantidad;
    }

    private boolean librosCaducados() {

        // Comprobar si hay al menos un libro pasado de fecha y se debería haber devuelto ya

        SQLiteDatabase gestor = super.getReadableDatabase();

        Cursor cursor = gestor.rawQuery("SELECT * FROM PRESTADOS WHERE PrestadoHasta < DATE('now');", new String[0]);
        boolean caducados = cursor.moveToNext();
        cursor.close();
        return caducados;
    }

    private void insertarLibro(int idLibro, int cantLibros) {
        // Pre: Id de Libro a añadir a PRESTADOS, cantidad de prestamos en curso. No se hacen comprobaciones de si
        //      se pudiera realmente insertar o no, SE ASUME QUE SE PUEDE INSERTAR Y NO SE HACEN COMPROBACIONES AQUÍ
        // Post: Se ha añadido el préstamo y se referencia con una foreign key a la instancia del libro en sí

        SQLiteDatabase gestor = super.getWritableDatabase();

        Integer[] parametros = new Integer[2]; // Las consultas con variables SE PARAMETRIZAN POR SEGURIDAD.
        parametros[0] = cantLibros;
        parametros[1] = idLibro;
        gestor.execSQL("INSERT INTO PRESTADOS VALUES(?, DATE('now'), DATE('now', '+14 days'), ?)", parametros);

    }
    public int añadirLibroAUsuario(int idLibro) {
        // Pre: Id del libro a añadir
        // Post: 0 -> libro añadido, 1 -> falta de espacio, 2 -> hay libros pasada caducidad, no permite rentar hasta devolverlos

        int librosPrestados = this.cantidadLibrosRentados();
        if (librosPrestados == 5) { // No van a haber más de 5, parar de añadir aquí
            return 1;
        } else if (this.librosCaducados()) {
            return 2;
        } else {
            this.insertarLibro(idLibro, librosPrestados);
            return 0;
        }



    }
    public void quitarLibroAUsuario(int idLibro) {
        // Pre: Id (pos) a borrar del prestamos (valor 0-4)
        // Post: Lista actualizada del usuario con los ids de posiciones actualizados


        // Borrar la instancia

        SQLiteDatabase gestor = super.getWritableDatabase();
        Integer[] parametros = new Integer[1];
        parametros[0] = idLibro;
        gestor.execSQL("DELETE FROM PRESTADOS WHERE Id=?",parametros);


        // Actualizar el resto de ids necesarios para mantener orden

        for (int pos = idLibro+1; pos != 5; pos++) {
            Integer[] parametros2 = {pos-1, pos};

            gestor.execSQL("UPDATE PRESTADOS SET Id=? WHERE Id=?", parametros2);
        }


    }


    public ArrayList<String> obtenerTitulosDeLibrosEnVenta() {
        // Post: Outputear titulos en array de manera que cada índice también encaja con su valor id.
        //       Los títulos ya se outputean en orden de id por lo que no se tienen que hacer ordenaciones

        SQLiteDatabase gestor = super.getReadableDatabase();

        Cursor cursor = gestor.rawQuery("SELECT Titulo FROM LIBROS", new String[0]);

        ArrayList<String> titulos = new ArrayList<String>();

        while (cursor.moveToNext()) {
            titulos.add(cursor.getString(0));
        }
        cursor.close();
        return titulos;
    }

    public ArrayList<String> obtenerTitulosDeMisLibros() {
        // Post: Outputear titulos en array de manera que cada indice tmb encaja con su valor id
        //       Los títulos ya se outputean en orden de id por lo que no se tienen que hacer ordenaciones

        SQLiteDatabase gestor = super.getReadableDatabase();
        Cursor cursor = gestor.rawQuery("SELECT LIBROS.Titulo FROM PRESTADOS INNER JOIN LIBROS ON PRESTADOS.IdLibro = LIBROS.Id", null);
        ArrayList<String> titulos = new ArrayList<String>();

        while (cursor.moveToNext()) {

            titulos.add(cursor.getString(0));
        }
        cursor.close();

        return titulos;
    }


    public String[] obtenerDatosDeLibroEnVenta(int idLibro) {

        // Pre: Id en rango
        // Post: Datos de libro en venta (Titulo, autor, desc en esp, desc en ing)

        SQLiteDatabase gestor = super.getReadableDatabase();
        String[] parametros = new String[1];
        parametros[0] = Integer.toString(idLibro);

        Cursor cursor = gestor.rawQuery("SELECT Titulo, Autor, Descripcion, DescripcionEn FROM LIBROS WHERE ? = Id;", parametros);

        String[] datos = new String[4];

        cursor.moveToNext();

        for (int i = 0; i != 4; i++) {
            datos[i] = cursor.getString(i);
        }

        cursor.close();

        return datos;
    }

    public String[] obtenerDatosDeLibroPrestado(int idLibro) {
        // Pre: Id de préstamo en rango 0-4
        // Post: Datos de libro prestado (titulo, autor, desc en esp, desc en ing, fecha prestado, fecha devolución)


        SQLiteDatabase gestor = super.getReadableDatabase();
        String[] parametros = new String[1];
        parametros[0] = Integer.toString(idLibro);

        Cursor cursor = gestor.rawQuery("SELECT Titulo, Autor, Descripcion, DescripcionEn, PrestadoDesde, PrestadoHasta FROM PRESTADOS INNER JOIN LIBROS ON PRESTADOS.IdLibro = LIBROS.Id WHERE ? = PRESTADOS.Id;", parametros);
        String[] datos = new String[6];
        cursor.moveToNext();

        for (int i = 0; i != 6; i++) {
            datos[i] = cursor.getString(i);
        }

        cursor.close();

        return datos;
    }


    private boolean libroYaAtrasado (int idLibro) {

        // Comprobar si la instancia de prestamo idLibro ya fue atrasada.
        // Por defecto los libros se prestan por 2 semanas y se permite atrasar 1 semana extra.

        SQLiteDatabase gestor = super.getReadableDatabase();

        String[] parametros = {Integer.toString(idLibro)};
        Cursor cursor = gestor.rawQuery("SELECT PrestadoDesde, PrestadoHasta FROM PRESTADOS WHERE id = ?", parametros);
        cursor.moveToNext();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {

            // Tras obtener las fechas, comprobar si hay al menos 2,5 semanas de diferencia entre ellas para ver
            // si hubo un aplazo ya

            Date fecha1 = df.parse(cursor.getString(0));
            Date fecha2 = df.parse(cursor.getString(1));

            cursor.close();
            long milisDif = fecha2.getTime() - fecha1.getTime();
            return milisDif > 1555200000; // 2'5 semanas en milis

        } catch (ParseException e) {
            cursor.close();
         //   gestor.close();
            return true;
        }

    }

    private int comprobarAtrasable (int idLibro) {
        // Post: 0 -> Atrasable, 1 -> Ya se atrasó, 2 -> Hay libros caducados

        if (this.librosCaducados()) {
            return 2;
        } else if (this.libroYaAtrasado(idLibro)) {
            return 1;
        } else {
            return 0;
        }


    }

    public int atrasarEntrega(int idLibro) {
        // Post: 0 -> Se ha atrasado, 1 -> Ya se atrasó, 2 -> Hay libros caducados

        int respuesta = this.comprobarAtrasable(idLibro);

        if (respuesta != 0) {
            return respuesta;
        }

        SQLiteDatabase gestor = super.getWritableDatabase();

        Integer[] parametros = {idLibro};
        gestor.execSQL("UPDATE PRESTADOS SET PrestadoHasta=DATE(PrestadoHasta, '+7 days') WHERE Id = ?",parametros);

        return 0;

    }




    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Metodo relacionado a mejoras necesarias por cambios de version en al app y demás
        // Se requiere escribirlo aquí aunque no haga nada
        // NOP
    }
}
