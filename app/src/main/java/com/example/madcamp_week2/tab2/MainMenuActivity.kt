package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.R
import com.example.madcamp_week2.tab2.MainActivityTab2

class MainMenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        val navigateButton: Button = findViewById(R.id.navigateButton)
        navigateButton.setOnClickListener {
            val intent = Intent(this, MainActivityTab2::class.java)
            startActivity(intent)
        }
    }
}
