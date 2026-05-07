package com.example.mychartsapp.presentation.ui

import com.example.mychartsapp.presentation.R

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Активность с настройками для IBA графика.
 * Содержит кнопки для различных настроек IBA графика, сохранения и отмены изменений.
 */
class IBAChartSettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_iba_chart_settings)
        
        // Кнопка "Назад" - закрываем активность
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        
        // Кнопка "Сохранить" - показ заглушки
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.normal_chart_button_save), Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка "Отмена" - показ заглушки
        findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.normal_chart_button_cancel), Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка настройки 1 - показ заглушки
        findViewById<Button>(R.id.setting1Btn).setOnClickListener {
            Toast.makeText(this, "setting1", Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка настройки 2 - показ заглушки
        findViewById<Button>(R.id.setting2Btn).setOnClickListener {
            Toast.makeText(this, "setting2", Toast.LENGTH_SHORT).show()
        }
        
        // Кнопка настройки 3 - показ заглушки
        findViewById<Button>(R.id.setting3Btn).setOnClickListener {
            Toast.makeText(this, "setting3", Toast.LENGTH_SHORT).show()
        }
    }
}