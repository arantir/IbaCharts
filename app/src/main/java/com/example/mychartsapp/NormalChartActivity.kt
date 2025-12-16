package com.example.mychartsapp

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

class NormalChartActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_chart)
        
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.openFileBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.open_file_toast), Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.clearChartBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.clear_chart_toast), Toast.LENGTH_SHORT).show()
        }
        
        findViewById<ImageButton>(R.id.settingsChartBtn).setOnClickListener {
            startActivity(Intent(this, NormalChartSettingsActivity::class.java))
        }
        
        setupSineWaveChart()
    }
    
    private fun setupSineWaveChart() {
        val chart: LineChart = findViewById(R.id.chart)
        val entries: ArrayList<Entry> = ArrayList()
        
        for (i in 0..1999) {
            val x: Float = i.toFloat()
            val y: Float = (Math.sin(i * 0.1) * 10).toFloat()
            entries.add(Entry(x, y))
        }
        
        val dataSet: LineDataSet = LineDataSet(entries, getString(R.string.sine_wave))
        dataSet.color = -16776961

        //Убираем точки.
        dataSet.setDrawCircles(false)
        dataSet.setDrawCircleHole(false)
        
        val lineData: LineData = LineData(dataSet)
        chart.data = lineData

        //Перемещаем ось Х вниз
        chart.xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

        chart.invalidate()
    }
}