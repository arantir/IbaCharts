package com.example.mychartsapp.presentation.ui
import com.example.mychartsapp.presentation.R

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NormalChartSettingsActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_chart_settings)
        
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener { finish() }
        
        findViewById<Button>(R.id.saveBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.normal_chart_button_save), Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.cancelBtn).setOnClickListener {
            Toast.makeText(this, getString(R.string.normal_chart_button_cancel), Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.setting1Btn).setOnClickListener {
            Toast.makeText(this, "setting1", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.setting2Btn).setOnClickListener {
            Toast.makeText(this, "setting2", Toast.LENGTH_SHORT).show()
        }
        
        findViewById<Button>(R.id.setting3Btn).setOnClickListener {
            Toast.makeText(this, "setting3", Toast.LENGTH_SHORT).show()
        }
    }
}
