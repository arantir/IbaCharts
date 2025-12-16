package com.example.mychartsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        supportActionBar?.hide()
        
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
    }
}