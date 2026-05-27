package com.example.mychartsapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import com.example.mychartsapp.presentation.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

/**
 * Compose компонент экрана обычного графика.
 * Отображает панель управления с кнопками назад, открыть файл, очистить, настройки,
 * а также демонстрационный график синусоиды.
 * 
 * @param onBackClick Callback для кнопки "Назад" (возврат на предыдущий экран)
 * @param onOpenFileClick Callback для кнопки "Открыть файл"
 * @param onClearChartClick Callback для кнопки "Очистить график"
 * @param onSettingsClick Callback для кнопки "Настройки" (переход к настройкам)
 */
@Composable
fun NormalChartScreen(
    onBackClick: () -> Unit,
    onOpenFileClick: () -> Unit,
    onClearChartClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    // Контекст для доступа к ресурсам
    val context = LocalContext.current
    
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
            // Кнопка "Назад" - закрытие активности
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            
            // Растягивающийся разделитель
            Spacer(modifier = Modifier.weight(1f))
            
            // Кнопка "Открыть файл"
            Button(onClick = onOpenFileClick) {
                Text("Открыть файл")
            }
            
            // Кнопка "Очистить график"
            Button(onClick = onClearChartClick) {
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
        
        // Разделительная линия
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp
        )
        
        // Область с графиком (обертка для MPAndroidChart)
        AndroidView(
            factory = { ctx ->
                LineChart(ctx).apply {
                    // Генерация демонстрационного графика синусоиды
                    setupSineWaveChart(this)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(8.dp)
        )
    }
}

/**
 * Настраивает демонстрационный график синусоиды.
 * Создает 2000 точек с шагом 0.1 и амплитудой 10.
 * @param chart График MPAndroidChart для настройки
 */
private fun setupSineWaveChart(chart: LineChart) {
    // Список точек для графика
    val entries: ArrayList<Entry> = ArrayList()
    
    // Генерация точек для синусоиды (2000 точек)
    for (i in 0..1999) {
        val x: Float = i.toFloat()
        val y: Float = (Math.sin(i * 0.1) * 10).toFloat()
        entries.add(Entry(x, y))
    }
    
    // Создание набора данных с названием "Синусоида"
    val dataSet: LineDataSet = LineDataSet(entries, "Синусоида")
    dataSet.color = -16776961 // Синий цвет
    
    // Отключаем отображение точек на линии для более чистого вида
    dataSet.setDrawCircles(false)
    dataSet.setDrawCircleHole(false)
    
    // Установка данных в график
    val lineData: LineData = LineData(dataSet)
    chart.data = lineData
    
    // Перемещаем ось X в нижнюю часть графика
    chart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
    
    // Обновляем отображение графика
    chart.invalidate()
}
