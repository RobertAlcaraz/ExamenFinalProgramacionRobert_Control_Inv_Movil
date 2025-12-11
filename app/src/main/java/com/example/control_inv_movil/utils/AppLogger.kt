package com.example.control_inv_movil.utils

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Un objeto singleton para manejar el registro de eventos en un archivo.
 */
object AppLogger {

    private const val NOMBRE_ARCHIVO_LOG = "inventario_log.txt"

    /**
     * Escribe un mensaje en el archivo de log.
     * @param context Contexto de la aplicación, necesario para encontrar el directorio de archivos.
     * @param tag Una etiqueta para categorizar el mensaje (ej. "CRUD", "ERROR").
     * @param mensaje El mensaje a registrar.
     */
    fun log(context: Context, tag: String, mensaje: String) {
        try {
            // Obtenemos la ruta al directorio de archivos interno y seguro de la app.
            val directorio = context.filesDir
            if (!directorio.exists()) {
                directorio.mkdirs()
            }
            val archivoLog = File(directorio, NOMBRE_ARCHIVO_LOG)

            // Usamos FileWriter en modo 'append' (true) para que los nuevos logs
            // se añadan al final del archivo sin borrar los anteriores.
            val writer = FileWriter(archivoLog, true)

            // Formateamos la fecha y hora actual para el registro.
            val formatoFecha = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fechaActual = formatoFecha.format(Date())

            // Escribimos la línea de log con el formato: [FECHA HORA] [TAG] Mensaje
            writer.append("[$fechaActual] [$tag] $mensaje\n")
            writer.flush()
            writer.close()

        } catch (e: IOException) {
            // Si ocurre un error al escribir, lo imprimimos en el Logcat normal.
            e.printStackTrace()
        }
    }
}
    