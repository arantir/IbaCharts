package com.example.mychartsapp.presentation.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mychartsapp.data.utils.IBAParser
import com.example.mychartsapp.domain.models.PDAFile
import com.example.mychartsapp.presentation.R
import com.example.mychartsapp.presentation.ui.IBAChart.MPAndroidChartManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.sin

/**
 * Compose экран для отображения графиков IBA (PDA файлов).
 * Поддерживает открытие .dat файлов и отображение до 5 каналов данных.
 * Для демонстрации показывает 5 тестовых синусоид.
 *
 * @param onBackClick Callback для возврата на предыдущий экран
 * @param onSettingsClick Callback для открытия настроек IBA графика
 */
@Composable
fun IBAChartScreen(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    
    /** Парсер для файлов формата IBA/PDA */
    val parser = IBAParser()
    
    /** Текущий загруженный PDA файл */
    var currentPDAFile by remember { mutableStateOf<PDAFile?>(null) }
    
    /** Менеджер для работы с графиками MPAndroidChart */
    val mpAndroidChartManager = remember { MPAndroidChartManager(context) }
    
    /**
     * Регистрация ланчера для выбора файла через системный файловый менеджер.
     * Обрабатывает результат выбора файла и проверяет расширение .dat.
     */
    val filePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileNameFromUri(context, uri)
                if (fileName != null && fileName.endsWith(".dat", ignoreCase = true)) {
                    val filePath = uri.toString()
                    parseAndDisplayFile(context, parser, filePath) { pdaFile ->
                        currentPDAFile = pdaFile
                    }
                } else {
                    Toast.makeText(context, "Ошибка: выбран файл не .dat формата", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    
    /**
     * Открывает системный файловый менеджер для выбора .dat файла.
     */
    fun openFilePicker() {
        val intent = android.content.Intent(android.content.Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(android.content.Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        filePickerLauncher.launch(intent)
    }
    
    /**
     * Очищает все графики и показывает тестовые синусоиды.
     */
    fun clearAllCharts() {
        currentPDAFile = null
        Toast.makeText(context, "Графики очищены, показаны тестовые синусоиды", Toast.LENGTH_SHORT).show()
    }
    
    // Цвета для 5 каналов графиков
    val colors = listOf(
        ContextCompat.getColor(context, R.color.color_red),
        ContextCompat.getColor(context, R.color.color_green),
        ContextCompat.getColor(context, R.color.color_blue),
        ContextCompat.getColor(context, R.color.color_purple),
        ContextCompat.getColor(context, R.color.color_orange)
    )
    
    // Названия для тестовых графиков
    val chartNames = listOf("Синусоида 1", "Синусоида 2", "Синусоида 3", "Синусоида 4", "Синусоида 5")
    
    // Основной вертикальный контейнер
    Column(modifier = Modifier.fillMaxSize()) {
        
        // Верхняя панель с кнопками управления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка "Назад"
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            
            // Растягивающийся разделитель
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка "Открыть файл"
            Button(onClick = { openFilePicker() }) {
                Text("Открыть файл")
            }
            
            // Кнопка "Очистить график"
            Button(onClick = { clearAllCharts() }) {
                Text("Очистить")
            }
            
            // Кнопка "Настройки"
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Настройки"
                )
            }
        }
        
        // Область отображения графиков
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (currentPDAFile != null) {
                // Отображаем графики из загруженного файла (до 5 каналов)
                for (i in 0 until minOf(currentPDAFile!!.channels.size, 5)) {
                    val channel = currentPDAFile!!.channels[i]
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "Канал ${channel.id}: ${channel.name}",
                                style = MaterialTheme.typography.titleSmall
                            )
                            
                            AndroidView(
                                factory = { ctx ->
                                    LineChart(ctx).apply {
                                        mpAndroidChartManager.setupChart(
                                            this,
                                            channel.name,
                                            colors[i % colors.size]
                                        )
                                        mpAndroidChartManager.registerChartForSync(this)
                                        
                                        // Заполняем график демо-данными (синусоида)
                                        val entries = generateSineWaveData()
                                        val dataSet = LineDataSet(entries, channel.name)
                                        dataSet.color = colors[i % colors.size]
                                        dataSet.setDrawCircles(false)
                                        dataSet.setDrawCircleHole(false)
                                        this.data = LineData(dataSet)
                                        this.invalidate()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }
                    }
                }
            } else {
                // Демонстрационный режим - показываем 5 тестовых синусоид
                for (i in 0 until 5) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = chartNames[i],
                                style = MaterialTheme.typography.titleSmall
                            )
                            
                            AndroidView(
                                factory = { ctx ->
                                    LineChart(ctx).apply {
                                        mpAndroidChartManager.setupChart(
                                            this,
                                            chartNames[i],
                                            colors[i % colors.size]
                                        )
                                        mpAndroidChartManager.registerChartForSync(this)
                                        
                                        // Генерация тестовых данных (синусоида)
                                        val entries = generateSineWaveData()
                                        val dataSet = LineDataSet(entries, chartNames[i])
                                        dataSet.color = colors[i % colors.size]
                                        dataSet.setDrawCircles(false)
                                        dataSet.setDrawCircleHole(false)
                                        this.data = LineData(dataSet)
                                        this.invalidate()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(250.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Генерирует тестовые данные для синусоиды.
 * @return Список точек для графика
 */
private fun generateSineWaveData(): List<Entry> {
    val entries = mutableListOf<Entry>()
    for (i in 0..199) {
        val x = i.toFloat()
        val y = (sin(i * 0.1) * 10).toFloat()
        entries.add(Entry(x, y))
    }
    return entries
}

/**
 * Получает имя файла из URI.
 */
private fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    return try {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                cursor.getString(nameIndex)
            } else {
                null
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Парсит и отображает выбранный файл.
 */
private fun parseAndDisplayFile(
    context: android.content.Context,
    parser: IBAParser,
    filePath: String,
    onResult: (PDAFile?) -> Unit
) {
    try {
        val pdaFile = parser.parseFile(filePath)
        onResult(pdaFile)
        
        if (pdaFile != null) {
            Toast.makeText(context, "Файл успешно загружен: ${pdaFile.channels.size} каналов", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Ошибка при чтении файла", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
