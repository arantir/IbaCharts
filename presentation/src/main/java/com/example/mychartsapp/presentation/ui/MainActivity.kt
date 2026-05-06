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
import com.example.mychartsapp.presentation.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    
    private lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        supportActionBar?.hide()
        
        val versionDataSource = VersionLocalDataSource(this)
        val versionRepository = VersionRepositoryImpl(versionDataSource)
        val getVersionUseCase = GetAppVersionUseCase(versionRepository)
        
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(getVersionUseCase) as T
                }
            }
        )[MainViewModel::class.java]
        
        findViewById<Button>(R.id.normalChartBtn).setOnClickListener {
            startActivity(Intent(this, NormalChartActivity::class.java))
        }
        
        findViewById<Button>(R.id.ibaChartBtn).setOnClickListener {
            startActivity(Intent(this, IBAChartActivity::class.java))
        }
        
        findViewById<Button>(R.id.settingsBtn).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        findViewById<Button>(R.id.exitBtn).setOnClickListener {
            finishAffinity()
        }
        
        viewModel.appVersion.observe(this) { version ->
            findViewById<TextView>(R.id.versionText).text = "Версия: ${version.versionName}"
        }
        
        viewModel.loadAppVersion()
    }
}
