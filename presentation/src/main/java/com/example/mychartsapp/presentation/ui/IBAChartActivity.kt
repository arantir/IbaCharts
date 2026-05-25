package com.example.mychartsapp.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.mychartsapp.presentation.R
import com.example.mychartsapp.presentation.ui.IBAChart.MPAndroidChartManager
import com.example.mychartsapp.presentation.ui.utils.DebugHelper
import com.example.mychartsapp.data.utils.IBAParser
import com.example.mychartsapp.domain.models.PDAFile
import com.github.mikephil.charting.charts.LineChart

/**
 * Активность для отображения графиков IBA (PDA файлов).
 * Поддерживает открытие .dat файлов и отображение до 5 каналов данных.
 */
class IBAChartActivity : AppCompatActivity() {

    // Графики для отображения 5 каналов
    private lateinit var mpAndroidChart1: LineChart
    private lateinit var mpAndroidChart2: LineChart
    private lateinit var mpAndroidChart3: LineChart
    private lateinit var mpAndroidChart4: LineChart
    private lateinit var mpAndroidChart5: LineChart
    
    // Менеджер для работы с графиками MPAndroidChart
    private lateinit var mpAndroidChartManager: MPAndroidChartManager
    // Парсер для файлов формата IBA/PDA
    private val parser = IBAParser()
    // Текущий загруженный PDA файл
    private var currentPDAFile: PDAFile? = null

    // Регистрация ланчера для выбора файла через системный файловый менеджер
    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        DebugHelper.log(this, "IBAChart", "filePicker result: ${result.resultCode}")
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileNameFromUri(uri)
                DebugHelper.log(this, "IBAChart", "fileName: $fileName")
                // Проверяем, что файл имеет расширение .dat
                if (fileName != null && fileName.endsWith(".dat", ignoreCase = true)) {
                    val filePath = uri.toString()
                    parseAndDisplayFile(filePath)
                } else {
                    val errorMsg = "Ошибка: выбран файл не .dat формата. Файл: $fileName"
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    DebugHelper.log(this, "IBAChart", errorMsg)
                }
            }
        }
    }

    /**
     * Получает имя файла из URI.
     * @param uri URI выбранного файла
     * @return имя файла или null в случае ошибки
     */
    private fun getFileNameFromUri(uri: Uri): String? {
        return try {
            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    cursor.getString(nameIndex)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            DebugHelper.log(this, "IBAChart", "getFileNameFromUri error: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        DebugHelper.log(this, "IBAChart", "STEP 1: onCreate START")
        super.onCreate(savedInstanceState)
        DebugHelper.log(this, "IBAChart", "STEP 2: after super.onCreate")
        
        try {
            setContentView(R.layout.activity_iba_chart)
            DebugHelper.log(this, "IBAChart", "STEP 3: setContentView OK")

            // Инициализация менеджеров и графиков
            initializeManagers()
            DebugHelper.log(this, "IBAChart", "STEP 4: initializeManagers OK")
            initializeCharts()
            DebugHelper.log(this, "IBAChart", "STEP 5: initializeCharts OK")
            setupUI()
            DebugHelper.log(this, "IBAChart", "STEP 6: setupUI OK")
            setupAllCharts()
            DebugHelper.log(this, "IBAChart", "STEP 7: setupAllCharts OK")

            // Обработка Intent (для открытия файла извне)
            handleIntent(intent)
            DebugHelper.log(this, "IBAChart", "STEP 8: onCreate END SUCCESS")
        } catch (e: Exception) {
            DebugHelper.log(this, "IBAChart", "ERROR: ${e.message}")
            DebugHelper.log(this, "IBAChart", "STACK: ${e.stackTraceToString()}")
            throw e
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        DebugHelper.log(this, "IBAChart", "onNewIntent: ${intent?.action}")
        handleIntent(intent)
    }
    
    /**
     * Обрабатывает Intent для открытия файла из файлового менеджера.
     * @param intent Intent с данными файла
     */
    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { uri ->
                val filePath = uri.toString()
                DebugHelper.log(this, "IBAChart", "handleIntent filePath: $filePath")
                parseAndDisplayFile(filePath)
            }
        }
    }
    
    /**
     * Открывает системный файловый менеджер для выбора .dat файла.
     */
    private fun openFilePicker() {
        DebugHelper.log(this, "IBAChart", "openFilePicker")
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        filePickerLauncher.launch(intent)
    }
    
    /**
     * Парсит и отображает выбранный файл.
     * @param filePath путь к файлу (URI)
     */
    private fun parseAndDisplayFile(filePath: String) {
        DebugHelper.log(this, "IBAChart", "parseAndDisplayFile: $filePath")
        try {
            val pdaFile = parser.parseFile(filePath)
            currentPDAFile = pdaFile
            
            if (pdaFile != null) {
                updateUIWithData(pdaFile)
                Toast.makeText(this, "Файл успешно загружен: ${pdaFile.channels.size} каналов", Toast.LENGTH_LONG).show()
                DebugHelper.log(this, "IBAChart", "File loaded: ${pdaFile.channels.size} channels")
            } else {
                Toast.makeText(this, "Ошибка при чтении файла", Toast.LENGTH_SHORT).show()
                DebugHelper.log(this, "IBAChart", "pdaFile is null")
            }
        } catch (e: Exception) {
            DebugHelper.log(this, "IBAChart", "parseAndDisplayFile error: ${e.message}")
            e.printStackTrace()
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    /**
     * Обновляет UI данными из загруженного файла.
     * @param pdaFile объект с данными PDA файла
     */
    private fun updateUIWithData(pdaFile: PDAFile) {
        pdaFile.channels.forEach { channel ->
            DebugHelper.log(this, "IBAChart", "Channel ${channel.id}: ${channel.name}")
        }
    }
    
    /**
     * Инициализирует менеджер графиков.
     */
    private fun initializeManagers() {
        mpAndroidChartManager = MPAndroidChartManager(this)
    }
    
    /**
     * Инициализирует все графики из layout.
     */
    private fun initializeCharts() {
        mpAndroidChart1 = findViewById(R.id.chart1)
        mpAndroidChart2 = findViewById(R.id.chart2)
        mpAndroidChart3 = findViewById(R.id.chart3)
        mpAndroidChart4 = findViewById(R.id.chart4)
        mpAndroidChart5 = findViewById(R.id.chart5)
    }
    
    /**
     * Настраивает UI элементы: кнопки навигации и управления.
     */
    private fun setupUI() {
        // Кнопка "Назад" - закрываем активность
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        
        // Кнопка "Открыть файл" - открываем файловый менеджер
        findViewById<Button>(R.id.openFileBtn).setOnClickListener {
            openFilePicker()
        }
        
        // Кнопка "Очистить график" - очищаем все графики
        findViewById<Button>(R.id.clearChartBtn).setOnClickListener {
            clearAllCharts()
            Toast.makeText(this, getString(R.string.clear_chart_toast), Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка "Настройки" - переход к настройкам IBA графика
        findViewById<ImageButton>(R.id.settingsChartBtn).setOnClickListener {
            startActivity(Intent(this, IBAChartSettingsActivity::class.java))
        }
    }
    
    /**
     * Настраивает все графики.
     */
    private fun setupAllCharts() {
        setupMPAndroidCharts()
    }
    
    /**
     * Настраивает 5 графиков MPAndroidChart с разными цветами.
     * Каждый график регистрируется для синхронизации при зуммировании.
     */
    private fun setupMPAndroidCharts() {
        mpAndroidChartManager.setupChart(
            mpAndroidChart1, 
            getString(R.string.mpandroid_chart_sine_wave_1), 
            ContextCompat.getColor(this, R.color.color_red)
        )
        mpAndroidChartManager.registerChartForSync(mpAndroidChart1)
        
        mpAndroidChartManager.setupChart(
            mpAndroidChart2, 
            getString(R.string.mpandroid_chart_sine_wave_2), 
            ContextCompat.getColor(this, R.color.color_green)
        )
        mpAndroidChartManager.registerChartForSync(mpAndroidChart2)
        
        mpAndroidChartManager.setupChart(
            mpAndroidChart3, 
            getString(R.string.mpandroid_chart_sine_wave_3), 
            ContextCompat.getColor(this, R.color.color_blue)
        )
        mpAndroidChartManager.registerChartForSync(mpAndroidChart3)
        
        mpAndroidChartManager.setupChart(
            mpAndroidChart4, 
            getString(R.string.mpandroid_chart_sine_wave_4), 
            ContextCompat.getColor(this, R.color.color_purple)
        )
        mpAndroidChartManager.registerChartForSync(mpAndroidChart4)
        
        mpAndroidChartManager.setupChart(
            mpAndroidChart5, 
            getString(R.string.mpandroid_chart_sine_wave_5), 
            ContextCompat.getColor(this, R.color.color_orange)
        )
        mpAndroidChartManager.registerChartForSync(mpAndroidChart5)
    }
    
    /**
     * Очищает все графики от данных.
     */
    private fun clearAllCharts() {
        mpAndroidChartManager.clearChart(mpAndroidChart1)
        mpAndroidChartManager.clearChart(mpAndroidChart2)
        mpAndroidChartManager.clearChart(mpAndroidChart3)
        mpAndroidChartManager.clearChart(mpAndroidChart4)
        mpAndroidChartManager.clearChart(mpAndroidChart5)
        
        setupAllCharts()
    }
}