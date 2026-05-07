package com.example.mychartsapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mychartsapp.data.datasources.VersionLocalDataSource
import com.example.mychartsapp.data.repositories.VersionRepositoryImpl
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
import com.example.mychartsapp.presentation.R
import com.example.mychartsapp.presentation.ui.utils.DebugHelper
import com.example.mychartsapp.presentation.viewmodels.MainViewModel

/**
 * Главный экран приложения.
 * Содержит кнопки для навигации к графикам, настройкам и выхода из приложения.
 * Также отображает текущую версию приложения.
 */
class MainActivity : AppCompatActivity() {
    
    /** ViewModel для управления данными главного экрана */
    private lateinit var viewModel: MainViewModel
    
    /**
     * Вызывается при создании активности.
     * Инициализирует UI, зависимости и подписывается на данные ViewModel.
     *
     * @param savedInstanceState Сохраненное состояние активности (если есть)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        DebugHelper.log(this, "MainActivity", "STEP 1: onCreate START")
        super.onCreate(savedInstanceState)
        DebugHelper.log(this, "MainActivity", "STEP 2: after super.onCreate")
        
        // Устанавливаем layout для активности
        setContentView(R.layout.activity_main)
        DebugHelper.log(this, "MainActivity", "STEP 3: setContentView OK")
        
        // Скрываем ActionBar для полноэкранного режима
        supportActionBar?.hide()
        DebugHelper.log(this, "MainActivity", "STEP 4: actionBar hidden")
        
        // Создаем зависимости для получения версии приложения
        val versionDataSource = VersionLocalDataSource(this)
        val versionRepository = VersionRepositoryImpl(versionDataSource)
        val getVersionUseCase = GetAppVersionUseCase(versionRepository)
        DebugHelper.log(this, "MainActivity", "STEP 5: dependencies created")
        
        // Инициализируем ViewModel с кастомной фабрикой
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(getVersionUseCase) as T
                }
            }
        )[MainViewModel::class.java]
        DebugHelper.log(this, "MainActivity", "STEP 6: ViewModel created")
        
        // Устанавливаем слушатель для кнопки "Обычный график"
        findViewById<Button>(R.id.normalChartBtn).setOnClickListener {
            DebugHelper.log(this, "MainActivity", "normalChartBtn clicked")
            startActivity(Intent(this, NormalChartActivity::class.java))
        }
        
        // Устанавливаем слушатель для кнопки "График ИБА"
        findViewById<Button>(R.id.ibaChartBtn).setOnClickListener {
            DebugHelper.log(this, "MainActivity", "ibaChartBtn clicked")
            startActivity(Intent(this, IBAChartActivity::class.java))
        }
        
        // Устанавливаем слушатель для кнопки "Настройки"
        findViewById<Button>(R.id.settingsBtn).setOnClickListener {
            DebugHelper.log(this, "MainActivity", "settingsBtn clicked")
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        // Устанавливаем слушатель для кнопки "Выход"
        findViewById<Button>(R.id.exitBtn).setOnClickListener {
            DebugHelper.log(this, "MainActivity", "exitBtn clicked")
            finishAffinity() // Закрываем все активности
        }
        
        // Наблюдаем за версией приложения и обновляем UI при изменении
        viewModel.appVersion.observe(this) { version ->
            findViewById<TextView>(R.id.versionText).text = "Версия: ${version.versionName}"
            DebugHelper.log(this, "MainActivity", "version observed - ${version.versionName}")
        }
        
        // Загружаем версию приложения
        viewModel.loadAppVersion()
        DebugHelper.log(this, "MainActivity", "STEP 7: onCreate END")
    }
}