package com.example.mychartsapp.domain.models

import java.io.RandomAccessFile

/**
 * Модель полного PDA файла.
 * Содержит заголовок, список каналов и данные по каждому каналу.
 *
 * @property header Заголовок PDA файла
 * @property channels Список всех каналов в файле
 * @property channelData Маппинг ID канала на его данные
 */
data class PDAFile(
    val header: PDAHeader,
    val channels: List<PDAChannel>,
    val channelData: Map<Int, ChannelData>
)

/**
 * Заголовок PDA файла.
 * Содержит основную информацию о файле и времени записи.
 *
 * @property clock Тактовая частота записи (Гц)
 * @property type Тип устройства записи
 * @property startTime Время начала записи
 * @property frames Количество фреймов в файле
 * @property version Версия формата файла
 * @property modules Маппинг ID модуля на его название
 * @property groups Маппинг ID группы на её название
 */
data class PDAHeader(
    val clock: Double,
    val type: String,
    val startTime: String,
    val frames: Int,
    val version: String,
    val modules: Map<Int, String>,
    val groups: Map<Int, String>
)

/**
 * Модель отдельного канала данных.
 *
 * @property id Уникальный идентификатор канала
 * @property name Название канала (например, "Вибрация")
 * @property unit Единица измерения (например, "м/с²", "°С")
 * @property dataType Тип данных канала (например, "int16", "float32")
 * @property minScale Минимальное значение шкалы для отображения
 * @property maxScale Максимальное значение шкалы для отображения
 * @property group Название группы канала
 * @property timeBase База времени канала (коэффициент пересчета времени)
 */
data class PDAChannel(
    val id: Int,
    val name: String,
    val unit: String,
    val dataType: String,
    val minScale: Double,
    val maxScale: Double,
    val group: String,
    val timeBase: Double
)

/**
 * Данные одного канала.
 * Содержит массивы временных меток и соответствующих им значений.
 *
 * @property channelId ID канала, которому принадлежат данные
 * @property timestamps Список временных меток для каждой точки
 * @property values Список значений для каждой точки
 */
data class ChannelData(
    val channelId: Int,
    val timestamps: List<Double>,
    val values: List<Double>
)

/**
 * Точка данных с временной меткой и значением.
 * Используется для построения графиков.
 *
 * @property timestamp Временная метка точки
 * @property value Значение точки
 */
data class DataPoint(
    val timestamp: Double,
    val value: Double
)
