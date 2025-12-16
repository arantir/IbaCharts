package com.example.mychartsapp.IBAChart

import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import android.view.MotionEvent
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.listener.ChartTouchListener

class MPAndroidChartManager(private val context: Context) {

    private val synchronizedCharts = mutableListOf<LineChart>()
    private var isSyncing = false

    /**
     * Устанавливает график и его данные.
     */
    fun setupChart(chart: LineChart, title: String, lineColor: Int) {
        configureChartAppearance(chart)
        addSineWaveData(chart, lineColor, title)
        setupSynchronization(chart)
        registerChartForSync(chart)
    }

    /**
     * Общие настройки графика.
     */
    private fun configureChartAppearance(chart: LineChart) {
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.description.isEnabled = false

        val legend = chart.legend
        legend.isEnabled = true
        legend.textColor = Color.BLACK

        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.LTGRAY
        xAxis.axisLineColor = Color.BLACK

        val leftAxis: YAxis = chart.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.LTGRAY
        leftAxis.axisLineColor = Color.BLACK

        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    /**
     * Генерирует и добавляет данные синусоиды.
     */
    private fun addSineWaveData(chart: LineChart, lineColor: Int, title: String) {
        val entries = ArrayList<Entry>()
        for (i in 0..1999) {
            val x = i.toFloat()
            val y = (Math.sin(i * 0.1) * 10f).toFloat()
            entries.add(Entry(x, y))
        }

        val dataSet = LineDataSet(entries, title)
        dataSet.color = lineColor
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.LINEAR

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()
    }

    /**
     * очистка графика
     */
    fun clearChart(chart: LineChart) {
        chart.clear()
        chart.invalidate()
    }

    /**
     * Настройка и активирование синхронизации двух графиков.
     */
    private fun setupSynchronization(chart: LineChart) {
        chart.setOnChartGestureListener(object : OnChartGestureListener {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                // ничего
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                // ничего
            }

            override fun onChartLongPressed(me: MotionEvent?) {
                // ничего
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
                // ничего
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                // ничего
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
                // ничего
            }

            override fun onChartScale(
                me: MotionEvent?,
                scaleX: Float,
                scaleY: Float
            ) {
                if (!isSyncing) {
                    synchronizeCharts(chart)
                }
            }

            override fun onChartTranslate(
                me: MotionEvent?,
                dX: Float,
                dY: Float
            ) {
                if (!isSyncing && dX != 0.0f) {
                    synchronizeCharts(chart)
                }
            }
        })
    }

    /**
     * Синхронизировать другие графики по оси X
     */
    private fun synchronizeCharts(sourceChart: LineChart) {
        if (synchronizedCharts.size < 2 || isSyncing) return

        isSyncing = true

        synchronizedCharts.forEach { targetChart ->
            if (targetChart != sourceChart) {

                // Получаем текущий масштаб и позицию исходного графика
                val scaleX = sourceChart.viewPortHandler.scaleX
                val transX = sourceChart.viewPortHandler.transX

                // Задаём текущий масштаб и позицию целевому графику
                targetChart.viewPortHandler.refresh(Matrix().apply { 
                    setScale(scaleX, 1f)
                    postTranslate(transX, 0f)
                }, targetChart, true)

                targetChart.invalidate()
            }
        }

        isSyncing = false
    }

    /**
     * Регистрация графика для синхронизации
     */
    fun registerChartForSync(chart: LineChart) {
        if (!synchronizedCharts.contains(chart)) {
            synchronizedCharts.add(chart)
        }
    }
}