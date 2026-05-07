package com.example.mychartsapp.presentation.ui.utils

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Утилита для отладки приложения.
 * Записывает отладочные сообщения в файл в папке Downloads и в logcat.
 * Используется для трассировки выполнения приложения.
 *
 * @author Alexander
 */
object DebugHelper {
    
    /** Имя файла для сохранения логов */
    private const val LOG_FILE_NAME = "app_debug_log.txt"
    
    /**
     * Записывает отладочное сообщение.
     * Сообщение добавляется в файл лога и выводится в logcat.
     *
     * @param context Контекст приложения
     * @param tag Тег для идентификации источника сообщения
     * @param message Текст сообщения
     */
    fun log(context: Context, tag: String, message: String) {
        try {
            // Формируем строку лога с временной меткой
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(Date())
            val logLine = "[$timestamp] [$tag] $message\n"
            
            // Ищем существующий файл лога
            val existingUri = findExistingLogFile(context)
            
            if (existingUri != null) {
                // Файл существует — дописываем в конец
                context.contentResolver.openOutputStream(existingUri, "wa")?.use { outputStream ->
                    outputStream.write(logLine.toByteArray())
                }
            } else {
                // Файл не существует — создаём новый
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, LOG_FILE_NAME)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    context.contentResolver.openOutputStream(it)?.use { outputStream ->
                        outputStream.write(logLine.toByteArray())
                    }
                }
            }
            
            // Также выводим в logcat для удобства
            android.util.Log.d(tag, message)
        } catch (e: Exception) {
            android.util.Log.e("DebugHelper", "Ошибка записи лога: ${e.message}")
        }
    }
    
    /**
     * Находит существующий файл лога в папке Downloads.
     *
     * @param context Контекст приложения
     * @return URI найденного файла или null, если файл не существует
     */
    private fun findExistingLogFile(context: Context): android.net.Uri? {
        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(LOG_FILE_NAME)
        
        return context.contentResolver.query(collection, null, selection, selectionArgs, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                android.net.Uri.withAppendedPath(collection, id.toString())
            } else {
                null
            }
        }
    }
    
    /**
     * Очищает файл лога, удаляя его полностью.
     *
     * @param context Контекст приложения
     */
    fun clearLog(context: Context) {
        try {
            val existingUri = findExistingLogFile(context)
            existingUri?.let {
                context.contentResolver.delete(it, null, null)
            }
        } catch (e: Exception) {
            android.util.Log.e("DebugHelper", "Ошибка очистки лога", e)
        }
    }
    
    /**
     * Возвращает путь к файлу лога в файловой системе.
     *
     * @param context Контекст приложения
     * @return Строка с путем к файлу лога
     */
    fun getLogPath(context: Context): String {
        return "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$LOG_FILE_NAME"
    }
}