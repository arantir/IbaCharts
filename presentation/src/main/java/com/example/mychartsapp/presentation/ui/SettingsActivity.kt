package com.example.mychartsapp.presentation.ui
import com.example.mychartsapp.presentation.R

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        
        findViewById<ImageButton>(R.id.backBtn).setOnClickListener {
            finish()
        }
    }
}
