package com.example.mychartsapp.presentation.ui.IBAChart

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

/**
 * Менеджер для управления графиками MPAndroidChart.
 * Обеспечивает синхронизацию масштабирования и прокрутки между несколькими графиками.
 *
 * @param context Контекст приложения для доступа к ресурсам
 */
class MPAndroidChartManager(private val context: Context) {

    /** Список графиков, участвующих в синхронизации */
    private val synchronizedCharts = mutableListOf<LineChart>()
    
    /** Флаг предотвращения рекурсивной синхронизации */
    private var isSyncing = false

    /**
     * Настраивает график с демонстрационными данными синусоиды.
     *
     * @param chart График для настройки
     * @param title Название набора данных
     * @param lineColor Цвет линии графика
     */
    fun setupChart(chart: LineChart, title: String, lineColor: Int) {
        configureChartAppearance(chart)
        addSineWaveData(chart, lineColor, title)
        setupSynchronization(chart)
        registerChartForSync(chart)
    }

    /**
     * Выполняет общие настройки внешнего вида графика.
     *
     * @param chart График для настройки
     */
    private fun configureChartAppearance(chart: LineChart) {
        // Включаем Touch и жесты
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.description.isEnabled = false

        // Настройки легенды
        val legend = chart.legend
        legend.isEnabled = true
        legend.textColor = Color.BLACK

        // Настройки оси X
        val xAxis: XAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.setDrawGridLines(true)
        xAxis.gridColor = Color.LTGRAY
        xAxis.axisLineColor = Color.BLACK

        // Настройки левой оси Y
        val leftAxis: YAxis = chart.axisLeft
        leftAxis.textColor = Color.BLACK
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.LTGRAY
        leftAxis.axisLineColor = Color.BLACK

        // Отключаем правую ось Y
        val rightAxis: YAxis = chart.axisRight
        rightAxis.isEnabled = false
    }

    /**
     * Генерирует демонстрационные данные синусоиды и добавляет их на график.
     *
     * @param chart График для добавления данных
     * @param lineColor Цвет линии
     * @param title Название набора данных
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
        dataSet.setDrawCircles(false)   // Не рисуем точки
        dataSet.setDrawValues(false)    // Не рисуем значения
        dataSet.mode = LineDataSet.Mode.LINEAR

        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()  // Обновляем отображение
    }

    /**
     * Очищает график от всех данных.
     *
     * @param chart График для очистки
     */
    fun clearChart(chart: LineChart) {
        chart.clear()
        chart.invalidate()
    }

    /**
     * Настраивает синхронизацию графика с другими.
     * При масштабировании или прокрутке синхронизирует все зарегистрированные графики.
     *
     * @param chart График для настройки синхронизации
     */
    private fun setupSynchronization(chart: LineChart) {
        chart.setOnChartGestureListener(object : OnChartGestureListener {
            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                // Не используется
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
                // Не используется
            }

            override fun onChartLongPressed(me: MotionEvent?) {
                // Не используется
            }

            override fun onChartDoubleTapped(me: MotionEvent?) {
                // Не используется
            }

            override fun onChartSingleTapped(me: MotionEvent?) {
                // Не используется
            }

            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
                // Не используется
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
     * Синхронизирует все зарегистрированные графики по оси X.
     * Копирует масштаб и позицию исходного графика на все остальные.
     *
     * @param sourceChart Исходный график, с которого копируются параметры
     */
    private fun synchronizeCharts(sourceChart: LineChart) {
        if (synchronizedCharts.size < 2 || isSyncing) return

        isSyncing = true

        synchronizedCharts.forEach { targetChart ->
            if (targetChart != sourceChart) {
                // Получаем текущий масштаб и позицию исходного графика
                val scaleX = sourceChart.viewPortHandler.scaleX
                val transX = sourceChart.viewPortHandler.transX

                // Применяем масштаб и позицию к целевому графику
                targetChart.viewPortHandler.refresh(
                    Matrix().apply { 
                        setScale(scaleX, 1f)
                        postTranslate(transX, 0f)
                    }, 
                    targetChart, 
                    true
                )

                targetChart.invalidate()
            }
        }

        isSyncing = false
    }

    /**
     * Регистрирует график для участия в синхронизации.
     *
     * @param chart График для регистрации
     */
    fun registerChartForSync(chart: LineChart) {
        if (!synchronizedCharts.contains(chart)) {
            synchronizedCharts.add(chart)
        }
    }
}