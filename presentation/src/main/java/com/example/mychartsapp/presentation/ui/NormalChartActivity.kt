package com.example.mychartsapp.presentation.ui

import com.example.mychartsapp.presentation.R

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.*

/**
 * Активность для отображения обычного графика (синусоида).
 * Позволяет открыть файл, очистить график и перейти к настройкам.
 */
class NormalChartActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_chart)
        
        // Кнопка "Назад" - закрываем активность
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        
        // Кнопка "Открыть файл" - показ заглушки
        findViewById<Button>(R.id.openFileBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.open_file_toast), Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка "Очистить график" - показ заглушки
        findViewById<Button>(R.id.clearChartBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.clear_chart_toast), Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка "Настройки" - переход к экрану настроек обычного графика
        findViewById<ImageButton>(R.id.settingsChartBtn).setOnClickListener {
            startActivity(Intent(this, NormalChartSettingsActivity::class.java))
        }
        
        // Демонстрационная отрисовка синусоиды
        setupSineWaveChart()
    }
    
    /**
     * Настраивает демонстрационный график синусоиды.
     * Создает 2000 точек с шагом 0.1 и амплитудой 10.
     */
    private fun setupSineWaveChart() {
        val chart: LineChart = findViewById(R.id.chart)
        val entries: ArrayList<Entry> = ArrayList()
        
        // Генерация точек для синусоиды
        for (i in 0..1999) {
            val x: Float = i.toFloat()
            val y: Float = (Math.sin(i * 0.1) * 10).toFloat()
            entries.add(Entry(x, y))
        }
        
        // Создание набора данных
        val dataSet: LineDataSet = LineDataSet(entries, getString(R.string.sine_wave))
        dataSet.color = -16776961 // Синий цвет

        // Отключаем отображение точек на линии
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
}