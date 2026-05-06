package com.example.mychartsapp.presentation.ui
import com.example.mychartsapp.presentation.R

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.mychartsapp.presentation.ui.IBAChart.MPAndroidChartManager
import com.example.mychartsapp.presentation.ui.utils.IBAParser
import com.example.mychartsapp.presentation.ui.models.PDAFile
import com.github.mikephil.charting.charts.LineChart
import kotlinx.coroutines.*

class IBAChartActivity : AppCompatActivity() {

    private lateinit var mpAndroidChart1: LineChart
    private lateinit var mpAndroidChart2: LineChart
    private lateinit var mpAndroidChart3: LineChart
    private lateinit var mpAndroidChart4: LineChart
    private lateinit var mpAndroidChart5: LineChart
    
    private lateinit var mpAndroidChartManager: MPAndroidChartManager
    private lateinit var parser: IBAParser
    private var currentPDAFile: PDAFile? = null

    private val filePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.data?.let { uri ->
                val fileName = getFileNameFromUri(uri)
                if (fileName != null && fileName.endsWith(".dat", ignoreCase = true)) {
                    val filePath = uri.toString()
                    parseAndDisplayFile(filePath)
                } else {
                    val errorMsg = "Ошибка: выбран файл не .dat формата. Файл: $fileName"
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show()
                    
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    val clip = android.content.ClipData.newPlainText("Ошибка файла", errorMsg)
                    clipboard.setPrimaryClip(clip)
                }
            }
        }
    }

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
            e.printStackTrace()
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iba_chart)
        
        parser = IBAParser(this)

        initializeManagers()
        initializeCharts()
        setupUI()
        setupAllCharts()

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { uri ->
                val filePath = uri.toString()
                parseAndDisplayFile(filePath)
            }
        }
    }
    
    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        filePickerLauncher.launch(intent)
    }
    
    private fun parseAndDisplayFile(filePath: String) {
        try {
            val pdaFile = parser.parseFile(filePath)
            currentPDAFile = pdaFile
            
            if (pdaFile != null) {
                updateUIWithData(pdaFile)
                Toast.makeText(this, "Файл успешно загружен: ${pdaFile.channels.size} каналов", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Ошибка при чтении файла", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun updateUIWithData(pdaFile: PDAFile) {
        pdaFile.channels.forEach { channel ->
            println("Channel ${channel.id}: ${channel.name}, Type: ${channel.dataType}")
        }
    }
    
    private fun initializeManagers() {
        mpAndroidChartManager = MPAndroidChartManager(this)
    }
    
    private fun initializeCharts() {
        mpAndroidChart1 = findViewById(R.id.chart1)
        mpAndroidChart2 = findViewById(R.id.chart2)
        mpAndroidChart3 = findViewById(R.id.chart3)
        mpAndroidChart4 = findViewById(R.id.chart4)
        mpAndroidChart5 = findViewById(R.id.chart5)
    }
    
    private fun setupUI() {
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.openFileBtn).setOnClickListener {
            openFilePicker()
        }
        
        findViewById<Button>(R.id.clearChartBtn).setOnClickListener {
            clearAllCharts()
            Toast.makeText(this, getString(R.string.clear_chart_toast), Toast.LENGTH_SHORT).show()
        }
        
        findViewById<ImageButton>(R.id.settingsChartBtn).setOnClickListener {
            startActivity(Intent(this, IBAChartSettingsActivity::class.java))
        }
    }
    
    private fun setupAllCharts() {
        setupMPAndroidCharts()
    }
    
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
    
    private fun clearAllCharts() {
        mpAndroidChartManager.clearChart(mpAndroidChart1)
        mpAndroidChartManager.clearChart(mpAndroidChart2)
        mpAndroidChartManager.clearChart(mpAndroidChart3)
        mpAndroidChartManager.clearChart(mpAndroidChart4)
        mpAndroidChartManager.clearChart(mpAndroidChart5)
        
        setupAllCharts()
    }
}
