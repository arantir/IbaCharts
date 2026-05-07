package com.example.mychartsapp.presentation.ui.utils

import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.net.Uri
import android.content.ContentResolver
import java.io.InputStream
import java.io.ByteArrayOutputStream
import com.example.mychartsapp.presentation.ui.models.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import android.util.Log

/**
 * Парсер файлов формата PDA/IBA.
 * Поддерживает чтение текстовой заголовочной части и бинарных данных.
 * Умеет работать с файлами через URI (content://, file://) и прямой путь.
 *
 * @param context Контекст приложения (опционально, нужен для доступа к файлам по URI)
 */
class IBAParser(private val context: Context? = null) {
    
    /**
     * Копирует текст в системный буфер обмена.
     * Используется для копирования ошибок парсинга.
     *
     * @param text Текст для копирования
     */
    private fun copyToClipboard(text: String) {
        context?.let {
            try {
                val clipboard = it.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = android.content.ClipData.newPlainText("Ошибка парсера PDA", text)
                clipboard.setPrimaryClip(clip)
            } catch (e: Exception) {
                Log.e("IBAParser", "Ошибка копирования в буфер", e)
            }
        }
    }
    
    /**
     * Основной метод парсинга PDA файла.
     *
     * @param filePath Путь к файлу (URI или абсолютный путь)
     * @param progressCallback Callback для отслеживания прогресса (индекс, всего, сообщение)
     * @return Объект PDAFile или null в случае ошибки
     */
    fun parseFile(filePath: String, progressCallback: ((Int, Int, String) -> Unit)? = null): PDAFile? {
        return try {
            if (filePath.startsWith("content://") || filePath.startsWith("file://")) {
                return parseFileFromUri(Uri.parse(filePath), progressCallback)
            }
            parseFileFromPath(filePath, progressCallback)
        } catch (e: Exception) {
            handleCriticalError(e, "parseFile")
            null
        }
    }
    
    /**
     * Парсит файл из URI (content:// или file://).
     *
     * @param uri URI файла
     * @param progressCallback Callback прогресса
     * @return Объект PDAFile или null
     */
    private fun parseFileFromUri(uri: Uri, progressCallback: ((Int, Int, String) -> Unit)? = null): PDAFile? {
        return try {
            if (context == null) {
                handleCriticalError(IllegalStateException("Контекст не доступен"), "parseFileFromUri")
                return null
            }
            
            val contentResolver: ContentResolver = context.contentResolver
            val inputStream: InputStream? = try {
                contentResolver.openInputStream(uri)
            } catch (e: Exception) {
                handleCriticalError(e, "openInputStream из URI: $uri")
                throw e
            }
            
            inputStream?.use { stream ->
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalRead = 0L
                
                try {
                    while (stream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        
                        // Ограничение размера файла 100MB
                        if (totalRead > 100 * 1024 * 1024) {
                            handleCriticalError(
                                OutOfMemoryError("Файл превышает 100MB"),
                                "parseFileFromUri - чтение файла"
                            )
                            throw OutOfMemoryError("Файл слишком большой: ${totalRead / (1024 * 1024)}MB")
                        }
                    }
                } catch (e: Exception) {
                    handleCriticalError(e, "Чтение потока данных")
                    throw e
                }
                
                val fileBuffer = outputStream.toByteArray()
                
                if (fileBuffer.isEmpty()) {
                    handleCriticalError(IllegalArgumentException("Файл пустой"), "parseFileFromUri")
                    throw IllegalArgumentException("ERR_STAGE_3: Файл пустой")
                }
                
                Log.d("IBAParser", "Прочитано ${fileBuffer.size} байт из файла")
                parseDataFromBuffer(fileBuffer, progressCallback)
            }
        } catch (e: Exception) {
            handleCriticalError(e, "parseFileFromUri общая ошибка")
            null
        }
    }
    
    /**
     * Парсит файл по прямому пути в файловой системе.
     *
     * @param filePath Абсолютный путь к файлу
     * @param progressCallback Callback прогресса
     * @return Объект PDAFile или null
     */
    private fun parseFileFromPath(filePath: String, progressCallback: ((Int, Int, String) -> Unit)? = null): PDAFile? {
        return try {
            // Конвертируем путь в URI если есть контекст
            if (context != null) {
                val uri = if (filePath.startsWith("/")) {
                    Uri.parse("file://$filePath")
                } else {
                    Uri.parse(filePath)
                }
                return parseFileFromUri(uri, progressCallback)
            }
            
            val file = java.io.File(filePath)
            if (!file.exists()) {
                handleCriticalError(IllegalArgumentException("Файл не существует"), "parseFileFromPath")
                throw IllegalArgumentException("ERR_STAGE_1: Файл не существует: $filePath")
            }
            
            val fileBuffer = file.readBytes()
            
            if (fileBuffer.isEmpty()) {
                handleCriticalError(IllegalArgumentException("Файл пустой"), "parseFileFromPath")
                throw IllegalArgumentException("ERR_STAGE_3: Файл пустой")
            }
            
            parseDataFromBuffer(fileBuffer, progressCallback)
        } catch (e: Exception) {
            handleCriticalError(e, "parseFileFromPath")
            null
        }
    }
    
    /**
     * Парсит данные из байтового буфера.
     * Разделяет текстовую часть заголовка и бинарные данные каналов.
     *
     * @param fileBuffer Байтовый буфер всего файла
     * @param progressCallback Callback прогресса
     * @return Объект PDAFile
     */
    private fun parseDataFromBuffer(fileBuffer: ByteArray, progressCallback: ((Int, Int, String) -> Unit)? = null): PDAFile {
        val fileContent = String(fileBuffer, Charsets.ISO_8859_1)
        
        // Ищем маркер конца ASCII части
        val endAsciiIndex = fileContent.indexOf("endASCII:")
        if (endAsciiIndex == -1) {
            handleCriticalError(IllegalArgumentException("Не найден endASCII"), "parseDataFromBuffer")
            throw IllegalArgumentException("ERR_STAGE_4: Не найден маркер endASCII:. Файл не в формате PDA")
        }
        
        val binaryDataStart = endAsciiIndex + "endASCII:".length
        
        if (binaryDataStart >= fileBuffer.size) {
            handleCriticalError(IllegalArgumentException("Нет бинарных данных"), "parseDataFromBuffer")
            throw IllegalArgumentException("ERR_STAGE_5: Нет бинарных данных после endASCII:")
        }
        
        // Отделяем текстовую и бинарную части
        val textPart = fileContent.substring(0, endAsciiIndex + "endASCII:".length)
        val binaryData = fileBuffer.copyOfRange(binaryDataStart, fileBuffer.size)
        
        // Парсим заголовок
        val header = try {
            parseHeader(textPart)
        } catch (e: Exception) {
            handleCriticalError(e, "parseHeader")
            throw IllegalArgumentException("ERR_STAGE_6: Ошибка парсинга заголовка: ${e.message}")
        }
        
        // Парсим список каналов
        val channels = try {
            parseChannels(textPart)
        } catch (e: Exception) {
            handleCriticalError(e, "parseChannels")
            throw IllegalArgumentException("ERR_STAGE_7: Ошибка парсинга каналов: ${e.message}")
        }
        
        if (channels.isEmpty()) {
            handleCriticalError(IllegalArgumentException("Нет каналов"), "parseDataFromBuffer")
            throw IllegalArgumentException("ERR_STAGE_8: Не найдено ни одного канала в файле")
        }
        
        Log.d("IBAParser", "Найдено ${channels.size} каналов, ${header.frames} фреймов")
        
        // Парсим бинарные данные каналов
        val channelData = try {
            parseBinaryDataWithProgress(binaryData, channels, header.frames, progressCallback)
        } catch (e: Exception) {
            handleCriticalError(e, "parseBinaryDataWithProgress")
            throw IllegalArgumentException("ERR_STAGE_9: Ошибка парсинга бинарных данных: ${e.message}")
        }
        
        if (channelData.isEmpty()) {
            handleCriticalError(IllegalArgumentException("Нет данных каналов"), "parseDataFromBuffer")
            throw IllegalArgumentException("ERR_STAGE_10: Не удалось прочитать данные каналов")
        }
        
        return PDAFile(header, channels, channelData)
    }
    
    /**
     * Парсит бинарные данные каналов с прогрессом.
     *
     * @param binaryData Массив бинарных данных
     * @param channels Список каналов
     * @param frames Ожидаемое количество фреймов
     * @param progressCallback Callback прогресса
     * @return Маппинг ID канала на его данные
     */
    private fun parseBinaryDataWithProgress(
        binaryData: ByteArray,
        channels: List<PDAChannel>,
        frames: Int,
        progressCallback: ((Int, Int, String) -> Unit)? = null
    ): Map<Int, ChannelData> {
        val channelData = mutableMapOf<Int, ChannelData>()
        
        if (channels.isEmpty()) {
            return channelData
        }
        
        // Определяем размер каждого канала в байтах
        val channelInfoList = mutableListOf<ChannelInfo>()
        for (channel in channels) {
            val size = when (channel.dataType.lowercase()) {
                "bit" -> 1
                "byte", "int8" -> 1
                "int16", "word" -> 2
                "int32", "dword" -> 4
                "float" -> 4
                "double" -> 8
                else -> 2
            }
            channelInfoList.add(ChannelInfo(channel, size))
        }
        
        val frameSize = channelInfoList.sumOf { it.size }
        
        if (frameSize == 0 || frames == 0) {
            return channelData
        }
        
        // Реальное количество фреймов (файл может быть обрезан)
        val expectedDataSize = frameSize * frames
        val actualFrames = if (binaryData.size < expectedDataSize) {
            binaryData.size / frameSize
        } else {
            frames
        }
        
        if (actualFrames == 0) {
            return channelData
        }
        
        Log.d("IBAParser", "Начинаем парсинг $actualFrames фреймов (frameSize=$frameSize)")
        
        val buffer = ByteBuffer.wrap(binaryData)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        
        // Временные списки для данных каждого канала
        val dataLists = channels.associate { channel ->
            channel.id to ChannelDataLists(mutableListOf(), mutableListOf())
        }
        
        // Читаем фрейм за фреймом
        for (frameIndex in 0 until actualFrames) {
            try {
                // Уведомляем о прогрессе каждые 50 фреймов
                if (frameIndex % 50 == 0 || frameIndex == actualFrames - 1) {
                    progressCallback?.invoke(
                        frameIndex, 
                        actualFrames, 
                        "Фрейм $frameIndex"
                    )
                }
                
                for ((channel, size) in channelInfoList) {
                    val timestamp = frameIndex * channel.timeBase
                    
                    // Читаем сырое значение в зависимости от типа данных
                    val rawValue = try {
                        when (channel.dataType.lowercase()) {
                            "bit" -> {
                                val byteValue = buffer.get().toInt() and 0xFF
                                (byteValue and 0x01).toDouble()
                            }
                            "byte", "int8" -> buffer.get().toDouble()
                            "int16", "word" -> {
                                if (buffer.remaining() < 2) {
                                    throw IllegalStateException("Недостаточно данных для int16")
                                }
                                buffer.short.toDouble()
                            }
                            "int32", "dword" -> {
                                if (buffer.remaining() < 4) {
                                    throw IllegalStateException("Недостаточно данных для int32")
                                }
                                buffer.int.toDouble()
                            }
                            "float" -> {
                                if (buffer.remaining() < 4) {
                                    throw IllegalStateException("Недостаточно данных для float")
                                }
                                buffer.float.toDouble()
                            }
                            "double" -> {
                                if (buffer.remaining() < 8) {
                                    throw IllegalStateException("Недостаточно данных для double")
                                }
                                buffer.double.toDouble()
                            }
                            else -> {
                                if (buffer.remaining() < 2) {
                                    throw IllegalStateException("Недостаточно данных для чтения")
                                }
                                buffer.short.toDouble()
                            }
                        }
                    } catch (e: IllegalStateException) {
                        Log.e("IBAParser", "Ошибка чтения данных фрейма $frameIndex: ${e.message}")
                        throw e
                    }
                    
                    // Масштабируем значение если не bit тип
                    val scaledValue = if (channel.dataType.lowercase() == "bit") {
                        rawValue
                    } else {
                        scaleValue(
                            rawValue,
                            channel.minScale,
                            channel.maxScale,
                            getMinForType(channel.dataType),
                            getMaxForType(channel.dataType)
                        )
                    }
                    
                    val lists = dataLists[channel.id]
                    lists?.timestamps?.add(timestamp)
                    lists?.values?.add(scaledValue)
                }
                
            } catch (e: Exception) {
                handleCriticalError(e, "parseBinaryDataWithProgress на фрейме $frameIndex")
                throw IllegalStateException("Ошибка на фрейме $frameIndex: ${e.message}")
            }
        }
        
        // Собираем результат
        for ((channelId, lists) in dataLists) {
            channelData[channelId] = ChannelData(
                channelId = channelId,
                timestamps = lists.timestamps,
                values = lists.values
            )
        }
        
        Log.d("IBAParser", "Успешно распаршено ${channelData.size} каналов")
        return channelData
    }
    
    /**
     * Обрабатывает критическую ошибку парсинга.
     * Логирует и копирует ошибку в буфер обмена.
     *
     * @param e Исключение
     * @param location Место возникновения ошибки
     */
    private fun handleCriticalError(e: Throwable, location: String) {
        val stackTrace = e.stackTraceToString()
        val errorMsg = "КРИТИЧЕСКАЯ ОШИБКА PDA ПАРСЕРА\n" +
                      "Место: $location\n" +
                      "Тип: ${e.javaClass.name}\n" +
                      "Сообщение: ${e.message ?: "нет сообщения"}\n" +
                      "Первая строка стека: ${e.stackTrace.firstOrNull() ?: "неизвестно"}\n" +
                      "Время: ${java.util.Date()}\n\n" +
                      "ПОЛНЫЙ СТЕКТРЕЙС:\n$stackTrace"
        
        Log.e("IBAParser", errorMsg)
        copyToClipboard(errorMsg)
    }
    
    /**
     * Парсит заголовок из текстовой части файла.
     *
     * @param textContent Текстовая часть файла
     * @return Объект PDAHeader
     */
    private fun parseHeader(textContent: String): PDAHeader {
        val lines = textContent.lines()
        var clock = 0.04
        var type = "real"
        var startTime = ""
        var frames = 0
        var version = ""
        val modules = mutableMapOf<Int, String>()
        val groups = mutableMapOf<Int, String>()
        
        for (line in lines) {
            when {
                line.startsWith("clk:") -> {
                    val clockStr = line.substringAfter("clk:").trim()
                    clock = if (clockStr.isNotEmpty()) clockStr.toDouble() else 0.04
                }
                line.startsWith("typ:") -> type = line.substringAfter("typ:").trim()
                line.startsWith("starttime:") -> startTime = line.substringAfter("starttime:").trim()
                line.startsWith("frames:") -> {
                    val framesStr = line.substringAfter("frames:").trim()
                    frames = if (framesStr.isNotEmpty()) framesStr.toInt() else 0
                }
                line.startsWith("version:") -> version = line.substringAfter("version:").trim()
                line.startsWith("Module_name_") -> {
                    try {
                        val id = line.substringAfter("Module_name_").substringBefore(":").trim().toInt()
                        val name = line.substringAfter(":").trim()
                        modules[id] = name
                    } catch (e: NumberFormatException) {
                        // Игнорируем ошибку парсинга номера модуля
                    }
                }
                line.startsWith("Group_name_") -> {
                    try {
                        val id = line.substringAfter("Group_name_").substringBefore(":").trim().toInt()
                        val name = line.substringAfter(":").trim()
                        groups[id] = name
                    } catch (e: NumberFormatException) {
                        // Игнорируем ошибку парсинга номера группы
                    }
                }
                line == "endheader:" -> {
                    break
                }
            }
        }
        
        return PDAHeader(clock, type, startTime, frames, version, modules, groups)
    }
    
    /**
     * Парсит список каналов из текстовой части файла.
     *
     * @param textContent Текстовая часть файла
     * @return Список каналов PDAChannel
     */
    private fun parseChannels(textContent: String): List<PDAChannel> {
        val channels = mutableListOf<PDAChannel>()
        val lines = textContent.lines()
        var currentChannel: PDAChannel? = null
        
        for (line in lines) {
            when {
                line.startsWith("beginchannel:") -> {
                    try {
                        val id = line.substringAfter("beginchannel:").trim().toInt()
                        currentChannel = PDAChannel(
                            id = id,
                            name = "",
                            unit = "",
                            dataType = "bit",
                            minScale = 0.0,
                            maxScale = 1.0,
                            group = "",
                            timeBase = 0.1
                        )
                    } catch (e: NumberFormatException) {
                        // Игнорируем ошибку парсинга ID
                    }
                }
                line.startsWith("name:") -> {
                    currentChannel = currentChannel?.copy(name = line.substringAfter("name:").trim())
                }
                line.startsWith("unit:") -> {
                    currentChannel = currentChannel?.copy(unit = line.substringAfter("unit:").trim())
                }
                line.contains("\$PDA_Typ:") -> {
                    val type = line.substringAfter("\$PDA_Typ:").trim()
                    currentChannel = currentChannel?.copy(dataType = type)
                }
                line.startsWith("minscale:") -> {
                    try {
                        val minScale = line.substringAfter("minscale:").trim().toDouble()
                        currentChannel = currentChannel?.copy(minScale = minScale)
                    } catch (e: NumberFormatException) {
                        // Игнорируем ошибку парсинга
                    }
                }
                line.startsWith("maxscale:") -> {
                    try {
                        val maxScale = line.substringAfter("maxscale:").trim().toDouble()
                        currentChannel = currentChannel?.copy(maxScale = maxScale)
                    } catch (e: NumberFormatException) {
                        // Игнорируем ошибку парсинга
                    }
                }
                line.startsWith("group:") -> {
                    currentChannel = currentChannel?.copy(
                        group = line.substringAfter("group:").trim()
                    )
                }
                line.contains("\$PDA_Tbase:") -> {
                    try {
                        val timeBase = line.substringAfter("\$PDA_Tbase:").trim().toDouble()
                        currentChannel = currentChannel?.copy(timeBase = timeBase)
                    } catch (e: NumberFormatException) {
                        // Игнорируем ошибку парсинга
                    }
                }
                line == "endchannel:" -> {
                    currentChannel?.let { channels.add(it) }
                    currentChannel = null
                }
                line.startsWith("digchannel:") -> {
                    currentChannel = currentChannel?.copy(dataType = "bit")
                }
            }
        }
        
        return channels.sortedBy { it.id }
    }
    
    /**
     * Масштабирует значение из сырого диапазона в целевой.
     *
     * @param value Исходное значение
     * @param minScale Минимальное значение шкалы
     * @param maxScale Максимальное значение шкалы
     * @param rawMin Минимальное сырое значение
     * @param rawMax Максимальное сырое значение
     * @return Масштабированное значение
     */
    private fun scaleValue(
        value: Double,
        minScale: Double,
        maxScale: Double,
        rawMin: Double,
        rawMax: Double
    ): Double {
        if (maxScale == minScale || rawMax == rawMin) return value
        return minScale + (value - rawMin) * (maxScale - minScale) / (rawMax - rawMin)
    }
    
    /**
     * Возвращает минимальное значение для типа данных.
     *
     * @param dataType Тип данных
     * @return Минимальное значение
     */
    private fun getMinForType(dataType: String): Double {
        return when (dataType.lowercase()) {
            "bit" -> 0.0
            "byte", "int8" -> 0.0
            "int16", "word" -> 0.0
            "int32", "dword" -> 0.0
            "float" -> 0.0
            "double" -> 0.0
            else -> 0.0
        }
    }
    
    /**
     * Возвращает максимальное значение для типа данных.
     *
     * @param dataType Тип данных
     * @return Максимальное значение
     */
    private fun getMaxForType(dataType: String): Double {
        return when (dataType.lowercase()) {
            "bit" -> 1.0
            "byte", "int8" -> 255.0
            "int16", "word" -> 65535.0
            "int32", "dword" -> 4294967295.0
            "float" -> 1.0
            "double" -> 1.0
            else -> 65535.0
        }
    }
    
    /** Вспомогательный класс: информация о канале с его размером в байтах */
    private data class ChannelInfo(val channel: PDAChannel, val size: Int)
    
    /** Вспомогательный класс: временные списки для данных канала */
    private data class ChannelDataLists(
        val timestamps: MutableList<Double>,
        val values: MutableList<Double>
    )
}